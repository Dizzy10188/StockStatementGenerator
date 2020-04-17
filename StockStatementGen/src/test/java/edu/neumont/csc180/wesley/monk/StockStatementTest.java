package edu.neumont.csc180.wesley.monk;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StockStatementTest {

    @Test
    void should_create_all_html_and_pdf_pages() throws IOException {
        StockStatement.main();
    }

    @Test
    void should_fail_HTML_Maker() throws IOException {
        StockStatement.HTMLCreater(new JSONObject());
    }

    @Test
    void should_pass_HTML_Maker() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("stock_transations.by.account.holder.json"));
            JSONArray accountList = (JSONArray) obj;
            JSONObject jsonObject = (JSONObject) accountList.get(0);
            StockStatement.HTMLCreater(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void should_fail_PDF_Maker() {
        StockStatement.PDFCreater(412);
    }

    @Test
    void should_pass_PDF_Maker() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("stock_transations.by.account.holder.json"));
            JSONArray accountList = (JSONArray) obj;
            JSONObject jsonObject = (JSONObject) accountList.get(0);
            StockStatement.HTMLCreater(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}