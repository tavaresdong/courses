package simpledb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

public class LockManager {
    private final ConcurrentMap<PageId, Object> _locks;
    private final ConcurrentMap<PageId, List<TransactionId>> _sharedTrans;
    private final ConcurrentMap<PageId, TransactionId> _ownerTrans;
    private final ConcurrentMap<TransactionId, Collection<PageId>> _pagesHoldsByTrans;

    private final DirectedAcyclicGraph<TransactionId, DefaultEdge> _dependency;
    // Singleton LockManager;
    private LockManager() {
        _locks = new ConcurrentHashMap<PageId, Object>();
        _sharedTrans = new ConcurrentHashMap<PageId, List<TransactionId>>();
        _ownerTrans = new ConcurrentHashMap<PageId, TransactionId>();
        _pagesHoldsByTrans = new ConcurrentHashMap<TransactionId, Collection<PageId>>();
        _dependency = new DirectedAcyclicGraph<TransactionId, DefaultEdge>(DefaultEdge.class);
    }
    
    public static LockManager getLockManager() {
        return new LockManager();
    }
    
    private Object getLock(PageId pageId) {
        _locks.putIfAbsent(pageId, new Object());
        return _locks.get(pageId);
    }
    
    public boolean acquireLock(TransactionId tid, PageId pid,
            Permissions perm) throws TransactionAbortedException {
        if (tid == null || pid == null || perm == null)
            throw new NullPointerException();
        
        if (perm == Permissions.READ_ONLY) {
            tryAquireROLockOrWait(tid, pid);
        } else if (perm == Permissions.READ_WRITE) {
            tryAquireRWLockOrWait(tid, pid);            
        } else {
            throw new IllegalArgumentException("Illegal Permission required:" + perm);
        }
        
        _pagesHoldsByTrans.putIfAbsent(tid, new LinkedBlockingQueue<PageId>());
        _pagesHoldsByTrans.get(tid).add(pid);
        return true;
    }
    
    private void releaseLock(TransactionId tid, PageId pid) {
        Object lock = getLock(pid);
        synchronized (lock) {
            // TODO
            if (_ownerTrans.containsKey(pid) &&
                    _ownerTrans.get(pid).equals(tid)) {
                _ownerTrans.remove(pid);
            }
            if (_sharedTrans.containsKey(pid)) {
                _sharedTrans.get(pid).remove(tid);
            }
            
            // Notify all acquirers waiting on this lock
            lock.notifyAll();
        }
    }
    
    public void releasePage(TransactionId tid, PageId pid) {
        releaseLock(tid, pid);
        if (_pagesHoldsByTrans.containsKey(tid)) {
            _pagesHoldsByTrans.get(tid).remove(pid);
        }
    }
    
    public void releasePages(TransactionId tid) { 
        if (_pagesHoldsByTrans.containsKey(tid)) {
            for (PageId pid : _pagesHoldsByTrans.get(tid)) {
                releaseLock(tid, pid);
            }
            _pagesHoldsByTrans.get(tid).clear();
        }
    }
    
    /**
     * Return true if tid holds lock of pid False otherwise
     * @param tid
     * @param pid
     * @return
     */
    public boolean holdsLock(TransactionId tid, PageId pid) {
        if (!_pagesHoldsByTrans.containsKey(tid)) {
            return false;
        }
        return _pagesHoldsByTrans.get(tid).contains(pid);
    }
    
    
    private void tryAquireRWLockOrWait(TransactionId tid, PageId pid) 
            throws TransactionAbortedException {
        Object lock = getLock(pid);
        boolean getRWLock = false;
        List<TransactionId> deps = new ArrayList<TransactionId>();
        synchronized (lock) {
            while (!getRWLock) {
                if (hasWritePermissions(tid, pid)) {
                    getRWLock = true;
                } else {
                    TransactionId holder = _ownerTrans.get(pid);
                    List<TransactionId> readers = _sharedTrans.get(pid);
                    
                    // No reader currently
                    if (readers == null || readers.isEmpty()) {
                        if (holder == null || holder.equals(tid)) {
                            _ownerTrans.put(pid, tid);
                            removeDeps(tid, deps);
                            getRWLock = true;
                        } else {
                            deps.clear();
                            deps.add(holder);
                            addDeps(tid, deps);
                        }
                    } else {
                        // Some readers exist
                        if (readers.size() == 1 && readers.get(0).equals(tid)) {
                            // The only reader is tid, upgrade lock's permission to RW
                            _ownerTrans.put(pid, tid);
                            _sharedTrans.get(pid).clear();
                            removeDeps(tid, deps);
                            getRWLock = true;
                        } else {
                            deps.clear();
                            deps.addAll(readers);
                            addDeps(tid, readers);
                        }
                    }
                    if (!getRWLock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    

    // Try aquire Read-Only Lock of page pid, Lock on page level
    // If failed, then wait for other threads to release this page
    // And re-aquire this page
    private void tryAquireROLockOrWait(TransactionId tid, PageId pid) 
            throws TransactionAbortedException {
        Object lock = getLock(pid);
        boolean getROLock = false;
        List<TransactionId> deps = new ArrayList<TransactionId>();
        synchronized (lock) {
            while (!getROLock) {
                if (hasReadPermissions(tid, pid)) {
                    getROLock = true;
                } else {
                    TransactionId holder = _ownerTrans.get(pid);
                    if (holder == null) {
                        removeDeps(tid, deps);
                        addSharer(tid, pid);
                        getROLock = true;
                    } else {
                        deps.clear();
                        deps.add(holder);
                        addDeps(tid, deps);
                    }
                    if (!getROLock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }
    
    
    // Reset Dependency for transaction tid
    private void addDeps(TransactionId tid, Collection<TransactionId> dependencies) 
            throws TransactionAbortedException {
        synchronized (_dependency) {
            if (!_dependency.containsVertex(tid)) {
                _dependency.addVertex(tid);
            }
            TransactionId cur = null;
            try {
                for (TransactionId dep : dependencies) {
                    if (!_dependency.containsVertex(dep)) {
                        _dependency.addVertex(dep);
                    }
                    cur = dep;
                    if (!tid.equals(dep) && 
                            !_dependency.containsEdge(tid, dep)) {
                        _dependency.addDagEdge(tid, dep);
                    }
                }

            } catch (CycleFoundException e) {
                _dependency.removeVertex(tid);
//                e.printStackTrace();
                System.out.println("Edge: "  + tid + " -> "
                        + cur + " is a cycle" + " aborting...");
                throw new TransactionAbortedException();
            }
        }
    }


    // Remove dependencies of transaction tid
    private void removeDeps(TransactionId tid, List<TransactionId> deps) {
        
        synchronized (_dependency) {
            for (TransactionId unconnect : deps) {
                _dependency.removeEdge(tid, unconnect);
            }
        }
    }

    // Add a sharer transaction to the page
    private void addSharer(TransactionId tid, PageId pid) {
        if (!_sharedTrans.containsKey(pid)) {
            _sharedTrans.put(pid, new ArrayList<TransactionId>());
        }
        _sharedTrans.get(pid).add(tid);
    }

    // Test if Transaction with tid already has read permission to pid
    private boolean hasReadPermissions(TransactionId tid,
            PageId pid) {
        
        // If transaction already holds write permission
        // then it can do reading of this page
    if (_ownerTrans.containsKey(pid) &&
            tid.equals(_ownerTrans.get(pid))) {
        return true;
    } else if (_sharedTrans.containsKey(pid) && 
                _sharedTrans.get(pid).contains(tid)) {
            return true;
        } else {
            return false;
        }
    }

    // Test if Transaction with tid already has write permission to pid
    private boolean hasWritePermissions(TransactionId tid, 
            PageId pid) {
        if (_ownerTrans.containsKey(pid) && 
                _ownerTrans.get(pid) == tid) {
            return true;
        }
        return false;
    }
}
