package platforms.leetcode.Q242;

import framework.runner.auto.SingleAutoModule;
import framework.test.TestSuite;
import java.util.List;

public final class AnagramModule implements SingleAutoModule<Q242Input, Boolean> {
  @Override public String name() { return "leetcode/anagram"; }
  @Override public TestSuite<Q242Input, Boolean> suite() { return Q242Suites.smoke(); }
  @Override public List<?> rawSolutions() { return List.of(new SubmissionAnagramSol1()); }
}