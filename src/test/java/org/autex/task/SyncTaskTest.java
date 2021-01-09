package org.autex.task;

import org.autex.TestParent;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class SyncTaskTest extends TestParent {
    @Test
    public void testParseProduct() throws Exception {
        File mockedResponse = loadFile("Complex/mockedResponses/KBS-mocked-response.json");
        try (InputStream responseStream = new FileInputStream(mockedResponse)) {
            new SyncTask(null, null, null, null, null).parseResponse(responseStream, new ArrayList<>());
        }
    }
}
