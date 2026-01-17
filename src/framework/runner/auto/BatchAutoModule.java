package framework.runner.auto;

import framework.oracle.Oracle;
import framework.oracle.Reference;
import framework.test.BatchTestSuite;
import framework.test.TestSuite;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public non-sealed interface BatchAutoModule<EI, EO, BI, BO> extends AutoModule {
  BatchTestSuite<EI, EO, BI> suite();
  default Map<String, BatchTestSuite<EI, EO, BI>> suites() {
    BatchTestSuite<EI, EO, BI> s = suite();
    return s == null ? Map.of() : Map.of("default", s);
  }
  List<?> rawSolutions();

  // batch only required
  Function<BO, List<EO>> splitter();

  // optional overrides
  default String methodName() { return null; }             // null이면 @SubmitMethod 또는 "solution" 사용
  default Reference<EI, EO> reference() { return null; }   // optional
  default Oracle<EI, EO> suiteOracle() { return null; }    // optional
}