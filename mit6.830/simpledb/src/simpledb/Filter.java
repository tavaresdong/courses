package simpledb;
import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends AbstractDbIterator {
    private DbIterator _child;
    private TupleDesc _td;
    private ArrayList<Tuple> _childTups = new ArrayList<Tuple>();
    private Predicate _p;
    private Iterator<Tuple> _it;

    /**
     * Constructor accepts a predicate to apply and a child
     * operator to read tuples to filter from.
     *
     * @param p The predicate to filter tuples with
     * @param child The child operator
     */
    public Filter(Predicate p, DbIterator child) {
        // some code goes here
        _p = p;
        _child = child;
        _td = child.getTupleDesc();
        _childTups = new ArrayList<Tuple>();
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return _td;
    }

    public void open()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        // some code goes here
        _child.open();
        while (_child.hasNext()) {
            Tuple t = _child.next();
            if (_p.filter(t)) {
                _childTups.add(t);
            }
        }
        _it = _childTups.iterator();
    }

    public void close() {
        // some code goes here
        _it = null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        _it = _childTups.iterator();
    }

    /**
     * AbstractDbIterator.readNext implementation.
     * Iterates over tuples from the child operator, applying the predicate
     * to them and returning those that pass the predicate (i.e. for which
     * the Predicate.filter() returns true.)
     *
     * @return The next tuple that passes the filter, or null if there are no more tuples
     * @see Predicate#filter
     */
    protected Tuple readNext()
        throws NoSuchElementException, TransactionAbortedException, DbException {
        // some code goes here
        if (_it != null && _it.hasNext()) {
            return _it.next();
        } else {
            return null;
        }
    }
}
