package LeetCode.Q3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * 슬라이딩 윈도우 사용
 * abccbabddb
 */
public class LeetCode3_2 {
  public int lengthOfLongestSubstring(String s) {
    int left = 0;
    int right = 0;

    String[] charArray = s.split("");
    Set<String> charSet = new HashSet<>();

    if (charArray.length == 1 && charArray[0].equals("")) {
      return 0;
    }

    int maxLength = 0;
    while (right < charArray.length) {
      String currentChar = charArray[right];
      if (charSet.contains(currentChar)) {
        while(charSet.contains(currentChar)) {
          charSet.remove(charArray[left]);
          left++;
        }
      }
      maxLength = Math.max(maxLength,right - left +1);
      charSet.add(currentChar);
      right++;
    }
    return maxLength;
  }
}

