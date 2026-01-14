package app;

import framework.bench.BenchmarkConfig;
import framework.core.Problem;
import framework.env.EnvInfo;
import java.util.*;

public class RunAll {
  public static void main(String[] args) {
    EnvInfo.printJvmEnvironment();

    BenchmarkConfig cfg = new BenchmarkConfig(
        5,   // warmup
        30,  // runs
        true,
        20
    );

    List<Problem> problems = List.of(
        platforms.leetcode.Q242.ProblemFactory.create(),
        platforms.programmers.Q76501.ProblemFactory.create()
//        platforms.programmers.dice.ProblemFactory.create()
    );

    for (Problem p : problems) {
      p.runAll(cfg);
    }

  }
}