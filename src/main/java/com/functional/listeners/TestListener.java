package com.functional.listeners;

import com.functional.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private WebDriver getDriver(ITestResult result) {
        try {
            // Try to get the driver from the test class instance
            return (WebDriver) result.getTestClass()
                    .getRealClass()
                    .getSuperclass()
                    .getDeclaredField("driver")
                    .get(result.getInstance());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = getDriver(result);
        if (driver != null) {
            TestUtils.takeScreenshot(driver);
        }
    }

    // Other ITestListener methods with empty implementations
    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestSuccess(ITestResult result) {}
    @Override public void onTestSkipped(ITestResult result) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}
}
