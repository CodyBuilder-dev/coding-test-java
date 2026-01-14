package framework.pretty;

import java.util.Arrays;

public final class PrettyPrinters {
  private PrettyPrinters() {}

  public static PrettyPrinter<int[]> intArray() {
    return a -> a == null ? "null" : Arrays.toString(a);
  }

  public static PrettyPrinter<boolean[]> booleanArray() {
    return a -> a == null ? "null" : Arrays.toString(a);
  }
}