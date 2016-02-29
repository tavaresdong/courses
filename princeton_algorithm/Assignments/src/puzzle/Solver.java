package puzzle;

import java.util.Comparator;
import java.util.ArrayDeque;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    
    private static final Comparator<Node> BY_HAMMING   = new ByHamming();
    private static final Comparator<Node> BY_MANHATTAN = new ByManhattan();
    private final boolean svble;
    private final int moves;
    private final ArrayDeque<Board> solution;
    
    private class Node {
        private Node prev;
        private Board curBoard;
        private Board prevBoard;
        private int moves;
        
        Node(Board cur, Board pre, int mvs, Node prevNode) 
        {
            curBoard  = cur;
            prevBoard = pre;
            moves     = mvs;
            prev      = prevNode;
        }
    }

    
    private static class ByHamming implements Comparator<Node>
    {

        @Override
        public int compare(Node o1, Node o2) {
            int ham1 = o1.moves + o1.curBoard.hamming();
            int ham2 = o2.moves + o2.curBoard.hamming();
            
            if (ham1 < ham2)
                return -1;
            else if (ham1 == ham2)
                return 0;
            else
                return 1;
        }
    }
    
    private static class ByManhattan implements Comparator<Node>
    {

        @Override
        public int compare(Node o1, Node o2) {
            int manhattan1 = o1.moves + o1.curBoard.manhattan();
            int manhattan2 = o2.moves + o2.curBoard.manhattan();
            
            if (manhattan1 < manhattan2)
                return -1;
            else if (manhattan1 == manhattan2)
                return 0;
            else
                return 1;
        }
    }

    
    public Solver(Board initial)
    {        
        Node nd = trySolve(initial);
        if (nd == null) {
            svble = false;
            moves    = -1;
            solution = null;
        } else {
            svble = true;
            moves    = nd.moves;
            solution = new ArrayDeque<Board>();
            while (nd != null) {
                solution.push(nd.curBoard);
                nd = nd.prev;
            }
        }
    }
    
    private Node trySolve(Board initial) 
    {
        MinPQ<Node> pq0 = new MinPQ<Node>(BY_MANHATTAN);
        MinPQ<Node> pq1 = new MinPQ<Node>(BY_MANHATTAN);
        
        pq0.insert(new Node(initial, null, 0, null));
        pq1.insert(new Node(initial.twin(), null, 0, null));
                
        boolean solvable = false;
        boolean twinSolvable = false;
        
        Node result = null;
        
        while (!solvable && !twinSolvable) {
            if (!pq0.isEmpty()) {
                Node node = pq0.delMin();
                if (node.curBoard.isGoal()) {
                    result = node;
                    solvable = true;
                } else {
                    // Find all neighbors and insert then to pq
                    for (Board nb : node.curBoard.neighbors()) {
                        if (node.prevBoard == null || 
                            !nb.equals(node.prevBoard)) {
                            pq0.insert(new Node(nb, node.curBoard, 
                                    node.moves + 1, node));
                        }
                    }
                }
            }
            if (!pq1.isEmpty()) {
                Node node = pq1.delMin();
                if (node.curBoard.isGoal()) {
                    twinSolvable = true;
                } else {
                    for (Board nb : node.curBoard.neighbors()) {
                        if (node.prevBoard == null || 
                            !nb.equals(node.prevBoard)) {
                            pq1.insert(new Node(nb, node.curBoard,
                                    node.moves + 1, node));
                        }
                    }
                }
            }
        }
                
        return result;
    }

    public boolean isSolvable()
    {
        return svble;
    }
    
    public int moves()
    {
        return moves;
    }
    
    public Iterable<Board> solution()
    {
        return solution;
    }
    
    public static void main(String[] args)
    {
        In in = new In("data\\8puzzle\\puzzle27.txt");
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        
        Board initial = new Board(blocks);
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

    }
}
