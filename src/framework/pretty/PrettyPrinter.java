package framework.pretty;

@FunctionalInterface
public interface PrettyPrinter<T> {
  String print(T value);

  static <T> PrettyPrinter<T> defaultPrinter() {
    return String::valueOf;
  }
}