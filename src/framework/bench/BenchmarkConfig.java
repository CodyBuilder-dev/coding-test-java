package framework.bench;

public final class BenchmarkConfig {
  public final int warmup;
  public final int runs;
  public final boolean forceGcBetween;
  public final int gcSleepMs;

  public BenchmarkConfig(int warmup, int runs) {
    this(warmup, runs, true, 20);
  }

  public BenchmarkConfig(int warmup, int runs, boolean forceGcBetween, int gcSleepMs) {
    this.warmup = warmup;
    this.runs = runs;
    this.forceGcBetween = forceGcBetween;
    this.gcSleepMs = gcSleepMs;
  }
}