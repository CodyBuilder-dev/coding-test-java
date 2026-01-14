package framework.core;

import framework.judge.BatchJudge;
import framework.test.BatchTestSuite;
import framework.bench.Benchmark;
import framework.bench.BenchmarkConfig;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class BatchProblem<EI, EO, BI, BO> implements Problem {
  private final String name;
  private final BatchTestSuite<EI, EO, BI> suite;
  private final BatchJudge<EI, EO, BI, BO> judge;
  private final List<Solution<BI, BO>> solutions;
  private final Function<BO, List<EO>> splitter;

  public BatchProblem(String name,
      BatchTestSuite<EI, EO, BI> suite,
      BatchJudge<EI, EO, BI, BO> judge,
      List<Solution<BI, BO>> solutions,
      Function<BO, List<EO>> splitter) {
    this.name = Objects.requireNonNull(name);
    this.suite = Objects.requireNonNull(suite);
    this.judge = Objects.requireNonNull(judge);
    this.solutions = List.copyOf(solutions);
    this.splitter = Objects.requireNonNull(splitter);
  }

  @Override public String name() { return name; }

  @Override
  public void runAll(BenchmarkConfig cfg) {
    System.out.println("\n\n================ PROBLEM: " + name + " ================");

    judge.run(suite, solutions);

    var res = Benchmark.benchmarkBatchSuiteWithPercentiles(
        suite,
        solutions,
        splitter,
        cfg,
        tc -> judge.isBenchmarkable(tc, suite) || suite.suiteOracle != null
    );
    Benchmark.print(name + " (batch)", res);
  }
}