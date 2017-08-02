package week2.ex3;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class Histogram4 implements Histogram {
  private final AtomicIntegerArray counts;

  public Histogram4(int span) {
    this.counts = new AtomicIntegerArray(span);
  }

  public void increment(int bin) {
    counts.incrementAndGet(bin);
  }
  public int getCount(int bin) {
    return counts.get(bin);
  }
  public int getSpan() {
    return counts.length();
  }

  @Override
  public int[] getBins() {
    int[] arr = new int[counts.length()];
    for (int i = 0; i < counts.length(); i++) {
      arr[i] = counts.get(i);
    }
    return arr;
  }
}
