package framework.io;

import framework.test.TestSuite;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExampleSuites {
  private ExampleSuites() {}

  /**
   * 관례:
   *   <projectRoot>/src/<anchor package path>/examples/inputs.txt
   *   <projectRoot>/src/<anchor package path>/examples/outputs.txt
   *
   * 예: anchor=platforms.programmers.Q76501.tests.SignedSumFileSuites
   * => src/platforms/programmers/Q76501/tests/examples/inputs.txt
   */
  public static <I, O> TestSuite<I, O> fromStandardFiles(
      Class<?> anchorClass,         // Module.class OR tests.*.class
      Class<I> inputClass,
      Class<O> outputClass
  ) {
    return fromStandardFiles(anchorClass, "examples", "inputs.txt", "outputs.txt", inputClass, outputClass);
  }

  /** 커스텀 폴더/파일명도 지원 */
  public static <I, O> TestSuite<I, O> fromStandardFiles(
      Class<?> anchorClass,
      String examplesDirName,       // usually "examples"
      String inputsFileName,        // usually "inputs.txt"
      String outputsFileName,       // usually "outputs.txt"
      Class<I> inputClass,
      Class<O> outputClass
  ) {
    Path examplesDir = resolveExamplesDir(anchorClass, examplesDirName);

    Path inFile = examplesDir.resolve(inputsFileName);
    Path outFile = examplesDir.resolve(outputsFileName);

    requireExists(examplesDir, "'/test/examples' dir", anchorClass, examplesDirName);
    requireExists(inFile, "'inputs.txt' file", anchorClass, examplesDirName);
    requireExists(outFile, "'outputs.txt' file", anchorClass, examplesDirName);

    return PairedListFileSuiteLoader.load(
        anchorClass.getName() + "/" + examplesDirName,
        inFile,
        outFile,
        inputClass,
        outputClass
    );
  }

  /**
   * anchor가 tests 패키지 안에 있든 없든 자동 보정:
   * - anchor pkg가 ...tests 로 끝나면 그대로
   * - 아니면 pkg + ".tests"로 보정
   * 그리고 그 아래 examplesDirName을 붙임
   */
  private static Path resolveExamplesDir(Class<?> anchorClass, String examplesDirName) {
    Path root = ProjectPaths.projectRoot();
    String pkg = anchorClass.getPackageName();
    String testsPkg = pkg.endsWith(".tests") ? pkg : (pkg + ".tests");

    return root.resolve("src")
        .resolve(testsPkg.replace('.', '/'))
        .resolve(examplesDirName);
  }


  private static void requireExists(Path p, String what, Class<?> anchorClass, String examplesDirName) {
    if (!Files.exists(p)) {
      Path workdir = Path.of("").toAbsolutePath().normalize();
      Path root = ProjectPaths.projectRoot().toAbsolutePath().normalize();

      String pkg = anchorClass.getPackageName();
      String testsPkg = pkg.endsWith(".tests") ? pkg : (pkg + ".tests");

      throw new IllegalStateException(
          "Required " + what + " not found.\n" +
              "  anchor      : " + anchorClass.getName() + "\n" +
              "  anchorPkg   : " + pkg + "\n" +
              "  resolvedPkg : " + testsPkg + "\n" +
              "  examplesDir : " + examplesDirName + "\n" +
              "  workdir     : " + workdir + "\n" +
              "  projectRoot : " + root + "\n" +
              "  missing     : " + p.toAbsolutePath().normalize()
      );
    }
  }
}