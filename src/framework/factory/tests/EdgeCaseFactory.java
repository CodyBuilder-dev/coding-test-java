package framework.factory.tests;

import framework.test.TestCase;

import java.util.*;
import java.util.function.Function;

public final class EdgeCaseFactory {
  private EdgeCaseFactory() {}

  // ---------- int[] -> O ----------

  public static <O> List<TestCase<int[], O>> intArrayEdgeCases(Function<int[], O> expectedFn, String descPrefix) {
    List<int[]> inputs = new ArrayList<>();

    inputs.add(new int[]{}); // empty
    inputs.add(new int[]{0});
    inputs.add(new int[]{1});
    inputs.add(new int[]{-1});
    inputs.add(new int[]{Integer.MAX_VALUE});
    inputs.add(new int[]{Integer.MIN_VALUE});

    inputs.add(new int[]{0, 0, 0, 0});
    inputs.add(new int[]{1, 1, 1, 1});
    inputs.add(new int[]{-1, -1, -1, -1});

    inputs.add(new int[]{1, 2, 3, 4, 5});         // sorted
    inputs.add(new int[]{5, 4, 3, 2, 1});         // reverse sorted
    inputs.add(new int[]{2, 1, 2, 1, 2, 1});      // repeating pattern

    // extremes mixed (overflow-sensitive problems에 도움)
    inputs.add(new int[]{Integer.MAX_VALUE, 0, Integer.MIN_VALUE});
    inputs.add(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE});

    List<TestCase<int[], O>> out = new ArrayList<>(inputs.size());
    for (int i = 0; i < inputs.size(); i++) {
      int[] in = inputs.get(i);
      O exp = expectedFn.apply(in.clone()); // 보호
      out.add(TestCase.expect(in, exp, descPrefix + "/edge#" + i));
    }
    return out;
  }

  // ---------- String -> O ----------

  public static <O> List<TestCase<String, O>> stringEdgeCases(Function<String, O> expectedFn, String descPrefix) {
    List<String> inputs = List.of(
        "",                 // empty
        "a",
        "A",
        "aa",
        "ab",
        "aba",
        "abcdefghijklmnopqrstuvwxyz",
        "aaaaaaaaaaaaaaaaaaaaaaaaaa", // repeats
        "0123456789",
        "a1b2c3",
        "aA",               // mixed case
        "  ",               // spaces
        "a a a"             // spaces inside
    );

    List<TestCase<String, O>> out = new ArrayList<>(inputs.size());
    for (int i = 0; i < inputs.size(); i++) {
      String in = inputs.get(i);
      O exp = expectedFn.apply(in);
      out.add(TestCase.expect(in, exp, descPrefix + "/edge#" + i));
    }
    return out;
  }

  // ---------- (String,String) -> O ----------
  // (anagram / edit distance / subsequence 등 2-string input 문제에 흔함)

  public static <O> List<TestCase<framework.factory.inputs.StrPair, O>> strPairEdgeCases(
      Function<framework.factory.inputs.StrPair, O> expectedFn,
      String descPrefix
  ) {
    var L = new ArrayList<framework.factory.inputs.StrPair>();
    L.add(new framework.factory.inputs.StrPair("", ""));
    L.add(new framework.factory.inputs.StrPair("a", ""));
    L.add(new framework.factory.inputs.StrPair("", "a"));
    L.add(new framework.factory.inputs.StrPair("a", "a"));
    L.add(new framework.factory.inputs.StrPair("a", "b"));
    L.add(new framework.factory.inputs.StrPair("ab", "ba"));
    L.add(new framework.factory.inputs.StrPair("abc", "cba"));
    L.add(new framework.factory.inputs.StrPair("aaaa", "aaaa"));
    L.add(new framework.factory.inputs.StrPair("aaaa", "aaab"));

    List<TestCase<framework.factory.inputs.StrPair, O>> out = new ArrayList<>(L.size());
    for (int i = 0; i < L.size(); i++) {
      var in = L.get(i);
      O exp = expectedFn.apply(in);
      out.add(TestCase.expect(in, exp, descPrefix + "/edge#" + i));
    }
    return out;
  }
}
