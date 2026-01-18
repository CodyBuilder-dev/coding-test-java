package framework.io;

import framework.test.TestCase;
import framework.test.TestSuite;
import framework.test.text.CoercersPublic;
import framework.test.text.ValueParserPublic;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public final class PairedListFileSuiteLoader {
  private PairedListFileSuiteLoader() {}

  public static <I, O> TestSuite<I, O> load(
      String suiteName,
      Path inputsFile,
      Path outputsFile,
      Class<I> inputClass,
      Class<O> outputClass
  ) {
    List<String> inLines = readNonEmptyLines(inputsFile);
    List<String> outLines = readNonEmptyLines(outputsFile);

    int arity = inputArity(inputClass);

    if (inLines.size() % arity != 0) {
      throw new IllegalArgumentException("inputs lines count not divisible by arity=" + arity
          + " (lines=" + inLines.size() + ") file=" + inputsFile);
    }

    int caseCount = inLines.size() / arity;
    if (outLines.size() != caseCount) {
      throw new IllegalArgumentException("case count mismatch: inputs cases=" + caseCount
          + " outputs lines=" + outLines.size()
          + " inputsFile=" + inputsFile + " outputsFile=" + outputsFile);
    }

    List<TestCase<I, O>> cases = new ArrayList<>(caseCount);
    for (int ci = 0; ci < caseCount; ci++) {
      // parse inputs for this case
      List<Object> parts = new ArrayList<>(arity);
      for (int k = 0; k < arity; k++) {
        String tok = inLines.get(ci * arity + k);
        parts.add(ValueParserPublic.parse(tok));
      }

      Object parsedExpected = ValueParserPublic.parse(outLines.get(ci));

      I input = buildInput(inputClass, parts, ci);
      @SuppressWarnings("unchecked")
      O expected = (O) CoercersPublic.coerce(parsedExpected, outputClass);

      cases.add(TestCase.expect(input, expected, "file#" + ci));
    }

    return TestSuite.of(suiteName, cases);
  }

  private static int inputArity(Class<?> inputClass) {
    return inputClass.isRecord() ? inputClass.getRecordComponents().length : 1;
  }

  private static <I> I buildInput(Class<I> inputClass, List<Object> parts, int caseIndex) {
    Object v;
    if (inputClass.isRecord()) {
      v = parts; // record components list
    } else {
      if (parts.size() != 1) throw new IllegalArgumentException("non-record input requires arity=1");
      v = parts.get(0);
    }
    @SuppressWarnings("unchecked")
    I input = (I) CoercersPublic.coerce(v, inputClass);
    return input;
  }

  private static List<String> readNonEmptyLines(Path file) {
    try {
      List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
      List<String> out = new ArrayList<>();
      for (String l : lines) {
        String t = l.trim();
        if (t.isEmpty()) continue;
        if (t.startsWith("#") || t.startsWith("//")) continue;
        out.add(t);
      }
      return out;
    } catch (IOException e) {
      throw new RuntimeException("failed to read: " + file, e);
    }
  }
}
