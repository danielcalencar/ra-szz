package br.ufrn.szz.framework.persistence;

public abstract class FactoryDAO 
{
	public abstract MetricDAO getMetricDAO();

	public static FactoryDAO getFactoryDAO(DAOType type) {
		if(type == DAOType.HIBERNATE)
		{
			return new HibernateFactoryDAO();
		}
		else
		{
			return null;
		}
	}
	
}
