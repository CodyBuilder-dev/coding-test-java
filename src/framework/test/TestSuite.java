package framework.test;

import framework.oracle.Oracle;
import java.util.List;
import java.util.Objects;

public final class TestSuite<I, O> {
  public final String name;
  public final List<TestCase<I, O>> cases;
  public final Oracle<I, O> suiteOracle; // nullable

  private TestSuite(String name, List<TestCase<I, O>> cases, Oracle<I, O> suiteOracle) {
    this.name = Objects.requireNonNull(name);
    this.cases = List.copyOf(Objects.requireNonNull(cases));
    this.suiteOracle = suiteOracle;
  }

  public static <I, O> TestSuite<I, O> of(String name, List<TestCase<I, O>> cases) {
    return new TestSuite<>(name, cases, null);
  }

  public static <I, O> TestSuite<I, O> of(String name, List<TestCase<I, O>> cases, Oracle<I, O> suiteOracle) {
    return new TestSuite<>(name, cases, suiteOracle);
  }
}