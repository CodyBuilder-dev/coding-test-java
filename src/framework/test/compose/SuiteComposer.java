package framework.test.compose;

import framework.oracle.Oracle;
import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;

public final class SuiteComposer {
  private SuiteComposer() {}

  /** 가장 단순 합치기 */
  public static <I,O> TestSuite<I,O> compose(
      String suiteName,
      List<SuiteContributor<I,O>> parts,
      Oracle<I,O> suiteOracle
  ) {
    List<TestCase<I,O>> all = new ArrayList<>();
    for (SuiteContributor<I,O> p : parts) {
      if (p == null) continue;
      List<TestCase<I,O>> cs = p.contribute();
      if (cs != null) all.addAll(cs);
    }
    return TestSuite.of(suiteName, all, suiteOracle); // 네 생성자에 맞춰 조정
  }
}