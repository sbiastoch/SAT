package praxisblatt03.parser;

import praxisblatt03.dataStructure.*;

public class Dimacs {
  private static String instances_path = "./";
  private static String instances[] = {
          "formula01.cnf",
          "formula02.cnf",
  };

  public static void main(String[] args) {

    System.out.println("Working Directory = " +
            System.getProperty("user.dir"));
    for (String instance: instances) {
      ClauseSet cs = new ClauseSet(instances_path + instance);
      Clause containsEmpty = cs.unitPropagation();
      System.out.println(cs);
      System.out.println("Clause containing empty: "+containsEmpty);
    }
  }
}
