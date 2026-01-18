package framework.runner.auto;

import framework.test.BatchTestSuite;

public interface AutoBatchExamples<EI, EO, BI> {
  /**
   * 예제 파일 기반 BatchTestSuite를 반환.
   * 파일 없으면 여기서 예외 던지는 게 맞음(요구사항).
   */
  BatchTestSuite<EI, EO, BI> autoExamplesSuite();
}
