package LeetCode;

import exceptions.WrongAnswerException;
import java.security.SecureRandom;

public class LeetCode2 {
  public static void main(String[] args) {
    LeetCode2_2 leetCode2_2 = new LeetCode2_2();
    LeetCode2_3 leetCode2_3 = new LeetCode2_3();

    for (int i = 0; i < 100; ++i) {
      ListNode l1 = createRandomListNode();
      ListNode l2 = createRandomListNode();

      ListNode result2 = leetCode2_2.addTwoNumbers(l1, l2);
      ListNode result3 = leetCode2_2.addTwoNumbers(l1, l2);


      System.out.println("L1 :");
      StringBuilder l1String = new StringBuilder();
      while (l1 != null){
        l1String.append(l1.val).append(",");
        l1 = l1.next;
      }
      System.out.println(l1String);


      System.out.println("L2 :");
      StringBuilder l2String = new StringBuilder();
      while (l2 != null){
        l2String.append(l2.val).append(",");
        l2 = l2.next;
      }
      System.out.println(l2String);

      System.out.println("Result2 :");
      StringBuilder result2String = new StringBuilder();
      while (result2 != null){
        result2String.append(result2.val).append(",");
        result2 = result2.next;
      }
      System.out.println(result2String);

      System.out.println("Result3 :");
      StringBuilder result3String = new StringBuilder();
      while (result3 != null){
        result3String.append(result3.val).append(",");
        result3 = result3.next;
      }
      System.out.println(result3String);

      if (!result2String.toString().equals(result3String.toString())) {
        throw new WrongAnswerException();
      }

    }

    System.out.println("정답입니다.");
  }

  public static ListNode createRandomListNode() {
    SecureRandom sr = new SecureRandom();
    int length = sr.nextInt(100)+1;

    ListNode first = new ListNode();
    ListNode pointer = first;

    first.val = sr.nextInt(10);
    for (int i = 1; i < length; ++i) {
      ListNode nextNode = new ListNode();
      nextNode.val = sr.nextInt(10);
      pointer.next = nextNode;
      pointer = nextNode;
    }

    return first;
  }
}
