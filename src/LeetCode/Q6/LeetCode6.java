package LeetCode.Q6;

import LeetCode.Q5.LeetCode5_2;
import exceptions.WrongAnswerException;
import java.security.SecureRandom;

public class LeetCode6 {
  private static final String CHARACTER_SET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
          "abcdefghijklmnopqrstuvwxyz" +
          ",.";
  private static final SecureRandom random = new SecureRandom(); // 보안적으로 강력한 랜덤 생성기 사용

  public static void main(String[] args) {
    LeetCode6_1 leetCode6_1 = new LeetCode6_1();
    LeetCode6_2 leetCode6_2 = new LeetCode6_2();

    for (int i = 0; i < 1000; i++) {
      String s = createRandomString();
      int numRows = random.nextInt(1000) +1;
      if (!leetCode6_1.convert(s, numRows).equals(leetCode6_2.convert(s, numRows))) {
        throw new WrongAnswerException();
      }

    }

    System.out.println("정답입니다.");
  }

  public static String createRandomString() {
    int length = random.nextInt(1000)+1;

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      // 정의된 문자 셋에서 랜덤하게 문자 선택
      int index = random.nextInt(CHARACTER_SET.length());
      sb.append(CHARACTER_SET.charAt(index));
    }
    return sb.toString();
  }
}
