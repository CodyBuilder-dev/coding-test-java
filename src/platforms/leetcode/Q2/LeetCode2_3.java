package platforms.leetcode.Q2;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Java에서 BigInteger 을 이용한 풀이
 */
public class LeetCode2_3 {
  public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    BigInteger l1Big = changeListNodeToBigInteger(l1);
    BigInteger l2Big = changeListNodeToBigInteger(l2);

    return changeBigIntegerToListNode(l1Big.add(l2Big));
  }

  public BigInteger changeListNodeToBigInteger(ListNode l){
    BigInteger i = BigInteger.ZERO;
    int digit = 0;

    if(l == null) return BigInteger.ZERO;

    while(l.next != null){
      BigInteger lBig = BigInteger.valueOf(l.val);
      i = i.add(lBig.multiply(BigDecimal.valueOf(Math.pow(10, digit)).toBigInteger()));

      digit++;
      l = l.next;
    }
    // 마지막 원소 처리
    BigInteger lBig = BigInteger.valueOf(l.val);
    i = i.add(lBig.multiply(BigDecimal.valueOf(Math.pow(10, digit)).toBigInteger()));

    return i;
  }


  public ListNode changeBigIntegerToListNode(BigInteger bigInteger){
    if(bigInteger.equals(BigInteger.ZERO)) return new ListNode();

    String intString = String.valueOf(bigInteger);
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
