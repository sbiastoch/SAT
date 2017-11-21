package praxisblatt02.parser;

import praxisblatt02.dataStructure.ClauseSet;

import java.io.IOException;

public class Dimacs {
  private static String instances_path = "/home/sbiastoch/Desktop/SAT-Solving/Übungen/vorgabe/blatt04/sat_instances/";
  private static String instances[] = {
    "formula01.cnf",
    "formula02.cnf",
  };

  public static void main(String[] args) throws IOException {
    for (String instance: instances) {
      ClauseSet cs = new ClauseSet(instances_path + instance);
      boolean containsEmpty = cs.unitPropagation();
      System.out.println(cs);
      System.out.println("Contains empty Clause: "+containsEmpty);
    }
  }
}
