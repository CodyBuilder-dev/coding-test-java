package framework.runner.auto;

import framework.core.Solution;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Objects;

public final class Inputs {
  private Inputs() {}

  public static Object[] explodeRecord(Object input, int expectedArity) {
    if (input == null) throw new IllegalArgumentException("input is null but method expects " + expectedArity + " args");

    Class<?> c = input.getClass();
    if (!c.isRecord()) {
      throw new IllegalArgumentException("method expects " + expectedArity +
          " args, but input is not a record: " + c.getName());
    }

    RecordComponent[] comps = c.getRecordComponents();
    if (comps.length != expectedArity) {
      throw new IllegalArgumentException("record arity mismatch: record has " + comps.length +
          " components but method expects " + expectedArity);
    }

    Object[] args = new Object[expectedArity];
    try {
      for (int i = 0; i < comps.length; i++) {
        args[i] = comps[i].getAccessor().invoke(input);
      }
      return args;
    } catch (Exception e) {
      throw new RuntimeException("failed to explode record input: " + c.getName(), e);
    }
  }
}