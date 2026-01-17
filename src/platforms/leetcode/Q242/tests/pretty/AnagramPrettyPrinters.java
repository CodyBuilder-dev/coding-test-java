package platforms.leetcode.Q242.tests.pretty;

import framework.pretty.PrettyPrinter;
import platforms.leetcode.Q242.model.Q242Input;

public final class AnagramPrettyPrinters {
  private AnagramPrettyPrinters() {}

  public static PrettyPrinter<Q242Input> anagramInput() {
    return in -> {
      if (in == null) return "null";
      return "AnagramInput{s=\"" + clip(in.s(), 80) + "\", t=\"" + clip(in.t(), 80) + "\"}";
    };
  }

  private static String clip(String s, int max) {
    if (s == null) return "null";
    if (s.length() <= max) return s;
    return s.substring(0, max) + "...(" + s.length() + ")";
  }
}