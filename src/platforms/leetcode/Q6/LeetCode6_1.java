package platforms.leetcode.Q6;

/**
 * 구현을 시뮬레이션한 풀이
 */
public class LeetCode6_1 {
  public String convert(String s, int numRows) {
    if(numRows == 1) return s;

    String[][] sMatrix = new String[numRows][1000];

    String[] sArray = s.split("");
    int i = 0;
    int j = 0;
    boolean down = true;

    int pointer = 0;
    while(pointer < sArray.length) {
      boolean downNext = isNextDown(i,j,numRows,down);
      int iNext = getNextI(i,j,numRows,down);
      int jNext = getNextJ(i,j,numRows,down);

      sMatrix[i][j] = sArray[pointer];
      i = iNext; j = jNext; down = downNext;
      pointer++;
    }

    StringBuilder sb = new StringBuilder();
    for(int row = 0; row < numRows; row++) {
      for(int col=0; col < sMatrix[0].length; col++){
        if(sMatrix[row][col] != null) sb.append(sMatrix[row][col]);
      }
    }
    return sb.toString();
  }
  public int getNextI(int i, int j, int numRows, boolean down){
    if(down) {
      if(i < numRows -1) {
        return i+1;
      } else {
        return i-1;
      }
    } else {
      if ( i > 0 ){
        return i-1;
      } else {
        return i+1;
      }
    }
  }

  public int getNextJ(int i, int j, int numRows, boolean down) {
    if(down) {
      if(i < numRows -1){
        return j;
      } else {
        return j+1;
      }
    } else {
      if (i > 0) {
        return j + 1;
      } else {
        return j;
      }
    }
  }

  public boolean isNextDown(int i, int j, int numRows, boolean down) {
    if(down) {
      if(i < numRows -1) {
        return true;
      } else {
        return false;
      }
    } else {
      if(i > 0) {
        return false;
      } else {
        return true;
      }
    }
  }
}
