package framework.test.text;

public final class CoercersPublic {
  private CoercersPublic() {}

  public static Object coerce(Object v, Class<?> target) {
    return Coercers.coerce(v, target);
  }
}