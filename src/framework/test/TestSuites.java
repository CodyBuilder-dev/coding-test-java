package framework.test;

import java.util.*;

public final class TestSuites {
  private TestSuites() {}

  public static <I, O> TestSuite<I, O> concat(String name, TestSuite<I, O>... suites) {
    List<TestCase<I, O>> all = new ArrayList<>();
    for (TestSuite<I, O> s : suites) {
      if (s == null) continue;
      all.addAll(s.cases);
    }
    // suiteOracle는 “합친 뒤”엔 모호해질 수 있어서, 기본은 null로 두는 걸 추천
    return TestSuite.of(name, all);
  }
}
