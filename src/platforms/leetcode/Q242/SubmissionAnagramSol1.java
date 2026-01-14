package platforms.leetcode.Q242;

public class SubmissionAnagramSol1 {
  // 플랫폼 요구 시그니처 그대로
  public boolean isAnagram(String s, String t) {
    if (s == null || t == null) return false;
    if (s.length() != t.length()) return false;

    int[] cnt = new int[256];
    for (int i = 0; i < s.length(); i++) cnt[s.charAt(i)]++;
    for (int i = 0; i < t.length(); i++) {
      if (--cnt[t.charAt(i)] < 0) return false;
    }
    return true;
  }
}
