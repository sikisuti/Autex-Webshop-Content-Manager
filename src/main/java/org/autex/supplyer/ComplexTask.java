package org.autex.supplyer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.*;
import org.autex.Configuration;
import org.autex.model.MetaData;
import org.autex.model.Product;
import org.apache.commons.codec.*;

public class ComplexTask extends Task<ObservableList<Product>> {
    private final File masterDataFile;
    private final File stockFile;

    public ComplexTask(File masterDataFile, File stockFile) {
        this.masterDataFile = masterDataFile;
        this.stockFile = stockFile;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
            return mergeMasterDataWithStock(loadMasterData());
    }

    private Map<String, XSSFRow> loadMasterData() throws IOException {
        Map<String, XSSFRow> itemList = new HashMap<>();
        updateTitle("1/3 Complex cikktörzs betöltése");
        try (InputStream masterDataStream = new FileInputStream(masterDataFile)) {
            XSSFWorkbook wb = new XSSFWorkbook(masterDataStream);
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                XSSFCell firstCell = row.getCell(1);
                String id = df.formatCellValue(firstCell);
                if (firstCell == null) {
                    break;
                }

                itemList.put(id, row);
                updateProgress(rowIndex, sheet.getLastRowNum());
            }
        }

        updateTitle(null);
        return itemList;
    }

    private ObservableList<Product> mergeMasterDataWithStock(Map<String, XSSFRow> masterData) throws IOException {
        updateTitle("2/3 Complex készlet betöltés");
        String username = Configuration.getInstance().getCredentialsProperty("username");
        String password = Configuration.getInstance().getCredentialsProperty("password");
        try (InputStream stockStream = new FileInputStream(stockFile);
             CloseableHttpClient client = HttpClients.createDefault()) {
            ObservableList<Product> content = FXCollections.observableArrayList();
            XSSFWorkbook wb = new XSSFWorkbook(stockStream);
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            updateTitle("3/3 Complex készlet-cikktörzs illesztés");

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                XSSFCell firstCell = row.getCell(0);
                String id = df.formatCellValue(firstCell);
                if (firstCell == null) {
                    break;
                }

                XSSFRow rowFromAllItems = masterData.get(id);
                if (rowFromAllItems != null) {
                    Product product = new Product();
                    Optional.ofNullable(rowFromAllItems.getCell(0)).ifPresent(cell -> product.setSku(df.formatCellValue(cell)));
                    product.setName(id);
                    Optional.ofNullable(rowFromAllItems.getCell(4)).ifPresent(cell -> product.setPrice(df.formatCellValue(cell)));
                    Optional.ofNullable(row.getCell(1)).ifPresent(cell -> {
                        MetaData metaData = new MetaData();
                        product.getMeta_data().add(metaData);
                        metaData.setKey("_brand");
                        metaData.setValue(df.formatCellValue(cell));
                    });
                    Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setStock_quantity(Integer.parseInt(df.formatCellValue(cell))));

                    content.add(product);

                    String host = Configuration.getInstance().getProperty("host");
                    String path = Configuration.getInstance().getProperty("productsPath");
                    HttpGet request = new HttpGet(host + path);
                    String auth = username + ":" + password;
                    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
                    String authHeader = "Basic " + new String(encodedAuth);
                    request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
                    HttpResponse resposne = client.execute(request);
                    HttpEntity entity = resposne.getEntity();
                    String responsePayload = EntityUtils.toString(entity);
                    ArrayNode responseArray = (ArrayNode) new ObjectMapper().readTree(responsePayload);
                }

                updateProgress(rowIndex, sheet.getLastRowNum());
            }

            updateTitle(null);
            return content;
        }
    }
}
