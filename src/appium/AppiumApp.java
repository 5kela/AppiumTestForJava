package appium;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;

import data.ServerInfo;
import data.TestData;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import script.AppScriptAction;


public class AppiumApp {

    private static AppiumApp instance;

    private static AndroidDriver<AndroidElement> driver;

    private static ServerInfo serverInfo;

    private static TestData testData;

    
	/**
	 *  コンストラクタ
	 */
    private AppiumApp() {

    }

	/**
	 *  実例を作成する
	 */
    public static AppiumApp getInstance() {
    	if (null == instance) {
            synchronized (AppiumApp.class) {
            	if (instance == null) {
            		instance = new AppiumApp();
            	}
            }
        }
        return instance;
    }

	/**
	 * @return the serverInfo
	 */
	public static ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public static void setServerInfo(ServerInfo serverInfo) {
		AppiumApp.serverInfo = serverInfo;
	}

	/**
	 * @return the testData
	 */
	public static TestData getTestData() {
		return testData;
	}

	/**
	 * @param testData the testData to set
	 */
	public static void setTestData(TestData testData) {
		AppiumApp.testData = testData;
	}

	/**
	 * Establish Connection
	 */
	public void connect() throws Exception {

		DesiredCapabilities capabilities = new DesiredCapabilities();
		
		capabilities.setCapability("platformName", serverInfo.getDesiredCapabilities().get("platformName"));
		capabilities.setCapability("deviceName", serverInfo.getDesiredCapabilities().get("deviceName"));
		capabilities.setCapability("udid", serverInfo.getDesiredCapabilities().get("deviceName"));
		capabilities.setCapability("appPackage", serverInfo.getDesiredCapabilities().get("appPackage"));
		capabilities.setCapability("app", serverInfo.getDesiredCapabilities().get("app"));
		capabilities.setCapability("appActivity", serverInfo.getDesiredCapabilities().get("appActivity"));
		capabilities.setCapability("newCommandTimeout", serverInfo.getDesiredCapabilities().get("newCommandTimeout"));

		capabilities.setCapability("sessionOverride", true);
		capabilities.setCapability("unicodeKeyboard", true);
		capabilities.setCapability("resetKeyboard", false);
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("noSign", true);

		URL url = new URL("http://"+serverInfo.getHost()+":"+serverInfo.getPort()+"/wd/hub");

		//AndroidDriver
		driver = new AndroidDriver<AndroidElement>(url, capabilities);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	/**
	 * Executive Messages
	 */
	public void message() throws InterruptedException {
		AppScriptAction sAtion = new AppScriptAction(serverInfo, testData);
		sAtion.doScriptActionApp(driver);
	}

	/**
	 * Connection Disconnect
	 */
	public void disconnect() throws Exception {
		
		// Quits this driver, closing every associated window that was open.
		try{
			driver.quit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}