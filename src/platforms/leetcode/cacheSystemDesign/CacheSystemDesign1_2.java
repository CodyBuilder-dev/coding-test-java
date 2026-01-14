package platforms.leetcode.cacheSystemDesign;

import java.util.HashMap;
import java.util.Map;

/**
 * 이중연결리스트를 Map에 넣어서 활용하면 O(1)로 삽입삭제가 가능함 ㄷㄷ
 */
public class CacheSystemDesign1_2 {
  class LRUCache {
    class Node {
      int key;
      int val;
      Node prev;
      Node next;

      Node(int key, int val){
        this.key = key;
        this.val = val;
      }
    }

    Node head = new Node(-1,-1);
    Node tail = new Node(-1,-1);
    Map<Integer,Node> map = new HashMap<>();
    int capacity;

    public LRUCache(int capacity) {
      this.capacity = capacity;
      this.head.next = tail;
      this.tail.prev = head;
    }

    public void addNodeNextToHead(Node node) {
      Node headNextNode = head.next;

      head.next = node;

      node.prev = head;
      node.next = headNextNode;

      headNextNode.prev = node;
    }

    public void deleteNode(Node node) {
      Node prevNode = node.prev;
      Node nextNode = node.next;

      prevNode.next = nextNode;
      nextNode.prev = prevNode;
    }

    public int get(int key) {
      // 키를 가져온다
      // 가져온 키는 헤드 앞으로 보낸다
      if(map.containsKey(key)) {
        Node valNode = map.get(key);
        deleteNode(valNode);
        addNodeNextToHead(valNode);
        return valNode.val;
      } else return -1;
    }

    public void put(int key, int value) {
      if (map.containsKey(key)) {
        Node valNode = map.get(key);
        valNode.val = value;
        deleteNode(valNode);
        addNodeNextToHead(valNode);
      } else {
        if(map.size() >= capacity) {
          Node lrNode = tail.prev;
          deleteNode(lrNode);
          map.remove(lrNode.key);
        }
        Node newNode = new Node(key,value);
        addNodeNextToHead(newNode);
        map.put(key,newNode);
      }
    }
  }

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
}
