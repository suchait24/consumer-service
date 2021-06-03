package com.infogain.gcp.poc.consumer.app;


import org.junit.Test;
import org.springframework.core.SpringVersion;

import static org.junit.Assert.assertEquals;

public class AppTests {

    @Test
    public void anyTest() {
        assertEquals("5.1.10.RELEASE", SpringVersion.getVersion());
    }
}
