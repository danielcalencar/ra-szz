package br.ufrn.raszz.persistence;

public abstract class FactoryDAO 
{
	public abstract RefacDAO getRefacDAO();
	public abstract SzzDAO getSzzDAO();
	public abstract UtilQueryDAO getUtilQueryDAO();

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
