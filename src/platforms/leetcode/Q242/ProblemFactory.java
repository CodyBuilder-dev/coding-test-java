package platforms.leetcode.Q242;

import framework.eq.Equalities;
import framework.judge.Judge;
import framework.core.Problem;
import framework.core.SingleProblem;
import framework.core.Solution;
import framework.core.WrappedSolution;
import framework.judge.NoOraclePolicy;
import java.util.List;

public final class ProblemFactory {
  private ProblemFactory() {}

  public static Problem create() {
    var suite = Q242Suites.smoke();
    var judge = Judge.<Q242Input, Boolean>builder(Equalities.objects())
        .withReference(new Q242Reference())
        .noOraclePolicy(NoOraclePolicy.SKIP)
        .prettyInput(AnagramPrettyPrinters.anagramInput())
        .prettyOutput(String::valueOf)
        .build();

    var s1 = new SubmissionAnagramSol1();
    var s2 = new SubmissionAnagramSol1_1();

    List<Solution<Q242Input, Boolean>> sols = List.of(
        new WrappedSolution<>(s1.getClass().getSimpleName(), in -> s1.isAnagram(in.s(), in.t())),
        new WrappedSolution<>(s2.getClass().getSimpleName(), in -> s2.isAnagram(in.s(), in.t()))
    );

    return new SingleProblem<>("leetcode/anagram", suite, judge, sols);
  }
}
