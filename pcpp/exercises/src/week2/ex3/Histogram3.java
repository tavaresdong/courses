package week2.ex3;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Histogram3 implements Histogram {
  private final AtomicInteger[] counts;

  public Histogram3(int span) {
    this.counts = new AtomicInteger[span];
    for (int i = 0; i < span; i++) {
      this.counts[i] = new AtomicInteger(0);
    }
  }

  public void increment(int bin) {
    counts[bin].incrementAndGet();
  }
  public int getCount(int bin) {
    return counts[bin].get();
  }
  public int getSpan() {
    return counts.length;
  }

  @Override
  public int[] getBins() {
    int[] arr = new int[counts.length];
    for (int i = 0; i < counts.length; i++) {
      arr[i] = counts[i].get();
    }
    return arr;
  }
}
