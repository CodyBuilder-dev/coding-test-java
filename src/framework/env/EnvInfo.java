package framework.env;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public final class EnvInfo {

  public static void printJvmEnvironment() {
    Runtime rt = Runtime.getRuntime();
    RuntimeMXBean rmx = ManagementFactory.getRuntimeMXBean();
    List<String> args = rmx.getInputArguments();

    System.out.println("\n================ JVM Environment ================");
    System.out.println("java.version   : " + System.getProperty("java.version"));
    System.out.println("java.vendor    : " + System.getProperty("java.vendor"));
    System.out.println("os.name        : " + System.getProperty("os.name"));
    System.out.println("os.arch        : " + System.getProperty("os.arch"));
    System.out.println("os.version     : " + System.getProperty("os.version"));
    System.out.println("available.cpus : " + rt.availableProcessors());

    // 입력 JVM 옵션들
    System.out.println("jvm.args       : " + args);

    // Xms/Xmx 추출(있으면)
    System.out.println("Xms            : " + findArgPrefix(args, "-Xms"));
    System.out.println("Xmx            : " + findArgPrefix(args, "-Xmx"));

    // 런타임 힙 상태(현재/최대)
    System.out.println("heap.total(MB) : " + (rt.totalMemory() / 1024 / 1024));
    System.out.println("heap.free(MB)  : " + (rt.freeMemory() / 1024 / 1024));
    System.out.println("heap.max(MB)   : " + (rt.maxMemory() / 1024 / 1024));
    System.out.println("=================================================\n");
  }

  private static String findArgPrefix(List<String> args, String prefix) {
    for (String a : args) {
      if (a != null && a.startsWith(prefix)) return a;
    }
    return "(not set)";
  }
}
