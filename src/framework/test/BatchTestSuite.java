package framework.test;

import framework.oracle.Oracle;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class BatchTestSuite<EI, EO, BI> {
  public final String name;
  public final List<BatchTestCase<EI, EO>> cases;
  public final Function<List<EI>, BI> batchInputBuilder;
  public final Oracle<EI, EO> suiteOracle; // nullable

  private BatchTestSuite(String name,
      List<BatchTestCase<EI, EO>> cases,
      Function<List<EI>, BI> batchInputBuilder,
      Oracle<EI, EO> suiteOracle) {
    this.name = Objects.requireNonNull(name);
    this.cases = List.copyOf(Objects.requireNonNull(cases));
    this.batchInputBuilder = Objects.requireNonNull(batchInputBuilder);
    this.suiteOracle = suiteOracle;
  }

  public static <EI, EO, BI> BatchTestSuite<EI, EO, BI> of(
      String name, List<BatchTestCase<EI, EO>> cases, Function<List<EI>, BI> batchInputBuilder
  ) {
    return new BatchTestSuite<>(name, cases, batchInputBuilder, null);
  }

  public static <EI, EO, BI> BatchTestSuite<EI, EO, BI> of(
      String name, List<BatchTestCase<EI, EO>> cases, Function<List<EI>, BI> batchInputBuilder, Oracle<EI, EO> suiteOracle
  ) {
    return new BatchTestSuite<>(name, cases, batchInputBuilder, suiteOracle);
  }
}