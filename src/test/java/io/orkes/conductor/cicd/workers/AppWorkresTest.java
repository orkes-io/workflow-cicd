package io.orkes.conductor.cicd.workers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AppWorkresTest {

    @Test
    void testGreetings() {
        AppWorkres workers = new AppWorkres();
        String name = "Orkes";
        String response = workers.greetings(name);
        assertNotNull(response);
        assertEquals("Hello, " + name, response);
    }
}