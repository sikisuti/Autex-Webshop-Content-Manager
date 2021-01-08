package org.autex;

import org.autex.util.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest extends TestParent {
    @Test
    public void testConfiguration() {
        String defaultPath = Configuration.getStringProperty("defaultPath");
        LOGGER.info(defaultPath);
        Assert.assertNotNull(defaultPath);
    }
}
