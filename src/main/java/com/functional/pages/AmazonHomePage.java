package com.functional.pages;

import com.functional.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AmazonHomePage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(AmazonHomePage.class);
    // Locators using relative XPath
    private final By searchBox = By.xpath("//input[@type='text' and contains(@placeholder, 'Search')]");
    private final By searchButton = By.xpath("//input[@type='submit' and @value='Go']");
    private final By searchResults = By.xpath("//div[contains(@class, 's-result-item') and @data-component-type='s-search-result']");

    // Updated login locators
    private final By signInButton = By.xpath("//a[contains(@data-nav-role, 'signin') or contains(@href, 'signin')]");
    private final By emailInput = By.xpath("//input[@type='email' or @id='ap_email']");
    private final By continueButton = By.xpath("//input[@type='submit' and (contains(@id,'continue') or contains(@aria-labelledby,'continue') or contains(@class,'continue'))]");
    private final By passwordInput = By.xpath("//input[@type='password' or @id='ap_password']");
    private final By signInSubmitButton = By.xpath("//input[@type='submit' and (contains(@id,'signInSubmit') or contains(@class,'signin'))]");
    private final By pageLoadIndicator = By.xpath("//div[@id='glow-toaster-body']");
    private final By errorMessage = By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error')]");

    public AmazonHomePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToHomePage() {
        driver.get(ConfigManager.getInstance().getBaseUrl());
        // Wait for any page load indicators to disappear
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoadIndicator));
        } catch (Exception e) {
            // Ignore if indicator is not present
        }
    }

    public void searchProduct(String productName) {
        WebElement searchBoxElement = waitForElement(searchBox);
        searchBoxElement.clear();
        searchBoxElement.sendKeys(productName);
        click(searchButton);

        // Wait for search results to appear within 10 seconds
        try {
            long startTime = System.currentTimeMillis();
            long maxWaitTime = 10000; // 10 seconds in milliseconds
            boolean resultsFound = false;

            while ((System.currentTimeMillis() - startTime) < maxWaitTime) {
                try {
                    List<WebElement> results = driver.findElements(searchResults);
                    if (!results.isEmpty() && results.get(0).isDisplayed()) {
                        resultsFound = true;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore and continue waiting
                }
                Thread.sleep(500); // Check every 500ms
            }

            if (!resultsFound) {
                throw new RuntimeException("Search results did not appear within 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Search was interrupted", e);
        }
    }

    public int getSearchResultsCount() {
        List<WebElement> results = driver.findElements(searchResults);
        return results.size();
    }

    public void login(String email, String password) {
        try {
            // Click sign in button
            WebElement signInBtn = waitForElement(signInButton);
            logger.debug("Sign in button found: {}", signInBtn != null);
            signInBtn.click();

            // Wait for email input and enter email
            WebElement emailElem = waitForElement(emailInput);
            logger.debug("Email input found: {}", emailElem != null);
            emailElem.clear();
            emailElem.sendKeys(email);

            // Click continue button
            WebElement continueBtn = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(continueButton));
            logger.debug("Continue button found: {}", continueBtn != null);
            continueBtn.click();

            // Check for any error messages
            try {
                WebElement error = driver.findElement(errorMessage);
                if (error.isDisplayed()) {
                    String errorMsg = "Login error: " + error.getText();
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            } catch (Exception e) {
                // No error message found, continue
                logger.debug("No error message found, continuing with login");
            }

            // Wait for password input and enter password
            WebElement passwordElem = waitForElement(passwordInput);
            logger.debug("Password input found: {}", passwordElem != null);
            passwordElem.clear();
            passwordElem.sendKeys(password);

            // Click sign in submit button
            WebElement signInSubmitBtn = waitForElement(signInSubmitButton);
            logger.debug("Sign in submit button found: {}", signInSubmitBtn != null);
            signInSubmitBtn.click();

            // Wait for login to complete
            wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
            logger.debug("Login successful - search box is visible");

        } catch (Exception e) {
            String errorMsg = "Login failed: " + e.getMessage();
            logger.error(errorMsg, e);
            com.functional.utils.TestUtils.takeScreenshot(driver);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public boolean isHomePageLoaded() {
        try {
            return waitForElement(searchBox).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
} 