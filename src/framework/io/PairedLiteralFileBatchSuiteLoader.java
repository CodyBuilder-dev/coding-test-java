package framework.io;

import framework.oracle.Oracle;
import framework.test.BatchTestCase;
import framework.test.BatchTestSuite;
import framework.test.text.LiteralParsers;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class PairedLiteralFileBatchSuiteLoader {
  private PairedLiteralFileBatchSuiteLoader() {}

  public interface BatchAdapter<EI, EO, BI, BO> {
    List<EI> splitInputs(BI batchInput);
    List<EO> splitOutputs(BO batchOutput);
    Function<List<EI>, BI> batchInputBuilder();
  }

  /** BI, BO가 배열(또는 List)일 때 쓰는 기본 어댑터 */
  @SuppressWarnings("unchecked")
  public static <EI, EO, BI, BO> BatchAdapter<EI, EO, BI, BO> arrayAdapter(Class<BI> biClass, Class<BO> boClass) {
    return new BatchAdapter<>() {
      @Override public List<EI> splitInputs(BI batchInput) { return (List<EI>) splitArrayOrList(batchInput, false); }
      @Override public List<EO> splitOutputs(BO batchOutput) { return (List<EO>) splitArrayOrList(batchOutput, true); }
      @Override public Function<List<EI>, BI> batchInputBuilder() {
        return eis -> (BI) buildArrayOrListFrom(biClass, eis);
      }
    };
  }

  public static <EI, EO, BI, BO> Map<String, BatchTestSuite<EI, EO, BI>> loadSuites(
      String suiteNamePrefix,
      Path inputsFile,
      Path outputsFile,
      Class<BI> batchInputClass,
      Class<BO> batchOutputClass,
      BatchAdapter<EI, EO, BI, BO> adapter,
      Oracle<EI, EO> suiteOracle
  ) {
    requireExists(inputsFile);
    requireExists(outputsFile);

    List<String> inLines = readNonEmptyLines(inputsFile);
    List<String> outLines = readNonEmptyLines(outputsFile);

    if (inLines.size() != outLines.size()) {
      throw new IllegalStateException(
          "Line count mismatch: inputs=" + inLines.size() + ", outputs=" + outLines.size()
      );
    }

    Map<String, BatchTestSuite<EI, EO, BI>> suites = new LinkedHashMap<>();

    for (int line = 0; line < inLines.size(); line++) {
      BI bi = LiteralParsers.parse(inLines.get(line), batchInputClass);
      BO bo = LiteralParsers.parse(outLines.get(line), batchOutputClass);

      List<EI> eis = adapter.splitInputs(bi);
      List<EO> eos = adapter.splitOutputs(bo);

      if (eis.size() != eos.size()) {
        throw new IllegalStateException(
            "Batch element count mismatch at line " + (line + 1)
                + " : inputs=" + eis.size() + ", outputs=" + eos.size()
        );
      }

      List<BatchTestCase<EI, EO>> cases = new ArrayList<>(eis.size());
      for (int i = 0; i < eis.size(); i++) {
        cases.add(BatchTestCase.expect(
            eis.get(i),
            eos.get(i),
            suiteNamePrefix + "#" + line + "::" + i,
            "examples"
        ));
      }

      BatchTestSuite<EI, EO, BI> suite = BatchTestSuite.of(
          suiteNamePrefix + "#" + line,
          cases,
          adapter.batchInputBuilder(),
          suiteOracle
      );

      suites.put("examples_auto#" + line, suite);
    }

    return suites;
  }

  // -------- array/list helpers --------

  private static List<?> splitArrayOrList(Object x, boolean boxingPrimitives) {
    if (x == null) return List.of();
    if (x instanceof List<?> l) return l;

    Class<?> c = x.getClass();
    if (!c.isArray()) throw new IllegalArgumentException("Not an array/list: " + c.getName());

    int n = Array.getLength(x);
    List<Object> out = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      // Array.get boxes primitives automatically
      out.add(Array.get(x, i));
    }
    return out;
  }

  private static Object buildArrayOrListFrom(Class<?> containerClass, List<?> elems) {
    if (List.class.isAssignableFrom(containerClass)) return new ArrayList<>(elems);
    if (containerClass.isArray()) {
      Class<?> comp = containerClass.getComponentType();
      Object arr = Array.newInstance(comp, elems.size());
      for (int i = 0; i < elems.size(); i++) Array.set(arr, i, elems.get(i));
      return arr;
    }
    throw new IllegalArgumentException("Unsupported BI container: " + containerClass.getName());
  }

  // -------- file helpers --------

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