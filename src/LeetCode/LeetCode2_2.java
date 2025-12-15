package LeetCode;

public class LeetCode2_2 {
  public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode first = new ListNode();
    ListNode firstPointer = first;

    int carry = 0;
    // 둘중 하나라도 존재하거나 carry가 존재하면 진행
    while(l1 != null || l2 != null || carry != 0) {
      int l1Val = l1 != null ? l1.val : 0;
      int l2Val = l2 != null ? l2.val : 0;

      firstPointer.val = (l1Val + l2Val + carry) % 10;
      carry = (l1Val + l2Val + carry) / 10 >= 1 ? 1 : 0;

      // 다음으로 넘어갈 수 있는 경우에만 끝난 노드 추가
      if((l1 != null && l1.next != null) || (l2 != null && l2.next != null) || carry != 0) {
        firstPointer.next = new ListNode();
        firstPointer = firstPointer.next;
      }

      l1 = l1 != null ? l1.next : null;
      l2 = l2 != null ? l2.next : null;
    }

    // 둘 다 미존재할 경우 반환
    return first;
  }
}
