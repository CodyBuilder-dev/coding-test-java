package platforms.leetcode.Q242.tests;

import framework.io.PairedListFileSuiteLoader;

import framework.test.TestSuite;
import java.nio.file.Path;
import platforms.leetcode.Q242.model.Q242Input;

public final class AnagramFileSuites {
  private AnagramFileSuites() {}

  public static TestSuite<Q242Input, Boolean> examples() {
    Path dir = Path.of("src/platforms/leetcode/Q242/tests/examples");
    return PairedListFileSuiteLoader.load(
        "anagram/examples_files",
        dir.resolve("inputs.txt"),
        dir.resolve("outputs.txt"),
        Q242Input.class,
        Boolean.class
    );
  }
}
