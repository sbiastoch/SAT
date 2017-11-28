package praxisblatt03.dataStructure;

import java.util.HashMap;
import java.util.Vector;

/**
 * A clause.
 *
 */
public class Clause {
	/* Literals of the clause */
	private Vector<Integer> literals;

	/* Watched literals in this clause */
	private int lit1;
	private int lit2;

	/**
	 * Creates a new clause with the given literals.
	 *
	 * @param literals literals of the clause
	 * @param variables
	 */
	public Clause(Vector<Integer> literals, HashMap<Integer, Variable> variables) {
		this.literals = literals;

		for(int lit: literals) {
			int varId = Math.abs(lit);
			variables.get(varId).getWatchedClauses().add(this);
		}
	}

	public ClauseState initWatch(HashMap<Integer, Variable> variables) {
		switch(literals.size()) {
			case 0:
				return ClauseState.EMPTY;
			case 1:
				lit1 = literals.elementAt(0);
				lit2 = literals.elementAt(0);
				return ClauseState.UNIT;
			default:
				lit1 = literals.elementAt(0);
				lit2 = literals.elementAt(1);
				return ClauseState.SUCCESS;
		}
	}

	private boolean litIsSat(int lit, HashMap<Integer, Variable> variables) {
		int varId = Math.abs(lit);
		Variable.State varState = variables.get(varId).getState();
		return (getPolarity(varId) == (varState == Variable.State.TRUE)) && !(varState == Variable.State.OPEN);
	}

	public ClauseState reWatch(HashMap<Integer, Variable> variables, int lit) {
		boolean changeFirstWatcher = lit == lit1;
		int numSearched = 1;
		int maxLitIdx = getLiterals().size();
		int currentLitIdx = (getLiterals().indexOf(lit) + 1) % maxLitIdx;
		int currentLit = getLiterals().get(currentLitIdx);
		int newVarId = Math.abs(currentLit);
		Variable.State newVarState = variables.get(newVarId).getState();

		System.out.println("reWatch for literal "+lit+" in "+this+", watches are "+lit1+", "+lit2);
		while(numSearched < maxLitIdx) {

			currentLit = getLiterals().get(currentLitIdx);
			newVarId = Math.abs(currentLit);
			newVarState = variables.get(newVarId).getState();
			System.out.print("Looking at "+currentLit);

			// Das neue watched literal muss verschieden zu den beiden aktuellen watched literals der Klausel sein
			// Problem: Im Falle, dass das einzige freie literal bereits gewatched wird, wird dieses nicht gefunden...
			if((changeFirstWatcher && currentLit == lit2) || (!changeFirstWatcher && currentLit == lit1)) {
				currentLitIdx = (currentLitIdx + 1) % maxLitIdx;
				numSearched++;
				System.out.println(", but is already watched");
				continue;
			}

			// und kann entweder unbelegt oder belegt und zu 1 evaluierend sein.
			if(newVarState == Variable.State.OPEN || litIsSat(currentLit, variables)) {

				//1. SUCCESS: Es wurde ein neues watched literal gefunden.
				if(changeFirstWatcher) {
					lit1 = currentLit;
				} else {
					lit2 = currentLit;
				}
				System.out.println("\nSuccess: New watch is "+currentLit);
				return ClauseState.SUCCESS;
			} else {
				System.out.println(", but is nether OPEN nor making the lit SAT");
			}

			currentLitIdx = (currentLitIdx + 1) % maxLitIdx;
			numSearched++;
		} // Es wurde kein neues watched literal gefunden => lit1==lit2

		int otherWatchLit = changeFirstWatcher ? lit2 : lit1;
		int otherWatchVarId = Math.abs(otherWatchLit);
		Variable.State otherWatchState = variables.get(otherWatchVarId).getState();

		System.out.println("Other watch is "+otherWatchLit);
		//4. UNIT: das zweite watched Literal zeigt auf ein unbelegtes Literal.
		if(otherWatchState == Variable.State.OPEN) {
			System.out.println("Unit");
			return ClauseState.UNIT;
		}

		//3. SAT: das zweite watched literal zeigt auf ein Literal, das zu 1 evaluiert.
		if(litIsSat(otherWatchLit, variables)) {
			System.out.println("Sat");
			return ClauseState.SAT;
		}

		//2. EMPTY: das zweite watched literal dieser Klausel
		//zeigt auf ein Literal, das zu 0 evaluiert.
		System.out.println("Empty");
		return ClauseState.EMPTY;
	}

	public boolean isWatched(int lit) {
		return lit == lit1 || lit == lit2;
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
	 * Returns an unassigned literal of this clause.
	 *
	 * @param variables variable objects
	 * @return an unassigned literal, if one exists, 0 otherwise
	 */
	public int getUnassigned(HashMap<Integer, Variable> variables) {
		int var1 = Math.abs(lit1);
		if(variables.get(var1).getState() == Variable.State.OPEN) {
			return var1;
		}

		int var2 = Math.abs(lit2);
		if(variables.get(var2).getState() == Variable.State.OPEN) {
			return var2;
		}

		return 0;
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
		return res + "}, lit1="+lit1+", lit2="+lit2;
	}

	public enum ClauseState {
		SAT, EMPTY, UNIT, SUCCESS
	}
}