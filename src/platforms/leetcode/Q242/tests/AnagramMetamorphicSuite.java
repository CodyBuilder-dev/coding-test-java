package platforms.leetcode.Q242.tests;

import framework.factory.gen.Generators;
import framework.factory.gen.Rand;
import framework.test.Metamorphic;
import framework.test.MetamorphicOracles;
import framework.factory.tests.MetamorphicTestFactory;
import framework.oracle.CheckResult;
import framework.test.TestSuite;
import java.util.Set;
import platforms.leetcode.Q242.model.Q242Input;

public final class AnagramMetamorphicSuite {

  public static TestSuite<Metamorphic.Input<platforms.leetcode.Q242.model.Q242Input>,
      Metamorphic.Output<Boolean>> suite() {

    Rand rnd = new Rand(1);

    var cases = MetamorphicTestFactory.<platforms.leetcode.Q242.model.Q242Input, Boolean>cases(
        200,
        () -> {
          // base generator: 길이 동일하게 만드는 게 메타모픽엔 중요
          // 여기서 "true 케이스 위주"로 만들거나, 랜덤으로 만들 수 있음
          // true 위주 예: t를 s의 permutation으로 생성
          String s = Generators.stringLower(rnd, 1, 40);
          String t = Generators.permuteString(rnd, s); // true 케이스 위주
          return new Q242Input(s, t);
        },
        in -> new platforms.leetcode.Q242.model.Q242Input(
            Generators.permuteString(rnd, in.s()), // s만 섞음
            in.t()
        ),
        "meta_perm_s",
        Set.of("meta")
    );

    Metamorphic.Property<platforms.leetcode.Q242.model.Q242Input, Boolean> prop =
        (baseIn, mutIn, baseOut, mutOut) -> {
          // baseOut이 true면 mutOut도 true여야 함
          if (Boolean.TRUE.equals(baseOut) && !Boolean.TRUE.equals(mutOut)) {
            return CheckResult.fail("metamorphic violated: base true but mutated false");
          }
          return CheckResult.pass();
        };

    return TestSuite.of(
        "anagram/metamorphic",
        cases,
        MetamorphicOracles.fromProperty(prop)
    );
  }
}