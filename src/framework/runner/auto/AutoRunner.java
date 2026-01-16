package framework.runner.auto;

import framework.bench.Benchmark;
import framework.bench.BenchmarkConfig;
import framework.core.Solution;
import framework.judge.BatchJudge;
import framework.judge.Judge;
import framework.judge.NoOraclePolicy;
import framework.oracle.Reference;
import framework.pretty.PrettyPrinter;
import framework.test.BatchTestSuite;
import framework.test.TestSuite;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class AutoRunner {
  private AutoRunner() {}

  public static void runAll(List<? extends AutoModule> modules, BenchmarkConfig cfg) {
    Objects.requireNonNull(modules);
    for (AutoModule m : modules) {
      if (m instanceof SingleAutoModule<?, ?> sm) runSingle(sm, cfg);
      else if (m instanceof BatchAutoModule<?, ?, ?, ?> bm) runBatch(bm, cfg);
      else throw new IllegalArgumentException("unknown module: " + m.getClass());
      System.out.println();
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <I, O> void runSingle(SingleAutoModule<I, O> module, BenchmarkConfig cfg) {
    System.out.println("\n\n================ PROBLEM: " + module.name() + " ================");

    // suite oracle override: module.suiteOracle()가 있으면 suite에 합쳐서 넣고 싶을 때
    final TestSuite<I, O> suite;
    if (module.suiteOracle() != null && module.suite().suiteOracle == null) {
      suite = TestSuite.of(module.suite().name, module.suite().cases, module.suiteOracle());
    } else {
      suite = module.suite();
    }

    List<framework.core.Solution<I, O>> sols = new ArrayList<>();
    for (Object raw : module.rawSolutions()) {
      sols.add(ReflectiveAdapter.adaptSingle(raw, module.methodName()));
    }

    // defaults
    PrettyPrinter<I> inP = DefaultPretty.defaultPrinter();
    PrettyPrinter<O> outP = DefaultPretty.defaultPrinter();

    // output class hint: try to infer from first expected if present
    Class<?> outClass = inferOutputClass(suite);
    var eq = DefaultEquality.<O>forOutputClass(outClass);

    // optional reference
    Reference<I, O> ref = module.reference();

    Judge<I, O> judge = Judge.<I, O>builder(eq)
        .prettyInput(inP)
        .prettyOutput(outP)
        .noOraclePolicy(NoOraclePolicy.SKIP)
        .build();

    // reference 넣을 수 있으면 넣기
    if (ref != null) {
      judge = Judge.<I, O>builder(eq)
          .withReference(ref)
          .prettyInput(inP)
          .prettyOutput(outP)
          .noOraclePolicy(NoOraclePolicy.SKIP)
          .build();
    }

    // 1) correctness
    judge.run(suite, sols);

    // 2) benchmark (SKIP 제외)
    var bench = Benchmark.benchmarkSuiteWithPercentiles(
        suite, sols, cfg,
        tc -> {
          if (tc.expected != null) return true;
          if (ref != null) return true;
          if (tc.caseOracle != null) return true;
          return suite.suiteOracle != null;
        }
    );
    Benchmark.print(module.name(), bench);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <EI, EO, BI, BO> void runBatch(BatchAutoModule<EI, EO, BI, BO> module, BenchmarkConfig cfg) {
    System.out.println("\n\n================ PROBLEM: " + module.name() + " ================");

    final BatchTestSuite<EI, EO, BI> suite;
    if (module.suiteOracle() != null && module.suite().suiteOracle == null) {
      suite = BatchTestSuite.of(module.suite().name, module.suite().cases, module.suite().batchInputBuilder, module.suiteOracle());
    } else {
      suite = module.suite();
    }

    List<framework.core.Solution<BI, BO>> sols = new ArrayList<>();
    for (Object raw : module.rawSolutions()) {
      sols.add(ReflectiveAdapter.adaptBatch(raw, module.methodName()));
    }

    Function<BO, List<EO>> splitter = module.splitter();

    PrettyPrinter<EI> inP = DefaultPretty.defaultPrinter();
    PrettyPrinter<EO> outP = DefaultPretty.defaultPrinter();

    Class<?> outClass = inferElementOutputClass(suite);
    var eq = DefaultEquality.<EO>forOutputClass(outClass);

    Reference<EI, EO> ref = module.reference();

    BatchJudge<EI, EO, BI, BO> judge =
        BatchJudge.<EI, EO, BI, BO>builder(eq, splitter)
            .prettyInput(inP)
            .prettyOutput(outP)
            .noOraclePolicy(NoOraclePolicy.SKIP)
            .build();

    if (ref != null) {
      judge =
          BatchJudge.<EI, EO, BI, BO>builder(eq, splitter)
              .withReference(ref)
              .prettyInput(inP)
              .prettyOutput(outP)
              .noOraclePolicy(NoOraclePolicy.SKIP)
              .build();
    }

    judge.run(suite, sols);

    var bench = Benchmark.benchmarkBatchSuiteWithPercentiles(
        suite, sols, splitter, cfg,
        tc -> {
          if (tc.expected != null) return true;
          if (ref != null) return true;
          if (tc.caseOracle != null) return true;
          return suite.suiteOracle != null;
        }
    );
    Benchmark.print(module.name() + " (batch)", bench);
  }

  private static <I, O> Class<?> inferOutputClass(TestSuite<I, O> suite) {
    for (var tc : suite.cases) {
      if (tc.expected != null) return tc.expected.getClass();
    }
    return Object.class;
  }

  private static <EI, EO, BI> Class<?> inferElementOutputClass(BatchTestSuite<EI, EO, BI> suite) {
    for (var tc : suite.cases) {
      if (tc.expected != null) return tc.expected.getClass();
    }
    return Object.class;
  }
}