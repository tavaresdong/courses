package queues;


import java.util.Iterator;
import java.util.NoSuchElementException;


public class Deque<Item> implements Iterable<Item> {

    // List Node representation
    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }
    
    private Node first;
    private Node last;
    private int size;
    
    public Deque()
    {
        first = null;
        last = null;
        size = 0;
    }
    
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    public int size()
    {
        return size;
    }
    
    public void addFirst(Item item)
    {
        if (item == null) 
            throw new NullPointerException();
        
        Node node = new Node();
        node.item = item;
        
        if (size == 0) {
            node.next = null;
            node.prev = null;
            first = node;
            last = node;
        } else {
            node.next = first;
            first.prev = node;
            node.prev = null;
            first = node;
        }
        size++;
    }
    
    public void addLast(Item item) 
    {
        if (item == null) 
            throw new NullPointerException();

        Node node = new Node();
        node.item = item;
        
        if (size == 0) {
            node.next = null;
            node.prev = null;
            first = node;
            last = node;
        } else {
            last.next = node;
            node.prev = last;
            last = node;
            node.next = null;
        }
        size++;
    }
    
    public Item removeFirst()
    {
        if (size == 0)
            throw new NoSuchElementException();
        
        Node orig = first;
        first = first.next;
        if (size == 1) {
            last  = null;
        } else {
            first.prev = null;
        }
        size--;
        return orig.item;
    }
    
    public Item removeLast()
    {
        if (size == 0)
            throw new NoSuchElementException();
        
        Node orig = last;
        last = last.prev;
        if (size == 1) {
            first = null;
        } else {
            last.next = null;
        }
        size--;
        return orig.item;
    }
    
    
    private class DequeIterator implements Iterator<Item>
    {
        private Node current = first;
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) 
                throw new NoSuchElementException();
            Item ret = current.item;
            current = current.next;
            return ret;
        }
        
    }
    
    @Override
    public Iterator<Item> iterator() 
    {
        return new DequeIterator();
    }
    
    public static void main(String[] args)
    {
    }
    
}
