package platforms.programmers.Q76501.submission;

public class SubmissionSignedSumSol1_1 {
  public int solution(int[] absolutes, boolean[] signs) {
    int sum = 0;
    for (int i = 0; i < absolutes.length; i++) {
      sum += absolutes[i] * (signs[i] ? 1 : -1);
    }
    return sum;
  }
}
