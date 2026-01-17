package framework.factory.tests;

import framework.core.Solution;
import framework.factory.tests.TestFactories.MetamorphicInput;
import framework.factory.tests.TestFactories.MetamorphicOutput;

public final class MetamorphicWrappers {
  private MetamorphicWrappers() {}

  /** Wrap a normal solution to run twice: once for base, once for mutated. */
  public static <I, O> Solution<MetamorphicInput<I>, MetamorphicOutput<O>> twice(Solution<I, O> sol) {
    return new Solution<>() {
      @Override public String name() { return sol.name() + "_twice"; }

      @Override public MetamorphicOutput<O> solve(MetamorphicInput<I> input) {
        O a = sol.solve(input.base());
        O b = sol.solve(input.mutated());
        return new MetamorphicOutput<>(a, b);
      }
    };
  }
}