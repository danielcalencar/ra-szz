package core.connector.model;

import java.util.ArrayList;
import java.util.List;

import core.connector.model.enums.ConnectorType;

public abstract class Connector<T> 
{
	protected String url;
	protected String user;
	protected String password;
	protected ConnectorType type;
	protected List<String> cookies; 
	protected T encapsulation;
	
	public T getEncapsulation() 
	{
		return encapsulation;
	}
	
	public void setEncapsulation(T connector) 
	{
		this.encapsulation = connector;
		this.cookies = new ArrayList<String>();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}
	
}