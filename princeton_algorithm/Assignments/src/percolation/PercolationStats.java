package percolation;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private int round;
    private int size;
    private double[] fractions = null;
    
    private double meanVal;
    private double stddevVal;

    public PercolationStats(int N, int T) 
    {
        if (N <= 0 || T <= 0) 
            throw new IllegalArgumentException();
        round = T;
        size = N;
        fractions = new double[round];
        
        for (int i = 0; i < T; i++) {
            fractions[i] = doSimulation();
        }
        
        // Order cannot change
        meanVal = StdStats.mean(fractions);
        stddevVal = StdStats.stddev(fractions);
    }
    
    private double doSimulation()
    {
        Percolation percolation = new Percolation(size);
        int numOpen = 0;
        while (!percolation.percolates()) {
            int r = StdRandom.uniform(1, size + 1);
            int c = StdRandom.uniform(1, size + 1);
            while (percolation.isOpen(r, c)) {
                r = StdRandom.uniform(1, size + 1);
                c = StdRandom.uniform(1, size + 1);
            }
            percolation.open(r, c);
            numOpen++;
        }
        
        return ((double) numOpen) / ((double) size * size);
    }
    
    public double mean()
    {
        return meanVal;
    }
    
    public double stddev()
    {
        return stddevVal;
    }
    
    public double confidenceLo()
    {
        return meanVal - (1.96 * stddevVal) / Math.sqrt(round);
    }
    
    public double confidenceHi()
    {
        return meanVal + (1.96 * stddevVal) / Math.sqrt(round);
    }
    
    public static void main(String[] args)
    {
        if (args.length != 2)
            throw new IllegalArgumentException();
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        
        PercolationStats percStat = new PercolationStats(N, T);
        StdOut.println("mean                    = " + percStat.mean());
        StdOut.println("stddev                  = " + percStat.stddev());
        StdOut.println("95% confidence interval = " 
        + percStat.confidenceLo() + ", " + percStat.confidenceHi());
    }
}
