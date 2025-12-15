package LeetCode.Q242;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LeetCode242_2 {
  public boolean isAnagram(String s, String t) {
    int[] sCharCount = new int[26];
    int[] tCharCount = new int[26];

    for(char sChar: s.toCharArray()){
      sCharCount[sChar - 'a']++;
    }
    for(char tChar: t.toCharArray()){
      tCharCount[tChar - 'a']++;
    }


    return Arrays.equals(sCharCount, tCharCount);
  }
}
