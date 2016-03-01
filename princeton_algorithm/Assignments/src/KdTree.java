import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {
    
    private class KdNode {
        Point2D point;
        KdNode left;
        KdNode right;
        RectHV rect;
        
        KdNode(Point2D pt, KdNode l, KdNode r, RectHV re)
        {
            point = pt;
            left  = l;
            right = r;
            rect  = re;
        }
    };
    
    private int sz;
    private KdNode root;
    
    public KdTree()
    {
        sz   = 0;
        root = null;
    }
    
    public boolean isEmpty()
    {
        return sz == 0;
    }
    
    public int size()
    {
        return sz;
    }
    
    public void insert(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        root = put(root, p, 0, 0.0, 0.0, 1.0, 1.0);
        sz++;
    }
    
    private KdNode put(KdNode nd, Point2D p, int turn,
                       double xmin, double ymin, 
                       double xmax, double ymax)
    {
        if (nd == null)
            return new KdNode(p, null, null, 
                              new RectHV(xmin, ymin, xmax, ymax));
        if (turn == 0) {
            if (p.x() < nd.point.x()) {
                return put(nd.left, p, 1 - turn, 
                           xmin, ymin, 
                           Math.min(nd.point.x(), xmax), ymax);
            } else {
                return put(nd.right, p, 1 - turn, 
                           Math.max(nd.point.x(), xmin), ymin,
                           xmax, ymax);
            }
        } else {
            if (p.y() < nd.point.y()) {
                return put(nd.left, p, 1 - turn,
                           xmin, ymin,
                           xmax, Math.min(nd.point.y(), ymax));
            } else {
                return put(nd.right, p, 1 - turn,
                           xmin, Math.max(nd.point.y(), ymin),
                           xmax, ymax);
            }
        }
    }
    
    public boolean contains(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        return search(root, p, 0);
    }
    
    private boolean search(KdNode nd, Point2D p, int turn) 
    {
        if (nd == null) return false;
        if (nd.point.equals(p)) {
            return true;
        } else if (turn == 0){
            if (p.x() < nd.point.x())
                return search(nd.left, p, 1 - turn);
            else
                return search(nd.right, p, 1 - turn);
        } else {
            if (p.y() < nd.point.y())
                return search(nd.left, p, 1 - turn);
            else
                return search(nd.right, p, 1 - turn);
        }
    }
    
    /**
     * Draw all points to standard draw
     */
    public void draw()
    {
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
        TreeSet<Point2D> pointsInRange = new TreeSet<Point2D>();
        collect(pointsInRange, root, rect);
        return pointsInRange;
    }
    
    private void collect(TreeSet<Point2D> points, KdNode nd,
                         RectHV rect)
    {
        if (nd == null) 
            return;
        
        // Pruning, return if the box not intersect with that of the node
        if (!nd.rect.intersects(rect))
            return;
        
        if (rect.contains(nd.point)) {
            points.add(nd.point);
        }
        
        collect(points, nd.left, rect);
        collect(points, nd.right, rect);
        
    }

    
    public Point2D nearest(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        
        Point2D result = find(root, Double.MAX_VALUE, p);
        return result;
    }
    

    private Point2D find(KdNode nd, double curDist, Point2D target) 
    {
        if (nd == null) 
            return null;
        Point2D curClosest = null;
        
        // Pruning
        if (nd.rect.distanceTo(target) > curDist) {
            return null;
        }
        
        if (nd.point.distanceTo(target) < curDist) {
            curClosest = nd.point;
            curDist    = nd.point.distanceTo(target);
        }
        
        Point2D left = find(nd.left, curDist, target);
        Point2D right = find(nd.right, curDist, target);
        
        if (left != null && left.distanceTo(target) < curDist) {
            curClosest = left;
            curDist = left.distanceTo(target);
        }
        
        if (right != null && right.distanceTo(target) < curDist) {
            curClosest = right;
            curDist = right.distanceTo(target);
        }
        
        return curClosest;
    }

    public static void main(String[] args)
    {
        
    }

}
