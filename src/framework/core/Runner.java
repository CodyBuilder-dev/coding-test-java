package framework.core;

@FunctionalInterface
public interface Runner<I, O> {
  O run(I input);
}