package org.autex.supplyer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.autex.TestParent;
import org.autex.model.Product;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class SupplyerTaskTest extends TestParent {
    @Test
    public void testParseProduct() throws Exception {
        File mockedResponse = loadFile("Complex/mockedResponses/KBS-mocked-response.json");
        try (InputStream responseStream = new FileInputStream(mockedResponse)) {
            new SyncProductTask(null, null, null, null, null).parseResponse(responseStream, new ArrayList<>());
        }
    }
}
