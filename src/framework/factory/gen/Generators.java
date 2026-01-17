package framework.factory.gen;

import framework.factory.gen.Rand;
import framework.factory.inputs.IntArrayPair;
import framework.factory.inputs.IntBoolArrayPair;
import framework.factory.inputs.StrPair;
import java.util.Arrays;

public final class Generators {
  private Generators() {}

  // --------- Strings ---------

  public static String stringLower(Rand rnd, int minLen, int maxLen) {
    int n = rnd.nextInt(minLen, maxLen);
    char[] c = new char[n];
    for (int i = 0; i < n; i++) c[i] = rnd.nextLower();
    return new String(c);
  }

  public static String stringFromAlphabet(Rand rnd, int minLen, int maxLen, String alphabet) {
    int n = rnd.nextInt(minLen, maxLen);
    char[] c = new char[n];
    for (int i = 0; i < n; i++) c[i] = alphabet.charAt(rnd.nextInt(0, alphabet.length() - 1));
    return new String(c);
  }

  // --------- Arrays ---------

  public static int[] intArray(Rand rnd, int n, int lo, int hi) {
    int[] a = new int[n];
    for (int i = 0; i < n; i++) a[i] = rnd.nextInt(lo, hi);
    return a;
  }

  public static int[] intArray(Rand rnd, int minLen, int maxLen, int lo, int hi) {
    return intArray(rnd, rnd.nextInt(minLen, maxLen), lo, hi);
  }

  public static boolean[] boolArray(Rand rnd, int n) {
    boolean[] a = new boolean[n];
    for (int i = 0; i < n; i++) a[i] = rnd.nextBool();
    return a;
  }

  public static int[][] int2D(Rand rnd, int rows, int colsMin, int colsMax, int lo, int hi) {
    int[][] m = new int[rows][];
    for (int i = 0; i < rows; i++) m[i] = intArray(rnd, rnd.nextInt(colsMin, colsMax), lo, hi);
    return m;
  }

  public static int[][] int2D(Rand rnd, int rowsMin, int rowsMax, int colsMin, int colsMax, int lo, int hi) {
    return int2D(rnd, rnd.nextInt(rowsMin, rowsMax), colsMin, colsMax, lo, hi);
  }

  // --------- Length-matched pair generators ---------

  /** (String s, String t) of same length; optionally make t a permutation of s */
  public static StrPair anagramPair(Rand rnd, int minLen, int maxLen, boolean makeTrue) {
    String s = stringLower(rnd, minLen, maxLen);
    String t;
    if (makeTrue) {
      t = permuteString(rnd, s);
    } else {
      // ensure likely false: change one char (same length 유지)
      char[] c = s.toCharArray();
      int idx = rnd.nextInt(0, c.length - 1);
      c[idx] = (c[idx] == 'a') ? 'b' : 'a';
      t = new String(c);
    }
    return new StrPair(s, t);
  }

  /** (int[] a, int[] b) same length */
  public static IntArrayPair intArrayPairSameLen(Rand rnd, int minLen, int maxLen, int lo, int hi) {
    int n = rnd.nextInt(minLen, maxLen);
    return new IntArrayPair(intArray(rnd, n, lo, hi), intArray(rnd, n, lo, hi));
  }

  /** (int[] ints, boolean[] bools) same length */
  public static IntBoolArrayPair intBoolPairSameLen(Rand rnd, int minLen, int maxLen, int intLo, int intHi) {
    int n = rnd.nextInt(minLen, maxLen);
    return new IntBoolArrayPair(intArray(rnd, n, intLo, intHi), boolArray(rnd, n));
  }

  // --------- helpers ---------

  public static void shuffleCharsInPlace(Rand rnd, char[] a) {
    for (int i = a.length - 1; i > 0; i--) {
      int j = rnd.nextInt(0, i);
      char t = a[i]; a[i] = a[j]; a[j] = t;
    }
  }

  public static String permuteString(Rand rnd, String s) {
    char[] c = s.toCharArray();
    shuffleCharsInPlace(rnd, c);
    return new String(c);
  }

  public static void shuffleIntsInPlace(Rand rnd, int[] a) {
    for (int i = a.length - 1; i > 0; i--) {
      int j = rnd.nextInt(0, i);
      int t = a[i]; a[i] = a[j]; a[j] = t;
    }
  }

  public static int[] sortedCopy(int[] a) {
    int[] b = a.clone();
    Arrays.sort(b);
    return b;
  }

  public static String sortChars(String s) {
    char[] c = s.toCharArray();
    Arrays.sort(c);
    return new String(c);
  }
}