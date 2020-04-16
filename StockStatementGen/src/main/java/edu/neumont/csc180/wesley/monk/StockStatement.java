package edu.neumont.csc180.wesley.monk;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.awt.Desktop;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;

public class StockStatement {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("stock_transations.by.account.holder.json"));
            System.out.println();
            JSONArray accountList = (JSONArray) obj;
            int i = 0;
            for (Object val : accountList) {
                if (i>=1) {
                    break;
                }
                HTMLCreater(val);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void HTMLCreater(Object val) throws IOException {
        JSONObject jsonObject = (JSONObject) val;
        if (jsonObject.get("account_number") != null && jsonObject.get("ssn") != null && jsonObject.get("first_name")
                != null && jsonObject.get("last_name") != null && jsonObject.get("email") 
                != null && jsonObject.get("phone") != null && jsonObject.get("beginning_balance") != null 
                && jsonObject.get("stock_trades") != null) {
            ZoneId zonedId = ZoneId.of("-07:00");
            LocalDate today = LocalDate.now(zonedId);

            File f = new File("html/" + jsonObject.get("account_number") + ".html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\" />\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                    "    <title></title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>Statement Date: " + today + "</h1>" +
                    "    <br />\n" +
                    "    <table>\n" +
                    "        <tr>\n" +
                    "            <th width=\"100px\">Account Number</th>\n" +
                    "            <th width=\"100px\">SSN</th>\n" +
                    "            <th width=\"100px\">First Name</th>\n" +
                    "            <th width=\"100px\">Last Name</th>\n" +
                    "            <th width=\"100px\">Email</th>\n" +
                    "            <th width=\"100px\">Phone</th>\n" +
                    "            <th width=\"100px\">Beginning Balance</th>\n" +
                    "        </tr>\n" +
                    "        <tr>\n" +
                    "            <td>" + jsonObject.get("account_number") + "</td>\n" +
                    "            <td>" + jsonObject.get("ssn") + "</td>\n" +
                    "            <td>" + jsonObject.get("first_name") + "</td>\n" +
                    "            <td>" + jsonObject.get("last_name") + "</td>\n" +
                    "            <td>" + jsonObject.get("email") + "</td>\n" +
                    "            <td>" + jsonObject.get("phone") + "</td>\n" +
                    "            <td>" + jsonObject.get("beginning_balance") + "</td>\n" +
                    "        </tr>\n" +
                    "    </table>\n" +
                    "    <br />\n" +
                    "    <br />\n" +
                    "    <table>\n" +
                    "       <tr width=\"200px\">\n" +
                    "           <th>Type</th>\n" +
                    "           <th>Stock Symbol</th>\n" +
                    "           <th># of Shares</th>\n" +
                    "           <th>Price Per Share</th>\n" +
                    "           <th>Total Amount</th>\n" +
                    "       </tr>");

            JSONArray jsonArray = (JSONArray) jsonObject.get("stock_trades");
            for (Object stock : jsonArray) {
                JSONObject stock_object = (JSONObject) stock;
                String x = (String) stock_object.get("price_per_share");
                x = x.replace("$", "");
                double price = Double.parseDouble(x);
                String y = stock_object.get("count_shares").toString();
                double shares = Double.parseDouble(y);
                double total = price * shares;
                bw.write("<tr width=\"200px\">\n" +
                        "    <td width=\"200px\">" + stock_object.get("type") + "</td>\n" +
                        "    <td width=\"200px\">" + stock_object.get("stock_symbol") + "</td>\n" +
                        "    <td width=\"200px\">" + stock_object.get("count_shares") + "</td>\n" +
                        "    <td width=\"200px\">" + stock_object.get("price_per_share") + "</td>\n" +
                        "    <td width=\"200px\">" + total + "</td>\n" +
                        "  </tr>");
            }
            bw.write("</table>\n" +
                    "</body>\n" +
                    "<footer>\n" +
                    "   <table>\n" +
                    "       <tr>\n" +
                    "           <th width=\"200px\">Total Cash</th>\n" +
                    "           <th width=\"200px\">Total Stocks</th>\n" +
                    "       </tr>\n");
            String bal = (String) jsonObject.get("beginning_balance");
            bal = bal.replace("$", "");
            double totalCash = Double.parseDouble(bal);
            double totalStocks = 0;

            for (Object stock : jsonArray) {
                JSONObject stock_object = (JSONObject) stock;
                String x = (String) stock_object.get("price_per_share");
                x = x.replace("$", "");
                double price = Double.parseDouble(x);
                String y = stock_object.get("count_shares").toString();
                double shares = Double.parseDouble(y);

                if (stock_object.get("type").toString().equalsIgnoreCase("Buy")) {
                    totalStocks += shares;
                    totalCash -= shares * price;
                } else {
                    totalStocks -= shares;
                    totalCash += shares * price;
                }
            }

            bw.write("<tr>\n" +
                    "   <th width=\"200px\">" + totalCash + "</th>\n" +
                    "   <th width=\"200px\">" + totalStocks + "</th>\n" +
                    "</tr>\n" +
                    "</table>\n" +
                    "</footer>\n" +
                    "</html>");
            bw.close();

            String accountNumber = jsonObject.get("account_number").toString();
            long accountID = Long.parseLong(accountNumber);
            System.out.println(accountID);
            PDFCreater(accountID);

//            try (OutputStream os = new FileOutputStream("pdf/" + jsonObject.get("account_number") + ".pdf")) {
//                PdfRendererBuilder builder = new PdfRendererBuilder();
//                builder.useFastMode();
//                builder.withUri("file:html/" + jsonObject.get("account_number") + ".html");
//                builder.toStream(os);
//                builder.run();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        else {
            throw new IllegalArgumentException("The JSON object doesn't have the correct values");
        }
    }

    private static void PDFCreater(long accountID) {
        File file = new File("html/" + accountID + ".html");
        if (file.exists()) {
            try (OutputStream os = new FileOutputStream("pdf/" + accountID + ".pdf")) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withUri("file:html/" + accountID + ".html");
                builder.toStream(os);
                builder.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("There is no HTML file to convert to PDF");
        }
    }
}
