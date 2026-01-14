package framework.oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;

/**
 * Reusable, problem-agnostic Oracles.
 *
 * Oracle<I,O> : CheckResult check(I input, O output)
 * CheckResult : (ok, message)
 */
public final class Oracles {

  private Oracles() {
  }

  // ---------------------------
  // Basic output sanity
  // ---------------------------

  /**
   * output must be non-null
   */
  public static <I, O> Oracle<I, O> notNull() {
    return (in, out) -> out != null ? CheckResult.pass() : CheckResult.fail("output is null");
  }

  /**
   * output must be null
   */
  public static <I, O> Oracle<I, O> isNull() {
    return (in, out) -> out == null ? CheckResult.pass()
        : CheckResult.fail("output is not null: " + out);
  }

  /**
   * output must satisfy predicate
   */
  public static <I, O> Oracle<I, O> satisfies(Predicate<O> pred, String failMsg) {
    Objects.requireNonNull(pred);
    return (in, out) -> pred.test(out) ? CheckResult.pass()
        : CheckResult.fail(failMsg + " | out=" + out);
  }

  /**
   * output must satisfy predicate; message is created lazily
   */
  public static <I, O> Oracle<I, O> satisfies(Predicate<O> pred, Function<O, String> failMsgFn) {
    Objects.requireNonNull(pred);
    Objects.requireNonNull(failMsgFn);
    return (in, out) -> pred.test(out) ? CheckResult.pass()
        : CheckResult.fail(failMsgFn.apply(out));
  }

  /**
   * output must be one of allowed values
   */
  public static <I, O> Oracle<I, O> oneOf(Collection<O> allowed) {
    Objects.requireNonNull(allowed);
    return (in, out) -> allowed.contains(out)
        ? CheckResult.pass()
        : CheckResult.fail("output not in allowed set. out=" + out + ", allowed=" + allowed);
  }

  // ---------------------------
  // Numeric range / sign / bounds
  // ---------------------------

  /**
   * int output must be within [min,max] computed from input
   */
  public static <I> Oracle<I, Integer> intRange(Function<I, Integer> minFn,
      Function<I, Integer> maxFn) {
    Objects.requireNonNull(minFn);
    Objects.requireNonNull(maxFn);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      int min = minFn.apply(in);
      int max = maxFn.apply(in);
      if (min > max)
        return CheckResult.fail("invalid range: min > max (" + min + " > " + max + ")");
      if (out < min || out > max)
        return CheckResult.fail("out of range: " + out + " not in [" + min + "," + max + "]");
      return CheckResult.pass();
    };
  }

  /**
   * long output must be within [min,max] computed from input
   */
  public static <I> Oracle<I, Long> longRange(Function<I, Long> minFn, Function<I, Long> maxFn) {
    Objects.requireNonNull(minFn);
    Objects.requireNonNull(maxFn);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      long min = minFn.apply(in);
      long max = maxFn.apply(in);
      if (min > max)
        return CheckResult.fail("invalid range: min > max (" + min + " > " + max + ")");
      if (out < min || out > max)
        return CheckResult.fail("out of range: " + out + " not in [" + min + "," + max + "]");
      return CheckResult.pass();
    };
  }

  /**
   * non-negative int output
   */
  public static <I> Oracle<I, Integer> nonNegativeInt() {
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      return out >= 0 ? CheckResult.pass() : CheckResult.fail("negative output: " + out);
    };
  }

  // ---------------------------
  // String shape
  // ---------------------------

  /**
   * string output length must be within [min,max] computed from input
   */
  public static <I> Oracle<I, String> stringLengthRange(Function<I, Integer> minLen,
      Function<I, Integer> maxLen) {
    Objects.requireNonNull(minLen);
    Objects.requireNonNull(maxLen);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      int min = minLen.apply(in);
      int max = maxLen.apply(in);
      if (min > max)
        return CheckResult.fail("invalid length range: min > max (" + min + " > " + max + ")");
      int n = out.length();
      if (n < min || n > max)
        return CheckResult.fail("length out of range: " + n + " not in [" + min + "," + max + "]");
      return CheckResult.pass();
    };
  }

  /**
   * output must match regex
   */
  public static <I> Oracle<I, String> matchesRegex(String regex) {
    Objects.requireNonNull(regex);
    Pattern p = Pattern.compile(regex);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      return p.matcher(out).matches()
          ? CheckResult.pass()
          : CheckResult.fail("regex mismatch: /" + regex + "/ out=\"" + out + "\"");
    };
  }

  /**
   * output must be contained in the input string (common for substring problems)
   */
  public static Oracle<String, String> isSubstringOfInput() {
    return (in, out) -> {
      if (in == null)
        return CheckResult.pass(); // platform might not give null; keep safe
      if (out == null)
        return CheckResult.fail("output is null");
      return in.contains(out)
          ? CheckResult.pass()
          : CheckResult.fail("output is not substring of input");
    };
  }

  // ---------------------------
  // Array/List shape
  // ---------------------------

  /**
   * int[] output length must be within [min,max] computed from input
   */
  public static <I> Oracle<I, int[]> intArrayLengthRange(Function<I, Integer> minLen,
      Function<I, Integer> maxLen) {
    Objects.requireNonNull(minLen);
    Objects.requireNonNull(maxLen);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      int min = minLen.apply(in);
      int max = maxLen.apply(in);
      if (min > max)
        return CheckResult.fail("invalid length range: min > max (" + min + " > " + max + ")");
      int n = out.length;
      if (n < min || n > max)
        return CheckResult.fail("length out of range: " + n + " not in [" + min + "," + max + "]");
      return CheckResult.pass();
    };
  }

  /**
   * list output size must be within [min,max] computed from input
   */
  public static <I, T> Oracle<I, List<T>> listSizeRange(Function<I, Integer> minSize,
      Function<I, Integer> maxSize) {
    Objects.requireNonNull(minSize);
    Objects.requireNonNull(maxSize);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      int min = minSize.apply(in);
      int max = maxSize.apply(in);
      if (min > max)
        return CheckResult.fail("invalid size range: min > max (" + min + " > " + max + ")");
      int n = out.size();
      if (n < min || n > max)
        return CheckResult.fail("size out of range: " + n + " not in [" + min + "," + max + "]");
      return CheckResult.pass();
    };
  }

  // ---------------------------
  // Idempotence & fixed-point properties
  // (These are generic but require you to provide transform/apply logic)
  // ---------------------------

  /**
   * Checks idempotence of a transformer: f(f(x)) == f(x). Useful for "normalize", "dedupe", "sort"
   * style routines when output is the normalized form.
   *
   * @param applyFn apply transformer to output again (i.e., f(out)).
   * @param eq      equality on output values
   */
  public static <I, O> Oracle<I, O> idempotent(Function<O, O> applyFn, BiPredicate<O, O> eq) {
    Objects.requireNonNull(applyFn);
    Objects.requireNonNull(eq);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      O out2 = applyFn.apply(out);
      if (!eq.test(out2, out)) {
        return CheckResult.fail(
            "idempotence violated: f(out) != out | f(out)=" + out2 + ", out=" + out);
      }
      return CheckResult.pass();
    };
  }

  /**
   * Checks fixed-point: g(out) == out (same as idempotent but you provide g directly).
   */
  public static <I, O> Oracle<I, O> fixedPoint(Function<O, O> g, BiPredicate<O, O> eq) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(eq);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      O gOut = g.apply(out);
      if (!eq.test(gOut, out)) {
        return CheckResult.fail(
            "fixed-point violated: g(out) != out | g(out)=" + gOut + ", out=" + out);
      }
      return CheckResult.pass();
    };
  }

  // ---------------------------
  // Metamorphic / relational properties
  // ---------------------------

  /**
   * Symmetry for pair-like inputs: property that result should be same under swap(input).
   * <p>
   * You provide: - swapFn: swaps input (e.g., (a,b)->(b,a)) - evalFn: re-evaluates the
   * function-under-test on swapped input - eq: compares outputs
   * <p>
   * Note: This oracle needs access to the implementation being tested. So it's mainly used outside
   * Judge, or inside custom harness.
   */
  public static <I, O> Oracle<I, O> symmetry(
      Function<I, I> swapFn,
      Function<I, O> evalFn,
      BiPredicate<O, O> eq
  ) {
    Objects.requireNonNull(swapFn);
    Objects.requireNonNull(evalFn);
    Objects.requireNonNull(eq);
    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      I swapped = swapFn.apply(in);
      O outSwapped = evalFn.apply(swapped);
      if (!eq.test(out, outSwapped)) {
        return CheckResult.fail(
            "symmetry violated: out != out(swapped) | out=" + out + ", swappedOut=" + outSwapped);
      }
      return CheckResult.pass();
    };
  }

  /**
   * Monotonicity under input-transform: if key(transform(in)) >= key(in) then output key should be
   * >= too. Generic helper for many "monotone" tasks.
   */
  public static <I, O> Oracle<I, O> monotoneNonDecreasing(
      Function<I, I> transformFn,
      ToLongFunction<I> inputKey,
      ToLongFunction<O> outputKey,
      Function<I, O> evalFn
  ) {
    Objects.requireNonNull(transformFn);
    Objects.requireNonNull(inputKey);
    Objects.requireNonNull(outputKey);
    Objects.requireNonNull(evalFn);

    return (in, out) -> {
      if (out == null)
        return CheckResult.fail("output is null");
      I in2 = transformFn.apply(in);

      long k1 = inputKey.applyAsLong(in);
      long k2 = inputKey.applyAsLong(in2);

      if (k2 < k1) {
        return CheckResult.fail(
            "transform did not increase input key: k2 < k1 (" + k2 + " < " + k1 + ")");
      }

      O out2 = evalFn.apply(in2);
      long o1 = outputKey.applyAsLong(out);
      long o2 = outputKey.applyAsLong(out2);

      if (o2 < o1) {
        return CheckResult.fail(
            "monotonicity violated: outKey(transform(in)) < outKey(in) (" + o2 + " < " + o1 + ")");
      }
      return CheckResult.pass();
    };
  }

  // ---------------------------
  // Composition helpers
  // ---------------------------

  /**
   * AND composition: all oracles must pass
   */
  @SafeVarargs
  public static <I, O> Oracle<I, O> all(Oracle<I, O>... oracles) {
    return compose("all", oracles, true);
  }


  /**
   * OR composition: at least one oracle must pass
   */
  @SafeVarargs
  public static <I, O> Oracle<I, O> any(Oracle<I, O>... oracles) {
    return compose("any", oracles, false);
  }


  /**
   * Decorate: if oracle fails, prepend message
   */
  public static <I, O> Oracle<I, O> named(String name, Oracle<I, O> oracle) {
    Objects.requireNonNull(name);
    if (oracle == null) return null;
    return (in, out) -> {
      CheckResult r = oracle.check(in, out);
      return r.ok() ? r : CheckResult.fail("[" + name + "] " + nullToEmpty(r.message()));
    };
  }

  /**
   * Internal composer.
   * - modeAll=true : AND (first failure)
   * - modeAll=false: OR  (first pass)
   * If all inputs are null -> returns null.
   */
  @SafeVarargs
  private static <I, O> Oracle<I, O> compose(String mode, Oracle<I, O>... oracles) {
    throw new AssertionError("Use overload with modeAll"); // prevent accidental use
  }

  private static <I, O> Oracle<I, O> compose(String mode, Oracle<I, O>[] oracles, boolean modeAll) {
    Objects.requireNonNull(oracles);

    // ✅ null 제거 + 리스트화(가독/간결)
    List<Oracle<I, O>> list = Arrays.stream(oracles)
        .filter(Objects::nonNull)
        .toList();

    if (list.isEmpty()) return null;

    if (modeAll) {
      return (in, out) -> {
        for (Oracle<I, O> o : list) {
          CheckResult r = o.check(in, out);
          if (!r.ok()) return r;
        }
        return CheckResult.pass();
      };
    } else {
      return (in, out) -> {
        StringBuilder sb = null;
        for (Oracle<I, O> o : list) {
          CheckResult r = o.check(in, out);
          if (r.ok()) return r;
          if (sb == null) sb = new StringBuilder();
          else sb.append(" | ");
          sb.append(nullToEmpty(r.message()));
        }
        return CheckResult.fail("all alternatives failed" + (sb == null ? "" : (": " + sb)));
      };
    }
  }

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }
}