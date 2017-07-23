package percolation;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    // size N: N * N square
    private final int size;

    // arr[i][j] == true means this node is open
    private boolean[][] arr;
    private int openSites;

    private final WeightedQuickUnionUF uf, ufFull;
    private int top, bottom;
    
    public Percolation(int N) 
    {
        if (N <= 0) 
            throw new IllegalArgumentException();
        size = N;
        top = N * N;
        bottom = N * N + 1;
        openSites = 0;
        
        arr = new boolean[N][N];
        uf = new WeightedQuickUnionUF(N * N + 2);
        ufFull = new WeightedQuickUnionUF(N * N + 1);
    }
    
    public void open(int i, int j) 
    {
        if (i < 1 || size < i || j < 1 || size < j)
            throw new IllegalArgumentException();
        if (isOpen(i, j)) return;
        arr[i - 1][j - 1] = true;
        openSites++;
        
        int index = getIndex(i, j);
        if (i == 1) {
            uf.union(index, top);
            ufFull.union(index, top);
        }
        if (i == size) {
            uf.union(index, bottom);
        }
        tryUnion(i, j, i - 1, j);
        tryUnion(i, j, i + 1, j);
        tryUnion(i, j, i, j - 1);
        tryUnion(i, j, i, j + 1);
    }
    
    // Try to see if neighbor is valid and open, then union these two nodes
    private void tryUnion(int curX, int curY, int neiX, int neiY) 
    {
        // Check four possible neighbors
        if (0 < neiX && neiX <= size && 0 < neiY && neiY <= size) {
            if (isOpen(neiX, neiY)) {
                uf.union(getIndex(curX, curY), 
                         getIndex(neiX, neiY));
                ufFull.union(getIndex(curX, curY),
                             getIndex(neiX, neiY));
            }
        }
    }
    
    private int getIndex(int i, int j) 
    {
        return (i - 1) * size + (j - 1);
    }
    
    public boolean isOpen(int i, int j) 
    {
        if (i < 1 || size < i || j < 1 || size < j)
            throw new IllegalArgumentException();
        
        return arr[i - 1][j - 1];
    }
    
    public boolean isFull(int i, int j) 
    {
        if (i < 1 || size < i || j < 1 || size < j)
            throw new IllegalArgumentException();
        if (isOpen(i, j) && ufFull.connected(getIndex(i, j), top))
            return true;
        return false;
        
    }
    
    public boolean percolates() 
    {
        return uf.connected(top, bottom);
    }

    public int numberOfOpenSites()
    {
        return openSites;
    }
    
    public static void main(String[] args) 
    {
        
    }

}
