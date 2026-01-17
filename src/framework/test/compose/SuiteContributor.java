package framework.test.compose;

import framework.test.TestCase;
import java.util.List;

@FunctionalInterface
public interface SuiteContributor<I,O> {
  List<TestCase<I,O>> contribute();
}