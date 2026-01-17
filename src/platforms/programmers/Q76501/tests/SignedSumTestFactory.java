package platforms.programmers.Q76501.tests;

import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import platforms.programmers.Q76501.model.SignedSumInput;

public final class SignedSumTestFactory {
  private final int maxLen;
  private final int maxAbs;

  public SignedSumTestFactory(int maxLen, int maxAbs) {
    this.maxLen = maxLen;
    this.maxAbs = maxAbs;
  }

  public TestSuite<SignedSumInput, Integer> create(long seed, int n) {
    Random rnd = new Random(seed);
    List<TestCase<SignedSumInput, Integer>> cases = new ArrayList<>(n);

    for (int i = 0; i < n; i++) {
      int len = rnd.nextInt(maxLen + 1);
      int[] abs = new int[len];
      boolean[] signs = new boolean[len];

      int expected = 0;
      for (int j = 0; j < len; j++) {
        abs[j] = 1 + rnd.nextInt(maxAbs);
        signs[j] = rnd.nextBoolean();
        expected += signs[j] ? abs[j] : -abs[j];
      }

      cases.add(TestCase.expect(new SignedSumInput(abs, signs), expected, "rnd#" + (i + 1), "random"));
    }

    return TestSuite.of("programmers/signedsum random(" + n + ")", cases);
  }
}
