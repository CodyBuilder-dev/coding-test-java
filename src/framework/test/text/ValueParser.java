package framework.test.text;

import java.util.*;

final class ValueParser {
  private ValueParser() {}

  static Object parseValue(String s) {
    s = s.trim();
    if (s.isEmpty()) throw new IllegalArgumentException("empty value");

    if (s.equals("null")) return null;
    if (s.equals("true")) return Boolean.TRUE;
    if (s.equals("false")) return Boolean.FALSE;

    if (isQuoted(s)) return unquote(s);

    if (s.startsWith("[") && s.endsWith("]")) {
      return parseArray(s);
    }

    // number (int/long 우선)
    if (looksLikeNumber(s)) {
      // long 범위면 Long, 아니면 Integer
      long v = Long.parseLong(s);
      if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) return (int) v;
      return v;
    }

    // fallback: bare word as String
    return s;
  }

  /** returns List<Object> (elements can be List for nested arrays) */
  private static List<Object> parseArray(String s) {
    String inner = s.substring(1, s.length() - 1).trim();
    if (inner.isEmpty()) return List.of();

    List<Object> out = new ArrayList<>();
    int i = 0;
    while (i < inner.length()) {
      // skip spaces
      while (i < inner.length() && Character.isWhitespace(inner.charAt(i))) i++;

      int start = i;
      int end = findValueEnd(inner, start);
      String token = inner.substring(start, end).trim();
      if (!token.isEmpty()) out.add(parseValue(token));

      i = end;
      // skip spaces
      while (i < inner.length() && Character.isWhitespace(inner.charAt(i))) i++;
      // optional comma
      if (i < inner.length() && inner.charAt(i) == ',') i++;
    }
    return out;
  }

  /** find end index for one value token in array context (handles quotes & nested arrays) */
  private static int findValueEnd(String s, int start) {
    int i = start;
    int depth = 0;
    boolean inQuote = false;
    char quote = 0;

    while (i < s.length()) {
      char c = s.charAt(i);

      if (inQuote) {
        if (c == quote && (i == start || s.charAt(i - 1) != '\\')) {
          inQuote = false;
        }
        i++;
        continue;
      }

      if (c == '"' || c == '\'') {
        inQuote = true;
        quote = c;
        i++;
        continue;
      }

      if (c == '[') { depth++; i++; continue; }
      if (c == ']') { depth--; i++; continue; }

      if (depth == 0 && c == ',') break;
      i++;
    }
    return i;
  }

  private static boolean isQuoted(String s) {
    return s.length() >= 2
        && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
  }

  private static String unquote(String s) {
    String body = s.substring(1, s.length() - 1);
    // minimal unescape: \" and \'
    body = body.replace("\\\"", "\"").replace("\\'", "'");
    body = body.replace("\\n", "\n").replace("\\t", "\t").replace("\\\\", "\\");
    return body;
  }

  private static boolean looksLikeNumber(String s) {
    int i = 0;
    if (s.charAt(0) == '-') i++;
    if (i == s.length()) return false;
    for (; i < s.length(); i++) {
      if (!Character.isDigit(s.charAt(i))) return false;
    }
    return true;
  }
}