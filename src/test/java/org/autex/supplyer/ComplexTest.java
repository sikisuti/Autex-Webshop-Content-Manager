package org.autex.supplyer;

import org.autex.TestParent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class ComplexTest extends TestParent {
    Complex complex;

    @Before
    public void setUp() {
        complex = new Complex();
    }

    @Test
    public void testConvert() throws Exception {
        File allItemsSource = loadFile("Complex/Cikktörzs KBS.xlsx");
        File inventorySource = loadFile("Complex/KBSklt0508.xlsx");
        try (FileInputStream fsAllItems = new FileInputStream(allItemsSource);
            FileInputStream fsInventory = new FileInputStream(inventorySource)) {
            complex.convert(fsAllItems, fsInventory);
            String actual = complex.getCSV().toString();
            Assert.assertEquals(loadFileString("Complex/expected/complex-expected.csv"), actual);
        }
    }
}
