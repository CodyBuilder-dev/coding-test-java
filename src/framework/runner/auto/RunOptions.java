package framework.runner.auto;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class RunOptions {
  private final Set<String> includePlatforms;   // e.g. "programmers", "leetcode"
  private final Pattern includeName;            // match problem name
  private final Pattern excludeName;
  private final Set<String> includeSuites;      // e.g. "smoke", "random"
  private final boolean listOnly;

  private RunOptions(Builder b) {
    this.includePlatforms = Set.copyOf(b.includePlatforms);
    this.includeName = b.includeName;
    this.excludeName = b.excludeName;
    this.includeSuites = Set.copyOf(b.includeSuites);
    this.listOnly = b.listOnly;
  }

  public static Builder builder() { return new Builder(); }

  public boolean listOnly() { return listOnly; }

  public boolean shouldRunModule(AutoModule m) {
    String name = m.name(); // e.g. "programmers/signedsum"
    String platform = extractPlatform(name);

    if (!includePlatforms.isEmpty() && !includePlatforms.contains(platform)) return false;
    if (includeName != null && !includeName.matcher(name).find()) return false;
    if (excludeName != null && excludeName.matcher(name).find()) return false;
    return true;
  }

  public boolean shouldRunSuite(String suiteTag) {
    if (includeSuites.isEmpty()) return true; // default: run all suites the module provides
    return includeSuites.contains(suiteTag);
  }

  private static String extractPlatform(String name) {
    int idx = name.indexOf('/');
    return idx >= 0 ? name.substring(0, idx) : name;
  }

  public static final class Builder {
    private final Set<String> includePlatforms = new LinkedHashSet<>();
    private Pattern includeName = null;
    private Pattern excludeName = null;
    private final Set<String> includeSuites = new LinkedHashSet<>();
    private boolean listOnly = false;

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

    public Builder listOnly(boolean v) {
      this.listOnly = v;
      return this;
    }

    public RunOptions build() { return new RunOptions(this); }
  }
}
