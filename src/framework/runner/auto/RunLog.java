package framework.runner.auto;

public final class RunLog {
  private RunLog() {}

  public static void run(String scope, String target) {
    System.out.println("[RUN ][" + scope + "] " + target);
  }

  public static void skip(String scope, String target, String reason) {
    System.out.println("[SKIP][" + scope + "] " + target + " : " + reason);
  }

  public static void info(String scope, String msg) {
    System.out.println("[INFO][" + scope + "] " + msg);
  }
}