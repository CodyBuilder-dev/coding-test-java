package framework.factory;

import framework.core.Solution;
import framework.oracle.Oracle;
import framework.test.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class DifferentialFactory {
  private DifferentialFactory() {}

  public static <I, O> List<TestCase<I, O>> fromReferenceSolution(
      int count,
      Supplier<I> genInput,
      Solution<I, O> referenceSolution,
      String descPrefix
  ) {
    List<TestCase<I, O>> out = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      I in = genInput.get();
      O exp = referenceSolution.solve(in);
      out.add(TestCase.expect(in, exp, descPrefix + "#" + i));
    }
    return out;
  }
}