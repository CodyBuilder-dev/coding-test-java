package LeetCode.Q3;

import LeetCode.Q2.LeetCode2_2;
import LeetCode.Q2.LeetCode2_3;
import LeetCode.Q2.ListNode;
import exceptions.WrongAnswerException;
import java.security.SecureRandom;
import java.util.Random;

public class LeetCode3 {
  private static final String CHARACTER_SET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
          "abcdefghijklmnopqrstuvwxyz" +
          "0123456789" +
          " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"; // 일반 ASCII 기호 및 공백 포함
  private static final SecureRandom random = new SecureRandom(); // 보안적으로 강력한 랜덤 생성기 사용

  public static void main(String[] args) {
    LeetCode3_2 leetCode3_2 = new LeetCode3_2();
    String s = createRandomString();
    System.out.println("문자열 테스트");
    System.out.println(s);
    System.out.println(leetCode3_2.lengthOfLongestSubstring(s));

    System.out.println("빈칸 테스트");
    System.out.println(leetCode3_2.lengthOfLongestSubstring(""));

    System.out.println("정답입니다.");
  }

  public static String createRandomString() {
    int length = random.nextInt(50001);

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      // 정의된 문자 셋에서 랜덤하게 문자 선택
      int index = random.nextInt(CHARACTER_SET.length());
      sb.append(CHARACTER_SET.charAt(index));
    }
    return sb.toString();
  }
}
