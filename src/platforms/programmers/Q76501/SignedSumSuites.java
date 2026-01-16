package platforms.programmers.Q76501;

import framework.test.TestCase;
import framework.test.TestSuite;
import java.util.List;

public class SignedSumSuites {
  public static TestSuite<SignedSumInput, Integer> smoke() {
    return TestSuite.of("programmers/signedsum smoke", List.of(
        TestCase.expect(new SignedSumInput(new int[]{4,7,12},  new boolean[]{true,false,true}), 9, "basic", "basic"),
        TestCase.expect(new SignedSumInput(new int[]{1,2,3},   new boolean[]{false,false,true}), 0,"basic2", "basic"),
        TestCase.expect(new SignedSumInput(new int[]{},        new boolean[]{}),0, "empty", "edge"),
        TestCase.expect(new SignedSumInput(new int[]{1000},    new boolean[]{false}), -1000,"single negative", "edge")
    ));
  }
}
