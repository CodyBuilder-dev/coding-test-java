package platforms.leetcode.dataStreamProcessing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 최초 1번 정렬해놓고, 그 이후로 add(int val) 호출시마다 O(N)으로 삽입
 */
public class DataStreamProcessing1_1 {
  class KthLargest {

    int k;
    List<Integer> scoreList = new ArrayList<>();

    public KthLargest(int k, int[] nums) {
      this.k = k;
      for(int num : nums) {
        scoreList.add(num);
      }
      scoreList.sort(Comparator.naturalOrder());
    }

    public int add(int val) {
      int i = 0;
      while(i < scoreList.size() && scoreList.get(i) < val) {
        i++;
      }
      scoreList.add(i,val);
      return scoreList.get(scoreList.size()-k);
    }
  }

/**
 * Your KthLargest object will be instantiated and called as such:
 * KthLargest obj = new KthLargest(k, nums);
 * int param_1 = obj.add(val);
 */
}
