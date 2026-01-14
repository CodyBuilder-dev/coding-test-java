package framework.eq;

public interface Equality<O> {
  boolean same(O expected, O actual);

  default String explainMismatch(O expected, O actual) {
    return "";
  }
}