package platforms.leetcode.Q242.tests;

import framework.factory.gen.Generators;
import framework.factory.gen.Rand;
import framework.factory.tests.TestFactories;
import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import platforms.leetcode.Q242.tests.oracle.AnagramOracles;
import platforms.leetcode.Q242.model.Q242Input;

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

  public TestSuite<Q242Input, Boolean> createUsingTestFactories(){
    Rand rnd = new Rand(1);

    Supplier<Q242Input> gen = () -> {
      String s = Generators.stringLower(rnd, 1, 30);
      boolean makeTrue = rnd.nextBool();
      String t;
      if (makeTrue) {
//        t = Generators.sortChars(s); // (간단히) 정렬로 permutation 만든 뒤 셔플하고 싶으면 추가
        // 진짜 permutation을 만들고 싶다면 char[] 셔플 구현하면 됨
        t = new StringBuilder(s).reverse().toString(); // 예시: 같은 multiset이면 true
      } else {
        t = Generators.stringLower(rnd, 1, 30);
      }
      return new Q242Input(s, t);
    };

    Function<Q242Input, Boolean> expected = in -> {
      if (in.s().length() != in.t().length()) return false;
      int[] cnt = new int[256];
      for (int i=0;i<in.s().length();i++) cnt[in.s().charAt(i)]++;
      for (int i=0;i<in.t().length();i++) if(--cnt[in.t().charAt(i)]<0) return false;
      return true;
    };

    var cases = TestFactories.withExpected(200, gen, expected, "rnd");
    return TestSuite.of("leetcode/anagram testFactories", cases);
  }
}
