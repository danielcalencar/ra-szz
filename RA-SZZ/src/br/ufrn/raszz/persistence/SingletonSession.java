package br.ufrn.raszz.persistence;

import org.hibernate.Session;

public class SingletonSession 
{
	
	private static Session hibernateSession; 
	
	public static Session getSession(String configName)
	{
		if(hibernateSession != null)
		{
			return hibernateSession;
		}
		else
		{
			hibernateSession = HibernateUtil.getMySqlSessionFactory(configName).openSession();
			return hibernateSession;
		}
	}
}
