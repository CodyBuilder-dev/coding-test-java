package framework.test.text;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.util.*;

final class Coercers {
  private Coercers() {}

  static Object coerce(Object v, Class<?> target) {
    if (v == null) return null;

    if (target.isInstance(v)) return v;

    // primitives wrappers
    if (target == int.class || target == Integer.class) return toInt(v);
    if (target == long.class || target == Long.class) return toLong(v);
    if (target == boolean.class || target == Boolean.class) return toBool(v);
    if (target == String.class) return String.valueOf(v);

    // arrays
    if (target.isArray()) {
      Class<?> comp = target.getComponentType();
      return coerceArray(v, comp);
    }

    // record: map by components
    if (target.isRecord()) {
      return coerceRecord(v, target);
    }

    throw new IllegalArgumentException("cannot coerce " + v.getClass().getName() + " to " + target.getName());
  }

  private static int toInt(Object v) {
    if (v instanceof Integer i) return i;
    if (v instanceof Long l) return (int) (long) l;
    if (v instanceof String s) return Integer.parseInt(s.trim());
    throw new IllegalArgumentException("not int: " + v);
  }

  private static long toLong(Object v) {
    if (v instanceof Long l) return l;
    if (v instanceof Integer i) return (long) i;
    if (v instanceof String s) return Long.parseLong(s.trim());
    throw new IllegalArgumentException("not long: " + v);
  }

  private static boolean toBool(Object v) {
    if (v instanceof Boolean b) return b;
    if (v instanceof String s) return Boolean.parseBoolean(s.trim());
    throw new IllegalArgumentException("not boolean: " + v);
  }

  private static Object coerceArray(Object v, Class<?> comp) {
    if (!(v instanceof List<?> list)) {
      // allow scalar to become length-1 array
      Object arr = Array.newInstance(comp, 1);
      Array.set(arr, 0, coerce(v, comp));
      return arr;
    }

    Object arr = Array.newInstance(comp, list.size());
    for (int i = 0; i < list.size(); i++) {
      Object elem = list.get(i);
      Array.set(arr, i, coerce(elem, comp));
    }
    return arr;
  }

  /** For record input: v is expected to be List<Object> representing components */
  private static Object coerceRecord(Object v, Class<?> recordClass) {
    if (!(v instanceof List<?> list)) {
      throw new IllegalArgumentException("record needs list components: " + recordClass.getName());
    }
    RecordComponent[] comps = recordClass.getRecordComponents();
    if (list.size() != comps.length) {
      throw new IllegalArgumentException("record arity mismatch: " + recordClass.getName()
          + " expects " + comps.length + " but got " + list.size());
    }

    Object[] args = new Object[comps.length];
    Class<?>[] types = new Class<?>[comps.length];
    for (int i = 0; i < comps.length; i++) {
      types[i] = comps[i].getType();
      args[i] = coerce(list.get(i), types[i]);
    }

    try {
      return recordClass.getDeclaredConstructor(types).newInstance(args);
    } catch (Exception e) {
      throw new RuntimeException("failed to instantiate record: " + recordClass.getName(), e);
    }
  }
}