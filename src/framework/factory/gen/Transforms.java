package framework.factory.gen;

import java.util.Arrays;

public final class Transforms {
  private Transforms() {}

  // ---------- String transforms ----------

  public static String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
  }

  public static String duplicate(String s, int times) {
    if (times < 0) throw new IllegalArgumentException("times<0");
    return s.repeat(times);
  }

  /** flip one char to ensure likely different (same length 유지) */
  public static String flipOneCharLower(Rand rnd, String s) {
    if (s.isEmpty()) return s;
    char[] c = s.toCharArray();
    int idx = rnd.nextInt(0, c.length - 1);
    char old = c[idx];
    c[idx] = (old == 'a') ? 'b' : 'a';
    return new String(c);
  }

  // ---------- int[] transforms ----------

  public static int[] reversedCopy(int[] a) {
    int[] b = a.clone();
    for (int i = 0, j = b.length - 1; i < j; i++, j--) {
      int t = b[i]; b[i] = b[j]; b[j] = t;
    }
    return b;
  }

  public static int[] sortedCopy(int[] a) {
    int[] b = a.clone();
    Arrays.sort(b);
    return b;
  }

  public static int[] shuffledCopy(Rand rnd, int[] a) {
    int[] b = a.clone();
    Generators.shuffleIntsInPlace(rnd, b);
    return b;
  }

  public static int[] concat(int[] a, int[] b) {
    int[] c = Arrays.copyOf(a, a.length + b.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }

  public static int[] append(int[] a, int v) {
    int[] b = Arrays.copyOf(a, a.length + 1);
    b[a.length] = v;
    return b;
  }

  public static int[] scale(int[] a, int k) {
    int[] b = a.clone();
    for (int i = 0; i < b.length; i++) b[i] *= k;
    return b;
  }

  public static int[] addConst(int[] a, int c) {
    int[] b = a.clone();
    for (int i = 0; i < b.length; i++) b[i] += c;
    return b;
  }

  /** add small random noise to each element */
  public static int[] addNoise(Rand rnd, int[] a, int noiseLo, int noiseHi) {
    int[] b = a.clone();
    for (int i = 0; i < b.length; i++) b[i] += rnd.nextInt(noiseLo, noiseHi);
    return b;
  }

  // ---------- int[][] transforms ----------

  public static int[][] transposeJaggedUnsafe(int[][] m) {
    // 주의: jagged(행 길이 다름)에서는 의미가 애매. 직사각이면 OK
    int rows = m.length;
    int cols = (rows == 0) ? 0 : m[0].length;
    int[][] t = new int[cols][rows];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) t[j][i] = m[i][j];
    }
    return t;
  }
}