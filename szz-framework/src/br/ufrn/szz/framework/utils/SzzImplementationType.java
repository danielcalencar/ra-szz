package br.ufrn.szz.framework.utils;

public enum SzzImplementationType {

	BSZZ("bicbszz"), AGSZZ("bicagszz"), MASZZ("bicmaszz"), RASZZ("bicraszz"), RSZZ("r_szz"), LSZZ("l_szz");
	    
    private final String table;
    SzzImplementationType(String table){
        this.table = table;
    }
    
    public String getTableName(){
        return table;
    }
	
}
