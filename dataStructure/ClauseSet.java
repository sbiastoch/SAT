package praxisblatt02.dataStructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import praxisblatt02.parser.DimacsParser;

/**
 * A set of clauses.
 * 
 */
public class ClauseSet {
	/* Number of variables */
	private int varNum;

	/* Clauses of this set */
	private Vector<Clause> clauses;

	/* List of all variables */
	private HashMap<Integer, Variable> variables;

	/**
	 * Constructs a clause set from the given DIMACS file.
	 * 
	 * @param filePath file path of the DIMACS file.
	 */
	public ClauseSet(String filePath) {
		clauses = new Vector<>();
		variables = new HashMap<>();
		DimacsParser reader = new DimacsParser();

		try {
			reader.readFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// read all clauses
		for(String[] clauseStr: reader.getClausels()) {
			Vector<Integer> literals = new Vector<>();

			// parse each literal and construct variables
			for(String litStr: clauseStr) {

				int lit = Integer.parseInt(litStr);
				literals.add(lit);

				int varId = Math.abs(lit);
				variables.putIfAbsent(varId, new Variable(varId));
			}

			clauses.add(new Clause(literals, variables));
		}
		varNum = variables.size();
	}

	/**
	 * Executes unit propagation and checks for the existence of an empty
	 * clause.
	 * 
	 * @return true, if an empty clause exists, otherwise false.
	 */
	public boolean unitPropagation() {

		// Repeat as long as unit clauses exists
		for(Clause unitClause = nextUnit(); unitClause != null; unitClause = nextUnit()) {

			// Get unassigned variable of current unit clause
			int varId = unitClause.getUnassigned(variables);

			// Retrieve variable from hashmap
			Variable var = variables.get(varId);

			// Get polarity of literal of that variable
			boolean litPolarity = unitClause.getPolarity(varId);

			// Assign variable according to polarity
			System.out.println("Assign x_"+varId+" = "+litPolarity+" (forced by "+unitClause+")");
			var.assign(litPolarity);
		}

		// Unsatisfiable constraint found
		return containsEmpty();
	}

	/**
	 * Returns the next unit clause, if one exists.
	 * 
	 * @return next unit clause, if one exists, otherwise null
	 */
	private Clause nextUnit() {
		for(Clause c: clauses) {
			if(c.isUnit() && !c.isSat()) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Checks, if an empty clause exists.
	 * 
	 * @return true, if an empty clause exists, otherwise false.
	 */
	private boolean containsEmpty() {
		for(Clause c: clauses) {
			if(c.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return clausesToString() + "\n\n" + varsToString();
	}

	/**
	 * Returns all clauses as string representation.
	 * 
	 * @return a string representation of all clauses.
	 */
	public String clausesToString() {
		String res = "";
		for (Clause clause : clauses)
			res += clause + "\n";
		return res;
	}

	/**
	 * Returns all variables as string representation.
	 * 
	 * @return a string representation of all variables.
	 */
	public String varsToString() {
		String res = "";
		for (int i = 1; i <= varNum; i++)
			res += "Variable " + i + ": " + variables.get(i) + "\n\n";
		return res;
	}
}