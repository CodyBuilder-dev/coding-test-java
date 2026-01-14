package framework.oracle;

@FunctionalInterface
public interface Oracle<I, O> {
  CheckResult check(I input, O output);
}