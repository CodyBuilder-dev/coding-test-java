package framework.eq;

import framework.eq.Equality;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public final class Equalities {
  private Equalities() {}

  // 일반 객체 비교 (Boolean, Integer, String, record 등)
  public static <T> Equality<T> objects() {
    return new Equality<>() {
      @Override public boolean same(T e, T a) { return Objects.equals(e, a); }
    };
  }

  // primitive arrays
  public static Equality<int[]> intArray() {
    return new Equality<>() {
      @Override public boolean same(int[] e, int[] a) { return Arrays.equals(e, a); }
      @Override public String explainMismatch(int[] e, int[] a) {
        return "expected=" + Arrays.toString(e) + ", actual=" + Arrays.toString(a);
      }
    };
  }

  public static Equality<long[]> longArray() {
    return new Equality<>() {
      @Override public boolean same(long[] e, long[] a) { return Arrays.equals(e, a); }
    };
  }

  public static Equality<boolean[]> booleanArray() {
    return new Equality<>() {
      @Override public boolean same(boolean[] e, boolean[] a) { return Arrays.equals(e, a); }
    };
  }

  // deep array compare (Object[] / 다차원)
  public static Equality<Object[]> deepObjectArray() {
    return new Equality<>() {
      @Override public boolean same(Object[] e, Object[] a) { return Arrays.deepEquals(e, a); }
    };
  }

  // 변환 후 비교(예: 출력 포맷 바꾸고 비교)
  public static <T, K> Equality<T> byKey(Function<T, K> keyFn) {
    return new Equality<>() {
      @Override public boolean same(T e, T a) { return Objects.equals(keyFn.apply(e), keyFn.apply(a)); }
      @Override public String explainMismatch(T e, T a) {
        return "expectedKey=" + keyFn.apply(e) + ", actualKey=" + keyFn.apply(a);
      }
    };
  }
}
