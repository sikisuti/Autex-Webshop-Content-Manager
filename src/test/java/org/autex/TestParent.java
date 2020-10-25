package org.autex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class TestParent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestParent.class);

    protected File loadFile(String path) throws URISyntaxException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(path);
        assert url != null;
        return new File(url.toURI());
    }
}
