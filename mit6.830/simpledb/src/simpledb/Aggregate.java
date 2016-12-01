package simpledb;

import java.util.*;

/**
 * The Aggregator operator that computes an aggregate (e.g., sum, avg, max,
 * min).  Note that we only support aggregates over a single column, grouped
 * by a single column.
 */
public class Aggregate extends AbstractDbIterator {

    private DbIterator _child;
    private Aggregator _aggregator;
    private DbIterator _iter;
    private TupleDesc  _td;
    /**
     * Constructor.  
     *
     *  Implementation hint: depending on the type of afield, you will want to construct an 
     *  IntAggregator or StringAggregator to help you with your implementation of readNext().
     * 
     *
     * @param child The DbIterator that is feeding us tuples.
     * @param afield The column over which we are computing an aggregate.
     * @param gfield The column over which we are grouping the result, or -1 if there is no grouping
     * @param aop The aggregation operator to use
     */
    public Aggregate(DbIterator child, int afield, int gfield, Aggregator.Op aop) {
        // some code goes here
        _child = child;
        _iter = null;
        
        TupleDesc td = child.getTupleDesc();
        Type atype = td.getType(afield);
        Type gtype = td.getType(gfield);
        if (atype == Type.INT_TYPE) {
            _aggregator = new IntAggregator
                    (gfield, gtype, afield, aop);
        } else {
            _aggregator = new StringAggregator
                    (gfield, gtype, afield, aop);
        }
        
        if (gfield == Aggregator.NO_GROUPING) {
            Type[] types = new Type[1];
            String[] names = new String[1];
            types[0] = atype;
            names[0] = null;
            _td = new TupleDesc(types, names);
        } else {
            Type[] types = new Type[2];
            String[] names = new String[2];
            types[0] = gtype;
            types[1] = atype;
            names[0] = null;
            names[1] = null;
            _td = new TupleDesc(types, names);
        }
    }

    public static String aggName(Aggregator.Op aop) {
        switch (aop) {
        case MIN:
            return "min";
        case MAX:
            return "max";
        case AVG:
            return "avg";
        case SUM:
            return "sum";
        case COUNT:
            return "count";
        }
        return "";
    }

    public void open()
        throws NoSuchElementException, DbException, TransactionAbortedException {
        // some code goes here
        _child.open();
        while (_child.hasNext()) {
            Tuple tup = _child.next();
            _aggregator.merge(tup);
        }
        _iter = _aggregator.iterator();
        _iter.open();
    }

    /**
     * Returns the next tuple.  If there is a group by field, then 
     * the first field is the field by which we are
     * grouping, and the second field is the result of computing the aggregate,
     * If there is no group by field, then the result tuple should contain
     * one field representing the result of the aggregate.
     * Should return null if there are no more tuples.
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (_iter != null && _iter.hasNext()) {
            return _iter.next();
        } else {
            return null;
        }
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        _iter = _aggregator.iterator();
        _iter.open();
    }

    /**
     * Returns the TupleDesc of this Aggregate.
     * If there is no group by field, this will have one field - the aggregate column.
     * If there is a group by field, the first field will be the group by field, and the second
     * will be the aggregate value column.
     * 
     * The name of an aggregate column should be informative.  For example:
     * "aggName(aop) (child_td.getFieldName(afield))"
     * where aop and afield are given in the constructor, and child_td is the TupleDesc
     * of the child iterator. 
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return _td;
    }

    public void close() {
        // some code goes here
        _iter = null;
    }
}
