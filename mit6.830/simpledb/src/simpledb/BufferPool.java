package simpledb;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;

    private LinkedHashMap<PageId, Page> _pages;
    private Queue<PageId> _pageids;
    private int _maxPages;
    private LockManager lockManager;
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here
        _pages = new LinkedHashMap<PageId, Page>();
        _pageids = new LinkedList<PageId>();
        _maxPages = numPages;
        lockManager = LockManager.getLockManager();
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here
        lockManager.acquireLock(tid, pid, perm);
        if (!_pages.containsKey(pid)) {
            
            
            // If this BufferPool is full, evict one page
            if (_pageids.size() == _maxPages) {
                evictPage();
            }
            Page page = Database.getCatalog().
                    getDbFile(pid.getTableId()).readPage(pid);
            _pages.put(pid, page);
            _pageids.add(pid);
        }
        return _pages.get(pid);
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for lab1|lab2
        lockManager.releasePage(tid, pid);
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public  void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
        transactionComplete(tid, true);
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public   boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for lab1|lab2
        return lockManager.holdsLock(tid, p);
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public   void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
        if (commit == true) {
            // Commit all changes
            // Flush all pages hold by transaction
            for (PageId pid : _pages.keySet()) {
                if (holdsLock(tid, pid)) {
                    flushPage(pid);
                }
            }
        } else {
            for (PageId pid : _pages.keySet()) {
                if (holdsLock(tid, pid)) {
                    // Re-read the original page from disk
                    Page origPage = Database.getCatalog().
                            getDbFile(pid.getTableId()).readPage(pid);
                    _pages.put(pid, origPage);
                }
            }
        }
        lockManager.releasePages(tid);
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to(Lock 
     * acquisition is not needed for lab2). May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and updates cached versions of any pages that have 
     * been dirtied so that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public  void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1        
        // Use interface DbFile, for we may need to manipulate other types of files
        DbFile dbfile = Database.getCatalog().getDbFile(tableId);
        ArrayList<Page> dirtypages = dbfile.addTuple(tid, t);
        for (Page p : dirtypages) {
            p.markDirty(true, tid);
        }
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit.  Does not need to update cached versions of any pages that have 
     * been dirtied, as it is not possible that a new page was created during the deletion
     * (note difference from addTuple).
     *
     * @param tid the transaction adding the tuple.
     * @param t the tuple to add
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        int tableid = t.getRecordId().getPageId().getTableId();
        
        // Use interface DbFile, for we may need to manipulate other types of files
        DbFile dbfile = Database.getCatalog().getDbFile(tableid);
        Page dirtypage = dbfile.deleteTuple(tid, t);
        dirtypage.markDirty(true, tid);
    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for lab1
        for (PageId pid : _pages.keySet()) {
            flushPage(pid);
        }
    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
        // only necessary for lab5
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        // some code goes here
        // not necessary for lab1
        if (!_pages.containsKey(pid)) {
            return;
        }
        Page page = _pages.get(pid);
        TransactionId lastModifier = page.isDirty();
        
        // This page is dirty, we need to flush it back to disk
        if (lastModifier != null) {
            int tableid = pid.getTableId();
            DbFile file = Database.getCatalog().getDbFile(tableid);
            file.writePage(page);
            page.markDirty(false, lastModifier);
        }
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2|lab3
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws DbException {
        // some code goes here
        // not necessary for lab1
        boolean pageEvicted = false;
        int cnt = 0;
        while (!pageEvicted) {
            if (cnt == _pageids.size()) {
                throw new DbException("All dirty pages, failed to evict");
            }
            PageId oldestPageId = _pageids.poll();
            if (oldestPageId == null) {
                throw new DbException("Evicting page from an empty DB");
            }
            
            Page page = _pages.get(oldestPageId);
            if (page.isDirty() != null) {
                // Don't evict dirty pages (NO¡¡STEAL)
                _pageids.add(oldestPageId);
            } else {
                try {
                    flushPage(oldestPageId);
                    _pages.remove(oldestPageId);
                    pageEvicted = true;
                } catch (IOException e) {
                    throw new DbException("Failed flushing pages");
                }
            }
            cnt++;
        }
    }

}
