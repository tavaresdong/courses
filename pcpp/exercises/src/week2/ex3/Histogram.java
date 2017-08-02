package week2.ex3;

/**
 * Created by yuchend on 17-8-2.
 */
public interface Histogram {
  void increment(int bin);
  int getCount(int bin);
  int getSpan();
  public int[] getBins();
}

