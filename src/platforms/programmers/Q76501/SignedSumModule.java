package platforms.programmers.Q76501;

import framework.runner.auto.SingleAutoModule;
import framework.test.TestSuite;
import java.util.List;

public final class SignedSumModule implements SingleAutoModule<SignedSumInput, Integer> {
  @Override public String name() { return "programmers/signedsum"; }
  @Override public TestSuite<SignedSumInput, Integer> suite() { return SignedSumSuites.smoke(); }
  @Override public List<?> rawSolutions() { return List.of(new SubmissionSignedSumSol1()); }
}