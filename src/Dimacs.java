import java.io.IOException;

public class Dimacs {
    public static void main(String[] args) throws IOException {
        String paths[] = {"sat_instances/aim-100-1_6-no-1.cnf",
         "sat_instances/barrel5-no.cnf",
         "sat_instances/hanoi4-yes.cnf",
         "sat_instances/longmult6-no.cnf",
         "sat_instances/ssa7552-160-yes.cnf",
         "sat_instances/aim-200-2_0-yes1-2.cnf",
         "sat_instances/goldb-heqc-k2mul.cnf",
         "sat_instances/hole8-no.cnf",
         "sat_instances/miza-sr06-md5-47-03.cnf"};
        DimacsParser d = new DimacsParser();
        try {
            for (String path :paths) {
                d.readFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
