package framework.runner.auto;

import framework.eq.Equality;
import framework.oracle.Oracle;
import framework.oracle.Reference;
import framework.test.BatchTestSuite;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class DefaultEquality {
  private DefaultEquality() {}

  @SuppressWarnings("unchecked")
  public static <O> Equality<O> forOutputClass(Class<?> outputClass) {
    // int[], long[], boolean[] ...
    if (outputClass != null && outputClass.isArray()) {
      Class<?> comp = outputClass.getComponentType();
      if (comp.isPrimitive()) {
        return (expected, actual) -> {
          if (expected == actual) return true;
          if (expected == null || actual == null) return false;
          // primitive arrays
          if (expected instanceof int[] e && actual instanceof int[] a) return Arrays.equals(e, a);
          if (expected instanceof long[] e && actual instanceof long[] a) return Arrays.equals(e, a);
          if (expected instanceof boolean[] e && actual instanceof boolean[] a) return Arrays.equals(e, a);
          if (expected instanceof double[] e && actual instanceof double[] a) return Arrays.equals(e, a);
          if (expected instanceof char[] e && actual instanceof char[] a) return Arrays.equals(e, a);
          if (expected instanceof byte[] e && actual instanceof byte[] a) return Arrays.equals(e, a);
          if (expected instanceof short[] e && actual instanceof short[] a) return Arrays.equals(e, a);
          if (expected instanceof float[] e && actual instanceof float[] a) return Arrays.equals(e, a);
          // fallback
          return Arrays.equals((Object[]) boxArray(expected), (Object[]) boxArray(actual));
        };
      } else {
        return (expected, actual) -> {
          if (expected == actual) return true;
          if (expected == null || actual == null) return false;
          return Arrays.deepEquals((Object[]) expected, (Object[]) actual);
        };
      }
    }

    // List
    if (outputClass != null && List.class.isAssignableFrom(outputClass)) {
      return Objects::equals; // List.equals does deep element-wise
    }

    // default
    return Objects::equals;
  }

  private static Object boxArray(Object primitiveArray) {
    int n = Array.getLength(primitiveArray);
    Object[] boxed = new Object[n];
    for (int i = 0; i < n; i++) boxed[i] = Array.get(primitiveArray, i);
    return boxed;
  }
}