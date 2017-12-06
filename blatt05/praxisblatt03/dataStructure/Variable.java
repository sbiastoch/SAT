package praxisblatt03.dataStructure;

import java.util.Stack;
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

	/*
	 *  In diesem Feld speichern Sie die Aktivität der jeweiligen Variable. Dazu verwenden Sie
	 *  folgende Strategie:
	 *  Zu Beginn erhält jede Variable die Anzahl ihrer Vorkommen als Aktivität.
	 *  Jedesmal wenn eine Variable in einer neu gelernten Klausel vorkommt, erhöhen Sie ihre Aktivität (·1.10).
	 *  Jedesmal wenn Sie eine neue Variable im CDCL Algorithmus wählen, verringern Sie die Aktivitäten aller Variablen (·0.95).
	 */
	public float activity;

	/* Grund der Belegung, null bei decision */
	public Clause reason;

	/* Entscheidungslevel */
	public int level;


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
		activity = 0;
		reason = null;
		level = 0;
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
	 * the corresponding clauses. Sets level and reason of this variable.
	 *
	 * @param val Belegung, die zugewiesen werden soll
	 * @param variables Map der Variablen
	 * @param units  Liste der Klauseln, die gerade unit sind
	 * @return Kommt es durch die Zuweisung zu einer leeren Klausel, so wird diese zurückgegeben, ansonsten wird null zurückgegeben.
	 */
	public Clause assign(boolean val, int lvl, Clause reason, Stack<Variable> stack, HashMap<Integer, Variable> variables, Vector<Clause> units) {

		//Die Variable wird mit dem jeweiligen Wert val belegt.
		state = val ? State.TRUE : State.FALSE;

		this.reason = reason;
		level = lvl;
		stack.push(this);

		System.out.println("Assigning Var"+this.getId()+"="+state + " at lvl="+lvl+", reason="+reason+" (activity="+activity+")");

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
						System.out.println("Clause "+clause+" became unit.");
						units.add(clause);

					}
				} else {
					//System.out.println("reWatch not needed, "+litId+" not watched in "+clause);
				}
			} else { // literal evaluiert zu 1
				units.remove(clause);
			}
		}

		//Gab es im gesamten Prozess keine leere Klauseln, so soll die assign Methode null zurückliefern.
		return null;
	}

	public void reset(HashMap<Integer, Variable> variables, Vector<Clause> units) {
		System.out.println("Resetting Variable "+getId()+" (lvl "+level+")");
		state = State.OPEN;
		reason = null;
		level = 0;
		for(Clause c : watched) {
			if(c.lit1 == c.lit2) {
				c.lit2 = c.getPolarity(getId()) ? getId() : -getId();
				if(c.reWatch(variables, c.lit1) != Clause.ClauseState.UNIT) {
					units.remove(c); // if a variable is set to be again unassigned, units may vanish
				}
			}
		}
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
		return "x"+getId()+"="+getState();
		//String res = "[" + state + " ";
		//res += "\n\tAdjacence List: " + adjacencyListToString();
		//return res + "\n]";
	}
}