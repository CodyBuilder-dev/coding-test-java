package LeetCode;

/**
 * Java에서 100단위 이상의 자리수는 int, long으로 해결할 수 없다는 것을 간과한 잘못된 풀이
 */
public class LeetCode2_1 {
  public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    long l1Long = changeListNodeToLong(l1);
    long l2Long = changeListNodeToLong(l2);

    return changeLongToListNode(l1Long + l2Long);
  }

  public long changeListNodeToLong(ListNode l){
    long i = 0;
    int digit = 0;

    if(l == null) return 0;

    while(l.next != null){
      i += l.val * (long) Math.pow(10, digit);
      digit++;
      l = l.next;
    }
    // 마지막 원소 처리
    i += l.val * (long) Math.pow(10, digit);

    return i;
  }


  public ListNode changeLongToListNode(Long integer){
    if( integer == 0 ) return new ListNode();

    String intString = String.valueOf(integer);
    String[] intDigitString = intString.split("");

    ListNode last = new ListNode(Integer.parseInt(intDigitString[0]));
    ListNode first = last;

    for(int i = 1 ; i < intDigitString.length ; i ++){
      first =  new ListNode(Integer.parseInt(intDigitString[i]));
      first.next = last;
      last = first;
    }

    return first;
  }
}
