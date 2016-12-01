package simpledb;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The delete operator.  Delete reads tuples from its child operator and
 * removes them from the table they belong to.
 */
public class Delete extends AbstractDbIterator {
    
    private TransactionId _tid;
    private DbIterator _child;
    public static final TupleDesc _td = Utility.getTupleDesc(1);
    private ArrayList<Tuple> _deletecount;
    private Iterator<Tuple> _iter;
    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * @param t The transaction this delete runs in
     * @param child The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
        _tid = t;
        _child = child;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return _td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        _deletecount = new ArrayList<Tuple>();
        int numTupleDeleted = 0;
        _child.open();
        while (_child.hasNext()) {
            Tuple t = _child.next();
            Database.getBufferPool().deleteTuple(_tid, t);
            numTupleDeleted ++;
        }
        
        Tuple deleteCount = new Tuple(_td);
        deleteCount.setField(0, new IntField(numTupleDeleted));
        _deletecount.add(deleteCount);
        _iter = _deletecount.iterator();
    }

    public void close() {
        // some code goes here
        _iter = null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        _iter = _deletecount.iterator();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (_iter != null && _iter.hasNext()) {
            return _iter.next();
        } else {
            return null;
        }
    }
}
