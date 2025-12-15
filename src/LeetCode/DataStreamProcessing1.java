package LeetCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 최초 1번 정렬해놓고, 그 이후로 add(int val) 호출시마다 O(N)으로 삽입
 */
public class DataStreamProcessing1 {

  public static void main(String[] args) {
    List<Integer> l = new ArrayList<>();
    List<Integer> l2 = new ArrayList<>();
    l.add(1);
    l.add(1, 2);
    System.out.println(l);
    l.add(0, 3);
    System.out.println(l);

  }
}
