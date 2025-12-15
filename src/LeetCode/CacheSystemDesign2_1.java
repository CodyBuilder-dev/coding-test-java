package LeetCode;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Pair에 이중연결리스트, TreeMap까지 써서 맞추긴 했는데 이게 맞나?
 */
public class CacheSystemDesign2_1 {
  class LFUCache {
    class Pair<T,U> {
      T first;
      U second;

      T getFirst() {
        return first;
      }
      U getSecond() {
        return second;
      }

      Pair(T first, U second) {
        this.first = first;
        this.second = second;
      }
    }

    class Node {
      int key;
      int val;
      int count;
      Node prev;
      Node next;

      Node(int key, int val, int count){
        this.key = key;
        this.val = val;
        this.count = count;
      }
    }

    int capacity;
    Map<Integer, Node> map = new HashMap<>();
    TreeMap<Integer, Pair<Node,Node>> countMap = new TreeMap<>();

    void addNodeNextToHead(int count, Node node) {
      if(!countMap.containsKey(count)){
        Node head = new Node(-1,-1,-1);
        Node tail = new Node(-1,-1,-1);

        head.next = tail;
        tail.prev = head;

        countMap.put(count, new Pair<>(head,tail));
      }
      Node head = countMap.get(count).getFirst();
      Node headNextNode = head.next;

      head.next = node;

      node.prev = head;
      node.next = headNextNode;

      headNextNode.prev = node;
    }

    void deleteNode(Node node) {
      Node prevNode = node.prev;
      Node nextNode = node.next;

      prevNode.next = nextNode;
      nextNode.prev = prevNode;

      if(prevNode.val == -1 && nextNode.val == -1) {
        countMap.remove(node.count);
      }
    }

    public LFUCache(int capacity) {
      this.capacity = capacity;
    }

    public int get(int key) {
      if(map.containsKey(key)){
        Node node = map.get(key);
        deleteNode(node);
        addNodeNextToHead(++node.count, node);

        return node.val;
      }
      else return -1;
    }

    public void put(int key, int value) {
      // 있으면 업데이트
      if (map.containsKey(key)){
        Node node = map.get(key);
        deleteNode(node);
        addNodeNextToHead(++node.count, node);
        node.val = value;
      } else {
        if(map.size() >= capacity) {
          for (Map.Entry<Integer, Pair<Node,Node>> entry : countMap.entrySet()) {
            if(entry != null) {
              Node lfNode =entry.getValue().getSecond().prev;
              deleteNode(lfNode);
              map.remove(lfNode.key);
            }
            break;
          }
        }
        Node node = new Node(key, value, 1);
        map.put(key, node);
        addNodeNextToHead(node.count, node);
      }
    }
  }

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */

}
