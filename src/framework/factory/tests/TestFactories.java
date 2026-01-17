package framework.factory.tests;

import framework.oracle.Oracle;
import framework.test.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TestFactories {
  private TestFactories() {}

  /** input generator + expected function */
  public static <I, O> List<TestCase<I, O>> withExpected(
      int count,
      Supplier<I> genInput,
      Function<I, O> expectedFn,
      String descPrefix
  ) {
    List<TestCase<I, O>> out = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      I in = genInput.get();
      O exp = expectedFn.apply(in);
      out.add(TestCase.expect(in, exp, descPrefix + "#" + i));
    }
    return out;
  }

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
      out.add(TestCase.oracle(in, caseOracle, descPrefix + "#" + i));
    }
    return out;
  }

  // ----- helper types for metamorphic -----

  public record MetamorphicInput<I>(I base, I mutated) {}

  public record MetamorphicOutput<O>(O baseOut, O mutOut) {}

  @FunctionalInterface
  public interface MetamorphicProperty<I, O> {
    framework.oracle.CheckResult check(I baseIn, I mutIn, O baseOut, O mutOut);
  }

  /** input generator + manual expected (e.g., you provide list of pairs) */
  public static <I, O> List<TestCase<I, O>> fromPairs(
      List<Map.Entry<I, O>> pairs,
      String descPrefix
  ) {
    List<TestCase<I, O>> out = new ArrayList<>(pairs.size());
    for (int i = 0; i < pairs.size(); i++) {
      var p = pairs.get(i);
      out.add(TestCase.expect(p.getKey(), p.getValue(), descPrefix + "#" + i));
    }
    return out;
  }
}