package platforms.leetcode.Q242.tests.reference;

import framework.oracle.Reference;
import platforms.leetcode.Q242.model.Q242Input;

public class Q242Reference implements Reference<Q242Input, Boolean> {
  @Override
  public Boolean computeExpected(Q242Input in) {
    // 신뢰 레퍼런스: 카운팅
    String s = in.s(), t = in.t();
    if (s == null || t == null) return false;
    if (s.length() != t.length()) return false;

    int[] cnt = new int[256];
    for (int i = 0; i < s.length(); i++) cnt[s.charAt(i)]++;
    for (int i = 0; i < t.length(); i++) {
      if (--cnt[t.charAt(i)] < 0) return false;
    }
    return true;
  }
}