package br.ufrn.raszz.persistence;

public class HibernateFactoryDAO extends FactoryDAO
{

	@Override
	public RefacDAO getRefacDAO(){
		return new RefacDAOImpl();
	}

	@Override
	public SzzDAO getSzzDAO() {
		return new SzzDAOImpl();
	}
	
	@Override
	public UtilQueryDAO getUtilQueryDAO() {
		return new UtilQueryDAOImpl();
	}

}

