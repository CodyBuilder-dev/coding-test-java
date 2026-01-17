package framework.factory.tests;

import framework.test.TestCase;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Mixes cases from buckets (small/medium/large) by weights.
 * Good for benchmarking and catching corner sizes.
 */
public final class SizeBucketsFactory {
  private SizeBucketsFactory() {}

  public record Bucket<I>(String name, int weight, Supplier<I> gen) {}

  public static <I, O> List<TestCase<I, O>> withExpectedBuckets(
      int totalCount,
      List<Bucket<I>> buckets,
      Function<I, O> expectedFn,
      String descPrefix,
      long seed
  ) {
    if (totalCount <= 0) return List.of();
    if (buckets == null || buckets.isEmpty()) throw new IllegalArgumentException("no buckets");

    // weighted choice
    int sumW = buckets.stream().mapToInt(Bucket::weight).sum();
    if (sumW <= 0) throw new IllegalArgumentException("sum weight <= 0");

    Random r = new Random(seed);
    List<TestCase<I, O>> out = new ArrayList<>(totalCount);

    for (int i = 0; i < totalCount; i++) {
      Bucket<I> b = pickWeighted(buckets, sumW, r.nextInt(sumW));
      I in = b.gen().get();
      O exp = expectedFn.apply(in);
      out.add(TestCase.expect(in, exp, descPrefix + "/" + b.name() + "#" + i));
    }
    return out;
  }

  private static <I> Bucket<I> pickWeighted(List<Bucket<I>> buckets, int sumW, int roll) {
    int acc = 0;
    for (Bucket<I> b : buckets) {
      acc += b.weight();
      if (roll < acc) return b;
    }
    return buckets.get(buckets.size() - 1);
  }
}
