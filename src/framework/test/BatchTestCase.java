package framework.test;

import framework.oracle.Oracle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class BatchTestCase<EI, EO> {
  public final EI input;
  public final EO expected;                 // null 가능
  public final Oracle<EI, EO> caseOracle;       // null 가능
  public final String desc;
  public final Set<String> tags;

  private BatchTestCase(EI input, EO expected, Oracle<EI, EO> caseOracle, String desc, Set<String> tags) {
    this.input = input;
    this.expected = expected;
    this.caseOracle = caseOracle;
    this.desc = desc;
    this.tags = tags == null ? Set.of() : Set.copyOf(tags);
  }

  public static <EI, EO> BatchTestCase<EI, EO> expect(EI input, EO expected, String desc, String... tags) {
    return new BatchTestCase<>(input, expected, null, desc, new HashSet<>(Arrays.asList(tags)));
  }

  public static <EI, EO> BatchTestCase<EI, EO> oracle(EI input, Oracle<EI, EO> oracle, String desc, String... tags) {
    return new BatchTestCase<>(input, null, oracle, desc, new HashSet<>(Arrays.asList(tags)));
  }

  public static <EI, EO> BatchTestCase<EI, EO> expectAndOracle(EI input, EO expected, Oracle<EI, EO> oracle, String desc, String... tags) {
    return new BatchTestCase<>(input, expected, oracle, desc, new HashSet<>(Arrays.asList(tags)));
  }
}