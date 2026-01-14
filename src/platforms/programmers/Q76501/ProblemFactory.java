package platforms.programmers.Q76501;

import framework.eq.Equalities;
import framework.judge.Judge;
import framework.core.Problem;
import framework.core.SingleProblem;
import framework.core.Solution;
import framework.core.WrappedSolution;
import framework.judge.NoOraclePolicy;
import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.ArrayList;
import java.util.List;

public final class ProblemFactory {
  private ProblemFactory() {}

  public static Problem create() {
    var smoke = SignedSumSuites.smoke(); // smoke는 expected 수동/또는 그대로
    var random = new SignedSumTestFactory(100, 1000).create(7L, 500);

    List<TestCase<SignedSumInput, Integer>> all = new ArrayList<>();
    all.addAll(smoke.cases);
    all.addAll(random.cases);

    var suite = TestSuite.of("programmers/signedsum combined", all);

    var judge = Judge.<SignedSumInput,Integer>builder(Equalities.objects())
        .noOraclePolicy(NoOraclePolicy.FAIL)
        .build();

    var s1 = new SubmissionSignedSumSol1();
    var s2 = new SubmissionSignedSumSol1_1();

    List<Solution<SignedSumInput, Integer>> sols = List.of(
        new WrappedSolution<>(s1.getClass().getSimpleName(), in -> s1.solution(in.absolutes(), in.signs())),
        new WrappedSolution<>(s2.getClass().getSimpleName(), in -> s2.solution(in.absolutes(), in.signs()))
    );

    return new SingleProblem<SignedSumInput,Integer>("programmers/signedsum", suite, judge, sols);
  }
}
