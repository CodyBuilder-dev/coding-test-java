package platforms.programmers.Q76501.tests;

import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import platforms.programmers.Q76501.model.SignedSumInput;

public class SignedSumSuites {
  public static TestSuite<SignedSumInput, Integer> smoke() {
    return TestSuite.of("programmers/signedsum smoke", List.of(
        TestCase.expect(new SignedSumInput(new int[]{4,7,12},  new boolean[]{true,false,true}), 9, "basic", "basic"),
        TestCase.expect(new SignedSumInput(new int[]{1,2,3},   new boolean[]{false,false,true}), 0,"basic2", "basic"),
        TestCase.expect(new SignedSumInput(new int[]{},        new boolean[]{}),0, "empty", "edge"),
        TestCase.expect(new SignedSumInput(new int[]{1000},    new boolean[]{false}), -1000,"single negative", "edge")
    ));
  }

  public static TestSuite<SignedSumInput, Integer> random() {
    Random rnd = new Random(1); // seed 고정 -> 재현 가능

    int cases = 300;            // 적당히
    int maxN = 200;             // 배열 길이
    int maxAbs = 1000;          // absolutes 값

    List<TestCase<SignedSumInput, Integer>> list = new ArrayList<>(cases);

    for (int ci = 0; ci < cases; ci++) {
      int n = 1 + rnd.nextInt(maxN);

      int[] absolutes = new int[n];
      boolean[] signs = new boolean[n];

      for (int i = 0; i < n; i++) {
        absolutes[i] = 1 + rnd.nextInt(maxAbs);
        signs[i] = rnd.nextBoolean();
      }

      int expected = expected(absolutes, signs);
      list.add(TestCase.expect(new SignedSumInput(absolutes, signs), expected, "rnd#" + ci));
    }

    return TestSuite.of("signedsum/random", list);
  }

  private static int expected(int[] absolutes, boolean[] signs) {
    int sum = 0;
    for (int i = 0; i < absolutes.length; i++) sum += signs[i] ? absolutes[i] : -absolutes[i];
    return sum;
  }
}
