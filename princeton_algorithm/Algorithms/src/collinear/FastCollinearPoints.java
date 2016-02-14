package collinear;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    
    private int numSegs;
    private Point[] pts;
    private Point[] tosort;
    private final LineSegment[] segs;
    
    public FastCollinearPoints(Point[] points)
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
        
        pts = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            pts[i] = points[i];

        Arrays.sort(pts);
        tosort = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            tosort[i] = points[i];
        
        segs = genSegments();
    }
    
    private LineSegment[] findLineSeg() {
        LineSegment[] segments = new LineSegment[2];
        
        for (int i = 0; i < pts.length; i++) {
            Point base = pts[i];
            
            // Sort, NlogN time complexity
            Arrays.sort(tosort, base.slopeOrder());
            int cnt = 0;
            int ind = 0;
            int maxind = 0;
            boolean neglect = false;
            double curSlope = Double.NEGATIVE_INFINITY;
            // Neglect same nodes
            while (ind < tosort.length &&
                   base.slopeTo(tosort[ind]) == Double.NEGATIVE_INFINITY) 
                ind++;
            for (; ind < tosort.length; ind++) {
                double slp = base.slopeTo(tosort[ind]);
                
                // A new line segment, Insert the previous one
                if (slp != curSlope) {
                    if (!neglect && cnt >= 3) {
                        // Insert a line segment, Only the longset segment
                        segments = insertSegment(
                                new LineSegment(base, tosort[maxind]), segments);
                    }
                    
                    neglect = false;
                    curSlope = slp;
                    cnt = 0;
                    maxind = ind;
                }
                
                // If base is in the middle of line , neglect this line
                if (base.compareTo(tosort[ind]) > 0) {
                    neglect = true;
                } else {
                    cnt++;
                    if (tosort[ind].compareTo(tosort[maxind]) > 0) {
                        maxind = ind;
                    }
                }
            }
            
            if (!neglect && cnt >= 3)
                segments = insertSegment(new 
                        LineSegment(base, tosort[maxind]), segments);
        }
        return segments;
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
        LineSegment[] segments = findLineSeg();
        
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }

}
