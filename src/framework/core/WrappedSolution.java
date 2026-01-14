package framework.core;

import java.util.Objects;

public final class WrappedSolution<I, O> implements Solution<I, O> {
  private final String name;
  private final Runner<I, O> runner;

  public WrappedSolution(String name, Runner<I, O> runner) {
    this.name = Objects.requireNonNull(name);
    this.runner = Objects.requireNonNull(runner);
  }

  @Override public O solve(I input) { return runner.run(input); }
  @Override public String name() { return name; }
}