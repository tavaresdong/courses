package puzzle;
import java.util.ArrayList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class Board {
    
    private final int dim;
    private final int[][] board;
    
    public Board(int[][] blocks)
    {
        dim = blocks.length;
        board = new int[dim][dim];
        
        // Defensive copy
        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j) {
                board[i][j] = blocks[i][j];
            }
    }
    
    /**
     * @return Dimension of the board
     */
    public int dimension() { return dim; }
    
    /**
     * Calculate the hamming distance by iterating over the N * N board
     * @return hamming distance(#blocks not on their places), 
     * not including the moves made
     */
    public int hamming() 
    {
        int ham = -1;
        for (int i = 0; i < dim * dim; i++) {
            if (board[i / dim][i % dim] != i + 1)
                ham++;
        }
        return ham;
    }
    
    /**
     * Calculate the manhattan distance by iterating over the N * N spots
     * and sum of the vertical and horizontal difference to their intended places
     * @return manhattan distance
     */
    public int manhattan() 
    {
        int man = 0;
        for (int i = 0; i < dim * dim; i++) {
            int r = i / dim, c = i % dim;
            if (board[r][c] == 0)
                continue;
            int manr = (board[r][c] - 1) / dim;
            int manc = (board[r][c] - 1) % dim;

            man += Math.abs(manr - r) + Math.abs(manc - c);
        }
        return man;
    }
    
    public boolean isGoal() 
    {
        if (hamming() == 0) 
            return true;
        return false;
    }
    
    /**
     * Create a Board by swapping a pair int the original Board
     * the pair should not contain empty block
     * @return the swapped twin Board of current Board
     */
    public Board twin() 
    {
        int r1 = 0, c1 = 0, r2 = 0, c2 = 0;
        for (r1 = 0; r1 < dim; r1++) {
            for (c1 = 0; c1 < dim; c1++) {
                if (board[r1][c1] != 0)
                    break;
            }
            if (c1 < dim)
                break;
        }
        
        for (r2 = dim - 1; r2 >= 0; r2--) {
            for (c2 = dim - 1; c2 >= 0; c2--)
                if (board[r2][c2] != 0)
                    break;
            if (c2 < dim)
                break;
        }
        
        return swapAndCreate(r1, c1, r2, c2);
    }
    
    /*
     * Swap the two blocks of original board
     * and create a new board
     */
    private Board swapAndCreate(int r1, int c1, int r2, int c2) 
    {
        int[][] nboard = new int[dim][dim];
        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j)
                nboard[i][j] = board[i][j];
        nboard[r1][c1] = board[r2][c2];
        nboard[r2][c2] = board[r1][c1];
        
        return new Board(nboard);
    }
    
    /**
     * equals method for the Board type:
     * check reference and class first, then do a conversion and
     * check internal data structures 
     */
    public boolean equals(Object y) 
    {
        if (this == y) return true;
        if (y == null || this.getClass() != y.getClass())
            return false;
        Board b = (Board) y;
        
        if (dim != b.dim)
            return false;
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++)
                if (board[i][j] != b.board[i][j])
                    return false;
        return true;
    }
    
    public Iterable<Board> neighbors() 
    {
        // Find the empty block
        int i = 0, j = 0;
        for (i = 0; i < dim; i++) {
            for (j = 0; j < dim; j++)
                if (board[i][j] == 0)
                    break;
            if (j != dim)
                break;
        }
        
        ArrayList<Board> list = new ArrayList<Board>();
        if (i > 0)
            list.add(swapAndCreate(i, j, i - 1, j));
        if (i < dim - 1)
            list.add(swapAndCreate(i, j, i + 1, j));
        if (j > 0)
            list.add(swapAndCreate(i, j, i, j - 1));
        if (j < dim - 1)
            list.add(swapAndCreate(i, j, i, j + 1));
        
        return list;
    }
    
    public String toString() 
    {
        StringBuffer output = new StringBuffer();
        output.append(dim);
        output.append("\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                output.append(board[i][j]);
                if (j < dim - 1)
                    output.append(" ");
            }
            output.append("\n");
        }
        return output.toString();
    }

    public static void main(String[] args) 
    {
        In in = new In("data\\8puzzle\\puzzle09.txt");
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        
        Board initial = new Board(blocks);
        StdOut.println(initial);
        
    }
}
