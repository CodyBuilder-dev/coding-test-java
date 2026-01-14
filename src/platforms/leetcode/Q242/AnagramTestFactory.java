package platforms.leetcode.Q242;

import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AnagramTestFactory {
  private final String alphabet;
  private final int maxLen;
  private final double pMakeAnagram;

  public AnagramTestFactory(String alphabet, int maxLen, double pMakeAnagram) {
    this.alphabet = alphabet;
    this.maxLen = maxLen;
    this.pMakeAnagram = pMakeAnagram;
  }

  public TestSuite<Q242Input, Boolean> create(long seed, int n) {
    Random rnd = new Random(seed);
    List<TestCase<Q242Input, Boolean>> cases = new ArrayList<>(n);

    for (int i = 0; i < n; i++) {
      String s = randomString(rnd, rnd.nextInt(maxLen + 1));
      boolean makeAna = rnd.nextDouble() < pMakeAnagram;

      String t;
      boolean expected;
      if (makeAna) {
        t = permute(rnd, s);
        expected = true;
      } else {
        // not anagram by construction: different length OR flip one char (when possible)
        if (s.length() == 0 || rnd.nextBoolean()) {
          t = s + pickChar(rnd);
          expected = false;
        } else {
          char[] arr = s.toCharArray();
          int pos = rnd.nextInt(arr.length);
          char newCh = pickDifferentChar(rnd, arr[pos]);
          arr[pos] = newCh;
          t = new String(arr);
          expected = false;
        }
      }

      var in = new Q242Input(s, t);
      cases.add(
          TestCase.expectAndOracle(in, expected, AnagramOracles.basicProperties(), "rnd#" + (i + 1), "random"));
    }

    return TestSuite.of("leetcode/anagram random(" + n + ")", cases);
  }

  private String randomString(Random rnd, int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(pickChar(rnd));
    return sb.toString();
  }

  private char pickChar(Random rnd) {
    return alphabet.charAt(rnd.nextInt(alphabet.length()));
  }

  private char pickDifferentChar(Random rnd, char cur) {
    for (int k = 0; k < 20; k++) {
      char c = pickChar(rnd);
      if (c != cur) return c;
    }
    return (cur == 'a') ? 'b' : 'a';
  }

  private String permute(Random rnd, String s) {
    char[] a = s.toCharArray();
    for (int i = a.length - 1; i > 0; i--) {
      int j = rnd.nextInt(i + 1);
      char tmp = a[i]; a[i] = a[j]; a[j] = tmp;
    }
    return new String(a);
  }
}
