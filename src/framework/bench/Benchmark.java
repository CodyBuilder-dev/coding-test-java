package framework.bench;

import framework.test.BatchTestCase;
import framework.test.BatchTestSuite;
import framework.test.TestCase;
import framework.test.TestSuite;
import framework.core.Solution;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Benchmark {

  public static final class Result {
    public final String name;
    public final double avgMs;
    public final double bestMs;
    public final double p50Ms;
    public final double p95Ms;
    public final double approxMemKb;

    Result(String name, double avgMs, double bestMs, double p50Ms, double p95Ms, double approxMemKb) {
      this.name = name;
      this.avgMs = avgMs;
      this.bestMs = bestMs;
      this.p50Ms = p50Ms;
      this.p95Ms = p95Ms;
      this.approxMemKb = approxMemKb;
    }
  }

  private static <I, O> int runSuiteOnce(TestSuite<I, O> suite, Solution<I, O> sol,
      java.util.function.Predicate<TestCase<I,O>> include) {
    int sink = 0;
    for (TestCase<I, O> tc : suite.cases) {
      if (!include.test(tc)) continue;
      O out = sol.solve(tc.input);
      sink ^= stableHash(out);
    }
    return sink;
  }

  // ---------- 일반 suite: p50/p95 포함 ----------
  public static <I, O> List<Result> benchmarkSuiteWithPercentiles(
      TestSuite<I, O> suite,
      List<Solution<I, O>> solutions,
      BenchmarkConfig cfg,
      java.util.function.Predicate<TestCase<I,O>> include
  ) {
    List<Result> results = new ArrayList<>();

    for (Solution<I, O> sol : solutions) {
      for (int i = 0; i < cfg.warmup; i++) runSuiteOnce(suite, sol, include);

      if (cfg.forceGcBetween) forceGc(cfg.gcSleepMs);
      long memBefore = usedMemoryBytes();

      long[] durs = new long[cfg.runs];
      long best = Long.MAX_VALUE;
      long sum = 0;

      for (int i = 0; i < cfg.runs; i++) {
        long t0 = System.nanoTime();
        int sink = runSuiteOnce(suite, sol, include);
        long t1 = System.nanoTime();

        long dt = t1 - t0;
        durs[i] = dt;
        sum += dt;
        if (dt < best) best = dt;
        blackhole(sink);
      }

      if (cfg.forceGcBetween) forceGc(cfg.gcSleepMs);
      long memAfter = usedMemoryBytes();

      double avgMs = (sum / (double) cfg.runs) / 1_000_000.0;
      double bestMs = best / 1_000_000.0;

      double p50Ms = percentileMs(durs, 50);
      double p95Ms = percentileMs(durs, 95);

      double memKb = Math.max(0, memAfter - memBefore) / 1024.0;

      results.add(new Result(sol.name(), avgMs, bestMs, p50Ms, p95Ms, memKb));
    }

    results.sort(Comparator.comparingDouble(r -> r.avgMs));
    return results;
  }

  // ---------- batch suite: p50/p95 포함 ----------
  public static <EI, EO, BI, BO> List<Result> benchmarkBatchSuiteWithPercentiles(
      BatchTestSuite<EI, EO, BI> suite,
      List<Solution<BI, BO>> solutions,
      Function<BO, List<EO>> batchOutputSplitter,
      BenchmarkConfig cfg,
      Predicate<BatchTestCase<EI, EO>> include
  ) {
    Objects.requireNonNull(suite);
    Objects.requireNonNull(solutions);
    Objects.requireNonNull(batchOutputSplitter);
    Objects.requireNonNull(cfg);
    Objects.requireNonNull(include);

    // ✅ 1) 벤치에 포함할 케이스만 필터링 (SKIP 제외)
    List<BatchTestCase<EI, EO>> filteredCases = new ArrayList<>();
    for (BatchTestCase<EI, EO> tc : suite.cases) {
      if (include.test(tc)) filteredCases.add(tc);
    }

    if (filteredCases.isEmpty()) {
      System.out.println("[Benchmark] No benchmarkable batch cases. (all filtered out)");
      return List.of();
    }

    // ✅ 2) 필터된 element input으로 batch input 만들기
    List<EI> elementInputs = new ArrayList<>(filteredCases.size());
    for (BatchTestCase<EI, EO> tc : filteredCases) elementInputs.add(tc.input);

    BI batchInput = suite.batchInputBuilder.apply(elementInputs);

    List<Result> results = new ArrayList<>(solutions.size());

    for (Solution<BI, BO> sol : solutions) {
      // warmup
      for (int i = 0; i < cfg.warmup; i++) {
        runBatchOnce(sol, batchInput, batchOutputSplitter, filteredCases.size());
      }

      if (cfg.forceGcBetween) forceGc(cfg.gcSleepMs);
      long memBefore = usedMemoryBytes();

      long[] durs = new long[cfg.runs];
      long best = Long.MAX_VALUE;
      long sum = 0;

      for (int i = 0; i < cfg.runs; i++) {
        long t0 = System.nanoTime();
        int sink = runBatchOnce(sol, batchInput, batchOutputSplitter, filteredCases.size());
        long t1 = System.nanoTime();

        long dt = t1 - t0;
        durs[i] = dt;
        sum += dt;
        if (dt < best) best = dt;

        blackhole(sink);
      }

      if (cfg.forceGcBetween) forceGc(cfg.gcSleepMs);
      long memAfter = usedMemoryBytes();

      double avgMs = (sum / (double) cfg.runs) / 1_000_000.0;
      double bestMs = best / 1_000_000.0;
      double p50Ms = percentileMs(durs, 50);
      double p95Ms = percentileMs(durs, 95);
      double memKb = Math.max(0, memAfter - memBefore) / 1024.0;

      results.add(new Result(sol.name(), avgMs, bestMs, p50Ms, p95Ms, memKb));
    }

    results.sort(Comparator.comparingDouble(r -> r.avgMs));
    return results;
  }

  // ✅ batch 호출 1회 + sink 계산(필터된 케이스 개수만큼만 사용)
  private static <BI, BO, EO> int runBatchOnce(
      Solution<BI, BO> sol,
      BI batchInput,
      Function<BO, List<EO>> splitter,
      int expectedSize
  ) {
    BO out = sol.solve(batchInput);
    List<EO> elems = splitter.apply(out);

    int sink = 0;
    if (elems != null) {
      int n = Math.min(expectedSize, elems.size());
      for (int i = 0; i < n; i++) sink ^= stableHash(elems.get(i));
      // 만약 출력 길이가 부족하면 sink에 영향이 적어 최적화될 수 있으니,
      // 부족한 경우도 흔적을 남기고 싶다면 아래처럼 섞어줄 수 있음:
      sink ^= (expectedSize * 31 + (elems.size()));
    } else {
      sink ^= expectedSize * 131;
    }
    return sink;
  }

  // ============================
  // 출력
  // ============================

  public static void print(String title, List<Result> results) {
    System.out.println("\n================ Benchmark: " + title + " ================");
    System.out.printf("%-24s | %9s | %9s | %9s | %9s | %12s%n",
        "Solution", "avg", "best", "p50", "p95", "mem(KB)");
    System.out.println("--------------------------------------------------------------------------");
    for (Result r : results) {
      System.out.printf("%-24s | %9.3f | %9.3f | %9.3f | %9.3f | %12.1f%n",
          r.name, r.avgMs, r.bestMs, r.p50Ms, r.p95Ms, r.approxMemKb);
    }
    System.out.println("==========================================================================\n");
  }
  // ============================
  // 유틸 (percentile/메모리/GC/blackhole)
  // ============================

  private static double percentileMs(long[] durs, int p) {
    long[] a = Arrays.copyOf(durs, durs.length);
    Arrays.sort(a);
    // “nearest-rank” 방식(간단하고 일관됨)
    int n = a.length;
    int idx = (int) Math.ceil((p / 100.0) * n) - 1;
    idx = Math.max(0, Math.min(n - 1, idx));
    return a[idx] / 1_000_000.0;
  }

  private static long usedMemoryBytes() {
    Runtime rt = Runtime.getRuntime();
    return rt.totalMemory() - rt.freeMemory();
  }

  private static void forceGc(int sleepMs) {
    for (int i = 0; i < 3; i++) {
      System.gc();
      if (sleepMs > 0) {
        try { Thread.sleep(sleepMs); } catch (InterruptedException ignored) {}
      }
    }
  }

  private static volatile int SINK = 0;
  private static void blackhole(int v) { SINK ^= v; }

  private static int stableHash(Object o) {
    if (o == null) return 0;
    if (o instanceof int[] a) return Arrays.hashCode(a);
    if (o instanceof long[] a) return Arrays.hashCode(a);
    if (o instanceof byte[] a) return Arrays.hashCode(a);
    if (o instanceof char[] a) return Arrays.hashCode(a);
    if (o instanceof Object[] a) return Arrays.deepHashCode(a);
    return o.hashCode();
  }
}