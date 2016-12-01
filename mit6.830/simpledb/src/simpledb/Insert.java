package simpledb;
import java.io.IOException;
import java.util.*;

/**
 * Inserts tuples read from the child operator into
 * the tableid specified in the constructor
 */
public class Insert extends AbstractDbIterator {

    private TransactionId _tid;
    private DbIterator _child;
    private int _tableid;
    private ArrayList<Tuple> _addcount;
    private Iterator<Tuple> _iter;
    public static final TupleDesc _td = Utility.getTupleDesc(1);
    
    /**
     * Constructor.
     * @param t The transaction running the insert.
     * @param child The child operator from which to read tuples to be inserted.
     * @param tableid The table in which to insert tuples.
     * @throws DbException if TupleDesc of child differs from table into which we are to insert.
     */
    public Insert(TransactionId t, DbIterator child, int tableid)
        throws DbException {
        // some code goes here
        _tid = t;
        _child = child;
        _tableid = tableid;  
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return _td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        _child.open();
        int numTupleDeleted = 0;
        while (_child.hasNext()) {
            Tuple t = _child.next();
            try {
                Database.getBufferPool().insertTuple
                            (_tid, _tableid, t);
                numTupleDeleted ++;
            } catch (IOException e) {
                throw new DbException("IO Error");
            }
        }
        Tuple insertCount = new Tuple(_td);
        insertCount.setField(0,new IntField(numTupleDeleted));
        _addcount = new ArrayList<Tuple>();
        _addcount.add(insertCount);
        _iter = _addcount.iterator();
    }

    public void close() {
        // some code goes here
        _iter = null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        _iter = _addcount.iterator();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool.
     * An instances of BufferPool is available via Database.getBufferPool().
     * Note that insert DOES NOT need check to see if a particular tuple is
     * a duplicate before inserting it.
     *
     * @return A 1-field tuple containing the number of inserted records, or
    * null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple readNext()
            throws TransactionAbortedException, DbException {
        // some code goes here
        if (_iter != null && _iter.hasNext()) {
            return _iter.next();
        } else {
            return null;
        }
    }
}
