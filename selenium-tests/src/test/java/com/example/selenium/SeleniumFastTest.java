package com.example.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeleniumFastTest {
    @Test
    void shouldOpenDataUrlAndReadTitle() {
        WebDriver driver = new HtmlUnitDriver();
        try {
            driver.get("data:text/html,<html><head><title>local</title></head><body>ok</body></html>");
            assertEquals("local", driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}

