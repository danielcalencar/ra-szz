import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import core.connector.model.Connector;
import core.connector.model.enums.ConnectorType;

import java.io.FileInputStream;
import java.io.IOException;


public abstract class Miner {
	public static final String HOST = "host";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String RESULT = "result";

	protected Map<ConnectorType, Connector> connectors = new HashMap<ConnectorType, Connector>();
	protected Map parameters = new HashMap();
	protected String inputFile;	
	protected String outputFile;

	public abstract void performSetup() throws Exception;

	public abstract void performMining() throws Exception;

	public void executeMining() throws Exception {
		performSetup();
		performMining();
	}

	public Map<ConnectorType, Connector> getConnectors() {
		return connectors;
	}

	public void setConnectors(Map<ConnectorType, Connector> connectors) {
		this.connectors = connectors;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
	
	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public String getProperty(String propertyName, String propertiesFilePath) throws IOException {
		//final String path = "./backhoe.properties";
		final String path = propertiesFilePath;
		final Properties properties = new Properties();	
		final FileInputStream file = new FileInputStream(path);
		properties.load(file);
		file.close();		
		final String property = properties.getProperty(propertyName);
		return property;
	}

	public void validateParameter() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
