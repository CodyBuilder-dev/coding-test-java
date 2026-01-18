package framework.runner.auto;

import framework.bench.Benchmark;
import framework.bench.BenchmarkConfig;
import framework.judge.BatchJudge;
import framework.judge.Judge;
import framework.judge.NoOraclePolicy;
import framework.oracle.Reference;
import framework.pretty.PrettyPrinter;
import framework.test.BatchTestSuite;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class AutoRunner {
  private AutoRunner() {}

  public static void runAll(List<? extends AutoModule> modules, BenchmarkConfig cfg, RunOptions opt) {
    Objects.requireNonNull(modules);
    for (AutoModule m : modules) {
      String moduleName = m.name();
      String modSkipReason = (opt == null) ? null : opt.explainSkipModule(m);
      if (modSkipReason != null) {
        if (opt.verbosity != RunOptions.Verbosity.QUIET) {
          RunLog.skip("MODULE", moduleName, modSkipReason);
        }
        continue;
      }

      if (opt == null || opt.verbosity != RunOptions.Verbosity.QUIET) {
        RunLog.run("MODULE", moduleName);
      }

      if (m instanceof SingleAutoModule<?, ?> sm) runSingle(sm, cfg, opt);
      else if (m instanceof BatchAutoModule<?, ?, ?, ?> bm) runBatch(bm, cfg, opt);
      else throw new IllegalArgumentException("unknown module: " + m.getClass());
      System.out.println();
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <I, O> void runSingle(SingleAutoModule<I, O> module, BenchmarkConfig cfg, RunOptions opt) {
    System.out.println("\n\n================ PROBLEM: " + module.name() + " ================");

    // suite oracle override: module.suiteOracle()가 있으면 suite에 합쳐서 넣고 싶을 때
    Map<String, TestSuite<I, O>> suites = module.suites();
    // 자동 파일테스트케이스 추가
    suites = AutoSuiteAugmenter.augmentWithAutoExamples(module, suites);

    if (suites == null || suites.isEmpty()) {
      throw new IllegalStateException(module.name() + ": no suites provided (override suite() or suites())");
    }

    List<framework.core.Solution<I, O>> sols = new ArrayList<>();
    for (Object raw : module.rawSolutions()) {
      sols.add(ReflectiveAdapter.adaptOrPassSingle(raw, module.methodName()));
    }

    // defaults
    PrettyPrinter<I> inP = DefaultPretty.defaultPrinter();
    PrettyPrinter<O> outP = DefaultPretty.defaultPrinter();
    // optional reference
    Reference<I, O> ref = module.reference();

    for (var entry : suites.entrySet()) {
      String suiteTag = entry.getKey();

      String suiteName = entry.getValue().name; // 또는 moduleName + "/" + suiteKey
      String reason = (opt == null) ? null : opt.explainSkipSuite(suiteTag);

      if (reason != null) {
        if (opt.verbosity != RunOptions.Verbosity.QUIET) {
          RunLog.skip("SUITE", suiteName, reason);
        }
        continue;
      }

      if (opt == null || opt.verbosity != RunOptions.Verbosity.QUIET) {
        RunLog.run("SUITE", suiteName);
      }

      final TestSuite<I, O> suite;
      // optional module-wide suiteOracle injection if suite has none
      if (module.suiteOracle() != null && entry.getValue().suiteOracle == null) {
        suite = TestSuite.of(entry.getValue().name, entry.getValue().cases, module.suiteOracle());
      } else {
        suite = entry.getValue();
      }

      // infer output class for default equality
      Class<?> outClass = inferOutputClass(suite);
      var eq = DefaultEquality.<O>forOutputClass(outClass);

      Judge<I, O> judge = (ref == null)
          ? Judge.<I, O>builder(eq)
          .prettyInput(inP).prettyOutput(outP)
          .noOraclePolicy(NoOraclePolicy.SKIP)
          .build()
          : Judge.<I, O>builder(eq)
              .withReference(ref)
              .prettyInput(inP).prettyOutput(outP)
              .noOraclePolicy(NoOraclePolicy.SKIP)
              .build();

      System.out.println("\n--- SUITE: " + suiteTag + " ---");
      judge.run(suite, sols);

      // benchmark: skip-exclude predicate (expected/ref/oracle/suiteOracle)
      var bench = Benchmark.benchmarkSuiteWithPercentiles(
          suite, sols, cfg,
          tc -> (tc.expected != null)
              || (ref != null)
              || (tc.caseOracle != null)
              || (suite.suiteOracle != null)
      );
      Benchmark.print(module.name() + " [" + suiteTag + "]", bench);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <EI, EO, BI, BO> void runBatch(BatchAutoModule<EI, EO, BI, BO> module,
      BenchmarkConfig cfg, RunOptions opt) {
    System.out.println("\n\n================ PROBLEM: " + module.name() + " ================");

    Map<String, BatchTestSuite<EI, EO, BI>> suites = module.suites();

    // ✅ 여기 추가: AutoBatchExamples를 구현한 모듈이면 examples suite 자동 추가
    suites = AutoSuiteAugmenter.augmentWithAutoBatchExamples(module, suites);

    if (suites == null || suites.isEmpty()) {
      throw new IllegalStateException(module.name() + ": no suites provided (override suite() or suites())");
    }

    List<framework.core.Solution<BI, BO>> sols = new ArrayList<>();
    for (Object raw : module.rawSolutions()) {
      sols.add(ReflectiveAdapter.adaptOrPassBatch(raw, module.methodName()));
    }

    Function<BO, List<EO>> splitter = module.splitter();

    PrettyPrinter<EI> inP = DefaultPretty.defaultPrinter();
    PrettyPrinter<EO> outP = DefaultPretty.defaultPrinter();
    Reference<EI, EO> ref = module.reference();

    for (var entry : suites.entrySet()) {
      String suiteTag = entry.getKey();
      if (!opt.shouldRunSuite(suiteTag)) continue;

      BatchTestSuite<EI, EO, BI> suite;
      if (module.suiteOracle() != null && entry.getValue().suiteOracle == null) {
        suite = BatchTestSuite.of(entry.getValue().name, entry.getValue().cases, entry.getValue().batchInputBuilder, module.suiteOracle());
      } else {
        suite = entry.getValue();
      }

      Class<?> outClass = inferElementOutputClass(suite);
      var eq = DefaultEquality.<EO>forOutputClass(outClass);

      BatchJudge<EI, EO, BI, BO> judge = (ref == null)
          ? BatchJudge.<EI, EO, BI, BO>builder(eq, splitter)
          .prettyInput(inP).prettyOutput(outP)
          .noOraclePolicy(NoOraclePolicy.SKIP)
          .build()
          : BatchJudge.<EI, EO, BI, BO>builder(eq, splitter)
              .withReference(ref)
              .prettyInput(inP).prettyOutput(outP)
              .noOraclePolicy(NoOraclePolicy.SKIP)
              .build();

      System.out.println("\n--- SUITE: " + suiteTag + " ---");
      judge.run(suite, sols);

      var bench = Benchmark.benchmarkBatchSuiteWithPercentiles(
          suite, sols, splitter, cfg,
          tc -> (tc.expected != null)
              || (ref != null)
              || (tc.caseOracle != null)
              || (suite.suiteOracle != null)
      );
      Benchmark.print(module.name() + " [" + suiteTag + "] (batch)", bench);
    }
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