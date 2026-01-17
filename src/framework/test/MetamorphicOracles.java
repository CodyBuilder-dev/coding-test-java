package framework.test;

import framework.test.Metamorphic.Input;
import framework.test.Metamorphic.Output;
import framework.oracle.CheckResult;
import framework.oracle.Oracle;

public final class MetamorphicOracles {
  private MetamorphicOracles() {}

  public static <I, O> Oracle<Input<I>, Output<O>> fromProperty(
      Metamorphic.Property<I, O> prop
  ) {
    return (in, out) -> {
      if (out == null) return CheckResult.fail("output null");
      return prop.check(in.base(), in.mutated(), out.baseOut(), out.mutOut());
    };
  }
}