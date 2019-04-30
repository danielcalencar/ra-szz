package br.ufrn.szz.framework.persistence;

public class HibernateFactoryDAO extends FactoryDAO
{
	
	@Override
	public MetricDAO getMetricDAO(){
		return new MetricDAOImpl();
	}

}

