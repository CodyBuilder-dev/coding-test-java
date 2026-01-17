package framework.test.text;

import framework.test.TestCase;
import framework.test.TestSuite;

import java.util.List;

public final class TextSuites {
  private TextSuites() {}

  public static <I, O> TestSuite<I, O> fromText(String suiteName, String raw, TextCaseParser<I, O> parser) {
    List<TestCase<I, O>> cases = parser.parse(raw);
    return TestSuite.of(suiteName, cases);
  }
}
