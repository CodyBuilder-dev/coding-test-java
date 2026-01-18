package framework.runner.auto;

import framework.io.ExampleSuites;
import framework.test.BatchTestSuite;
import framework.test.TestSuite;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AutoSuiteAugmenter {
  private AutoSuiteAugmenter() {}

  @SuppressWarnings({"unchecked","rawtypes"})
  public static <I,O> Map<String, TestSuite<I,O>> augmentWithAutoExamples(
      Object module,
      Map<String, TestSuite<I,O>> suites
  ) {
    Map<String, TestSuite<I,O>> out = new LinkedHashMap<>(suites);

    AutoExamples ann = module.getClass().getAnnotation(AutoExamples.class);
    if (ann == null) return out;

    // ✅ 파일 없으면 ExampleSuitesPath에서 에러 던지게 됨(요구사항 반영)
    TestSuite ex = ExampleSuites.fromStandardFiles(
        module.getClass(),
        ann.dir(),
        ann.inputs(),
        ann.outputs(),
        (Class) ann.input(),
        (Class) ann.output()
    );

    out.put(ann.suiteSuffix(), (TestSuite<I,O>) ex);
    return out;
  }

  public static <EI, EO, BI> Map<String, BatchTestSuite<EI, EO, BI>> augmentWithAutoBatchExamples(
      Object module,
      Map<String, BatchTestSuite<EI, EO, BI>> suites
  ) {
    Map<String, BatchTestSuite<EI, EO, BI>> out = new LinkedHashMap<>(suites);

    if (module instanceof AutoBatchExamples<?, ?, ?> abe) {
      @SuppressWarnings("unchecked")
      AutoBatchExamples<EI, EO, BI> typed = (AutoBatchExamples<EI, EO, BI>) abe;

      // 파일 없으면 여기서 예외 터지는 게 요구사항에 맞음
      BatchTestSuite<EI, EO, BI> ex = typed.autoExamplesSuite();
      out.put("examples_auto", ex);
    }
    return out;
  }
}