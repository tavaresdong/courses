package week2.ex3;// For week 2
// sestoft@itu.dk * 2014-09-04

import java.util.Arrays;

public class SimpleHistogram {
  public static void main(String[] args) {
    final Histogram histogram = new Histogram1(30);
    histogram.increment(7);
    histogram.increment(13);
    histogram.increment(7);
    dump(histogram);
  }

  public static void dump(Histogram histogram) {
    int totalCount = 0;
    for (int bin = 0; bin < histogram.getSpan(); bin++) {
      System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
      totalCount += histogram.getCount(bin);
    }
    System.out.printf("      %9d%n", totalCount);
  }
}


class Histogram1 implements Histogram {
  private int[] counts;
  public Histogram1(int span) {
    this.counts = new int[span];
  }
  public void increment(int bin) {
    counts[bin] = counts[bin] + 1;
  }
  public int getCount(int bin) {
    return counts[bin];
  }
  public int getSpan() {
    return counts.length;
  }
  public int[] getBins() { return Arrays.copyOf(counts, counts.length);}
}

