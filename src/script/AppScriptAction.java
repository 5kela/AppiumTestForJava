package script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;

import data.ServerInfo;
import data.TestData;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class AppScriptAction {

	private static ServerInfo serverInfo ;

	private static TestData testData;

	private static final String SPACE  =  "";

    private static final String JAVAPATH = "/java";

    private static final String SERVERNAME = "{server_name}";


    /**
	 *  コンストラクタ
	 */
    public AppScriptAction(ServerInfo serverInfo, TestData testData) {
    	AppScriptAction.serverInfo = serverInfo;
    	AppScriptAction.testData = testData;
    }

	/**
	 * Executive Messages
	 */
	public void doScriptActionApp(AndroidDriver<AndroidElement> driver) {

		try {
			String action = testData.getAction();
			String target = testData.getTarget();
			String value = testData.getValue();
			String context = testData.getContext();

			if (!SPACE.equals(context)) {
				driver.context(context);
			}

			switch(action) {
				case "close":
				{
					driver.closeApp();
					break;
				}
				case "input":
				{
					if (!SPACE.equals(target)) {
						String[] findCondition = target.split(",");
						String targetClassName = findCondition[0];
						String targetIndex = findCondition[1];
						
						List<AndroidElement> textFieldsList = driver.findElementsByClassName(targetClassName);
						textFieldsList.get(Integer.parseInt(targetIndex)).clear();
						textFieldsList.get(Integer.parseInt(targetIndex)).sendKeys(value);
					}
					break;
				}
				case "click":
				{
					if (!SPACE.equals(target)) {
						String[] findCondition = target.split(",");
						String targetClassName = findCondition[0];
						String targetIndex = findCondition[1];
						
						List<AndroidElement> buttonList = driver.findElementsByClassName(targetClassName);
						buttonList.get(Integer.parseInt(targetIndex)).click();
					}
					Thread.sleep(1000);
					break;
				}
				case "tap":
				{
					if(!SPACE.equals(target) && target.contains(","))
					{
						String[] tap_position = target.split(",");
						int tap_position_x = Integer.parseInt(tap_position[0]);
						int tap_position_y = Integer.parseInt(tap_position[1]);

						TouchAction tapAction = new TouchAction(driver);
						tapAction.tap(tap_position_x, tap_position_y).perform();
					}
					break;
				}
				case "press":
				{
					if(!SPACE.equals(target) && target.contains(","))
					{
						String[] tap_position = target.split(",");
						int press_position_x = Integer.parseInt(tap_position[0]);
						int press_position_y = Integer.parseInt(tap_position[1]);

						TouchAction pressAction = new TouchAction(driver);
						pressAction.press(press_position_x, press_position_y).perform();
					}
					break;
				}
				case "longpress":
				{
					if(!SPACE.equals(target) && target.contains(","))
					{
						String[] longpress_position = target.split(",");
						int longpress_position_x = Integer.parseInt(longpress_position[0]);
						int longpress_position_y = Integer.parseInt(longpress_position[1]);

						TouchAction pressAction = new TouchAction(driver);
						if(!SPACE.equals(value))
						{
							int longPressTime = Integer.parseInt(value);
							pressAction
							.longPress(longpress_position_x, longpress_position_y, longPressTime)
							.release().perform();
						}
						else
						{
							pressAction.longPress(longpress_position_x, longpress_position_y).perform();
						}
					}
					break;
				}
				case "wait":
				{
					TouchAction t = new TouchAction(driver);
					if(!SPACE.equals(value)) {
						t.waitAction(Integer.parseInt(value));
					} else {
					   	t.waitAction();
					}
					t.perform();
					break;
				}
				case "wait_launch":
				{
					//このメッソドは同期です、だから、指定した要素を検索することができる時、アプリケーションはもう起動しました
					WebElement webElement = driver.findElementById(target);
					while(!webElement.isDisplayed());
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
					if(!screenShotSaveFile.exists())
					{
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
					if(!pageSourceFile.exists())
					{
						pageSourceFile.createNewFile();
					}
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(pageSourceFile),"UTF-8");
					BufferedWriter bufferedWriter = new BufferedWriter(osw);
					bufferedWriter.write(pageSource);
					bufferedWriter.close();
					osw.close();
					break;
				}
				case "installApp":
				{
					//「value」はインストールソフトウェアのパス
					driver.installApp(target);
					break;
				}
				case "launch":
				{
					driver.launchApp();
					break;
				}
				case "resetApp":
				{
					driver.resetApp();
					break;
				}
				case "runAppInBackground":
				{
					//アプリケーションはバックグラウンドで指定した時間を実行される
					driver.runAppInBackground(Integer.parseInt(value));
					break;
				}
				case "removeApp":
				{
					//「context」は「bundleId」です、「bundleId」はペーケージ名
					driver.removeApp(target);
					break;
				}
				case "lock":
				{
					driver.lockDevice();
					break;
				}
				case "unlock":
				{
					driver.unlockDevice();
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
					}
					else
					{
						fos.write(driver.pullFile(value));
					}
					fos.close();
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
				case "slide":
				{
					// 原点1
					int original_x = 0;
					int original_y = 0;
					
					// 终点2
					int final_x = 0;
					int final_y = 0;
					
					if(!SPACE.equals(target))
					{
						String[] locationArry = target.split(",");
						original_x = Integer.parseInt(locationArry[0]);
						original_y = Integer.parseInt(locationArry[1]);
						final_x = Integer.parseInt(locationArry[2]);
						final_y = Integer.parseInt(locationArry[3]);
						TouchAction slideAction = new TouchAction(driver);
						slideAction.longPress(original_x, original_y);
						slideAction.moveTo(final_x, final_y);
						slideAction.release();
						slideAction.perform();
					}
					break;
				}
				case "wait_element_appeared":
				{
					if (!SPACE.equals(target)) {
						String[] findCondition = target.split(",");
						String targetClassName = findCondition[0];
						String targetIndex = findCondition[1];
						
						boolean isDisplayed = false;
						List<AndroidElement> elementList = driver.findElementsByClassName(targetClassName);
						WebElement webElement = elementList.get(Integer.parseInt(targetIndex));
						while (!isDisplayed)
						{
							TouchAction touchAction = new TouchAction(driver);
							touchAction.waitAction();
							isDisplayed = webElement.isDisplayed();
						}
					}
					break;
				}
				case "wait_element_disappeared":
				{
					if (!SPACE.equals(target)) {
						String[] findCondition = target.split(",");
						String targetClassName = findCondition[0];
						String targetIndex = findCondition[1];
						
						boolean isDisplayed = true;
						List<AndroidElement> elementList = driver.findElementsByClassName(targetClassName);
						WebElement webElement = elementList.get(Integer.parseInt(targetIndex));
						while (isDisplayed)
						{
							TouchAction touchAction = new TouchAction(driver);
							touchAction.waitAction();
							isDisplayed = webElement.isDisplayed();
						}
					}
					break;
				}
				case "zoom":
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
					
					if(action.equals("pinch"))
					{
						//zoom in the image
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
					}
					else if(action.equals("zoom"))
					{
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
					}
					multiTouch.add(action0).add(action1).perform();
					break;
				}
				default:
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    /**
	 *  コンストラクタ
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
