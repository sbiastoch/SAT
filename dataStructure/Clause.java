package praxisblatt02.dataStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * A clause.
 * 
 */
public class Clause {
	/* Literals of the clause */
	private Vector<Integer> literals;

	/* Number of unassigned literals within this clause */
	private int numUnassigned;

	/* Current state of satisfaction */
	private boolean sat;

	/**
	 * Creates a new clause with the given literals.
	 *
	 * @param literals literals of the clause
	 * @param variables
	 */
	public Clause(Vector<Integer> literals, HashMap<Integer, Variable> variables) {
		this.literals = literals;
		setNumUnassigned(size());
		setSat(size() == 0);

		for(int lit: literals) {
			int varId = Math.abs(lit);
			variables.get(varId).getAdjacencyList().add(this);
		}
	}

	/**
	 * Returns the literals of this clause.
	 * 
	 * @return literals of this clause
	 */
	public Vector<Integer> getLiterals() {
		return literals;
	}

	/**
	 * Returns the number of unassigned literals in this clause.
	 * 
	 * @return number of unassigned literals
	 */
	public int getNumUnassigned() {
		return numUnassigned;
	}

	/**
	 * Sets the number of unassigned literals in this clause.
	 * 
	 * @param unassigned
	 */
	public void setNumUnassigned(int unassigned) {
		this.numUnassigned = unassigned;
	}

	/**
	 * Returns the current satisfaction state of this clause.
	 * 
	 * @return satisfaction state of this clause
	 */
	public boolean getSat() {
		return sat;
	}

	/**
	 * Sets the satisfaction state of this clause to the given value.
	 * 
	 * @param sat new satisfaction state
	 */
	public void setSat(boolean sat) {
		this.sat = sat;
	}

	/**
	 * Returns an unassigned literal of this clause.
	 * 
	 * @param variables variable objects
	 * @return an unassigned literal, if one exists, 0 otherwise
	 */
	public int getUnassigned(HashMap<Integer, Variable> variables) {
		for(int literal: getLiterals()) {
			int literalId = Math.abs(literal);
			Variable var = variables.get(literalId);

			if(var.getState() == Variable.State.OPEN) {
				return var.getId();
			}
		}
		return 0;
	}

	/**
	 * Returns the current unit state of this clause.
	 * 
	 * @return true if this clause is unit, otherwise false
	 */
	public boolean isUnit() {
		return numUnassigned == 1;
	}

	/**
	 * Returns the current satisfaction state.
	 * 
	 * @return true if this clause is satisfied, otherwise false
	 */
	public boolean isSat() {
		return getSat();
	}

	/**
	 * Returns the current empty state of this clause.
	 * 
	 * @return true if this clause is empty, otherwise false
	 */
	public boolean isEmpty() {
		return numUnassigned == 0 && !isSat();
	}

	/**
	 * Returns the phase of the variable within this clause.
	 * 
	 * @param num variable ID (>= 1)
	 * @return true, if variable is positive within this clause, otherwise false
	 */
	public boolean getPolarity(int num) {
		return literals.contains(num);
	}

	/**
	 * Returns the size of this clause.
	 * 
	 * @return size of this clause.
	 */
	public int size() {
		return literals.size();
	}

	@Override
	public String toString() {
		String res = "{ ";
		for (Integer i : literals)
			res += i + " ";
		return res + "}" + ", sat = " + sat + ", unassigned = " + numUnassigned;
	}
}