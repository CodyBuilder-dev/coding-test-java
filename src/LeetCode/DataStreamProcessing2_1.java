package LeetCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * endsWith으로 짜면 엄청나게 느리지만 돌긴 한다.
 */
public class DataStreamProcessing2_1 {
  class StreamChecker {

    String[] words;
    String stream;
    public StreamChecker(String[] words) {
      this.words = words;
    }

    public boolean query(char letter) {
      stream += letter;

      boolean isSuffix = false;
      for(int i =0; i<words.length; ++i) {
        isSuffix = isSuffix || stream.endsWith(words[i]);
      }

      return isSuffix;
    }
  }

/**
 * Your StreamChecker object will be instantiated and called as such:
 * StreamChecker obj = new StreamChecker(words);
 * boolean param_1 = obj.query(letter);
 */
}
