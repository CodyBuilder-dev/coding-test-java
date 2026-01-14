package framework.oracle;

public interface Reference<I, O> {
  O computeExpected(I input);
}
