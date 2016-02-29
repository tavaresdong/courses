package collinear;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    
    private int numSegs;
    private Point[] pts;
    private final LineSegment[] segs;

    public BruteCollinearPoints(Point[] points)
    {
        if (points == null)
            throw new NullPointerException();
        for (int i = 0; i < points.length; i++)
            for (int j = i + 1; j < points.length; j++) {
                Point pi = points[i];
                Point pj = points[j];
                if (pi == null || pj == null)
                    throw new NullPointerException();
                else if (pi.compareTo(pj) == 0) 
                    throw new IllegalArgumentException();
            }
        
        numSegs = 0;        
        // Sort the points so they are in order
        pts = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            pts[i] = points[i];
        Arrays.sort(pts);
        segs = genSegments();
    }
    
    private LineSegment[] shrink(LineSegment[] segments) {
        if (segments == null || segments.length == numSegs)
            return segments;
        else {
            LineSegment[] nsegs = new LineSegment[numSegs];
            for (int i = 0; i < numSegs; i++)
                nsegs[i] = segments[i];
            segments = nsegs;
        }
        return segments;
    }

    private LineSegment[] insertSegment(LineSegment lseg, LineSegment[] segments)
    {
        if (lseg == null || segments == null)
            throw new NullPointerException();
        
        // Expand the array to store more elements
        if (segments.length == numSegs) {
            int resize = segments.length * 2;
            LineSegment[] nsegs = new LineSegment[resize];
            
            // Copy old elements
            if (segments != null)
                for (int i = 0; i < numSegs; i++)
                    nsegs[i] = segments[i];
            segments = nsegs;
        }
        
        segments[numSegs] = lseg;
        numSegs++;
        return segments;
    }
    
    public int numberOfSegments()
    {
        return numSegs;
    }
    
    public LineSegment[] segments()
    {
        LineSegment[] segments = new LineSegment[segs.length];
        for (int i = 0; i < segs.length; i++)
            segments[i] = segs[i];
        return segments;
    }
    private LineSegment[] genSegments()
    {
        LineSegment[] segments = new LineSegment[2];
        for (int p = 0; p < pts.length - 3; p++)
            for (int q = p + 1; q < pts.length - 2; q++)
                for (int r = q + 1; r < pts.length - 1; r++)
                    for (int s = r + 1; s < pts.length; s++) {
                        Point pp = pts[p];
                        Point pq = pts[q];
                        Point pr = pts[r];
                        Point ps = pts[s];
                        

                        if (pp.slopeTo(pq) == pp.slopeTo(pr) &&
                                pp.slopeTo(pq) == pp.slopeTo(ps)) {
                            segments = insertSegment(new 
                                    LineSegment(pp, ps), segments);
                        }
                        
                    }
        segments = shrink(segments);
        return segments;
    }
    
    public static void main(String[] args)
    {
        // read the N points from a file
        In in = new In(args[0]);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.show(0);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}
