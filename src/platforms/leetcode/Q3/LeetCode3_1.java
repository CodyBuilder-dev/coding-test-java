package platforms.leetcode.Q3;

import java.util.HashMap;
import java.util.Map;

/**
 * 앞에서부터 순회하는 잘못된 풀이
 */
public class LeetCode3_1 {
  public int lengthOfLongestSubstring(String s) {
    String[] charArray = s.split("");
    Map<String,Integer> charMap = new HashMap<>();

    int maxLength = 0;
    int currentLength = 0;

    if(charArray.length == 1 && charArray[0].equals("")) {
      return 0;
    } else {
      charMap.put(charArray[0],1);
      currentLength = 1;
    }

    for(int i=0; i<charArray.length-1; ++i) {
      String currentChar = charArray[i];
      String nextChar = charArray[i+1];

      if (charMap.get(nextChar) == null) {
        currentLength++;
      } else {
        maxLength = Math.max(maxLength,currentLength);
        currentLength = 1;
        charMap = new HashMap<>();
        charMap.put(currentChar,1);
        if(!nextChar.equals(currentChar)){
          currentLength++;
        }
      }
      charMap.put(nextChar, 1);
    }
    maxLength = Math.max(maxLength,currentLength);

    return maxLength;
  }
}
