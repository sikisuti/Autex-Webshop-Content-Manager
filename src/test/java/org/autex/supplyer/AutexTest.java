package org.autex.supplyer;

import org.autex.TestParent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class AutexTest extends TestParent {
    Autex autex;

    @Before
    public void setUp() {
        autex = new Autex();
    }

    @Test
    public void testConvert() throws Exception {
        File source = loadFile("Autex/Autex-export.xls");
        try (FileInputStream fis = new FileInputStream(source)) {
            String acutal = autex.convert(fis).toString();
            Assert.assertEquals(loadFileString("Autex/expected/autex-expected.csv"), acutal);
        }
    }
}
