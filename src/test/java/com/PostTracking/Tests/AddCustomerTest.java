package com.PostTracking.Tests;

//Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
public class AddCustomerTest {
private WebDriver driver;
private Map<String, Object> vars;
JavascriptExecutor js;
@Before
public void setUp() {
	System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\Support\\chromedriver.exe");
	driver = new ChromeDriver();
 js = (JavascriptExecutor) driver;
 vars = new HashMap<String, Object>();
}
@After
public void tearDown() {
 driver.quit();
}
@Test
public void postalTrackingAddCustomer() {
 driver.get("http://localhost:8080/customers");
 driver.manage().window().setSize(new Dimension(1296, 1000));
 driver.findElement(By.cssSelector(".btn-secondary:nth-child(1)")).click();
 driver.findElement(By.id("txtFirstName")).click();
 driver.findElement(By.id("txtFirstName")).sendKeys("Kyle");
 driver.findElement(By.id("txtLastName")).sendKeys("Smith");
 driver.findElement(By.id("txtPhoneNumber")).sendKeys("236-123-4567");
 driver.findElement(By.id("txtEmailAddress")).click();
 driver.findElement(By.id("txtEmailAddress")).sendKeys("kyle.smith@example.ca");
 driver.findElement(By.id("txtAddress")).sendKeys("520 Tenth Street");
 driver.findElement(By.id("txtCity")).click();
 driver.findElement(By.id("txtCity")).sendKeys("Winterfell");
 driver.findElement(By.cssSelector(".form-group:nth-child(7)")).click();
 driver.findElement(By.id("txtProvince")).click();
 driver.findElement(By.id("txtProvince")).sendKeys("Westeros");
 driver.findElement(By.id("txtZipCode")).click();
 driver.findElement(By.id("txtZipCode")).sendKeys("S8F-2G6");
 driver.findElement(By.cssSelector(".btn:nth-child(9)")).click();
}
}
