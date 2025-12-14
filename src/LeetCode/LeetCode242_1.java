package LeetCode;

import java.util.HashMap;
import java.util.Map;

public class LeetCode242_1 {

  public LeetCode242_1() {
  }

  public boolean isAnagram(String s, String t) {
    Map<String, Integer> sMap = new HashMap();
    Map<String, Integer> tMap = new HashMap();

    String[] sList = s.split("");
    String[] tList = t.split("");

    for(String sChar: sList){
      putStringCharInMap(sMap, sChar);
    }
    for(String tChar: tList) {
      putStringCharInMap(tMap, tChar);
    }

    return sMap.equals(tMap);
  }


  public void putStringCharInMap(Map<String,Integer> map, String charFromString) {
    int i;
    if ( map.get(charFromString) == null ) {i = 0;}
    else {i = map.get(charFromString)+1;}

    map.put(charFromString, i);
  }
}
