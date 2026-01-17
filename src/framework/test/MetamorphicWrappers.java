package framework.test;

import framework.core.Solution;

public final class MetamorphicWrappers {
  private MetamorphicWrappers() {}

  public static <I, O> Solution<Metamorphic.Input<I>, Metamorphic.Output<O>> twice(Solution<I, O> sol) {
    return new Solution<>() {
      @Override public String name() { return sol.name() + "_twice"; }

      @Override
      public Metamorphic.Output<O> solve(Metamorphic.Input<I> input) {
        O a = sol.solve(input.base());
        O b = sol.solve(input.mutated());
        return new Metamorphic.Output<>(a, b);
      }
    };
  }
}