package framework.test;

import framework.test.Metamorphic.Input;
import framework.test.Metamorphic.Output;
import framework.oracle.Oracle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TestCase<I, O> {
  public final I input;
  public final O expected;            // null 가능
  public final Oracle<I, O> caseOracle;   // null 가능
  public final String desc;
  public final Set<String> tags;

  private TestCase(I input, O expected, Oracle<I, O> caseOracle, String desc, Set<String> tags) {
    this.input = input;
    this.expected = expected;
    this.caseOracle = caseOracle;
    this.desc = desc;
    this.tags = tags == null ? Set.of() : Set.copyOf(tags);
  }

  // 1) expected 기반
  public static <I, O> TestCase<I, O> expect(I input, O expected, String desc, String... tags) {
    return new TestCase<>(input, expected, null, desc, new HashSet<>(Arrays.asList(tags)));
  }

  // 2) oracle 기반
  public static <I, O> TestCase<I, O> oracle(I input, Oracle<I, O> oracle, String desc, String... tags) {
    return new TestCase<>(input, null, oracle, desc, new HashSet<>(Arrays.asList(tags)));
  }

  // 3) 둘 다 (정답도 있고 성질도 같이 체크하고 싶을 때)
  public static <I, O> TestCase<I, O> expectAndOracle(I input, O expected, Oracle<I, O> oracle, String desc, String... tags) {
    return new TestCase<>(input, expected, oracle, desc, new HashSet<>(Arrays.asList(tags)));
  }

  // 4) 빈 테스트케이스 (Metamorphic용. suitesOracle로 검증)
  public static <I, O> TestCase<Input<I>, Output<O>> empty(Input<I> input, String desc, Set<String> tags) {
    return new TestCase(input, null, null, desc, new HashSet<>(Arrays.asList(tags)));
  }

}