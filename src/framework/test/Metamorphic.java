package framework.test;

import framework.oracle.CheckResult;

public final class Metamorphic {
  private Metamorphic() {}

  public record Input<I>(I base, I mutated) {}
  public record Output<O>(O baseOut, O mutOut) {}

  @FunctionalInterface
  public interface Property<I, O> {
    CheckResult check(I baseIn, I mutIn, O baseOut, O mutOut);
  }
}