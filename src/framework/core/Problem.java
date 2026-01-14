package framework.core;

import framework.bench.BenchmarkConfig;

public interface Problem {
  String name();
  void runAll(BenchmarkConfig cfg); // 채점 + 벤치 포함 실행
}