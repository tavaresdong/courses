package week2.ex3;

import java.util.concurrent.atomic.LongAdder;

// Q: why is LongAdder more scalable than AtomicInteger?
public class Histogram5 implements Histogram {
  private final LongAdder[] counts;

  public Histogram5(int span) {
    this.counts = new LongAdder[span];
    for (int i = 0; i < span; i++) {
      this.counts[i] = new LongAdder();
    }
  }

  public void increment(int bin) {
    counts[bin].increment();
  }
  public int getCount(int bin) {
    return counts[bin].intValue();
  }
  public int getSpan() {
    return counts.length;
  }

  @Override
  public int[] getBins() {
    int[] arr = new int[counts.length];
    for (int i = 0; i < counts.length; i++) {
      arr[i] = counts[i].intValue();
    }
    return arr;
  }
}
