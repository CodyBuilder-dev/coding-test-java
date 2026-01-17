package framework.runner.auto;

import framework.oracle.Oracle;
import framework.oracle.Reference;
import framework.test.TestSuite;
import java.util.List;
import java.util.Map;

public non-sealed interface SingleAutoModule<I, O> extends AutoModule {
  /** 기존 단일 suite용. 그대로 둬도 되고, suites()만 override 해도 됨 */
  default TestSuite<I, O> suite() { return null; }

  /** 여러 suite를 제공하고 싶으면 여기만 override */
  default Map<String, TestSuite<I, O>> suites() {
    TestSuite<I, O> s = suite();
    return s == null ? Map.of() : Map.of("default", s);
  }

  List<?> rawSolutions(); // 제출 클래스 인스턴스들

  // optional overrides
  default String methodName() { return null; }           // null이면 @SubmitMethod 또는 "solution" 사용
  default Reference<I, O> reference() { return null; }   // optional
  default Oracle<I, O> suiteOracle() { return null; }    // optional: suite.suiteOracle로 넣고 싶으면 사용
}