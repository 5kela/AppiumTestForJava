package script;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;

import data.ServerInfo;
import data.TestData;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class WebScriptAction {

	private static ServerInfo serverInfo;

	private static TestData testData;

	private static final String SPACE = "";

	private static final String JAVAPATH = "/java";

	private static final String SERVERNAME = "{server_name}";

	/**
	 * コンストラクタ
	 */
	public WebScriptAction(ServerInfo serverInfo, TestData testData) {
		WebScriptAction.serverInfo = serverInfo;
		WebScriptAction.testData = testData;
	}

	/**
	 * Executive Messages
	 */
	public void doScriptActionWeb(AndroidDriver<AndroidElement> driver) {
		try {
			String action = testData.getAction();
			String target = testData.getTarget();
			String value = testData.getValue();
			String context = testData.getContext();

			if (!SPACE.equals(context)) {
				driver.context(context);
			}

			switch (action) {
			case "launch":
			{
				driver.get(target);
				break;
			}
			case "close":
			{
				driver.close();
				break;
			}
			case "input":
			{
				WebElement el = driver.findElementById(target);
				if(el.isDisplayed()) {
					el.clear();
					el.sendKeys(value);
				}
				break;
			}
			case "click":
			{
				WebElement el = driver.findElementById(target);
				if(el.isDisplayed()) {
					el.click();
				}
				break;
			}
			case "press":
			{
				TouchAction press = new TouchAction(driver);
				press.press(driver.findElementById(target));
				press.perform();
				break;
			}
			case "longpress":
			{
				WebElement el = driver.findElementById(target);
				if(el.isDisplayed()) {
					TouchAction action_longpress = new TouchAction(driver);
					action_longpress.longPress(el, Integer.parseInt(value));
					action_longpress.perform();
				}
				break;
			}
			case "wait":
			{
				TouchAction waitAction = new TouchAction(driver);
				if(!SPACE.equals(value)) {
					waitAction.waitAction(Integer.parseInt(value)).perform();
			    } else {
			    	waitAction.waitAction().perform();
			    }
				break;
			}
			case "wait_launch":
			{
				boolean isLaunched = false;
				while(!isLaunched) {
					WebElement el = driver.findElementById(target);
					if(el.isDisplayed()) {
						isLaunched = true;
					} else {
						Thread.sleep(6);
					}
				}
				break;
			}
			case "keyevent":
			{
				driver.pressKeyCode(Integer.parseInt(target));
				break;
			}
			case "screenshot":
			{
				File screenShotSaveFile = new File(getFilePath(value));
				if (!screenShotSaveFile.exists()) {
					screenShotSaveFile.createNewFile();
				}
				byte screenShot[] = driver.getScreenshotAs(OutputType.BYTES);
				FileOutputStream out = new FileOutputStream(screenShotSaveFile);
				out.write(screenShot);
				out.close();
				break;
			}
			case "dom":
			{
				String pageSource = driver.getPageSource();
				InputStream in = getStringStream(pageSource);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				String result = "";
				String domString = "";
				while ((result = reader.readLine()) != null) {
					
					//get DOM option
					if (result.contains(target)) {
						domString = domString + result.intern();
						System.out.println("dom : "+domString);
					}
				}
				
				//ファイルの出力
				File domFile = new File(getFilePath(value));
				if (!domFile.exists()) {
					domFile.createNewFile();
				}
				FileOutputStream outHtml = new FileOutputStream(domFile);
				outHtml.write(domString.getBytes());
				outHtml.close();
				break;
			}
			case "pageSource":
			{
				String pageSource = driver.getPageSource();
				File pageSourceFile = new File(getFilePath(value));
				if (!pageSourceFile.exists()) {
					pageSourceFile.createNewFile();
				}

				FileOutputStream outHtml = new FileOutputStream(pageSourceFile);
				outHtml.write(pageSource.getBytes());
				outHtml.close();
				break;
			}
			case "tap":
			{
				WebElement el = driver.findElementById(target);
				if(el.isDisplayed()) {
					TouchAction tapAction = new TouchAction(driver);
					tapAction.tap(el).perform();
				}
				break;
			}
			case "push":
			{
				//valueはロカールファイルのパス
				//targetはリモートファイルのパス
				File pushFile = new File(value);

				if(pushFile.isDirectory())
				{
					throw new Exception("「プッシュファイル」アクションのファイルは正しくない、「value」はファイルなければなりません");
				}
				if(!target.contains("."))
				{
					throw new Exception("「プッシュファイル」アクションの目標ファイルは正しくない、「target」はファイルなければなりません");
				}
				driver.pushFile(target, pushFile);
				break;
			}
			case "pull":
			{
				//valueはリモートファイルのパス
				//targetはロカールファイルのパス
				File pullFile = new File(target);
				FileOutputStream fos = new FileOutputStream(pullFile);

				if(!value.contains("."))
				{
					//「value」はフォルダたら、出力のファイルの型は「zip」,だから、「target」は「zip」ファイルのパスなければなりません
					fos.write(driver.pullFolder(value));
				} else {
					fos.write(driver.pullFile(value));
				}
				fos.close();
				break;
			}
			case "installApp":
			{
				//「target」はインストールソフトウェアのパス
				driver.installApp(target);
				break;
			}
			case "resetWeb":
			{
				driver.resetApp();
				break;
			}
			case "runWebInBackground":
			{
				//アプリケーションはバックグラウンドで指定した時間を実行される
				driver.runAppInBackground(Integer.parseInt(value));
				break;
			}
			case "removeApp":
			{
				//「target」は「bundleId」です、「bundleId」はペーケージ名
				driver.removeApp(target);
				break;
			}
			case "rotate":
			{
				if(value.compareToIgnoreCase("landscape") == 0)
				{
					driver.rotate(ScreenOrientation.LANDSCAPE);
				}
				else if(value.compareToIgnoreCase("portrait") == 0)
				{
					driver.rotate(ScreenOrientation.PORTRAIT);
				}
				break;
			}
			case "lock":
			{
				if(!driver.isLocked()) {
					driver.lockDevice();
				}
				break;
			}
			case "unlock":
			{
				if(driver.isLocked()) {
					driver.unlockDevice();
				}
				break;
			}
			case "wait_element_appeared":
			{
				boolean isDisplayed = false;
				WebElement el = driver.findElementById(target);
				while (!isDisplayed)
				{
					TouchAction touchAction = new TouchAction(driver);
					touchAction.waitAction();
					isDisplayed = el.isDisplayed();
				}
				break;
			}
			case "wait_element_disappeared":
			{
				boolean isDisplayed = false;
				WebElement el = driver.findElementById(target);
				while (isDisplayed)
				{
					TouchAction touchAction = new TouchAction(driver);
					touchAction.waitAction();
					isDisplayed = el.isDisplayed();
				}
				break;
			}
			case "slide":
			{
				Thread.sleep(10000);
				// 原点（original_x、original_y）
				int original_x = 0;
				int original_y = 0;
				
				// 移動量（deviation_x、deviation_y）
				int deviation_x = 0;
				int deviation_y = 0;
				if (!SPACE.equals(target)) {
					String[] locationArry = target.split(",");
					original_x = Integer.parseInt(locationArry[0]);
					original_y = Integer.parseInt(locationArry[1]);
					deviation_x = Integer.parseInt(locationArry[2]);
					deviation_y = Integer.parseInt(locationArry[3]);
					TouchAction slideAction = new TouchAction(driver);
					slideAction.longPress(original_x, original_y);
					slideAction.moveTo(deviation_x, deviation_y);
					slideAction.release();
					slideAction.perform();
				}
				break;
			}
			case "zoom":
			{
				MultiTouchAction multiTouch = new MultiTouchAction(driver);
				// 原点（original_x、original_y）
				int original_1_x = 0;
				int original_1_y = 0;

				// 原点（original_x、original_y）
				int original_2_x = 0;
				int original_2_y = 0;

				String[] locationArry = target.split(",");
				original_1_x = Integer.parseInt(locationArry[0]);
				original_1_y = Integer.parseInt(locationArry[1]);
				original_2_x = Integer.parseInt(locationArry[2]);
				original_2_y = Integer.parseInt(locationArry[3]);

				TouchAction action0 = null;
				TouchAction action1 = null;

				//zoom in the image
				action0 = new TouchAction(driver)
						.press(original_1_x, original_1_y)
						.waitAction(100)
						.moveTo((int)(original_1_x - original_2_x)/3 ,(int)(original_1_y - original_2_y)/3)
						.waitAction(100)
						.release();
				action1 = new TouchAction(driver)
						.press(original_2_x, original_2_y)
						.waitAction(200)
						.moveTo((int)(original_2_x - original_1_x)/3 ,(int)(original_2_y - original_1_y)/3)
						.waitAction(100)
						.release();
				multiTouch.add(action0).add(action1).perform();
				break;
			}
			case "pinch":
			{
				MultiTouchAction multiTouch = new MultiTouchAction(driver);
				// 原点（original_x、original_y）
				int original_1_x = 0;
				int original_1_y = 0;

				// 原点（original_x、original_y）
				int original_2_x = 0;
				int original_2_y = 0;

				String[] locationArry = target.split(",");
				original_1_x = Integer.parseInt(locationArry[0]);
				original_1_y = Integer.parseInt(locationArry[1]);
				original_2_x = Integer.parseInt(locationArry[2]);
				original_2_y = Integer.parseInt(locationArry[3]);

				TouchAction action0 = null;
				TouchAction action1 = null;

				//pinch in the image
				action0 = new TouchAction(driver)
						.press(original_1_x, original_1_y)
						.waitAction(100)
						.moveTo((int)(original_2_x - original_1_x)/3 ,(int)(original_2_y - original_1_y)/3)
						.waitAction(100)
						.release();
				action1 = new TouchAction(driver)
						.press(original_2_x, original_2_y)
						.waitAction(200)
						.moveTo((int)(original_1_x - original_2_x)/3 ,(int)(original_1_y - original_2_y)/3)
						.waitAction(100)
						.release();
				multiTouch.add(action0).add(action1).perform();
				break;
			}
			default:
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
	 *  パスの取得クラス
	 */
	private String getFilePath(String filePath) {

		String outFilePath = "";

		File classpathRoot = new File(System.getProperty("user.dir"));

		//get java path
		String javaRootPath = classpathRoot.getPath().toString();

		//斜線⇒反斜線
		javaRootPath = javaRootPath.replaceAll("\\\\", "/");

		//cut java folder
		javaRootPath = javaRootPath.replace(JAVAPATH, SPACE);

		//{server_name}⇒ServerName
		filePath = filePath.replace(SERVERNAME, serverInfo.getServerName());

		//cut './'
		filePath = filePath.substring(1);

		//edit output path
		outFilePath = javaRootPath + filePath;

		System.out.println(outFilePath);

		return outFilePath;
	}

    /**
	 *  InputStreamクラス
	 */
	private static InputStream getStringStream(String inString) {
		if(inString != null && !inString.trim().equals("")) {
			try{
				ByteArrayInputStream inStream = new ByteArrayInputStream(inString.getBytes());
				return inStream;
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}
}
