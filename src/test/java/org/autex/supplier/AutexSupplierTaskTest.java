package org.autex.supplier;

import org.autex.TestParent;
import org.autex.supplier.AutexSupplierTask;
import org.junit.Before;

public class AutexSupplierTaskTest extends TestParent {
    AutexSupplierTask autexSupplierTask;

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
