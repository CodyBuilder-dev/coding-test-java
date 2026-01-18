package app;

import static framework.env.EnvInfo.printJvmEnvironment;

import framework.bench.BenchmarkConfig;
import framework.runner.auto.AutoRunner;
import framework.runner.auto.RunOptions;
import java.util.*;
import platforms.leetcode.Q242.AnagramMetamorphicModule;
import platforms.leetcode.Q242.AnagramModule;
import platforms.programmers.Q76501.SignedSumModule;

public class RunAll {
  public static void main(String[] args) {

    printJvmEnvironment();

    BenchmarkConfig cfg = new BenchmarkConfig(
        5,    // warmup
        30,   // runs
        true, // forceGcBetween
        10    // gcSleepMs
    );

    // 예시: programmers만, suite는 smoke만, 이름에 dice 포함된 것만 실행
    RunOptions opt = RunOptions.builder()
//        .includePlatform("leetcode")
//        .includeNameRegex("dice")
//        .includeSuites("smoke")
        .build();

    AutoRunner.runAll(List.of(
        new SignedSumModule(),
        new AnagramModule(),
        new AnagramMetamorphicModule()
    ), cfg, opt);
  }
}