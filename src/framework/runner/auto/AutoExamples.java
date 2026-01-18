package framework.runner.auto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoExamples {
  Class<?> input();
  Class<?> output();

  /** 기본값: tests/examples/inputs.txt, outputs.txt */
  String dir() default "examples";
  String inputs() default "inputs.txt";
  String outputs() default "outputs.txt";

  /** suite 이름에 붙일 suffix */
  String suiteSuffix() default "examples_auto";
}