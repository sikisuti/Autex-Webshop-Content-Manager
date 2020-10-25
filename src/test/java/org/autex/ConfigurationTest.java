package org.autex;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest extends TestParent {
    @Test
    public void testConfiguration() {
        String defaultPath = Configuration.getInstance().getProperty("defaultPath");
        LOGGER.info(defaultPath);
        Assert.assertNotNull(defaultPath);
    }
}
