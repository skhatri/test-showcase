package com.example.selenium;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeleniumGoogleTest {
    @Test
    @Tag("network")
    void shouldLoadGoogleTitle() {
        WebDriver driver = new HtmlUnitDriver(false);
        try {
            driver.get("https://www.google.com.au/");
            String title = driver.getTitle() == null ? "" : driver.getTitle();
            assertTrue(title.toLowerCase().contains("google"));
        } finally {
            driver.quit();
        }
    }
}

