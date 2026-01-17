package framework.test.compose;

import framework.test.TestSuite;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SuiteSet {
  private final Map<String, TestSuite<?,?>> suites = new LinkedHashMap<>();
  public SuiteSet add(String name, TestSuite<?,?> suite) { suites.put(name, suite); return this; }
  public Map<String, TestSuite<?,?>> asMap() { return suites; }
}