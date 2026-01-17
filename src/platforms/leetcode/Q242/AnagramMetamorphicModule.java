package platforms.leetcode.Q242;

import framework.core.Solution;
import framework.factory.gen.Generators;
import framework.factory.gen.Rand;
import framework.factory.tests.MetamorphicSuiteTransform;
import framework.test.MetamorphicWrappers;
import framework.oracle.CheckResult;
import framework.runner.auto.ReflectiveAdapter;
import framework.runner.auto.SingleAutoModule;
import framework.test.Metamorphic;
import framework.test.Metamorphic.Input;
import framework.test.Metamorphic.Output;
import framework.test.TestCase;
import framework.test.TestSuite;
import framework.test.TestSuites;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import platforms.leetcode.Q242.model.Q242Input;
import platforms.leetcode.Q242.solutions.SubmissionAnagramSol1;
import platforms.leetcode.Q242.solutions.SubmissionAnagramSol1_1;
import platforms.leetcode.Q242.tests.AnagramFileSuites;
import platforms.leetcode.Q242.tests.AnagramTestFactory;
import platforms.leetcode.Q242.tests.Q242Suites;

public final class AnagramMetamorphicModule implements SingleAutoModule<Metamorphic.Input<Q242Input>, Metamorphic.Output<Boolean>> {

  @Override
  public String name() {
    return "leetcode/Q242/anagram_metamorphic";
  }

  @Override
  public Map<String, TestSuite<Input<Q242Input>, Output<Boolean>>> suites() {

    TestSuite<Q242Input, Boolean> normal = TestSuites.concat("anagram/merged",
        Q242Suites.smoke(), // 네가 손으로 만든 것
        AnagramFileSuites.examples(),
        AnagramTestFactory.createUsingTestFactories()
    );

    Rand rnd = new Rand(1);
    Function<Q242Input, Q242Input> transform = in ->
        new Q242Input(Generators.permuteString(rnd, in.s()), in.t()); // s만 섞기

    Metamorphic.Property<Q242Input, Boolean> prop = (baseIn, mutIn, baseOut, mutOut) -> {
      if (Boolean.TRUE.equals(baseOut) && !Boolean.TRUE.equals(mutOut)) {
        return CheckResult.fail("violated: base true but mutated false");
      }
      return CheckResult.pass();
    };

    // (선택) examples 태그만 meta로 만들기
        Predicate<TestCase<Q242Input, Boolean>> include = null;
//    Predicate<TestCase<Q242Input, Boolean>> include = tc -> tc.tags.contains("examples");

    // 변환
    TestSuite<Metamorphic.Input<Q242Input>, Metamorphic.Output<Boolean>> meta =
        MetamorphicSuiteTransform.fromSuite(
            normal,
            transform,
            prop,
            "/metamorphic",
            include,
            true
        );

    return java.util.Map.of(
        "smoke", meta
    );
  }

  @Override
  public List<Solution<Input<Q242Input>, Output<Boolean>>> rawSolutions() {
    // 1) raw 제출 객체들 (메서드명 제각각이어도 상관 없음)
    List<?> raw = List.of(
        new SubmissionAnagramSol1(),
        new SubmissionAnagramSol1_1()
    );

    // 2) 런타임에 Solution<Input, Boolean>로 통일
    List<Solution<Q242Input, Boolean>> base =
        ReflectiveAdapter.adaptAllSingle(raw, null); // null이면 @SubmitMethod 우선

    // 3) 메타모픽 twice 래핑
    List<Solution<Metamorphic.Input<Q242Input>, Metamorphic.Output<Boolean>>> out = new ArrayList<>();
    for (Solution<Q242Input, Boolean> s : base) out.add(MetamorphicWrappers.twice(s));
    return out;
  }

}