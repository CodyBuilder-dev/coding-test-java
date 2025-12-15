package LeetCode.Q5;

import LeetCode.Q3.LeetCode3_2;
import java.security.SecureRandom;

public class LeetCode5 {
  private static final String CHARACTER_SET =
          "abcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom random = new SecureRandom(); // 보안적으로 강력한 랜덤 생성기 사용

  public static void main(String[] args) {
    LeetCode5_2 leetCode5_2 = new LeetCode5_2();
    String s = createRandomString();
    System.out.println("문자열 테스트");
    System.out.println(s);
    System.out.println(leetCode5_2.longestPalindrome(s));

    System.out.println("빈칸 테스트");
    System.out.println(leetCode5_2.longestPalindrome(""));

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
