package LeetCode.Q6;

/**
 * 규칙을 찾는 풀이
 */
public class LeetCode6_2 {
  public String convert(String s, int numRows) {
    if(numRows == 1) return s;
    int cycle = 2*numRows - 2;

    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < numRows; ++i) {
      int index = i;
      boolean isFirst = true;
      while(index < s.length()){
        if(i == 0 || i == numRows - 1) { // 끝행은 2*numRows - 2 주기로 움직임
          sb.append(s.charAt(index));
          index+=cycle;
        } else { // 중간행은 첫점프와 뒷점프 간격이 다름
          sb.append(s.charAt(index));
          if(isFirst) {
            index += (numRows - 1 - i)*2;
          } else {
            index += i*2;
          }
          isFirst = !isFirst;
        }
      }
    }

    return sb.toString();
  }
}
