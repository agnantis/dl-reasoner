package uom.dl.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	/**
	 * Returns a list of all possible pairs that can be created from a list of Individuals.
	 * The order of the elements of a pair is irrelevant, i.e. (a, b) is the same as (b, a), 
	 * so only one will be returned.
	 * @param inds a list of <i>distinct</i> individuals
	 * @return a list of all possible pairs
	 */
	public static List<IndividualPair> getPairs(List<Individual> inds) {
		List<IndividualPair> pairs = new ArrayList<>();
		int start = 0;
		for (int j = 0; j < inds.size(); ++j){
			for (int i = start+1; i < inds.size(); ++i) {
				pairs.add(new IndividualPair(inds.get(start), inds.get(i)));
			}
			++start;
		}
		return pairs;
	}
	
	
	public static class IndividualPair {
		private final Individual indA;
		private final Individual indB;
		
		public IndividualPair(Individual x, Individual y) {
			this.indA = x;
			this.indB = y;
		}

		public Individual getFirst() {
			return indA;
		}
		
		public Individual getSecond() {
			return indB;
		}
		
		@Override
		public String toString() {
			return "(" + indA + "," + indB + ")";
		}		
	}
	
	public static void main(String[] args) {
		Individual ind1 = new Individual("a");
		Individual ind2 = new Individual("b");
		Individual ind3 = new Individual("c");
		Individual ind4 = new Individual("d");
		List<Individual> list = Arrays.asList(ind1, ind2, ind3, ind4);
		
		System.out.println(getPairs(list));
	}

}
