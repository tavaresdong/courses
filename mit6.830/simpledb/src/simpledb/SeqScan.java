package simpledb;
import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {
    
    private TransactionId _tid;
    private int _tableid;
    private String _tableAlias;
    DbFile _dbfile;
    DbFileIterator _iter;

    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     *
     * @param tid The transaction this scan is running as a part of.
     * @param tableid the table to scan.
     * @param tableAlias the alias of this table (needed by the parser);
     *         the returned tupleDesc should have fields with name tableAlias.fieldName
     *         (note: this class is not responsible for handling a case where tableAlias
     *         or fieldName are null.  It shouldn't crash if they are, but the resulting
     *         name can be null.fieldName, tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
        _tid = tid;
        _tableid = tableid;
        _tableAlias = tableAlias;
        _dbfile = Database.getCatalog().getDbFile(tableid);
        _iter = _dbfile.iterator(_tid);
    }

    public void open()
        throws DbException, TransactionAbortedException {
        // some code goes here
        _iter.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor.
     * @return the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        TupleDesc origTd = Database.getCatalog().getTupleDesc(_tableid);
        Type[] aliastype = new Type[origTd.numFields()];
        String[] aliasname = new String[origTd.numFields()];
        for (int i = 0; i < origTd.numFields(); i++) {
            aliastype[i] = origTd.getType(i);
            aliasname[i] = _tableAlias + "." + origTd.getFieldName(i);
        }
        
        return new TupleDesc(aliastype, aliasname);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here
        return _iter.hasNext();
    }

    public Tuple next()
        throws NoSuchElementException, TransactionAbortedException, DbException {
        // some code goes here
        return _iter.next();
    }

    public void close() {
        // some code goes here
        _iter.close();
    }

    public void rewind()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        // some code goes here
        _iter.rewind();
    }
}
