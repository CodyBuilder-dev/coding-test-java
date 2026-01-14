package platforms.leetcode.cacheSystemDesign;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * Queue를 이용한 비효율적인 풀이
 */
public class CacheSystemDesign1_1 {
  class LRUCache {
    private int capacity;
    private ArrayDeque<Integer> queue;
    private Map<Integer,Integer> map;

    public LRUCache(int capacity) {
      this.capacity = capacity;
      this.queue = new ArrayDeque<>(capacity);
      this.map = new HashMap<>(capacity);
    }

    public int get(int key) {
      Integer val = map.get(key);
      if(val != null) {
        this.queue.remove(key);
        this.queue.offer(key);
        return val;
      }
      return -1;
    }

    public void put(int key, int value) {
      if(map.get(key) == null) {
        if(queue.size() == capacity) {
          int removedKey = queue.poll();
          map.remove(removedKey);
          map.put(key,value);
          queue.offer(key);
        } else {
          map.put(key,value);
          queue.offer(key);
        }
      } else {
        map.put(key,value);
        queue.remove(key);
        queue.offer(key);
      }
    }
  }

}
