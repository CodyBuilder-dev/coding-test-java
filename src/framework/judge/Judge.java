package framework.judge;

import framework.eq.Equality;
import framework.oracle.CheckResult;
import framework.oracle.Oracles;
import framework.oracle.Oracle;
import framework.oracle.Reference;
import framework.pretty.PrettyPrinter;
import framework.runner.auto.RunLog;
import framework.runner.auto.RunOptions;
import framework.test.TestCase;
import framework.test.TestSuite;
import framework.core.Solution;
import java.util.List;
import java.util.Objects;

public final class Judge<I, O> {
  private final Equality<O> eq;
  private final Reference<I, O> ref; // null 허용
  private final NoOraclePolicy policy;
  private final PrettyPrinter<I> inPrinter;
  private final PrettyPrinter<O> outPrinter;

  private Judge(Equality<O> eq,
      Reference<I, O> ref,
      NoOraclePolicy policy,
      PrettyPrinter<I> inPrinter,
      PrettyPrinter<O> outPrinter) {
    this.eq = Objects.requireNonNull(eq);
    this.ref = ref;
    this.policy = Objects.requireNonNull(policy);
    this.inPrinter = Objects.requireNonNull(inPrinter);
    this.outPrinter = Objects.requireNonNull(outPrinter);
  }

  public static <I, O> Builder<I, O> builder(Equality<O> eq) {
    return new Builder<>(eq);
  }

  public static final class Builder<I, O> {
    private final Equality<O> eq;
    private Reference<I, O> ref = null;
    private NoOraclePolicy policy = NoOraclePolicy.SKIP;
    private PrettyPrinter<I> inPrinter = PrettyPrinter.defaultPrinter();
    private PrettyPrinter<O> outPrinter = PrettyPrinter.defaultPrinter();

    private Builder(Equality<O> eq) { this.eq = Objects.requireNonNull(eq); }

    public Builder<I, O> withReference(Reference<I, O> ref) {
      this.ref = Objects.requireNonNull(ref);
      return this;
    }

    public Builder<I, O> noOraclePolicy(NoOraclePolicy policy) {
      this.policy = Objects.requireNonNull(policy);
      return this;
    }

    public Builder<I, O> prettyInput(PrettyPrinter<I> p) {
      this.inPrinter = Objects.requireNonNull(p);
      return this;
    }

    public Builder<I, O> prettyOutput(PrettyPrinter<O> p) {
      this.outPrinter = Objects.requireNonNull(p);
      return this;
    }

    public Judge<I, O> build() {
      return new Judge<>(eq, ref, policy, inPrinter, outPrinter);
    }
  }

  public void run(TestSuite<I, O> suite, List<Solution<I, O>> solutions) {
    System.out.println("\n================= SUITE: " + suite.name + " =================");
    for (Solution<I, O> sol : solutions) {
      runOne(suite, sol);
      System.out.println();
    }
  }

  private void runOne(TestSuite<I, O> suite, Solution<I, O> sol) {
    long start = System.nanoTime();
    int pass = 0, fail = 0, skip = 0;

    System.out.println("----------------------------------------------------");
    System.out.println("Solution: " + sol.name());
    System.out.println("----------------------------------------------------");

    for (int i = 0; i < suite.cases.size(); i++) {
      TestCase<I, O> tc = suite.cases.get(i);

//      Case 단위 필터링까지 필요하다면, 매개변수에 opt 넣고 여기서 하면 됨
//      String caseReason = (opt == null) ? null : opt.explainSkipCaseTags(tc.tags);
//
//      if (caseReason != null) {
//        if (opt.verbosity == RunOptions.Verbosity.VERBOSE) {
//          RunLog.skip("CASE", suite.name + " :: " + tc.desc, caseReason);
//        }
//        continue;
//      }

      O actual = sol.solve(tc.input);

      CheckResult r;
      boolean isSkip = false;

      O expected = tc.expected;
      if (expected == null && ref != null) expected = ref.computeExpected(tc.input);

      Oracle<I, O> combinedOracle = Oracles.all(suite.suiteOracle, tc.caseOracle);
      if (expected != null) {
        // ✅ expected로 채점
        boolean ok = eq.same(expected, actual);
        r = ok ? CheckResult.pass() : CheckResult.fail(eq.explainMismatch(expected, actual));

        // (선택) ✅ expected가 있어도 oracle을 추가검사하고 싶으면:
        if (r.ok() && combinedOracle != null) {
          CheckResult or = combinedOracle.check(tc.input, actual);
          if (!or.ok()) r = CheckResult.fail("oracle failed even though expected matched: " + or.message());
        }

      } else if (combinedOracle != null) {
        // ✅ oracle로 채점
        r = combinedOracle.check(tc.input, actual);

      } else {
        // ✅ 아무 검증수단 없음
        if (policy == NoOraclePolicy.FAIL) r = CheckResult.fail("No expected/reference/oracle (policy=FAIL)");
        else { isSkip = true; r = new CheckResult(true, "SKIP"); }
      }

      if (isSkip) skip++;
      else if (r.ok()) pass++;
      else fail++;

      String status = isSkip ? "SKIP ⚪" : (r.ok() ? "PASS ✅" : "FAIL ❌");
      System.out.printf("%2d) %s | %s%n", i + 1, status, tc.desc == null ? "" : tc.desc);

      if (!isSkip && !r.ok()) {
        System.out.println("    input   : " + inPrinter.print(tc.input));
        if (expected != null) System.out.println("    expected: " + outPrinter.print(expected));
        System.out.println("    actual  : " + outPrinter.print(actual));
        if (r.message() != null && !r.message().isBlank()) System.out.println("    detail  : " + r.message());
      }
    }

    long end = System.nanoTime();
    System.out.printf("Result: PASS=%d FAIL=%d SKIP=%d | time=%.3f ms%n",
        pass, fail, skip, (end - start) / 1_000_000.0);
  }

  // 벤치에서 SKIP 제외를 위해, "검증 가능한 케이스인지" 판단하는 함수 제공
  public boolean isBenchmarkable(TestCase<I, O> tc, TestSuite<I, O> suite) {
    if (tc.expected != null) return true;
    if (ref != null) return true;
    if (tc.caseOracle != null) return true;
    return suite.suiteOracle != null; // ✅ 추가          // oracle이라도 있으면 의미 있음
  }
}