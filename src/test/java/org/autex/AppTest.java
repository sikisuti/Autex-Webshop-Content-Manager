package org.autex;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);

    private App app;

    @Before
    public void setUp() {
        app = new App();
    }
}
