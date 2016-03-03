package kdtree;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    
    private class KdNode {
        private Point2D point;
        private KdNode left;
        private KdNode right;
        private RectHV rect;
        
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
        
    }
    
    private KdNode put(KdNode nd, Point2D p, int turn,
                       double xmin, double ymin, 
                       double xmax, double ymax)
    {
        if (nd == null) {
            sz++;
            return new KdNode(p, null, null, 
                              new RectHV(xmin, ymin, xmax, ymax));
        }
        
        // Neglect nodes that collides with one
        // Already in the tree
        double ndx = nd.point.x();
        double ndy = nd.point.y();
        if (nd.point.equals(p))
            return nd;
        else if (turn == 0) {
            if (p.x() < ndx) {
                nd.left = put(nd.left, p, 1 - turn, 
                           xmin, ymin, 
                           Math.min(ndx, xmax), ymax);
            } else {
                nd.right = put(nd.right, p, 1 - turn, 
                           Math.max(ndx, xmin), ymin,
                           xmax, ymax);
            }
        } else {
            if (p.y() < ndy) {
                nd.left =  put(nd.left, p, 1 - turn,
                           xmin, ymin,
                           xmax, Math.min(ndy, ymax));
            } else {
                nd.right = put(nd.right, p, 1 - turn,
                           xmin, Math.max(ndy, ymin),
                           xmax, ymax);
            }
        }
        
        return nd;
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
        } else if (turn == 0) {
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
        if (isEmpty())
            return;
        drawNode(root, 0, 0.0, 0.0, 1.0, 1.0);
    }
    
    private void drawNode(KdNode nd, int turn, 
                          double xmin, double ymin, double xmax, double ymax) 
    {
        if (nd == null)
            return;
        StdDraw.setPenRadius(0.02);
        if (turn == 0) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(nd.point.x(), ymin, nd.point.x(), ymax);
            drawNode(nd.left, 1 - turn, xmin, ymin, nd.point.x(), ymax);
            drawNode(nd.right, 1 - turn, nd.point.x(), ymin, xmax, ymax);
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(xmin, nd.point.y(), xmax, nd.point.y());
            drawNode(nd.left, 1 - turn, xmin, ymin, xmax, nd.point.y());
            drawNode(nd.right, 1 - turn, xmin, nd.point.y(), xmax, ymax);
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.2);
        nd.point.draw();
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

    
    /**
     * Find the nearest point to p, If there is no points
     * return null
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p)
    {
        if (p == null)
            throw new NullPointerException();
        
        Point2D result = find(root, Double.MAX_VALUE, p, 0);
        return result;
    }
    

    private Point2D find(KdNode nd, double curDist, Point2D target, int turn) 
    {
        Point2D curClosest = null;
        
        if (nd == null) 
            return curClosest;
        
        // Pruning
        if (nd.rect.distanceTo(target) > curDist) {
            return null;
        }
        
        if (nd.point.distanceTo(target) < curDist) {
            curClosest = nd.point;
            curDist    = nd.point.distanceTo(target);
        }
        
        // Always search the plane that target is on according to
        // current node
        if ((turn == 0 && target.x() < nd.point.x()) ||
            (turn == 1 && target.y() < nd.point.y())) {
            Point2D left = find(nd.left, curDist, target, 1 - turn);
            if (left != null && left.distanceTo(target) < curDist) {
                curClosest = left;
                curDist = left.distanceTo(target);
            }
            Point2D right = find(nd.right, curDist, target, 1 - turn);
            if (right != null && right.distanceTo(target) < curDist) {
                curClosest = right;
                curDist = right.distanceTo(target);
            }
        } else {
            Point2D right = find(nd.right, curDist, target, 1 - turn);
            if (right != null && right.distanceTo(target) < curDist) {
                curClosest = right;
                curDist = right.distanceTo(target);
            }
            Point2D left = find(nd.left, curDist, target, 1 - turn);
            if (left != null && left.distanceTo(target) < curDist) {
                curClosest = left;
                curDist = left.distanceTo(target);
            }
        }
        return curClosest;
    }

    public static void main(String[] args)
    {
        
    }

}
