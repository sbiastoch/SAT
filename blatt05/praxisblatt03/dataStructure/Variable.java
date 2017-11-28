package praxisblatt03.dataStructure;

import java.util.Vector;
import java.util.HashMap;

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

	/* variable ID (range from 1 to n) */
	private int id;


	/* Clauses containing this variable */
	private Vector<Clause> watched;

	/**
	 * Creates a variable with the given ID.
	 *
	 * @param id ID of the variable
	 */
	public Variable(int id) {
		this.id = id;
		state = State.OPEN;
		watched = new Vector<>();
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
	public Vector<Clause> getWatchedClauses() {
		return watched;
	}

	/**
	 * Assigns variable with the given value and updates the internal state of
	 * the corresponding clauses.
	 *
	 * @param val Belegung, die zugewiesen werden soll
	 * @param variables Map der Variablen
	 * @param units  Liste der Klauseln, die gerade unit sind
	 * @return Kommt es durch die Zuweisung zu einer leeren Klausel, so wird diese zurückgegeben, ansonsten wird null zurückgegeben.
	 */
	public Clause assign(boolean val, HashMap<Integer, Variable> variables, Vector<Clause> units) {

		//Die Variable wird mit dem jeweiligen Wert val belegt.
		state = val ? State.TRUE : State.FALSE;

		//In allen Klauseln, in denen diese Variable beobachtet wird
		for(Clause clause: getWatchedClauses()) {

			//und das zugehörige Literal zu 0 evaluiert...
			boolean varPolarity = clause.getPolarity(getId());
			int litId = varPolarity ? getId() : -getId();
			if(varPolarity != val) {

				// nur wenn das literal eines der watched literals ist
				if(clause.isWatched(litId)) {
					//...muss ein neues watched literal gesucht werden (Methode reWatch in der Klasse Clause).
					Clause.ClauseState ret = clause.reWatch(variables, litId);


					if (ret == Clause.ClauseState.EMPTY) { //Gibt reWatch zurück, dass die Klausel nun leer ist (EMPTY)

						// so soll die assign Methode diese leere Klausel zurück geben
						return clause;

					} else if (ret == Clause.ClauseState.UNIT) { //Gibt reWatch zurück, dass die Klausel nun unit ist (UNIT)

						// so soll die Klausel zu der Liste units hinzugefügt werden.
						units.add(clause);

					}
				} else {
					System.out.println("reWatch not needed, "+litId+" not watched in "+clause);
				}
			} else { // literal evaluiert zu 1
				units.remove(clause);
			}
		}

		//Gab es im gesamten Prozess keine leere Klauseln, so soll die assign Methode null zurückliefern.
		return null;
	}

	private String adjacencyListToString() {
		String ret = "\t\t";
		for(Clause c: watched) {
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