package com.PostTracking.PostTracking;

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

import junit.framework.Assert;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
public class SeachPackageByCustomerTest {
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
	public void untitled() {
	 driver.get("http://127.0.0.1:8080/packages");
	 driver.manage().window().setSize(new Dimension(1296, 1000));
	 
	 driver.findElement(By.id("selCustomerSearch")).click();
	 {
	   WebElement dropdown = driver.findElement(By.id("selCustomerSearch"));
	   dropdown.findElement(By.xpath("//option[. = 'Jon Snow']")).click();
	 }
	 {
	   WebElement dropdown = driver.findElement(By.id("selCustomerSearch"));
	   dropdown.findElement(By.xpath("//option[. = 'Jon Snow']")).click();
	 }
	 {
	   WebElement dropdown = driver.findElement(By.id("selCustomerSearch"));
	   dropdown.findElement(By.xpath("//option[. = 'Jon Snow']")).click();
	 }
	 driver.findElement(By.id("selCustomerSearch")).click();
	 driver.findElement(By.id("btnSearch")).click();
	 assertTrue(driver.getPageSource().contains("Jon"));	 
	}
}

