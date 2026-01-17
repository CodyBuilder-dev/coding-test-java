package platforms.programmers.Q76501;

import framework.runner.auto.SingleAutoModule;
import java.util.List;
import platforms.programmers.Q76501.model.SignedSumInput;
import platforms.programmers.Q76501.submission.SubmissionSignedSumSol1;
import platforms.programmers.Q76501.tests.SignedSumSuites;

public final class SignedSumModule implements SingleAutoModule<SignedSumInput, Integer> {
  @Override public String name() { return "programmers/signedsum"; }
  @Override
  public java.util.Map<String, framework.test.TestSuite<SignedSumInput, Integer>> suites() {
    return java.util.Map.of(
        "smoke", SignedSumSuites.smoke(),
        "random", SignedSumSuites.random()
    );
  }
  @Override public List<?> rawSolutions() { return List.of(new SubmissionSignedSumSol1()); }
}