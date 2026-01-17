package framework.factory.gen;

import framework.oracle.Oracle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class Rand {
  private final Random r;

  public Rand(long seed) { this.r = new Random(seed); }

  public int nextInt(int loInclusive, int hiInclusive) {
    if (loInclusive > hiInclusive) throw new IllegalArgumentException("lo>hi");
    int bound = hiInclusive - loInclusive + 1;
    return loInclusive + r.nextInt(bound);
  }

  public boolean nextBool() { return r.nextBoolean(); }

  public char nextUpper() { return (char) ('A' + r.nextInt(26)); }
  public char nextLower() { return (char) ('a' + r.nextInt(26)); }

  public double nextDouble01() { return r.nextDouble(); }

  public <T> T pick(T[] arr) { return arr[r.nextInt(arr.length)]; }
}