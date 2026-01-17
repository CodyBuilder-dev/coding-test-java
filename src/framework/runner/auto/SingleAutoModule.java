package framework.runner.auto;

import framework.oracle.Oracle;
import framework.oracle.Reference;
import framework.test.TestSuite;
import java.util.List;
import java.util.Map;

public non-sealed interface SingleAutoModule<I, O> extends AutoModule {
  default Map<String, TestSuite<I, O>> suites() { return Map.of(); }

  List<?> rawSolutions(); // 제출 클래스 인스턴스들

  // optional overrides
  default String methodName() { return null; }           // null이면 @SubmitMethod 또는 "solution" 사용
  default Reference<I, O> reference() { return null; }   // optional
  default Oracle<I, O> suiteOracle() { return null; }    // optional: suite.suiteOracle로 넣고 싶으면 사용
}