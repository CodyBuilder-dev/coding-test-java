package LeetCode.Q5;

import java.util.Stack;

/**
 * 뒤집어서 이동시키는 것은 잘못된 풀이
 */
public class LeetCode5_1 {
  public String longestPalindrome(String s) {
    StringBuilder sb = new StringBuilder(s);
    String reverse = sb.reverse().toString();

    String longestPalidrome = "";
    int reverseRightShift = 0;
    while(reverseRightShift < s.length()) {
      String longestSameString = getLongestSameString( s.substring(reverseRightShift,s.length()), reverse.substring(0,reverse.length()-reverseRightShift));
      longestPalidrome = longestSameString.length() >= longestPalidrome.length() ? longestSameString : longestPalidrome;
      //System.out.println("가장 긴 팰린드롬: " + longestPalidrome);
      reverseRightShift++;
    }

    int reverseLeftShift = 0;
    while(reverseLeftShift < s.length()) {
      String longestSameString = getLongestSameString( s.substring(0,s.length()-reverseLeftShift), reverse.substring(reverseLeftShift, reverse.length()));
      longestPalidrome = longestSameString.length() >= longestPalidrome.length() ? longestSameString : longestPalidrome;
      //System.out.println("가장 긴 팰린드롬: " + longestPalidrome);
      reverseLeftShift++;
    }

    return longestPalidrome;

  }
  public String getLongestSameString(String s1, String s2) {
    String longestSameString = "";
    StringBuilder sb = new StringBuilder();
    // System.out.println(s1 + " " + s2);

    if(s1.length() != s2.length()) throw new RuntimeException("두 문자열 길이가 다릅니다.");

    for (int i = 0; i < s1.length(); i++) {
      if(s1.charAt(i) == s2.charAt(i)) {
        sb.append(s1.charAt(i));
        longestSameString = longestSameString.length() >= sb.length() ? longestSameString : sb.toString();
      } else {
        sb = new StringBuilder();
      }
    }
    return longestSameString;
  }
}
