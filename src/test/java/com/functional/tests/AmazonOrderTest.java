package com.functional.tests;

import com.functional.config.ConfigManager;
import com.functional.pages.AmazonHomePage;
import com.functional.pages.AmazonProductPage;
import com.functional.pages.AmazonCheckoutPage;
import com.functional.utils.TestUtils;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.manager.SeleniumManager;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

@Feature("Amazon Order Workflow")
public class AmazonOrderTest {
    private WebDriver driver;
    private AmazonHomePage homePage;
    private AmazonProductPage productPage;
    private AmazonCheckoutPage checkoutPage;
    private static String EMAIL = "";
    private static String PASSWORD = "";
    private static String fullName = "";
    private static String addressLine1 = "";
    private static String addressLine2 = "";
    private static String city = "";
    private static String state = "";
    private static String postalCode = "";
    private static String phoneNumber = "";
    private static String searchTerm = "";


    @BeforeMethod
    public void setUp() {
        ConfigManager config = ConfigManager.getInstance();
        EMAIL = config.getLoginEmail();
        PASSWORD = config.getLoginPassword();
        fullName = config.getfullName();
        addressLine1 = config.getAddressLine1();
        addressLine2 = config.getAddressLine2();
        city = config.getCity();
        state = config.getState();
        postalCode = config.getPostalCode();
        phoneNumber = config.getPhoneNumber();
        searchTerm = config.getProductSearchTerm();


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        // Selenium Manager will automatically handle the driver binary
        driver = new ChromeDriver(options);

        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        // Initialize page objects
        homePage = new AmazonHomePage(driver);
        productPage = new AmazonProductPage(driver);
        checkoutPage = new AmazonCheckoutPage(driver);
    }

    @Test
    @Story("Complete Order Workflow")
    @Description("Test the complete order workflow from search to order placement and cancellation")
    public void testCompleteOrderWorkflow() {
        try {
            // Step 1: Navigate to Amazon.in
            homePage.navigateToHomePage();
            Assert.assertTrue(homePage.isHomePageLoaded(), "Homepage should load within 3 seconds");

            // Step 2: Login
            homePage.login(EMAIL, PASSWORD);

            // Step 3: Search for product
            homePage.searchProduct(searchTerm);

            // Step 4: Apply filters
            productPage.applyBrandFilter();
            productPage.applyPriceRangeFilter("2,500");

            // Step 5: Select and add to cart
            productPage.selectFirstProduct();
            productPage.FirstProductAddToCart();

            // Step 6: Proceed to checkout
            productPage.proceedToCheckout();

            // Step 7: Fill shipping address
            checkoutPage.fillShippingAddress(
                    fullName,
                    addressLine1,
                    addressLine2,
                    city,
                    state,
                    postalCode,
                    phoneNumber
            );

            // Step 8: Select payment method and place order
            checkoutPage.selectCODPayment();
            checkoutPage.placeOrder();

            // Step 9: Verify order details
            String orderNumber = checkoutPage.getOrderNumber();
            Assert.assertNotNull(orderNumber, "Order number should be present");
            // Step 10: Cancel order
            checkoutPage.cancelOrder();
        } catch (Exception e) {
            TestUtils.takeScreenshot(driver);
            throw e;
        }
    }
} 