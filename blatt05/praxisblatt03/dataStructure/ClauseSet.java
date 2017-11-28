package praxisblatt03.dataStructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import praxisblatt03.parser.DimacsParser;

/**
 * A set of clauses.
 *
 */
public class ClauseSet {
	/* Number of variables */
	private int varNum;

	/* Clauses of this set */
	private Vector<Clause> clauses;

	/* Current unit clauses */
	private Vector<Clause> units;

	/* List of all variables */
	private HashMap<Integer, Variable> variables;

	/**
	 * Constructs a clause set from the given DIMACS file.
	 *
	 * @param filePath file path of the DIMACS file.
	 */
	public ClauseSet(String filePath) {
		clauses = new Vector<>();
		units = new Vector<>();
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

			Clause clause = new Clause(literals, variables);
			clauses.add(clause);
			if(clause.initWatch(variables) == Clause.ClauseState.UNIT) {
				units.add(clause);
			}
		}
		varNum = variables.size();
	}

	/**
	 *  Die Methode unitPropagation()
	 soll nun eine leere Klausel zurückliefern, sobald eine leere Klausel gefunden wird, ansonsten null.
	 *
	 * @return true, if an empty clause exists, otherwise false.
	 */
	public Clause unitPropagation() {

		Clause unitClause = units.firstElement();

		System.out.println("Units: \n"+units);
		// Get unassigned literal of current unit clause
		int lit = unitClause.getUnassigned(variables);
		int varId = Math.abs(lit);

		// Retrieve variable from hashmap
		Variable var = variables.get(varId);

		// Get polarity of literal of that variable
		boolean litPolarity = unitClause.getPolarity(varId);

		// Assign variable according to polarity
		System.out.println("Assign x_"+varId+" = "+litPolarity+" (forced by "+unitClause+")");
		Clause emptyClause = var.assign(litPolarity, variables, units);
		if(emptyClause != null) {
			// Unsatisfiable constraint found
			return emptyClause;
		}

		if(units.isEmpty()) {
			// No empty clause found
			return null;
		} else {
			// Repeat as long as unit clauses exists
			return unitPropagation();
		}
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
			res += "blatt03.dataStructure.Variable " + i + ": " + variables.get(i) + "\n\n";
		return res;
	}
}