package framework.judge;

import framework.eq.Equality;
import framework.oracle.CheckResult;
import framework.oracle.Oracle;
import framework.oracle.Oracles;
import framework.oracle.Reference;
import framework.pretty.PrettyPrinter;
import framework.test.BatchTestCase;
import framework.test.BatchTestSuite;
import framework.core.Solution;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class BatchJudge<EI, EO, BI, BO> {

  private final Equality<EO> eq;
  private final Reference<EI, EO> ref; // nullable
  private final Function<BO, List<EO>> splitter;
  private final NoOraclePolicy policy;
  private final PrettyPrinter<EI> inPrinter;
  private final PrettyPrinter<EO> outPrinter;

  private BatchJudge(Equality<EO> eq,
      Reference<EI, EO> ref,
      Function<BO, List<EO>> splitter,
      NoOraclePolicy policy,
      PrettyPrinter<EI> inPrinter,
      PrettyPrinter<EO> outPrinter) {
    this.eq = Objects.requireNonNull(eq);
    this.ref = ref;
    this.splitter = Objects.requireNonNull(splitter);
    this.policy = Objects.requireNonNull(policy);
    this.inPrinter = Objects.requireNonNull(inPrinter);
    this.outPrinter = Objects.requireNonNull(outPrinter);
  }

  public static <EI, EO, BI, BO> Builder<EI, EO, BI, BO> builder(
      Equality<EO> eq, Function<BO, List<EO>> splitter) {
    return new Builder<>(eq, splitter);
  }

  public static final class Builder<EI, EO, BI, BO> {

    private final Equality<EO> eq;
    private final Function<BO, List<EO>> splitter;
    private Reference<EI, EO> ref = null;
    private NoOraclePolicy policy = NoOraclePolicy.SKIP;
    private PrettyPrinter<EI> inPrinter = PrettyPrinter.defaultPrinter();
    private PrettyPrinter<EO> outPrinter = PrettyPrinter.defaultPrinter();

    private Builder(Equality<EO> eq, Function<BO, List<EO>> splitter) {
      this.eq = Objects.requireNonNull(eq);
      this.splitter = Objects.requireNonNull(splitter);
    }

    public Builder<EI, EO, BI, BO> withReference(Reference<EI, EO> ref) {
      this.ref = Objects.requireNonNull(ref);
      return this;
    }

    public Builder<EI, EO, BI, BO> noOraclePolicy(NoOraclePolicy policy) {
      this.policy = Objects.requireNonNull(policy);
      return this;
    }

    public Builder<EI, EO, BI, BO> prettyInput(PrettyPrinter<EI> p) {
      this.inPrinter = Objects.requireNonNull(p);
      return this;
    }

    public Builder<EI, EO, BI, BO> prettyOutput(PrettyPrinter<EO> p) {
      this.outPrinter = Objects.requireNonNull(p);
      return this;
    }

    public BatchJudge<EI, EO, BI, BO> build() {
      return new BatchJudge<>(eq, ref, splitter, policy, inPrinter, outPrinter);
    }
  }

  public void run(BatchTestSuite<EI, EO, BI> suite, List<Solution<BI, BO>> solutions) {
    System.out.println("\n================= BATCH SUITE: " + suite.name + " =================");
    for (Solution<BI, BO> sol : solutions) {
      runOne(suite, sol);
      System.out.println();
    }
  }

  private void runOne(BatchTestSuite<EI, EO, BI> suite, Solution<BI, BO> sol) {
    List<EI> elementInputs = new ArrayList<>(suite.cases.size());
    for (BatchTestCase<EI, EO> tc : suite.cases)
      elementInputs.add(tc.input);

    BI batchInput = suite.batchInputBuilder.apply(elementInputs);

    long start = System.nanoTime();
    BO batchOutput = sol.solve(batchInput);
    long end = System.nanoTime();

    List<EO> actuals = splitter.apply(batchOutput);
    if (actuals == null)
      actuals = List.of();

    int pass = 0, fail = 0, skip = 0;

    System.out.println("----------------------------------------------------");
    System.out.println("Solution: " + sol.name());
    System.out.println("----------------------------------------------------");

    for (int i = 0; i < suite.cases.size(); i++) {
      BatchTestCase<EI, EO> tc = suite.cases.get(i);
      EO actual = (i < actuals.size()) ? actuals.get(i) : null;

      CheckResult r;
      boolean isSkip = false;

      EO expected = tc.expected;
      if (expected == null && ref != null)
        expected = ref.computeExpected(tc.input);

      Oracle<EI, EO> combinedOracle = Oracles.all(suite.suiteOracle, tc.caseOracle);
      if (expected != null) {
        // ✅ expected로 채점
        boolean ok = eq.same(expected, actual);
        r = ok ? CheckResult.pass() : CheckResult.fail(eq.explainMismatch(expected, actual));

        // (선택) ✅ expected가 있어도 oracle을 추가검사하고 싶으면:
        if (r.ok() && combinedOracle != null) {
          CheckResult or = combinedOracle.check(tc.input, actual);
          if (!or.ok())
            r = CheckResult.fail("oracle failed even though expected matched: " + or.message());
        }
      } else if (combinedOracle != null) {
        // ✅ oracle로 채점
        r = combinedOracle.check(tc.input, actual);
      } else {
        // ✅ 아무 검증수단 없음
        if (policy == NoOraclePolicy.FAIL)
          r = CheckResult.fail("No expected/reference/oracle (policy=FAIL)");
        else {
          isSkip = true;
          r = new CheckResult(true, "SKIP");
        }
      }

      if (isSkip)
        skip++;
      else if (r.ok())
        pass++;
      else
        fail++;

      String status = isSkip ? "SKIP ⚪" : (r.ok() ? "PASS ✅" : "FAIL ❌");
      System.out.printf("%2d) %s | %s%n", i + 1, status, tc.desc == null ? "" : tc.desc);

      if (!isSkip && !r.ok()) {
        System.out.println("    input   : " + inPrinter.print(tc.input));
        if (expected != null)
          System.out.println("    expected: " + outPrinter.print(expected));
        System.out.println("    actual  : " + outPrinter.print(actual));
        if (r.message() != null && !r.message().isBlank())
          System.out.println("    detail  : " + r.message());
      }
    }

    System.out.printf("Result: PASS=%d FAIL=%d SKIP=%d | time=%.3f ms%n",
        pass, fail, skip, (end - start) / 1_000_000.0);
  }

  public boolean isBenchmarkable(BatchTestCase<EI, EO> tc, BatchTestSuite<EI, EO, BI> suite) {
    if (tc.expected != null)
      return true;
    if (ref != null)
      return true;
    if (tc.caseOracle != null)
      return true;
    return suite.suiteOracle != null; // ✅ 추가
  }
}