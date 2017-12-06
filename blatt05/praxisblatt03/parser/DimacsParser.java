package praxisblatt03.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DimacsParser {

    //Daten aus der P Zeile
    private int pVars; /* total number of variables according to document definition */
    private int pClauses; /* total number of clauses according to document definition  */

    private int clauseCount; /* true number of clauses occuring in document */
    private Set<Integer> literalCounts = new HashSet<>(); /* Set of literals. Duplicates of can be semantically ignores => Set */
    private Map<Integer, Integer> varCounts = new HashMap<>();  /* variables mapped to number of occurrences*/
    private List<String[]> unitClausels = new ArrayList<>(); /* list of clauses which contain only a single literal */
    private List<String[]> clausels = new ArrayList<>(); /* list of all clauses */

    public DimacsParser() {
        reset();
    }

    /**
     * Werte zurücksetzen
     */
    private void reset() {
        pVars = 0;
        pClauses = 0;
        clauseCount = 0;
        literalCounts = new HashSet<Integer>();
        varCounts = new HashMap<Integer, Integer>();
        unitClausels = new ArrayList<>();
    }

    /**
     * Bestimmen von literalCounts,varCounts,clausels
     *
     * @param data
     * @throws IOException
     */
    private void parser(List<String> data) throws IOException {
        reset();

        for (String line : data) {
            // Skip coment lines: "c<comment>"
            if (line.startsWith("c") || line.isEmpty()) {
                continue;
            }

            // Split line by one or more spaces
            String[] clause = line.split("\\s+");

            // Parse metadata-line: "p <totalVars> <totalClauses>"
            if (line.startsWith("p")) {
                pVars = Integer.parseInt(clause[clause.length - 2]);
                pClauses = Integer.parseInt(clause[clause.length - 1]);
                continue;
            }

            // Each line is a clause, e.g. -3 2 5 -42
            // Iterate through all literals of current clause
            for (String literalStr : clause) {
                if (literalStr.equals("")) break;
                int literal = Integer.parseInt(literalStr);

                // 0 denotes end of clause
                if (literal == 0) {
                    String[] clauseWithoutEOL = Arrays.copyOf(clause, clause.length - 1);
                    clausels.add(clauseWithoutEOL);

                    // unitClausel consists only of a single literal
                    if (isUnit(clause)) {
                        unitClausels.add(clauseWithoutEOL);
                    }
                    break;
                }
                // Count
                literalCounts.add(literal);

                //Variablen speichern falls schon vorhanden häufigkeit um 1 erhöhen.
                int var = Math.abs(literal);
                int oldCount = varCounts.getOrDefault(var, 0); // old count, if exists
                varCounts.put(var, oldCount + 1);
            }

            clauseCount++;

        }
    }

    public List<String[]> getClausels() {
        return clausels;
    }

    private boolean isUnit(String[] clause) {
        return clause.length == 2; // contains only literal and 0 as eol
    }

    /**
     * Ausgabe der Statistik
     */
    public void print() {
        System.out.println("Problem line: #vars " + pVars + ", #clauses : " + pClauses);
        System.out.println("blatt03.dataStructure.Variable count: " + varCounts.size());
        System.out.println("blatt03.dataStructure.Clause count: " + clauseCount);
        System.out.println("Literal count: " + literalCounts.size());
        System.out.println("Maximum occurrences of variable: " + getMaxVarCount());
        System.out.println("Variables with maximum number occurrences: " + getMaxEntries());
        System.out.println("Positive pure literals: " + getPureLiteralPos());
        System.out.println("Negative pure literals: " + getPureLiteralNeg());
        System.out.println("unitClausels: " + Arrays.toString(clausesToString(unitClausels)));
        System.out.println("");

    }

    private String[] clausesToString(List<String[]> clausels) {
        String[] ret = new String[clausels.size()];
        for (int i = 0; i < clausels.size(); i++) {
            ret[i] = clauseToString(clausels.get(i));
        }
        return ret;
    }

    private String clauseToString(String[] clause) {
        String ret = "";
        for (int i = 0; i < clause.length; i++) {
            String literal = clause[i];
            if (literal.equals("0")) break;
            ret = literal;
            if (i < clause.length - 1) {
                ret += ' ';
            }
        }
        return ret;
    }

    private int getMaxVarCount() {
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : varCounts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        return max;
    }

    /**
     * Tupel aus Wert und max(Häufigkeit).
     */
    private List<Integer> getMaxEntries() {
        int maxVarOccCnt = getMaxVarCount();
        List<Integer> maxOccVariables = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : varCounts.entrySet()) {
            if (entry.getValue() == maxVarOccCnt) {
                maxOccVariables.add(entry.getKey());
            }
        }

        return maxOccVariables;
    }

    private Set<Integer> getPureLiteralPos() {
        Set<Integer> lite = new HashSet<Integer>();
        for (int l : literalCounts) {
            if (l > 0)
                if (!literalCounts.contains(-l)) {
                    lite.add(l);
                }
        }
        return lite;
    }

    private Set<Integer> getPureLiteralNeg() {
        Set<Integer> lite = new HashSet<Integer>();
        for (int l : literalCounts) {
            if (l < 0)
                if (!literalCounts.contains(-l)) {
                    lite.add(l);
                }
        }
        return lite;
    }


    /**
     * Einlesen der Files und als Liste speichern zur weiterverarbeitung
     *
     * @param path
     * @throws IOException
     */
    public void readFile(String path) throws IOException {
        System.out.println("File: " + path);
        List<String> data = Files.readAllLines(Paths.get(path));
        parser(data);
    }
}