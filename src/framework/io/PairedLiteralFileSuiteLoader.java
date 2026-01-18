package framework.io;

import framework.test.TestCase;
import framework.test.TestSuite;
import framework.test.text.LiteralParsers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PairedLiteralFileSuiteLoader {
  private PairedLiteralFileSuiteLoader() {}

  public static <I,O> TestSuite<I,O> load(
      String name,
      Path inputsFile,
      Path outputsFile,
      Class<I> inputClass,
      Class<O> outputClass
  ) {
    requireExists(inputsFile);
    requireExists(outputsFile);

    List<String> inLines = readNonEmptyLines(inputsFile);
    List<String> outLines = readNonEmptyLines(outputsFile);

    if (inLines.size() != outLines.size()) {
      throw new IllegalStateException("Line count mismatch: inputs=" + inLines.size() + ", outputs=" + outLines.size());
    }

    List<TestCase<I,O>> cases = new ArrayList<>(inLines.size());
    for (int i = 0; i < inLines.size(); i++) {
      I in = LiteralParsers.parse(inLines.get(i), inputClass);
      O out = LiteralParsers.parse(outLines.get(i), outputClass);
      cases.add(TestCase.expect(in, out, name + "#" + i, "examples"));
    }

    return TestSuite.of(name, cases, null);
  }

  private static void requireExists(Path p) {
    if (!Files.exists(p)) throw new IllegalStateException("Required file not found: " + p.toAbsolutePath().normalize());
  }

  private static List<String> readNonEmptyLines(Path p) {
    try {
      List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
      List<String> out = new ArrayList<>();
      for (String s : lines) {
        if (s == null) continue;
        String t = s.trim();
        if (!t.isEmpty()) out.add(t);
      }
      return out;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file: " + p.toAbsolutePath().normalize(), e);
    }
  }
}