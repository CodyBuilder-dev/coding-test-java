package platforms.programmers.Q76501;

import framework.runner.auto.SubmitMethod;

@SubmitMethod("solution")
public class SubmissionSignedSumSol1 {
  // 플랫폼 요구 시그니처 그대로
  public int solution(int[] absolutes, boolean[] signs) {
    int sum = 0;
    for (int i = 0; i < absolutes.length; i++) {
      sum += signs[i] ? absolutes[i] : -absolutes[i];
    }
    return sum;
  }
}
