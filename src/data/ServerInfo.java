package data;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerInfo {
	
	private String serverName;
	
	private String host;
	
	private String port;
	
	private Map<String, String> desiredCapabilities = new LinkedHashMap<String, String>();
	
	
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host_info) {
		this.host = host_info;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port_info) {
		this.port = port_info;
	}

	/**
	 * @return the desiredCapabilities
	 */
	public Map<String, String> getDesiredCapabilities() {
		return desiredCapabilities;
	}

	/**
	 * @param desiredCapabilities the desiredCapabilities to set
	 */
	public void setDesiredCapabilities(Map<String, String> desiredCapabilities) {
		this.desiredCapabilities = desiredCapabilities;
	}
}
