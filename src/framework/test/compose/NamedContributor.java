package framework.test.compose;

import framework.test.TestCase;
import java.util.List;

public record NamedContributor<I,O>(String name, SuiteContributor<I,O> contributor) {
  public List<TestCase<I,O>> contribute() { return contributor.contribute(); }
}