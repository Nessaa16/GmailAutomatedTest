package com.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class automationTest {
    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=ProtocolHandler");

        System.setProperty("webdriver.chrome.driver", "C:\\WebDrivers\\chromedriver.exe");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            driver.get("https://mail.google.com/");

            //enter email
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("identifierId")));
            emailField.sendKeys("Testonly1616@gmail.com");

            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
            nextButton.click();

            //wait for password field to show up
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Passwd")));
            passwordField.sendKeys("test_123");

            //enter password
            WebElement passwordNext = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
            passwordNext.click();

            System.out.println("If OTP is required, enter it manually. Press Enter to continue...");
            new Scanner(System.in).nextLine();

            wait.until(ExpectedConditions.titleContains("Inbox"));
            System.out.println("Login successful!");

            Thread.sleep(4000);

            //dismiss pop up
            try {
                WebElement shadowHost = driver.findElement(By.cssSelector("div[role='dialog']"));
                JavascriptExecutor js = (JavascriptExecutor) driver;
                WebElement shadowRoot = (WebElement) js.executeScript("return arguments[0].shadowRoot", shadowHost);
                WebElement dismissLink = (WebElement) js.executeScript(
                        "return arguments[0].querySelector('a[href*=guest]')", shadowRoot);
                dismissLink.click();
                System.out.println("Popup dismissed");
            } catch (Exception e) {
                System.out.println("Popup not found" + e.getMessage());
            }

            Thread.sleep(3000); // Allow emails to load
            String subject = findAndDeleteLatestUnreadEmail(driver, wait);
            if (subject != null) {
                System.out.println("Deleted email with subject: " + subject);
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static String findAndDeleteLatestUnreadEmail(WebDriver driver, WebDriverWait wait) {
        try {
            List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zA.zE"));
            if (unreadEmails.isEmpty()) {
                System.out.println("No unread emails found.");
                return null;
            }

            WebElement latestUnread = unreadEmails.get(0);
            String subject;

            try {
                WebElement subjectElement = latestUnread.findElement(By.cssSelector("span[data-thread-id] span"));
                subject = subjectElement.getText().trim();
            } catch (Exception e) {
                subject = "Unknown Subject";
            }

            System.out.println("Found unread email: " + subject);

            try {
                WebElement checkbox = latestUnread.findElement(By.cssSelector("div[role='checkbox']"));
                checkbox.click();
                Thread.sleep(1000);

                WebElement deleteButton = driver.findElement(By.xpath("//div[@aria-label='Delete' or @data-tooltip='Delete']"));
                deleteButton.click();
                System.out.println("Email deleted!");
            } catch (Exception e) {
                System.out.println("Failed to delete from list view. Trying from inside the email...");
                latestUnread.click();
                Thread.sleep(2000);

                WebElement deleteInside = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@aria-label='Delete' or @data-tooltip='Delete']")));
                deleteInside.click();
                Thread.sleep(2000);
                System.out.println("Deleted from opened view.");
            }

            return subject;

        } catch (Exception e) {
            System.out.println("Error deleting email: " + e.getMessage());
            return null;
        }
    }
}
