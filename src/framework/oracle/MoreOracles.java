package framework.oracle;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Optional helper Oracles. You can merge these into Oracles.java if you prefer.
 */
public final class MoreOracles {
  private MoreOracles() {}

  /** input must satisfy predicate (useful when running random tests to assert generator correctness) */
  public static <I, O> Oracle<I, O> requireInput(Predicate<I> pred, String failMsg) {
    Objects.requireNonNull(pred);
    return (in, out) -> pred.test(in) ? CheckResult.pass() : CheckResult.fail("bad input: " + failMsg);
  }

  /** output key must equal input key (e.g., multiset size preserved) */
  public static <I, O, K> Oracle<I, O> keyPreserved(Function<I, K> inKey, Function<O, K> outKey) {
    Objects.requireNonNull(inKey);
    Objects.requireNonNull(outKey);
    return (in, out) -> {
      if (out == null) return CheckResult.fail("output is null");
      K k1 = inKey.apply(in);
      K k2 = outKey.apply(out);
      boolean ok = Objects.equals(k1, k2);
      return ok ? CheckResult.pass() : CheckResult.fail("key not preserved: inKey=" + k1 + ", outKey=" + k2);
    };
  }

  /** output must not throw in downstream usage (you provide a validator that may throw) */
  public static <I, O> Oracle<I, O> noThrow(Function<O, ?> validator, String stage) {
    Objects.requireNonNull(validator);
    Objects.requireNonNull(stage);
    return (in, out) -> {
      try {
        validator.apply(out);
        return CheckResult.pass();
      } catch (Throwable t) {
        return CheckResult.fail("threw at " + stage + ": " + t.getClass().getSimpleName() + ": " + t.getMessage());
      }
    };
  }
}
