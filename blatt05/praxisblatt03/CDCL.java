package praxisblatt03;

import praxisblatt03.dataStructure.Clause;
import praxisblatt03.dataStructure.ClauseSet;
import praxisblatt03.dataStructure.Variable;

import java.util.*;

public class CDCL {
    /*
    *  Instanz, auf der der CDCL Algorithmus arbeitet
    */
    private ClauseSet instance;

    /*
    * Der Stack, auf dem die Variablen, die belegt werden, gespeichert werden. Beachten Sie,
    * dass auf diesem Stack sowohl die Variablen landen, die gesetzt werden (decision variables)
    * als auch die, die während UP impliziert werden (implication variables). Beim Backtracking
    * nehmen Sie dann alle Belegungen bis zum entsprechenden Level zurück.
    */
    public Stack<Variable> stack;

    private int level;

    public CDCL(ClauseSet c) {
        instance = c;
        stack = new Stack<>();
        level = 0;
    }

    /*
     * Diese Methode führt den CDCL Algorithmus auf der aktuellen Instanz instance aus.
     * Den CDCL Algorithmus finden Sie als Pseudo Code auf den Folien zur Vorlesung.
     */
    public boolean solve() {
        for(int i = 0; i < 25; i++) {
            System.out.println(instance.clausesToString());
            // Unit Propagation
            System.out.println("\n\nDoing UP with "+instance.units);
            Clause emptyClause = instance.unitPropagation(level, stack);
            System.out.println("Units are now: \n"+instance.units);
            printVariables();

            if(emptyClause != null) { // empty clause, do backtracking
                System.out.println("Empty clause, backtracking.");
                int backtrackLevel = analyseConflict(emptyClause); // Lerne neue Klausel
                if(backtrackLevel == -1) { return false; }
                backtrack(backtrackLevel);
            } else { // no more unit clauses, decision needed
                Variable x = getNextVar(); // ... wähle neue Variable,
                if(x == null) {
                    return true;
                }
                System.out.println("Decision needed.");

                level++; // Erhöhe Level, ...
                x.assign(false, level, null, stack, instance.variables, instance.units); // ... belege Variable
            }
        }
        return false;
    }

    /*
        Removes all variabes from stack that are on a higher level then target level
     */
    private void backtrack(int lvl) {
        System.out.println("Jumping back from level "+level+" to "+lvl);
        Variable v;
        do {
            v = stack.pop();
            level = v.level;
            v.reset(instance.variables, instance.units);
        } while(level - 1 > lvl);
        System.out.println("Now at level "+level);
    }

    /*
    *  Diese Methode berechnet die Resolvente zwischen c1 und c2 und gibt diese als neue Klausel zurück.
    */
    private Clause resolve(Clause c1, Clause c2) {
        Set<Integer> allLiterals = new HashSet<>();

        allLiterals.addAll(c1.getLiterals());
        allLiterals.addAll(c2.getLiterals());
        int conflictLit = -1;
        for(int lit : allLiterals) {
            if(allLiterals.contains(lit) && allLiterals.contains(-lit)) {
                conflictLit = lit;
                break;
            }
        }
        //System.out.println("Resolving "+c1+" and "+c2+", conflicting literal is "+conflictLit);
        allLiterals.remove(conflictLit);
        allLiterals.remove(-conflictLit);

        Vector<Integer> ret = new Vector<>();
        ret.addAll(allLiterals);
        Clause c = new Clause(ret, instance.variables);
        //System.out.println("Resolved to "+c);
        return c;
    }

    /*
     * Diese Methode berechnet die 1UIP Klausel ausgehend von einer leeren Klausel conflict und einem Grund reason.
     */
/*    private Clause get1UIP(Clause conflict, Clause reason) {
        Clause firstNewClause = resolve(conflict, reason);
        Clause fncReason = null;
        // Take first literal that is not a dicision
        for(int lit : firstNewClause.getLiterals()) {
            Variable v = instance.variables.get(Math.abs(lit));
            if(v.reason != null) {
                fncReason = v.reason;
            }
        }
        Clause uip1 = resolve(firstNewClause, fncReason);
        System.out.println("1UIP is "+uip1);
        return uip1;
    }
*/
    /*
     * Diese Methode bekommt eine leere Klausel conflict. Anhand der letzten Variable auf dem Stack
     * kann der Grund für diesen Konflikt festgestellt werden. Die Methode lernt eine neue Klausel (1UIP)
      * aus dem Konflikt und gibt das Level zurück, zu dem zurückgesprungen werden muss.
     */
    private int analyseConflict(Clause conflict)  {
        System.out.println("Analyzing conflict...");
        if(level == 0) {
            return -1;
        }

        Variable lastVariableBeforeConflict = stack.peek();
        Clause reason = lastVariableBeforeConflict.reason;
        Clause newClause = resolve(conflict, reason);
        Variable [] deepestVars = twoDeepestVars(newClause);
        System.out.println("Last Variable was "+lastVariableBeforeConflict+" at level "+lastVariableBeforeConflict.level+" resulting in conflict clause : "+conflict);

        while(deepestVars[1] != null && deepestVars[0].level == deepestVars[1].level) { // while multiple on same level
            System.out.println("New clause: "+newClause+", reason: "+reason);
            Variable cv = deepestVars[0];
            reason = cv.reason;
            newClause = resolve(newClause, reason);
            deepestVars = twoDeepestVars(newClause);

        }
        instance.addClause(newClause);
        instance.units.add(newClause);
        if(deepestVars[1] == null) {
            System.out.println();
        }
        int newLevel = deepestVars[1].level; // 2. größtes lvl in clause
        System.out.println("Backtrack to "+newLevel + " and leared new clause "+newClause);
        return newLevel;
    }

    private Variable[] twoDeepestVars(Clause c) {
        Variable [] max = {null, null}; // 0 is largest, 1 is snd largest
        for(Variable v : c.getVariables(instance.variables)) {
            //if(v.reason == null) continue;
            if(max[0] == null || v.level > max[0].level) {
                max[1] = max[0];
                max[0] = v;
            } else if(max[1] == null || v.level > max[1].level) {
                max[1] = v;
            }
        }
        return max;
    }

    /*
     * Diese Methode gibt die nächste Variable zurück, die belegt werden soll. Dies soll die Variable
     * mit der höchsten Aktivität sein.
     */
    private Variable getNextVar() {
        float max = 0;
        Variable maxVar = null;
        for(Variable v : instance.variables.values()) {
            if(v.activity > max && v.getState() == Variable.State.OPEN) {
                max = v.activity;
                maxVar = v;
            }
            v.activity += .95;
        }
        return maxVar;
    }

    private void printVariables() {
        System.out.print("Variable states:\n");
        for(Variable v : instance.variables.values()) {
            if(v.getState() != Variable.State.OPEN) {
                System.out.print(v+"\t");
            }
        }
        System.out.println("");
    }
}
