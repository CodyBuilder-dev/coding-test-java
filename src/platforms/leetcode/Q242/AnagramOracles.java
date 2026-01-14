package platforms.leetcode.Q242;

import framework.oracle.CheckResult;
import framework.oracle.Oracle;

public final class AnagramOracles {
  private AnagramOracles() {}

  public static Oracle<Q242Input, Boolean> basicProperties() {
    return (in, out) -> {
      if (out == null) return CheckResult.fail("output is null");

      String s = in.s();
      String t = in.t();
      if (s == null || t == null) return CheckResult.pass(); // 플랫폼이 null 안 준다면 제거 가능

      // property: length mismatch -> must be false
      if (s.length() != t.length() && out) {
        return CheckResult.fail("length mismatch but returned true");
      }

      // property: reflexive
      // (이 케이스 자체가 (s,s)로 들어온 경우만 강제)
      if (s.equals(t) && !out) {
        return CheckResult.fail("s==t but returned false");
      }

      return CheckResult.pass();
    };
  }
}