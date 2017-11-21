package praxisblatt02.dataStructure;

import java.util.Vector;

/**
 * A variable.
 * 
 */
public class Variable {

	/* Assignment states of a variable */
	public enum State {
		TRUE, FALSE, OPEN
	};

	/* Current assignment */
	private State state;

	/* Variable ID (range from 1 to n) */
	private int id;

	/* Clauses containing this variable */
	private Vector<Clause> adjacencyList;

	/**
	 * Creates a variable with the given ID.
	 * 
	 * @param id ID of the variable
	 */
	public Variable(int id) {
		this.id = id;
		state = State.OPEN;
		adjacencyList = new Vector<>();
	}

	/**
	 * Returns the current assignment state of this variable.
	 * 
	 * @return current assignment state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the ID of this variable.
	 * 
	 * @return ID of this variable
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the adjacency list of this variable.
	 * 
	 * @return adjacency list of this variable
	 */
	public Vector<Clause> getAdjacencyList() {
		return adjacencyList;
	}

	/**
	 * Assigns variable with the given value and updates the internal state of
	 * the corresponding clauses.
	 * 
	 * @param val value to be assigned
	 */
	public void assign(boolean val) {
		state = val ? State.TRUE : State.FALSE;
		for(Clause clause: getAdjacencyList()) {

			// Set satisfied if variable is assigned to true
			if(clause.getPolarity(getId()) == val) {
				clause.setSat(true);
				System.out.println("Clause "+clause+" is now sat (x"+getId()+"="+val+").");
			}

			// Update unassigned counts
			int unassigned = clause.getNumUnassigned() - 1;
			clause.setNumUnassigned(unassigned);

			// Set unsatisfied if clause has no more unassigned variables and is not already sat
			if(unassigned == 0 && !clause.getSat()) {
				clause.setSat(false);
			}
		}
	}

	private String adjacencyListToString() {
		String ret = "\t\t";
		for(Clause c: adjacencyList) {
			ret += c.toString() + "\n\t\t";
		}
		return "[\n"+ret+"\n\t]";
	}

	@Override
	public String toString() {
		String res = "[" + state + " ";
		res += "\n\tAdjacence List: " + adjacencyListToString();
		return res + "\n]";
	}
}