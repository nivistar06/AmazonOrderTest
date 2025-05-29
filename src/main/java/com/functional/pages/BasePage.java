package com.functional.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger logger;
    protected String defaultWindow;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerFactory.getLogger(getClass());
        logger.debug("Initialized {}", getClass().getSimpleName());
    }

    protected WebElement waitForElement(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.trace("Element found and visible: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Failed to find visible element: {}", locator, e);
            throw e;
        }
    }

    protected WebElement waitForElementClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.trace("Element is clickable: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Element not clickable: {}", locator, e);
            throw e;
        }
    }

    protected void click(By locator) {
        logger.debug("Clicking on element: {}", locator);
        try {
            WebElement element = waitForElementClickable(locator);
            element.click();
            logger.trace("Successfully clicked on element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to click on element: {}", locator, e);
            throw e;
        }
    }

    protected void sendKeys(By locator, String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.warn("Attempting to send empty or null text to element: {}", locator);
        }
        logger.debug("Sending text '{}' to element: {}", text, locator);
        try {
            WebElement element = waitForElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.trace("Successfully entered text in element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to send keys to element: {}", locator, e);
            throw e;
        }
    }

    protected String getText(By locator) {
        logger.debug("Getting text from element: {}", locator);
        try {
            String text = waitForElement(locator).getText().trim();
            logger.trace("Retrieved text '{}' from element: {}", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", locator, e);
            throw e;
        }
    }

    protected void switchToWindow(String title) {
        logger.debug("Switching to window with title: {}", title);
        try {
            defaultWindow = driver.getWindowHandle();
            driver.switchTo().window(title);
            logger.trace("Successfully switched to window with title: {}", title);
        } catch (Exception e) {
            logger.error("Failed to switch to window with title: {}", title, e);
            throw e;
        }
    }

    protected boolean isElementPresent(By locator) {
        logger.debug("Checking if element is present: {}", locator);
        try {
            waitForElement(locator);
            logger.trace("Element is present: {}", locator);
            return true;
        } catch (Exception e) {
            logger.trace("Element is not present: {}", locator);
            return false;
        }
    }
} 