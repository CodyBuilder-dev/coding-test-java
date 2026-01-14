package platforms.programmers.Q76501;

import framework.oracle.Reference;

public class SignedSumReference implements Reference<SignedSumInput, Integer> {
  @Override
  public Integer computeExpected(SignedSumInput in) {
    int[] a = in.absolutes();
    boolean[] s = in.signs();
    if (a == null || s == null || a.length != s.length) return 0;

    int sum = 0;
    for (int i = 0; i < a.length; i++) sum += s[i] ? a[i] : -a[i];
    return sum;
  }
}