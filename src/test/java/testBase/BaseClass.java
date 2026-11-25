package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import org.apache.logging.log4j.LogManager;//log4j
import org.apache.logging.log4j.Logger;   //log4j


public class BaseClass {

	public static WebDriver driver;
	public Logger logger;
	public Properties p;
	
	
	@BeforeClass(groups= {"Sanity","Regression","Master","DataDriven"})
	@Parameters({"os", "browser"})
	public void setup(String os, String br) throws IOException
	
	{
		//loading properties file
		 FileReader file=new FileReader(".//src//test//resources//config.properties");
		 p=new Properties();
		 p.load(file);
		
		
		//loading log4j 
		logger=LogManager.getLogger(this.getClass());//Log4j

		if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
		{
			DesiredCapabilities cap = new DesiredCapabilities();

			//os
			switch (os.toLowerCase())
			{
				case "windows" : cap.setPlatform(Platform.WIN11); break;
				case "mac" : cap.setPlatform(Platform.MAC); break;
                default: System.out.println("No matching os."); return;
			}

			//browser
			switch (br.toLowerCase())
			{
				case "chrome" : cap.setBrowserName("chrome"); break;
				case "edge" : cap.setBrowserName("MicrosoftEdge"); break;
				case "firefox" : cap.setBrowserName("Firefox"); break;
                default: System.out.println("No matching browser."); return;
			}

			driver = new RemoteWebDriver(new URL("http://172.16.0.129:4444/wd/hub"), cap);
		}

		if(p.getProperty("execution_env").equalsIgnoreCase("local"))
		{
			//launching browser based on condition
			switch(br.toLowerCase())
			{
				case "chrome": driver=new ChromeDriver(); break;
				case "edge": driver=new EdgeDriver(); break;
				case "firefox": driver=new FirefoxDriver(); break;
				default: System.out.println("No matching browser..");
					return;
			}
		}

		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		
		
		driver.get(p.getProperty("appURL"));
		driver.manage().window().maximize();
	}
	
	@AfterClass(groups= {"Sanity","Regression","Master","DataDriven"})
	public void tearDown()
	{
		if (driver != null) {
			driver.quit();
		}
	}
	

	public String randomString()
	{
		String generatedString=RandomStringUtils.randomAlphabetic(5);
		return generatedString;
	}
	
	public String randomNumber()
	{
		String generatedString=RandomStringUtils.randomNumeric(10);
		return generatedString;
	}
	
	public String randomAlphaNumeric()
	{
		String str=RandomStringUtils.randomAlphabetic(3);
		String num=RandomStringUtils.randomNumeric(3);
		
		return (str+"@"+num);
	}
	
	public String captureScreen(String tname) throws IOException {

		if (driver == null) {
			System.out.println("Driver is null - screenshot skipped for: " + tname);
			return null;
		}

		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
				
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
		
		String targetFilePath=System.getProperty("user.dir")+"\\screenshots\\" + tname + "_" + timeStamp + ".png";
		File targetFile=new File(targetFilePath);
		
		sourceFile.renameTo(targetFile);
			
		return targetFilePath;

	}
	
	
	
}
