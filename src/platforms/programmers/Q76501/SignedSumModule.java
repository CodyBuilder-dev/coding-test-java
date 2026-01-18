package platforms.programmers.Q76501;

import framework.io.ExampleSuites;
import framework.runner.auto.AutoExamples;
import framework.runner.auto.SingleAutoModule;
import framework.test.TestSuite;
import java.util.List;
import platforms.programmers.Q76501.model.SignedSumInput;
import platforms.programmers.Q76501.submission.SubmissionSignedSumSol1;

@AutoExamples(input = SignedSumInput.class, output = Integer.class)
public final class SignedSumModule implements SingleAutoModule<SignedSumInput, Integer> {
  @Override public String name() { return "programmers/signedsum"; }
  @Override
  public java.util.Map<String, framework.test.TestSuite<SignedSumInput, Integer>> suites() {
    return java.util.Map.of();
  }
  @Override public List<?> rawSolutions() { return List.of(new SubmissionSignedSumSol1()); }
}