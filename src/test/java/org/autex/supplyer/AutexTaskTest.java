package org.autex.supplyer;

import org.autex.TestParent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class AutexTaskTest extends TestParent {
    AutexTask autexTask;

    @Before
    public void setUp() {
//        autexTask = new AutexTask();
    }

    /*@Test
    public void testConvert() throws Exception {
        File source = loadFile("Autex/Autex-export.xls");
        try (FileInputStream fis = new FileInputStream(source)) {
            autexTask.convert(fis);
            String acutal = autexTask.getCSV().toString();
            Assert.assertEquals(loadFileString("Autex/expected/autex-expected.csv"), acutal);
        }
    }*/
}
