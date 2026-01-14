package framework.oracle;

public record CheckResult(boolean ok, String message) {
  public static CheckResult pass() { return new CheckResult(true, ""); }
  public static CheckResult fail(String msg) { return new CheckResult(false, msg); }
}