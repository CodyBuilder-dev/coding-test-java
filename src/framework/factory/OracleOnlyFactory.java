package framework.factory;

import framework.oracle.Oracle;
import framework.test.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OracleOnlyFactory {
  private OracleOnlyFactory() {}

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