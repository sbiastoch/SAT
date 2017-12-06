package praxisblatt03.parser;

import praxisblatt03.CDCL;
import praxisblatt03.dataStructure.*;

public class Dimacs {
  private static String instances_path = "./sat_instances/";
  private static String instances[] = {
        //  "formula01.cnf",
            "small_aim/yes/aim-50-1_6-yes1-1.cnf",
         //   "small_aim/no/aim-50-1_6-no-1.cnf",
  };

  public static void main(String[] args) {

    System.out.println("Working Directory = " +
            System.getProperty("user.dir"));
    for (String instance: instances) {
      ClauseSet cs = new ClauseSet(instances_path + instance);
      System.out.println("Solving Clauseset:\n"+cs.clausesToString());
      CDCL cdcl = new CDCL(cs);
      boolean sat = cdcl.solve();
      System.out.println("Clauseset is sat: "+sat);
    }
  }
}
