package br.ufrn.raszz.model.szz;

public enum LineType{
	ADDITION,
	DELETION,
	CONTEXT;

	public String toString() {
		switch(this) {
			case ADDITION: return "addition";
			case DELETION: return "deletion";
			case CONTEXT: return "context";
			default: throw new IllegalArgumentException();
		}
	}
}
