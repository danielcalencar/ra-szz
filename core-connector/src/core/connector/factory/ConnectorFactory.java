package core.connector.factory;

import core.connector.model.Connector;

public abstract class ConnectorFactory 
{
	public abstract Connector<?> createConnector(String user, String password, String url) throws Exception;
	
}
