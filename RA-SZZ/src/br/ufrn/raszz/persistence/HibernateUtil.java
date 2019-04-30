package br.ufrn.raszz.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;



public class HibernateUtil 
{
	//private final static SessionFactory postgreSqlSessionFactory = buildPostgreSQLSessionFactory();
	private static ServiceRegistry serviceRegistry;
	
	private static SessionFactory buildMySQLSessionFactory(String configName)
	{
		try
		{
			Configuration config = new Configuration();
			config.configure(configName);
			
			serviceRegistry = new ServiceRegistryBuilder().
					applySettings(config.getProperties()).buildServiceRegistry();
			return config.buildSessionFactory(serviceRegistry);
		}
		catch(Throwable ex)
		{
	        System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
		}
	}
	
	/*private static SessionFactory buildPostgreSQLSessionFactory()
	{
		try
		{
			Configuration config = new Configuration();
			config.configure("hibernate2.cfg.xml");
			
			serviceRegistry = new ServiceRegistryBuilder().
					applySettings(config.getProperties()).buildServiceRegistry();
			return config.buildSessionFactory(serviceRegistry);
		}
		catch(Throwable ex)
		{
	        System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
		}
	}*/
	
	public static SessionFactory getMySqlSessionFactory(String config)
	{
		return buildMySQLSessionFactory(config);
	}
	
	/*public static SessionFactory getPostgreSqlSessionFactory()
	{
		return postgreSqlSessionFactory;
	}*/
}
