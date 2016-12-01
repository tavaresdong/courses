package simpledb;
import java.io.*;

public class test {
    public static void main(String[] args) {
        Type types[] = new Type[]{Type.INT_TYPE, Type.INT_TYPE};
        String names[] = new String[]{"f1", "f2"};
        TupleDesc descriptor = new TupleDesc(types, names);
        
        HeapFile table1 = new HeapFile(new File("data.dat"), descriptor);
        Database.getCatalog().addTable(table1, "test");
        
        TransactionId tid = new TransactionId();
        SeqScan f = new SeqScan(tid, table1.getId(), "test");
        
        try {
            f.open();
            while (f.hasNext()) {
                Tuple tup = f.next();
                System.out.println(tup);
            }
            f.close();
            Database.getBufferPool().transactionComplete(tid);
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
    }
}
