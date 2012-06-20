package uom.dl.elements;

public class Individual {
	private final String name;
	private static int index = 1;

	public Individual() {
		this("z" + index);
		++index;
	}
	public Individual(String name) {
		this.name = name;
	}
	
	public Individual(char name) {
		this("" + name);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Individual)
			return this.name.equals(((Individual)obj).name);
		return false;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
