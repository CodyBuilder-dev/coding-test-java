package platforms.leetcode.Q242;

import java.util.*;

public class SubmissionAnagramSol1_1 {
  // 다른 풀이(정렬)
  public boolean isAnagram(String s, String t) {
    if (s == null || t == null) return false;
    if (s.length() != t.length()) return false;

    char[] a = s.toCharArray();
    char[] b = t.toCharArray();
    Arrays.sort(a);
    Arrays.sort(b);
    return Arrays.equals(a, b);
  }
}
