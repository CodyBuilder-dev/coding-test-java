package framework.runner.auto;

import framework.eq.Equality;
import framework.pretty.PrettyPrinter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DefaultPretty {
  private DefaultPretty() {}

  public static <T> PrettyPrinter<T> defaultPrinter() {
    return v -> {
      if (v == null) return "null";
      Class<?> c = v.getClass();
      if (c.isArray()) {
        // primitive arrays
        if (v instanceof int[] a) return Arrays.toString(a);
        if (v instanceof long[] a) return Arrays.toString(a);
        if (v instanceof boolean[] a) return Arrays.toString(a);
        if (v instanceof double[] a) return Arrays.toString(a);
        if (v instanceof char[] a) return Arrays.toString(a);
        if (v instanceof byte[] a) return Arrays.toString(a);
        if (v instanceof short[] a) return Arrays.toString(a);
        if (v instanceof float[] a) return Arrays.toString(a);
        // object arrays
        return Arrays.deepToString((Object[]) v);
      }
      return String.valueOf(v);
    };
  }
}