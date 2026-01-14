package platforms.leetcode.Q5;

/**
 * 뒤집어서 이동시키는 것은 잘못된 풀이
 */
public class LeetCode5_2 {
  public String longestPalindrome(String s) {
    if(s.length() == 1) return s;

    String longestPalindrome = "";

    for(int i = 0 ; i < s.length() - 1; ++i) {
      String odd = getPalindromeLengthFromCenter(s,i,i);
      String even = getPalindromeLengthFromCenter(s,i,i+1);

      if( odd.length() > longestPalindrome.length()) longestPalindrome = odd;
      if( even.length() > longestPalindrome.length()) longestPalindrome = even;
    }

    return longestPalindrome;
  }

  public String getPalindromeLengthFromCenter(String s, int left, int right){
    while(left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
      left--;
      right++;
    }

    return s.substring(left+1,right);
  }
}
