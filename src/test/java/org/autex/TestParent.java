package org.autex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestParent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestParent.class);

    protected File loadFile(String path) throws URISyntaxException {
        return new File(getUri(path));
    }

    protected String loadFileString(String path) throws URISyntaxException, IOException {
        return Files.readString(Path.of(getUri(path)));
    }

    private URI getUri(String path) throws URISyntaxException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(path);
        assert url != null;
        return url.toURI();
    }
}
