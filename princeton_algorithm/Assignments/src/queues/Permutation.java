package queues;


import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args)
    {
        if (args.length != 1)
            throw new IllegalArgumentException();
        int k = Integer.parseInt(args[0]);
        
        RandomizedQueue<String> rq = 
                    new RandomizedQueue<String>();
        
        while (!StdIn.isEmpty()) {
            String str = StdIn.readString();
            rq.enqueue(str);
        }
        
        while (k > 0) {
            StdOut.println(rq.dequeue());
            k--;
        }
    }

}
