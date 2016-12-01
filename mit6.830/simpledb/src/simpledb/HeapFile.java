package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection
 * of tuples in no particular order.  Tuples are stored on pages, each of
 * which is a fixed size, and the file is simply a collection of those
 * pages. HeapFile works closely with HeapPage.  The format of HeapPages
 * is described in the HeapPage constructor.
 *
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    
    private final File _file;
    private final TupleDesc _td;
    private final int _id;
    private int _numPages;
    
    /**
     * Constructs a heap file backed by the specified file.
     *
     * @param f the file that stores the on-disk backing store for this heap file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        _file = f;
        _td = td;
        _id = _file.getAbsolutePath().hashCode();
        _numPages = (int) (_file.length() / (long) BufferPool.PAGE_SIZE);
    }

    /**
     * Returns the File backing this HeapFile on disk.
     *
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return _file;
    }

    /**
    * Returns an ID uniquely identifying this HeapFile. Implementation note:
    * you will need to generate this tableid somewhere ensure that each
    * HeapFile has a "unique id," and that you always return the same value
    * for a particular HeapFile. We suggest hashing the absolute file name of
    * the file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
    *
    * @return an ID uniquely identifying this HeapFile.
    */
    public int getId() {
        // some code goes here
        return _id;
    }
    
    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
    	// some code goes here
        return _td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        int pageno = pid.pageno();
        byte[] buffer = new byte[BufferPool.PAGE_SIZE];
        try {
            RandomAccessFile raf = new RandomAccessFile(_file, "r");
            raf.seek(BufferPool.PAGE_SIZE * pageno);
            int bytesread = raf.read(buffer);
            raf.close();
            if (bytesread == buffer.length) {
                HeapPageId hpi = new HeapPageId(pid.getTableId(), pid.pageno());
                return new HeapPage(hpi, buffer);
            }
            throw new RuntimeException("Could not read entire page");
        } catch (IOException e) {
            throw new IllegalArgumentException();
        } 
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
        int pageno = page.getId().pageno();
        byte[] buffer = page.getPageData();
        RandomAccessFile raf = new RandomAccessFile(_file, "rw");
        raf.seek(BufferPool.PAGE_SIZE * pageno);
        raf.write(buffer);
        raf.close();
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return _numPages;
    }
    
    // Try add a tuple to page with pageno
    // return null if failed to add
    private HeapPage tryAddTupleToPage(TransactionId tid, Tuple t, int pageno)
            throws TransactionAbortedException, DbException {
        boolean added = false;
        HeapPage ret = null;
        HeapPageId pageid = new HeapPageId(_id, pageno);
        HeapPage page = (HeapPage) Database.getBufferPool().getPage
                    (tid, pageid, Permissions.READ_WRITE);
        if (page.getNumEmptySlots() != 0) {
            page.addTuple(t);
            ret = page;
        }
        Database.getBufferPool().releasePage(tid, page.getId());
        return ret;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> addTuple(TransactionId tid, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        ArrayList<Page> affected = new ArrayList<Page>();
        for (int ind = 0; ind < numPages(); ind++){
            HeapPage page = tryAddTupleToPage(tid, t, ind);
            if (page != null) {
                affected.add(page);
                break;
            }
        }
        
        // All existing pages are full, Insert new page
        if (affected.isEmpty()) {
            _numPages += 1;
            HeapPageId newPageId = new HeapPageId(_id, _numPages - 1);
            HeapPage newPage = new HeapPage(newPageId, HeapPage.createEmptyPageData());
            writePage(newPage);
            HeapPage page = tryAddTupleToPage(tid, t, _numPages - 1);
            if (page != null) {
                affected.add(page);
            }
        }
        return affected;
    }

    /**
     * Removes the specifed tuple from the file on behalf of the specified
     * transaction.
     * This method will acquire a lock on the affected pages of the file, and
     * may block until the lock can be acquired.
     *
     * @throws DbException if the tuple cannot be deleted or is not a member
     *   of the file
     */
    public Page deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
        // not necessary for lab1
        PageId pageid = t.getRecordId().getPageId();
        if (pageid.getTableId() != _id) {
            throw new DbException("Tuple is not a member of the file");
        }
        
        HeapPage page = (HeapPage) Database.getBufferPool().getPage
                (tid, pageid, Permissions.READ_WRITE);
        
        page.deleteTuple(t);
        
        return page;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new HeapFileIterator(_id, _numPages, tid);
    }
    
    private class HeapFileIterator implements DbFileIterator {
        
        private int tableId;
        private int pageCount;
        private TransactionId tid;
        private boolean isOpen;
        private int curPageNo;
        private Iterator<Tuple> pageIter;
        
        HeapFileIterator(int id, int npages, TransactionId t) {
            tableId = id;
            pageCount = npages;
            tid = t;
            isOpen = false;
        }

        @Override
        public void open() throws DbException, 
                                  TransactionAbortedException {
            if (isOpen) {
                throw new DbException("The table is already open");
            } else {
                rewind();
            }
        }
        
        private Iterator<Tuple> nextPage() throws TransactionAbortedException, 
                                                  DbException {
            HeapPageId hpi = new HeapPageId(tableId, curPageNo);
            HeapPage hp = (HeapPage) Database.getBufferPool().getPage
                        (tid, hpi, Permissions.READ_ONLY);
            return hp.iterator();
        }

        @Override
        public boolean hasNext() throws DbException, 
                                        TransactionAbortedException {
            if (!isOpen)
                return false;
            if (pageIter.hasNext()) {
                return true;
            } else if (curPageNo < pageCount - 1) {
                curPageNo++;
                pageIter = nextPage();
                if (pageIter.hasNext())
                    return true;
            }
            return false;
        }

        @Override
        public Tuple next() throws DbException, 
                                   TransactionAbortedException, 
                                   NoSuchElementException {
            if (!isOpen) {
                throw new NoSuchElementException();
            }
            return pageIter.next();
        }

        @Override
        public void rewind() throws DbException, 
                                    TransactionAbortedException {
            isOpen = true;
            curPageNo = 0;
            pageIter = nextPage();
        }

        @Override
        public void close() {
            isOpen = false;
        }
        
    }
    
}

