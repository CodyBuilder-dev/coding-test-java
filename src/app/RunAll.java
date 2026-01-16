package app;

import static framework.env.EnvInfo.printJvmEnvironment;

import framework.bench.BenchmarkConfig;
import framework.runner.auto.AutoRunner;
import java.util.*;
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

    AutoRunner.runAll(List.of(
        new SignedSumModule(),
        new AnagramModule()
    ), cfg);
  }
}