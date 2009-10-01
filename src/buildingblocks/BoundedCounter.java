package buildingblocks;

/**
 * This example illustrates the family of producer-consumer problems
 * where there is a shared data structure.  Here, this is a bounded
 * counter, but it could also be a bounded buffer (which has real storage
 * for items produced and consumed).  One can experiment by making
 * the producer or consumer slower or faster or by adding more clients
 * (producers or consumers).
 */

public class BoundedCounter {
  public static void main(String[] args) {
    SimpleBoundedCounter counter = new SimpleBoundedCounter();
    new Producer(counter).start();
    new Producer(counter).start();
    new Consumer(counter).start();
    new Consumer(counter).start();
  }

  static class Producer extends Thread {

    SimpleBoundedCounter counter;

    public Producer(SimpleBoundedCounter counter) {
      this.counter = counter;
    }

    public void run() {
      while (true) {
        try {
          Thread.sleep(200);
          System.out.println(Thread.currentThread() + " trying to increment counter");
          counter.inc();
          System.out.println(Thread.currentThread() + " done incrementing counter -> " + counter.count());
        } catch (InterruptedException e) {
          System.err.println(Thread.currentThread() + " interrupted");
        }
      }
    }
    public String toString() { return "Producer@" + hashCode() % 100; }
  }

  static class Consumer extends Thread {

    SimpleBoundedCounter counter;

    public Consumer(SimpleBoundedCounter counter) {
      this.counter = counter;
    }

    public void run() {
      while (true) {
        try {
          Thread.sleep(300);
          System.out.println(Thread.currentThread() + " trying to decrement counter");
          counter.dec();
          System.out.println(Thread.currentThread() + " done decrementing counter -> " + counter.count());
        } catch (InterruptedException e) {
          System.err.println(Thread.currentThread() + " interrupted");
        }
      }
    }
    public String toString() { return "Consumer@" + hashCode() % 100; }
  }
}

/**
 * This class implements a simple bounded counter.  The increment and
 * decrement methods block until the respective operation is enabled; this
 * makes it unnecessary for the clients to poll the counter.
 */

class SimpleBoundedCounter {

  static final long MIN = 0;  // minimum allowed value

  static final long MAX = 1; // maximum allowed value

  protected long count = MIN;

  public synchronized long count() { return count; }

  public synchronized void inc() throws InterruptedException {
    awaitUnderMax(); // guarded method
    setCount(count + 1);
  }

  public synchronized void dec() throws InterruptedException {
    awaitOverMin();
    setCount(count - 1);
  }

  protected void setCount(long newValue) { // PRE: lock held
    System.out.println(Thread.currentThread() + " proceeding");
    count = newValue;
    notifyAll(); // wake up any thread depending on new value
  }

  protected void awaitUnderMax() throws InterruptedException {
    while (count == MAX) {
      System.out.println(Thread.currentThread() + " waiting");
      wait();
    }
  }

  protected void awaitOverMin() throws InterruptedException {
    while (count == MIN) {
      System.out.println(Thread.currentThread() + " waiting");
      wait();
    }
  }
}
