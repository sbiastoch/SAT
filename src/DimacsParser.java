import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DimacsParser {

    //Daten aus der P Zeile
    private int pVars ;
    private int pClauses ;

    private int countClauses ;
    private Set<Integer> literales = new HashSet<Integer>();
    private Map<Integer, Integer> vars = new HashMap<Integer, Integer>();
    private List<Integer> unitClausels = new ArrayList<>();
    private Map.Entry<Integer, Integer> maxEntry = null;

    /**
     * Bestimmen von literales,vars,clausels
     * @param data
     * @throws IOException
     */
    private void parser(List<String> data) throws IOException {
        reset();
        for (String line : data) {
            if (line.startsWith("c") || line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");

            if (line.startsWith("p")) {
                pVars = Integer.parseInt(parts[parts.length - 2]);
                pClauses = Integer.parseInt(parts[parts.length - 1]);
                continue;
            }
            // DATA
            for (String literal : parts) {
                if(literal.equals(""))break;
                int part = Integer.parseInt(literal);
                //0 = zeilenende
                if (part == 0) break;

                literales.add(part);

                //Variablen speichern falls schon vorhanden häufigkeit um 1 erhöhen.
                int count = vars.containsKey(Math.abs(part)) ? vars.get(Math.abs(part)) : 0;
                vars.put(Math.abs(part), count + 1);

                //unitClausel bestimmen
                if (parts.length <= 2) {
                    unitClausels.add(part);
                }
            }

            countClauses += 1;

        }
        getMaxEntry();
        print();
    }

    /**
     * Ausgabe der Statistik
     */
    private void print() {
        System.out.println("Problem line: #vars " + pVars + " ,#clauses : " + pClauses);
        System.out.println("Variable count= " + vars.size());
        System.out.println("Clause count= " + countClauses);
        System.out.println("Literal count= " + literales.size());
        System.out.println("Maximum occurrences of variable= " + maxEntry.getValue());
        System.out.println("Variables with maximum number occurrences= " + maxEntry.getKey());
        System.out.println("Positive pure literals" + getPureLiteralPos());
        System.out.println("Negative pure literals" + getPureLiteralNeg());
        System.out.println("unitClausel= " + unitClausels);
        System.out.println("");

    }

    /**
     *Tupel aus Wert und max(Häufigkeit).
     *
     */
    private void getMaxEntry(){
        for (Map.Entry<Integer, Integer> entry : vars.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
    }

    private  Set<Integer> getPureLiteralPos() {
        Set<Integer> lite = new HashSet<Integer>();
        for (int l : literales) {
            if (l>0)
                if (!literales.contains(-l)) {
                    lite.add(l);
                }
        }
        return lite;
    }
    private  Set<Integer> getPureLiteralNeg() {
        Set<Integer> lite = new HashSet<Integer>();
        for (int l : literales) {
            if (l<0)
                if (!literales.contains(-l)) {
                    lite.add(l);
                }
        }
        return lite;
    }



    /**
     * Einlesen der Files und als Liste speichern zur weiterverarbeitung
     * @param path
     * @throws IOException
     */
    public  void readFile(String path) throws IOException {
        System.out.println("File: "+path);
        List<String> data = Files.readAllLines(Paths.get(path));
        parser(data);

    }

    /**
     * Werte zurücksetzen
     */
    private void reset(){
        pVars = 0;
        pClauses =0;
        countClauses =0;
        literales = new HashSet<Integer>();
        vars = new HashMap<Integer, Integer>();
        unitClausels = new ArrayList<>();
        maxEntry = null;
    }
}
