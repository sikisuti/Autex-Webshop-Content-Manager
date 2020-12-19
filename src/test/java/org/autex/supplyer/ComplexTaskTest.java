package org.autex.supplyer;

import org.autex.TestParent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class ComplexTaskTest extends TestParent {
    ComplexTask complexTask;

    @Before
    public void setUp() {
//        complexTask = new ComplexTask();
    }

    /*@Test
    public void testConvert() throws Exception {
        File allItemsSource = loadFile("Complex/Cikktörzs KBS.xlsx");
        File inventorySource = loadFile("Complex/KBSklt0508.xlsx");
        try (FileInputStream fsAllItems = new FileInputStream(allItemsSource);
            FileInputStream fsInventory = new FileInputStream(inventorySource)) {
            complexTask.convert(fsAllItems, fsInventory);
            String actual = complexTask.getCSV().toString();
            Assert.assertEquals(loadFileString("Complex/expected/complex-expected.csv"), actual);
        }
    }*/
}
