package week2.ex1;// For week 2
// sestoft@itu.dk * 2014-08-29

import java.util.concurrent.atomic.AtomicInteger;

class MyAtomicInteger {
  private int value;
  public MyAtomicInteger(int v) {
    value = v;
  }

  public synchronized int addAndGet(int amount) {
    value += amount;
    return value;
  }

  public synchronized int get() {
    return value;
  }
}

class TestCountFactors {

  public static void main(String[] args) {
    //sequentialCount();
    parallelCount();
  }

  public static void sequentialCount() {
    final int range = 5_000_000;
    int count = 0;
    long startTime = System.nanoTime();
    for (int p = 0; p < range; p++)
      count += countFactors(p);
    long endTime = System.nanoTime();
    System.out.printf("Total number of factors is %9d%n", count);
    System.out.printf("Total time spent is %d%n", (endTime - startTime));
  }

  public static void parallelCount() {
    final int range = 5_000_000;
    final int piece = 5_000_00;
    final int concurrency = 10;
    final AtomicInteger totalCount = new AtomicInteger(0);
    int span = (int) Math.sqrt((double)range);

    week2.ex3.Histogram histogram = new week2.ex3.Histogram5(span);

    Thread[] threads = new Thread[concurrency];
    for (int i = 0; i < concurrency; i++) {
      final int tnum = i;
      threads[i] = new Thread(() -> {
        for (int pos = tnum * piece; pos < (tnum + 1) * piece; pos++) {
          int primeFactors = countPrimeFactors(pos);
          //totalCount.addAndGet(factors);
          histogram.increment(primeFactors);
        }
      });
    }

    long startTime = System.nanoTime();
    for (Thread t : threads) {
      t.start();
    }

    try {
      for (Thread t : threads) {
        t.join();
      }
      week2.ex3.SimpleHistogram.dump(histogram);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    long endTime = System.nanoTime();
    //System.out.printf("Total number of factors is %9d%n", totalCount.get());
    //System.out.printf("Total time spent is %d%n", (endTime - startTime));
  }

  public static int countFactors(int p) {
    if (p < 2) 
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
	      factorCount++;
	      p /= k;
      } else 
	      k++;
    }
    return factorCount;
  }

  static boolean isPrime(int p) {
    int factors = countFactors(p);
    if (factors == 1)
      return true;
    return false;
  }

  public static int countPrimeFactors(int p) {
    if (p < 2)
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0 && isPrime(k)) {
        factorCount++;
        p /= k;
      } else
        k++;
    }
    return factorCount;
  }

}
