package queues;

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] array;
    private int capacity;
    private int size;
    
    public RandomizedQueue() {
        array = null;
        size = 0;
        capacity = 0;
    }
    
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    public int size()
    {
        return size;
    }
    
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        
        if (size == capacity) {
            int nsize = 2 * size;
            if (nsize == 0) nsize = 2;
            resize(nsize);
        }
        array[size] = item;
        size++;
    }
    
    private void resize(int N) {
        Item[] narray = (Item[]) new Object[N];
        for (int i = 0; i < size; i++) {
            narray[i] = array[i];
        }
        array = narray;
        capacity = N;
    }

    // Randomly move the pointer, remove and return the element
    public Item dequeue() {
        if (isEmpty()) 
            throw new NoSuchElementException();
        
        int rnd = StdRandom.uniform(0, size);
        Item ret = array[rnd];
        size -= 1;
        array[rnd] = array[size];
        array[size] = null;
        
        if (size * 4 <= capacity) {
            capacity /= 2;
            resize(capacity);
        }
        return ret;
    }
    
    public Item sample() {
        if (isEmpty()) 
            throw new NoSuchElementException();

        int rnd = StdRandom.uniform(0, size);
        return array[rnd];
    }
    
    private class RandQueueIterator implements Iterator<Item> {
        
        private int[] indexes;
        private int curIndex;
        
        RandQueueIterator() {
            curIndex = 0;
            indexes = new int[size];
            for (int i = 0; i < size; i++) {
                indexes[i] = i;
            }
            StdRandom.shuffle(indexes);
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            while (curIndex < indexes.length &&
                    array[indexes[curIndex]] == null) {
                curIndex++;
            }
            if (curIndex == indexes.length)
                return false;
            return true;
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Item item = array[indexes[curIndex]];
            curIndex++;
            return item;
        }
        
    }
    
    @Override
    public Iterator<Item> iterator() {
        return new RandQueueIterator();
    }

    public static void main(String[] args) {

    }
}
