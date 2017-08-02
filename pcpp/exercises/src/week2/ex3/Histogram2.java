package week2.ex3;

import java.util.Arrays;

/**
 * Created by yuchend on 17-8-2.
 */
public class Histogram2 implements Histogram {
  private final int[] counts;
  public Histogram2(int span) {
    this.counts = new int[span];
  }
  public synchronized void increment(int bin) {
    counts[bin] = counts[bin] + 1;
  }
  public synchronized int getCount(int bin) {
    return counts[bin];
  }
  public int getSpan() {
    return counts.length;
  }

  @Override
  public synchronized int[] getBins() {
    // Either return a defensive copy
    // or return an unmodifiable view of the internal array
    // If you simply return the internal array(escape)
    // Clients can mess up with synchronized increment and make the histogram
    // thread unsafe.
    return Arrays.copyOf(counts, counts.length);
  }
}

