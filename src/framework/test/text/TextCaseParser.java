package framework.test.text;

import framework.test.TestCase;
import java.util.List;

@FunctionalInterface
public interface TextCaseParser<I, O> {
  List<TestCase<I, O>> parse(String rawText);
}
