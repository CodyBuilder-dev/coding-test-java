package framework.core;

import framework.bench.Benchmark;
import framework.bench.BenchmarkConfig;
import framework.judge.Judge;
import framework.test.TestSuite;
import java.util.List;
import java.util.Objects;

public final class SingleProblem<I, O> implements Problem {
  private final String name;
  private final TestSuite<I, O> suite;
  private final Judge<I, O> judge;
  private final List<framework.core.Solution<I, O>> solutions;

  public SingleProblem(String name,
      TestSuite<I, O> suite,
      Judge<I, O> judge,
      List<framework.core.Solution<I, O>> solutions) {
    this.name = Objects.requireNonNull(name);
    this.suite = Objects.requireNonNull(suite);
    this.judge = Objects.requireNonNull(judge);
    this.solutions = List.copyOf(solutions);
  }

  @Override public String name() { return name; }

  @Override
  public void runAll(BenchmarkConfig cfg) {
    System.out.println("\n\n================ PROBLEM: " + name + " ================");

    // 채점
    judge.run(suite, solutions);

    // 벤치: SKIP 케이스 제외(include predicate를 judge에서 가져옴)
    var res = Benchmark.benchmarkSuiteWithPercentiles(
        suite,
        solutions,
        cfg,
        tc -> judge.isBenchmarkable(tc, suite) || suite.suiteOracle != null
    );
    Benchmark.print(name, res);
  }
}
