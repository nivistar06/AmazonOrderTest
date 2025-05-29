package com.functional.tests;

import com.functional.config.ConfigManager;
import com.functional.utils.TestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Feature("Sample Tests")
public class SampleTest {
    private WebDriver driver;
    private ConfigManager config;

    @BeforeMethod
    public void setUp() {
        config = ConfigManager.getInstance();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @Story("Basic Navigation")
    @Description("Verify that the application loads successfully")
    public void testBasicNavigation() {
        driver.get(config.getBaseUrl());
        TestUtils.logInfo("Navigated to: " + config.getBaseUrl());
        TestUtils.takeScreenshot(driver);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 