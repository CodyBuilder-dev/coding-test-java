package framework.test.text;

public final class ValueParserPublic {
  private ValueParserPublic() {}

  public static Object parse(String token) {
    return ValueParser.parseValue(token);
  }
}