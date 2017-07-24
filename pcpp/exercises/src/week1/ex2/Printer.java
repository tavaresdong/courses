package week1.ex2;

/**
 * Created by yuchend on 17-7-24.
 */
public class Printer {
    /*
        This method should be synchronized to print -| pairs
     */
    public static void print() {
        synchronized (Printer.class) {
            System.out.print("-");
            try {
                Thread.sleep(50);
            } catch (InterruptedException exn) {

            }
            System.out.print("|");
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true)
                Printer.print();
        });

        Thread t2 = new Thread(() -> {
            while (true)
                Printer.print();
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {}
    }
}
