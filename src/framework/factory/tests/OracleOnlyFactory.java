package framework.factory.tests;

import framework.oracle.Oracle;
import framework.test.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class OracleOnlyFactory {
  private OracleOnlyFactory() {}

  /** oracle-only: no expected; caseOracle handles validation */
  public static <I, O> List<TestCase<I, O>> oracleOnly(
      int count,
      Supplier<I> genInput,
      Oracle<I, O> caseOracle,
      String descPrefix
  ) {
    List<TestCase<I, O>> out = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      I in = genInput.get();
      out.add(TestCase.oracle(in, caseOracle, descPrefix + "#" + i)); // 네 TestCase에 이런 팩토리 있다고 가정
    }
    return out;
  }
}