import java.io.IOException;

public class Dimacs {
  private static String instances_path = "../sat_instances/";
  private static String instances[] = {
    "aim-100-1_6-no-1.cnf",
    "barrel5-no.cnf",
    "hanoi4-yes.cnf",
    "longmult6-no.cnf",
    "ssa7552-160-yes.cnf",
    "aim-200-2_0-yes1-2.cnf",
    "goldb-heqc-k2mul.cnf",
    "hole8-no.cnf",
    "miza-sr06-md5-47-03.cnf",
    //"test.cnf",
  };

  public static void main(String[] args) throws IOException {
    DimacsParser d = new DimacsParser();

    try {
      for (String instance: instances) {
        d.readFile(instances_path + instance);
        d.print();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
