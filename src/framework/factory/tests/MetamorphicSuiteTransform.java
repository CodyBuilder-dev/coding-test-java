package framework.factory.tests;

import framework.oracle.Oracle;
import framework.test.Metamorphic;
import framework.test.MetamorphicOracles;
import framework.test.TestCase;
import framework.test.TestSuite;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MetamorphicSuiteTransform {
  private MetamorphicSuiteTransform() {}

  /** 기본: 모든 케이스를 meta로 변환 */
  public static <I,O> TestSuite<Metamorphic.Input<I>, Metamorphic.Output<O>> fromSuite(
      TestSuite<I,O> baseSuite,
      Function<I,I> transform,
      Metamorphic.Property<I,O> property,
      String newNameSuffix
  ) {
    return fromSuite(baseSuite, transform, property, newNameSuffix, tc -> true, false);
  }

  /**
   * @param include 어떤 케이스를 meta로 포함할지(예: tags로 필터)
   * @param keepDescPrefix base case desc를 meta desc에 포함할지
   */
  public static <I,O> TestSuite<Metamorphic.Input<I>, Metamorphic.Output<O>> fromSuite(
      TestSuite<I,O> baseSuite,
      Function<I,I> transform,
      Metamorphic.Property<I,O> property,
      String newNameSuffix,
      Predicate<TestCase<I,O>> include,
      boolean keepDescPrefix
  ) {
    Objects.requireNonNull(baseSuite);
    Objects.requireNonNull(transform);
    Objects.requireNonNull(property);
    if (include == null) include = tc -> true;

    List<TestCase<Metamorphic.Input<I>, Metamorphic.Output<O>>> cases = new ArrayList<>();

    for (TestCase<I,O> tc : baseSuite.cases) {
      if (!include.test(tc)) continue;

      I baseIn = tc.input;
      I mutIn = transform.apply(baseIn);

      var in = new Metamorphic.Input<>(baseIn, mutIn);

      String desc = keepDescPrefix
          ? ("meta :: " + tc.desc)
          : (tc.desc);

      // meta suite는 expected/caseOracle 필요 없음
      // tags는 base tags + "meta"
      Set<String> tags = new LinkedHashSet<>(tc.tags);
      tags.add("meta");

      cases.add(TestCase.empty(in, desc, tags));
    }

    Oracle<Metamorphic.Input<I>, Metamorphic.Output<O>> suiteOracle =
        MetamorphicOracles.fromProperty(property);

    String name = baseSuite.name + (newNameSuffix == null ? "/meta" : newNameSuffix);
    return TestSuite.of(name, cases, suiteOracle);
  }
}
