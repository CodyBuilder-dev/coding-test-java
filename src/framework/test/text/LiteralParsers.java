package framework.test.text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses "array literal" texts like:
 *   ["A","B"], [[1,2],[3,4]], [true,false], "hello", 123
 * Not strict JSON; tolerant with whitespace, smart quotes, and leading/trailing junk.
 */
public final class LiteralParsers {
  private LiteralParsers() {}

  /** Normalize smart quotes and strip junk before first '[' or '"' or digit/minus/letter */
  public static String sanitize(String s) {
    if (s == null) return "";
    String t = s.trim()
        .replace('“', '"').replace('”', '"')
        .replace('‘', '\'').replace('’', '\'');
    // If it contains '[', strip everything before first '[' (common for noisy logs)
    int b = t.indexOf('[');
    if (b >= 0) {
      t = t.substring(b);
      int e = t.lastIndexOf(']');
      if (e >= 0 && e + 1 < t.length()) t = t.substring(0, e + 1);
      return t.trim();
    }
    // If it contains '"', strip before first '"'
    int q = t.indexOf('"');
    if (q >= 0) return t.substring(q).trim();
    // else keep as-is (numbers/booleans)
    return t.trim();
  }

  /** Parse to supported targets: primitive/wrapper, String, T[], T[][] */
  @SuppressWarnings("unchecked")
  public static <T> T parse(String raw, Class<T> targetType) {
    String s = sanitize(raw);
    Tokenizer tk = new Tokenizer(s);

    Object val = parseValue(tk, targetType);
    tk.skipWs();
    // tolerate trailing junk
    return (T) val;
  }

  // -------- core parsing dispatch --------

  private static Object parseValue(Tokenizer tk, Class<?> targetType) {
    tk.skipWs();

    if (targetType.isArray()) {
      Class<?> comp = targetType.getComponentType();
      if (comp.isArray()) {
        // T[][] : parse outer list of inner arrays
        return parse2DArray(tk, comp, targetType);
      } else {
        // T[] : parse 1D array
        return parse1DArray(tk, comp);
      }
    }

    // scalar
    if (targetType == String.class) return tk.readStringFlexible();
    if (targetType == int.class || targetType == Integer.class) return tk.readInt();
    if (targetType == long.class || targetType == Long.class) return tk.readLong();
    if (targetType == double.class || targetType == Double.class) return tk.readDouble();
    if (targetType == boolean.class || targetType == Boolean.class) return tk.readBoolean();

    throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
  }

  private static Object parse1DArray(Tokenizer tk, Class<?> elemType) {
    tk.expect('[');
    List<Object> elems = new ArrayList<>();
    tk.skipWs();
    if (tk.peek(']')) { tk.next(); return newArray(elemType, elems); }

    while (true) {
      tk.skipWs();
      elems.add(parseValue(tk, elemType));
      tk.skipWs();
      if (tk.peek(',')) { tk.next(); continue; }
      break;
    }
    tk.skipWs();
    tk.expect(']');
    return newArray(elemType, elems);
  }

  /**
   * outerType is elem array class, e.g. String[] for String[][]
   * targetType is full 2D class, e.g. String[][].class
   */
  private static Object parse2DArray(Tokenizer tk, Class<?> outerElemArrayType, Class<?> targetType) {
    // targetType is something[][]; component is something[]
    tk.expect('[');
    List<Object> rows = new ArrayList<>();
    tk.skipWs();
    if (tk.peek(']')) { tk.next(); return newArray(outerElemArrayType, rows); }

    while (true) {
      tk.skipWs();
      // parse inner array of base element type
      Object row = parseValue(tk, outerElemArrayType);
      rows.add(row);
      tk.skipWs();
      if (tk.peek(',')) { tk.next(); continue; }
      break;
    }
    tk.skipWs();
    tk.expect(']');
    return newArray(outerElemArrayType, rows);
  }

  private static Object newArray(Class<?> componentType, List<Object> elems) {
    Object arr = Array.newInstance(componentType, elems.size());
    for (int i = 0; i < elems.size(); i++) {
      Array.set(arr, i, elems.get(i));
    }
    return arr;
  }

  // -------- tokenizer --------

  private static final class Tokenizer {
    private final String s;
    private int i;

    Tokenizer(String s) { this.s = s == null ? "" : s; }

    void skipWs() {
      while (i < s.length()) {
        char c = s.charAt(i);
        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') i++;
        else break;
      }
    }

    boolean peek(char c) {
      skipWs();
      return i < s.length() && s.charAt(i) == c;
    }

    void expect(char c) {
      skipWs();
      if (i >= s.length() || s.charAt(i) != c) {
        throw new IllegalStateException("Expected '" + c + "' at pos " + i + " in: " + s);
      }
      i++;
    }

    void next() { if (i < s.length()) i++; }

    String readStringFlexible() {
      skipWs();
      if (i < s.length()) {
        char c = s.charAt(i);
        if (c == '"') return readString();                 // 기존 방식
        if (c == '“' || c == '”') return readSmartQuoted(); // 스마트따옴표도 지원(있으면)
      }
      // 따옴표가 없으면 bareword를 문자열로 읽음
      return readBareWordString();
    }

    String readSmartQuoted() {
      skipWs();
      if (i >= s.length()) throw new IllegalStateException("Expected string at pos " + i + " in: " + s);
      char q = s.charAt(i);
      if (q != '“' && q != '”') throw new IllegalStateException("Expected smart-quoted string at pos " + i + " in: " + s);
      i++; // open
      StringBuilder sb = new StringBuilder();
      while (i < s.length()) {
        char c = s.charAt(i++);
        if (c == '“' || c == '”') break; // close
        if (c == '\\' && i < s.length()) sb.append(s.charAt(i++));
        else sb.append(c);
      }
      return sb.toString();
    }

    String readString() {
      skipWs();
      if (i >= s.length() || s.charAt(i) != '"') {
        throw new IllegalStateException("Expected string at pos " + i + " in: " + s);
      }
      i++; // "
      StringBuilder sb = new StringBuilder();
      while (i < s.length()) {
        char c = s.charAt(i++);
        if (c == '"') break;
        if (c == '\\' && i < s.length()) { // basic escaping
          sb.append(s.charAt(i++));
        } else {
          sb.append(c);
        }
      }
      return sb.toString();
    }

    /** abc, ABC_DEF, foo-bar 같은 토큰을 문자열로 읽음 */
    String readBareWordString() {
      skipWs();
      int start = i;
      while (i < s.length()) {
        char c = s.charAt(i);
        // 구분자에서 멈춤
        if (c == ',' || c == ']' || c == '[' || c == ' ' || c == '\t' || c == '\r' || c == '\n')
          break;
        i++;
      }
      if (i == start) {
        throw new IllegalStateException("Expected bareword string at pos " + i + " in: " + s);
      }
      return s.substring(start, i);
    }

    int readInt() { return (int) readLong(); }

    long readLong() {
      skipWs();
      int sign = 1;
      if (i < s.length() && s.charAt(i) == '-') { sign = -1; i++; }
      int start = i;
      long val = 0;
      while (i < s.length()) {
        char c = s.charAt(i);
        if (c < '0' || c > '9') break;
        val = val * 10 + (c - '0');
        i++;
      }
      if (i == start) throw new IllegalStateException("Expected number at pos " + i + " in: " + s);
      return val * sign;
    }

    double readDouble() {
      skipWs();
      int start = i;
      // permissive: read until delimiter
      while (i < s.length()) {
        char c = s.charAt(i);
        if (c == ',' || c == ']' || c == ' ' || c == '\t' || c == '\r' || c == '\n') break;
        i++;
      }
      if (i == start) throw new IllegalStateException("Expected double at pos " + i + " in: " + s);
      String token = s.substring(start, i);
      return Double.parseDouble(token);
    }

    boolean readBoolean() {
      skipWs();
      if (s.startsWith("true", i)) { i += 4; return true; }
      if (s.startsWith("false", i)) { i += 5; return false; }
      throw new IllegalStateException("Expected boolean at pos " + i + " in: " + s);
    }
  }
}
