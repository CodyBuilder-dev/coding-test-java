package framework.factory.tests;

import framework.test.Metamorphic;
import framework.test.Metamorphic.Input;
import framework.test.Metamorphic.Output;
import framework.test.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class MetamorphicTestFactory {
  private MetamorphicTestFactory() {}

  /**
   * Metamorphic test factory (property-based).
   *
   * You provide:
   * - base input generator
   * - transform: input -> mutated input
   * - property: (baseInput, mutatedInput, baseOutput, mutatedOutput) -> oracle check
   *
   * This does NOT compute expected; it checks a relation between outputs.
   * Typically used with suite-level oracle to avoid per-case oracle, but per-case works too.
   */
  public static <I, O> List<TestCase<Input<I>, Output<O>>> cases(
      int count,
      Supplier<I> baseGen,
      Function<I, I> transform,
      String descPrefix,
      Set<String> tags
  ) {
    List<TestCase<Metamorphic.Input<I>, Metamorphic.Output<O>>> out = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      I base = baseGen.get();
      I mut = transform.apply(base);
      var in = new Metamorphic.Input<>(base, mut);

      // expected=null, caseOracle=null (suiteOracle로 검증할 거라)
      out.add(TestCase.empty(in,descPrefix + "#" + i, tags));
    }
    return out;
  }
}
