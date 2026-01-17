package platforms.leetcode.Q242.tests;

import framework.oracle.CheckResult;
import framework.oracle.Oracles;
import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.List;
import platforms.leetcode.Q242.model.Q242Input;

public class Q242Suites {
  public static TestSuite<Q242Input, Boolean> smoke() {
    return TestSuite.of("leetcode/anagram smoke", List.of(
        TestCase.expect(new Q242Input("anagram", "nagaram"),true, "basic true", "basic"),
        TestCase.expect(new Q242Input("rat", "car"), false,"basic false", "basic"),
        TestCase.expect(new Q242Input("", ""), true,"empty true", "edge"),
        TestCase.expect(new Q242Input("a", "a"), true,"single true", "edge"),
        TestCase.expect(new Q242Input("ab", "ba"), true,"swap true", "edge"),
        TestCase.expect(new Q242Input("ab", "abc"), false, "length mismatch false", "edge")
        ),
        Oracles.all(
            Oracles.notNull(),
            (Q242Input in, Boolean out) -> (in.s().length() != in.t().length() && Boolean.TRUE.equals(out))
                ? CheckResult.fail("length mismatch but returned true")
                : CheckResult.pass()
        )
    );
  }
}
