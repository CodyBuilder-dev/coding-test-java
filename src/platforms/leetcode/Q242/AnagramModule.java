package platforms.leetcode.Q242;

import framework.runner.auto.SingleAutoModule;
import framework.test.TestSuite;
import framework.test.TestSuites;
import java.util.List;
import java.util.Map;
import platforms.leetcode.Q242.model.Q242Input;
import platforms.leetcode.Q242.solutions.SubmissionAnagramSol1;
import platforms.leetcode.Q242.tests.AnagramFileSuites;
import platforms.leetcode.Q242.tests.Q242Suites;

public final class AnagramModule implements SingleAutoModule<Q242Input, Boolean> {
  @Override public String name() { return "leetcode/anagram"; }
  @Override public TestSuite<Q242Input, Boolean> suite() { return Q242Suites.smoke(); }
  @Override
  public Map<String, TestSuite<Q242Input, Boolean>> suites() {
    var manual = Q242Suites.smoke(); // 네가 손으로 만든 것

    var merged = TestSuites.concat("anagram/merged", manual, AnagramFileSuites.examples()
    );

    return java.util.Map.of(
        "smoke", merged
        // "random", AnagramSuites.random() 등 추가 가능
    );
  }
  @Override public List<?> rawSolutions() { return List.of(new SubmissionAnagramSol1()); }
}