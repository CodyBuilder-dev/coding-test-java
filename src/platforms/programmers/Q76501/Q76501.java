package platforms.programmers.Q76501;

public class Q76501 {
  public int solution(int[] absolutes, boolean[] signs) {
    int answer = 0;
    int length = absolutes.length;
    for(int i = 0; i< length; ++i) {
      answer += absolutes[i] * (signs[i] ? 1 : -1);
    }
    return answer;
  }
}
