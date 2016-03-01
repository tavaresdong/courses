
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
    
    private TreeSet<Point2D> points;
    
    public PointSET()
    {
        points = new TreeSet<Point2D>();
    }
    
    public boolean isEmpty()
    {
        return points.isEmpty();
    }
    
    public int size()
    {
        return points.size();
    }
    
    public void insert(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        points.add(p);
    }
    
    public boolean contains(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        return points.contains(p);
    }
    
    /**
     * Draw all points to standard draw
     */
    public void draw()
    {
        for (Point2D p : points)
            p.draw();
    }
    
    /**
     * 
     * @param rect the box range
     * @return All points thats in range of rect
     */
    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null)
            throw new NullPointerException();
        
        TreeSet<Point2D> part = new TreeSet<Point2D>();
        for (Point2D p : points)
            if (rect.contains(p))
                part.add(p);
        return part;
    }
    
    public Point2D nearest(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        
        double dist = Double.MAX_VALUE;
        Point2D result = null;
        
        for (Point2D pt : points) {
            if (pt.distanceTo(p) < dist) {
                dist = pt.distanceTo(p);
                result = pt;
            }
        }
        
        return result;
    }
    
    public static void main(String[] args)
    {
        
    }

}
