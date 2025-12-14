package LeetCode;

import exceptions.WrongAnswerException;
import java.security.SecureRandom;

public class LeetCode242 {
  public static void main(String[] args) {
    LeetCode242_1 leetCode242_1 = new LeetCode242_1();
    LeetCode242_2 leetCode242_2 = new LeetCode242_2();

    for(int i=0 ; i <100; i++){
      boolean result1 = leetCode242_1.isAnagram(randomString(), randomString());
      boolean result2 = leetCode242_2.isAnagram(randomString(), randomString());

      if (result1 != result2) {
        throw new WrongAnswerException();
      }
    }

    System.out.println("정답입니다.");
  }

  public static String randomString() {
    SecureRandom sr = new SecureRandom();
    int length = sr.nextInt(50000) + 1;
    return generateRandomString(length);
  }

  public static String generateRandomString(int length) {
    SecureRandom random = new SecureRandom();
    StringBuilder buffer = new StringBuilder(length);
    int leftLimit = 97; // 'a'의 아스키 코드
    int rightLimit = 122; // 'z'의 아스키 코드

    for (int i = 0; i < length; i++) {
      // 0부터 (rightLimit - leftLimit + 1) 범위의 랜덤 정수 생성
      int randomLimitedInt = leftLimit + random.nextInt(rightLimit - leftLimit + 1);
      buffer.append((char) randomLimitedInt); // 해당 정수(문자)를 추가
    }

    return buffer.toString();
  }
}
