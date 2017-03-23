package config;

import java.util.LinkedHashMap;
import java.util.Map;

import appium.AppiumApp;
import appium.AppiumWeb;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import data.ServerInfo;
import data.TestData;

public class Config {

	private static Config instance = null;

	private static final String CONNECT = "connect";

	private static final String DISCONNECT = "disconnect";

	private static final String MESSAGE = "message";

	private static final String STATUS = "status";

	private static final ServerInfo serverInfo = new ServerInfo();

	private static final TestData testData = new TestData();

	private static final String HOST = "host";

	private static final String PORT = "port";

	private static final String DESIREDCAPABILITIES = "desiredCapabilities";

	private static final String PLATFORMNAME = "platformName";

	private static final String DEVICENAME = "deviceName";

	private static final String APP = "app";

	private static final String APPPACKAGE = "appPackage";
	
	private static final String APPACTIVITY ="appActivity";

	private static final String NEWCOMMANDTIMEOUT = "newCommandTimeout";

	private static final String ACTION = "action";

	private static final String TARGET = "target";

	private static final String VALUE = "value";

	private static final String CONTEXT = "context";

	private static String doType = "";

	private String jsonServerInfo = "";

	private String jsonTestData = "";

	/**
	 * 実例を作成する
	 */
	public static Config config() {
		if (null == instance) {
			instance = new Config();
		}
		return instance;
	}

	/**
	 * APP処理実行メソッド
	 */
	public void executeApp(String[] args) {

		if (checkOptions(args)) {
			AppiumApp appium = AppiumApp.getInstance();
			AppiumApp.setServerInfo(serverInfo);
			AppiumApp.setTestData(testData);
			try {
				if (!doType.equals(null)) {
					switch (doType) {
					case CONNECT:
						appium.connect();
						break;
					case DISCONNECT:
						appium.disconnect();
						break;
					case MESSAGE:
						appium.message();
						break;
					case STATUS:
						break;
					default:
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * WEB処理実行メソッド
	 */
	public void executeWeb(String[] args) {

		if (checkOptions(args)) {
			AppiumWeb appium = AppiumWeb.getInstance();
			AppiumWeb.setServerInfo(serverInfo);
			AppiumWeb.setTestData(testData);
			try {
				if (!doType.equals(null)) {
					switch (doType) {
					case CONNECT:
						appium.connect();
						break;
					case DISCONNECT:
						appium.disconnect();
						break;
					case MESSAGE:
						appium.message();
						break;
					case STATUS:
						break;
					default:
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * パラメータチェックメソッド
	 *
	 * @param args
	 *            パラメータ
	 * @return パラメータチェック結果
	 */
	private boolean checkOptions(String[] args) {

		// パラメータ個数=0の場合、下記のエラーメッセージを出力して、処理を終了する。
		if (args == null || args.length == 0) {
			return false;
		}

		// 処理タイプを取得する
		doType = args[0];
		
		// server_nameの設定
		serverInfo.setServerName(args[4]);
		
		if (Config.CONNECT.equals(doType)) {
			jsonServerInfo = args[1];
			jsonServerInfo = jsonServerInfo.replaceAll("\"","\'");
			Config.ReadJsonServerInfoTmnf(jsonServerInfo);
		}

		if (Config.MESSAGE.equals(doType)) {
			jsonTestData = args[1];
			jsonTestData = jsonTestData.replaceAll("\"","\'");
			Config.ReadJsonTestDataTmnf(jsonTestData);
		}

		return true;
	}

	/**
	 * get server info
	 *
	 * @param jsonTestDataPath
	 *            the jsonTestDataPath to set
	 */
	public static void ReadJsonServerInfoTmnf(String data) {
		try {
			JsonParser parser = new JsonParser();
			JsonObject object = (JsonObject) parser.parse(data);

			serverInfo.setHost(object.get(Config.HOST).getAsString());
			serverInfo.setPort(object.get(Config.PORT).getAsString());
			Map<String, String> desiredCapabilitiesMap = new LinkedHashMap<String, String>();
			JsonObject desiredCapabilities = object.get(Config.DESIREDCAPABILITIES).getAsJsonObject();

			desiredCapabilitiesMap.put(Config.PLATFORMNAME, desiredCapabilities.get(Config.PLATFORMNAME).getAsString());
			desiredCapabilitiesMap.put(Config.DEVICENAME, desiredCapabilities.get(Config.DEVICENAME).getAsString());
			if (desiredCapabilities.get(Config.APP) != null)
			{
				desiredCapabilitiesMap.put(Config.APP, desiredCapabilities.get(Config.APP).getAsString());
			}	
			if(desiredCapabilities.get(Config.APPPACKAGE) != null)
			{
				desiredCapabilitiesMap.put(Config.APPPACKAGE, desiredCapabilities.get(Config.APPPACKAGE).getAsString());
			}
			desiredCapabilitiesMap.put(Config.APPACTIVITY, desiredCapabilities.get(Config.APPACTIVITY).getAsString());
			desiredCapabilitiesMap.put(Config.NEWCOMMANDTIMEOUT, desiredCapabilities.get(Config.NEWCOMMANDTIMEOUT).getAsString());

			serverInfo.setDesiredCapabilities(desiredCapabilitiesMap);
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get server info
	 *
	 * @param jsonTestDataPath
	 *            the jsonTestDataPath to set
	 */
	public static void ReadJsonTestDataTmnf(String data) {
		try {
			JsonParser parse = new JsonParser();
			JsonObject json = (JsonObject) parse.parse(data);
			testData.setAction(json.get(Config.ACTION).getAsString());
			testData.setTarget(json.get(Config.TARGET).getAsString());
			testData.setValue(json.get(Config.VALUE).getAsString());
			testData.setContext(json.get(Config.CONTEXT).getAsString());
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
	}
}
