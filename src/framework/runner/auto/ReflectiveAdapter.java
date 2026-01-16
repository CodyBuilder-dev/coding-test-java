package framework.runner.auto;

import framework.core.Solution;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public final class ReflectiveAdapter {
  private ReflectiveAdapter() {}

  public static <I, O> Solution<I, O> adaptSingle(Object raw, String explicitMethodName) {
    Objects.requireNonNull(raw);
    Method m = findSubmitMethod(raw.getClass(), explicitMethodName);
    m.setAccessible(true);

    return new Solution<>() {
      @Override public String name() { return raw.getClass().getSimpleName(); }

      @SuppressWarnings("unchecked")
      @Override public O solve(I input) {
        try {
          // input이 record/POJO라면, "input 하나"로 받는 메서드일 수도 있고
          // "input을 풀어서 여러 파라미터"로 받는 메서드일 수도 있다.
          // -> 규칙: 여러 파라미터 시그니처는 input이 record이고 component들을 펼쳐서 invoke.
          Object res;
          if (m.getParameterCount() == 1) {
            res = m.invoke(raw, input);
          } else {
            Object[] args = Inputs.explodeRecord(input, m.getParameterCount());
            res = m.invoke(raw, args);
          }
          return (O) res;
        } catch (Exception e) {
          throw new RuntimeException("invoke failed: " + raw.getClass().getName() + "#" + m.getName(), e);
        }
      }
    };
  }

  public static <BI, BO> Solution<BI, BO> adaptBatch(Object raw, String explicitMethodName) {
    Objects.requireNonNull(raw);
    Method m = findSubmitMethod(raw.getClass(), explicitMethodName);
    m.setAccessible(true);

    return new Solution<>() {
      @Override public String name() { return raw.getClass().getSimpleName(); }

      @SuppressWarnings("unchecked")
      @Override public BO solve(BI input) {
        try {
          Object res = m.invoke(raw, input);
          return (BO) res;
        } catch (Exception e) {
          throw new RuntimeException("invoke failed: " + raw.getClass().getName() + "#" + m.getName(), e);
        }
      }
    };
  }

  private static Method findSubmitMethod(Class<?> clazz, String explicit) {
    final String name;
    if (explicit == null || explicit.isBlank()) {
      SubmitMethod ann = clazz.getAnnotation(SubmitMethod.class);
      name = (ann != null) ? ann.value() : "solution";
    } else {
      name = explicit;
    }

    Method[] methods = clazz.getMethods();
    Method[] candidates = Arrays.stream(methods)
        .filter(m -> m.getName().equals(name))
        .toArray(Method[]::new);

    if (candidates.length == 0) {
      String avail = Arrays.stream(methods)
          .map(Method::getName)
          .distinct()
          .sorted()
          .reduce((a,b) -> a + ", " + b).orElse("(none)");
      throw new IllegalArgumentException(
          "No method named '" + name + "' in " + clazz.getName() + ". Available: " + avail
      );
    }
    if (candidates.length == 1) return candidates[0];

    // overload: pick the one with most params (common in submit sigs), otherwise first
    Method best = candidates[0];
    for (Method m : candidates) {
      if (m.getParameterCount() > best.getParameterCount()) best = m;
    }
    return best;
  }
}