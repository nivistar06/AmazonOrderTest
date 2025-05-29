package com.functional.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebElement;

import java.util.List;

import java.time.Duration;

public class AmazonCheckoutPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(AmazonCheckoutPage.class);
    // Locators using relative XPath
    private final By fullNameInput = By.id("address-ui-widgets-enterAddressFullName");
    private final By phoneNumberInput = By.id("address-ui-widgets-enterAddressPhoneNumber");
    private final By pincodeInput = By.id("address-ui-widgets-enterAddressPostalCode");
    private final By addressLine1Input = By.id("address-ui-widgets-enterAddressLine1");
    private final By addressLine2Input = By.id("address-ui-widgets-enterAddressLine2");
    private final By cityInput = By.id("address-ui-widgets-enterAddressCity");
    private final By stateInput = By.id("address-ui-widgets-enterAddressStateOrRegion");

    private final By codPaymentMethod = By.xpath("//span[contains(text(), 'Cash on Delivery')]/ancestor::label/input[@type='radio']");
    private final By placeOrderButton = By.xpath("//input[@name='placeYourOrder1']");
    private final By orderNumber = By.xpath("//div[contains(@class, 'order-number')]//span");
    private final By deliveryDate = By.xpath("//div[contains(@class, 'delivery-date')]//span");
    private final By cancelOrderButton = By.xpath("//a[contains(text(), 'Cancel')]");
    private final By confirmCancelButton = By.xpath("//input[@value='Cancel Order']");

    public AmazonCheckoutPage(WebDriver driver) {
        super(driver);
        logger.debug("Initialized AmazonCheckoutPage");
    }

    public void fillShippingAddress(String fullName, String addressLine1, String addressLine2,
                                    String city, String state, String pincode, String phoneNumber) {
        logger.info("Filling shipping address form");
        List<WebElement> deliverySection = driver.findElements(By.id("deliver-to-customer-text"));

        if (!deliverySection.isEmpty()) {
            logger.info("Delivery section is present: " + deliverySection.get(0).getText());
        } else {

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1500000));
            WebElement addAddressBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("add-new-address-desktop-sasp-tango-link")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addAddressBtn);
            addAddressBtn.click();

            try {
                logger.debug("Entering full name: {}", maskSensitiveData(fullName));
                WebElement fullNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        fullNameInput
                ));
                fullNameField.clear();
                fullNameField.sendKeys(fullName);

                logger.debug("Entering phone number : {}", maskSensitiveData(phoneNumber));
                WebElement PhoneNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        phoneNumberInput
                ));
                PhoneNumberField.clear();
                PhoneNumberField.sendKeys(phoneNumber);

                logger.debug("Entering pincode : {}", maskSensitiveData(pincode));
                WebElement PincodeField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        pincodeInput
                ));
                PincodeField.clear();
                PincodeField.sendKeys(pincode);


                logger.debug("Entering address line 1: {}", maskSensitiveData(addressLine1));
                WebElement addressLine1Field = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        addressLine1Input
                ));
                addressLine1Field.clear();
                addressLine1Field.sendKeys(addressLine1);

                logger.debug("Entering address line 2: {}", maskSensitiveData(addressLine2));
                WebElement addressLine2Field = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        addressLine2Input
                ));
                addressLine2Field.clear();
                addressLine2Field.sendKeys(addressLine2);


                logger.debug("Entering city: {}", maskSensitiveData(city));
                WebElement cityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        cityInput
                ));
                cityField.clear();
                cityField.sendKeys(city);

                WebElement checkbox = driver.findElement(By.id("address-ui-widgets-use-as-my-default"));
                if (!checkbox.isSelected()) {
                    checkbox.click();
                }

                WebElement useThisAddressButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[@id='checkout-primary-continue-button-id']")));

                useThisAddressButton.click();

                logger.info("Successfully filled shipping address form");

            } catch (Exception e) {
                String errorMsg = "Failed to fill shipping address: " + e.getMessage();
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "[MASKED]";
        }
        return data.substring(0, 2) + "****" + data.substring(data.length() - 2);
    }

    public void selectCODPayment() {
        logger.info("Selecting Cash on Delivery payment method");
        try {
            click(codPaymentMethod);
            logger.info("Successfully selected Cash on Delivery");
        } catch (Exception e) {
            String errorMsg = "Failed to select COD payment: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public void placeOrder() {
        logger.info("Placing order");
        // Next step button click
//        WebElement nextStepButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//a[@id='prime-panel-fallback-button' and contains(text(), 'Next step')]")
//        ));

//        nextStepButton.click();

        // place order click

        // Click the "Use this payment method" button
        WebElement paymentButton = driver.findElement(By.cssSelector("input[data-testid='secondary-continue-button']"));
        paymentButton.click();


        WebElement placeOrderSpan;
        try {
            placeOrderSpan = wait.until(ExpectedConditions.elementToBeClickable(By.id("bottomSubmitOrderButtonId")));
            System.out.println("Place Order button (bottomSubmitOrderButtonId) is clickable.");
        } catch (Exception e) {
            // Fallback to the input element if the span is not found
            placeOrderSpan = wait.until(ExpectedConditions.elementToBeClickable(By.id("placeOrder")));
            System.out.println("Fallback to Place Order input (placeOrder) is clickable.");
        }
        placeOrderSpan.click();
    }

    public String getOrderNumber() {
        logger.debug("Retrieving order number");
        // review or edit recent order
        String orderId;
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement reviewOrdersLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.linkText("Review or edit your recent orders")));
            reviewOrdersLink.click();

            // Locate the span containing the order number
            WebElement orderIdElement = driver.findElement(By.cssSelector("div.yohtmlc-order-id > span.a-color-secondary[dir='ltr']"));
            orderId = orderIdElement.getText();
            logger.info("order number {}", orderId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to click 'Review or edit your recent orders':", e);
        }
        return orderId;
    }


    public void cancelOrder() {
        logger.info("Starting order cancellation process");
        // cancel order
        WebElement cancelItemsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@id='a-autoid-1-announce' and contains(text(), 'Cancel items')]")
        ));
        cancelItemsLink.click();


        WebElement reasonDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("cancel.reason")
        ));

        // Select the "Order Created by Mistake" option
        Select cancelReason = new Select(reasonDropdown);
        cancelReason.selectByValue("mistake");

        WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("cancelButton")
        ));
        cancelButton.click();
    }
} 