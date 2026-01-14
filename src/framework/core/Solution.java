package framework.core;

public interface Solution<I, O> {
  O solve(I input);
  default String name() { return getClass().getSimpleName(); }
}