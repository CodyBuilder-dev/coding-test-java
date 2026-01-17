package framework.runner.auto;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class RunOptions {
  public enum Verbosity { QUIET, NORMAL, VERBOSE }

  public final Verbosity verbosity;

  private final Set<String> includePlatforms;   // e.g. "programmers", "leetcode"
  private final Pattern includeName;            // match problem name
  private final Pattern excludeName;
  private final Set<String> includeSuites;      // e.g. "smoke", "random"
  // optional: meta 태그 같은 것들
  public final Set<String> includeTags; // empty => no tag filtering
  private final boolean listOnly;

  private RunOptions(Builder b) {
    this.verbosity = b.verbosity;
    this.includePlatforms = Set.copyOf(b.includePlatforms);
    this.includeName = b.includeName;
    this.excludeName = b.excludeName;
    this.includeSuites = Set.copyOf(b.includeSuites);
    this.includeTags = Set.copyOf(b.includeTags);
    this.listOnly = b.listOnly;
  }

  public static Builder builder() { return new Builder(); }

  public static final class Builder {
    private Verbosity verbosity = null;
    private final Set<String> includePlatforms = new LinkedHashSet<>();
    private Pattern includeName = null;
    private Pattern excludeName = null;
    private final Set<String> includeSuites = new LinkedHashSet<>();
    private final Set<String> includeTags = new LinkedHashSet<>();
    private boolean listOnly = false;

    public Builder verbosity(Verbosity verbosity) {
      this.verbosity = verbosity;
      return this;
    }

    public Builder includePlatform(String... platforms) {
      includePlatforms.addAll(Arrays.asList(platforms));
      return this;
    }

    public Builder includeNameRegex(String regex) {
      this.includeName = Pattern.compile(regex);
      return this;
    }

    public Builder excludeNameRegex(String regex) {
      this.excludeName = Pattern.compile(regex);
      return this;
    }

    /** run only these suite tags: "smoke", "random", "regression"... */
    public Builder includeSuites(String... tags) {
      includeSuites.addAll(Arrays.asList(tags));
      return this;
    }

    public Builder includeTags(String... tags) {
      includeTags.addAll(Arrays.asList(tags));
      return this;
    }

    public Builder listOnly(boolean v) {
      this.listOnly = v;
      return this;
    }

    public RunOptions build() { return new RunOptions(this); }
  }

  public boolean listOnly() { return listOnly; }

  public boolean shouldRunSuite(String suiteTag) {
    if (includeSuites.isEmpty()) return true; // default: run all suites the module provides
    return includeSuites.contains(suiteTag);
  }

  private static String extractPlatform(String name) {
    int idx = name.indexOf('/');
    return idx >= 0 ? name.substring(0, idx) : name;
  }

  // ---------------- explain helpers ----------------

  /** null => include, non-null => skip reason
   * @param m*/
  public String explainSkipModule(AutoModule m) {
    String moduleName = m.name();
    String platform = extractPlatform(moduleName);

    if (!includePlatforms.isEmpty() && !includePlatforms.contains(platform)) {
      return "module not in includePlatforms";
    }
    if (includeName != null && !includeName.matcher(moduleName).find()) {
      return "module does not match includeName";
    }
    if (excludeName != null && excludeName.matcher(moduleName).find()) {
      return "module matches excludeName";
    }
    return null;
  }

  /** suiteFullName는 보통 moduleName + "/" + suiteName 등 네 방식에 맞춰 */
  public String explainSkipSuite(String suiteNameOrKey) {
    if (!includeSuites.isEmpty() && !includeSuites.contains(suiteNameOrKey)) {
      return "suite not in includeSuites";
    }
    return null;
  }

  /** 케이스 태그 필터 (원하면 케이스 로그까지) */
  public String explainSkipCaseTags(Set<String> caseTags) {
    if (includeTags == null || includeTags.isEmpty()) return null;
    if (caseTags == null || caseTags.isEmpty()) return "case has no tags (includeTags active)";
    for (String t : caseTags) {
      if (includeTags.contains(t)) return null; // one-of match
    }
    return "case tags not matched includeTags";
  }
}
