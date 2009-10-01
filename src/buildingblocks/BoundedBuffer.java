package buildingblocks;

/**
 * This example illustrates the family of producer-consumer problems
 * where there is a shared data structure.  Here, this is a bounded
 * buffer.  One can experiment by making the producer or consumer slower
 * or faster or by adding more clients (producers or consumers).
 */

public class BoundedBuffer {
  public static void main(String[] args) {
    BoundedBufferWithStateTracking buffer = new BoundedBufferWithStateTracking(5);
    new Producer(buffer).start();
//    new Producer(buffer).start();
//    new Producer(buffer).start();
//    new Producer(buffer).start();
//    new Producer(buffer).start();
//    new Producer(buffer).start();
    new Consumer(buffer).start();
//    new Consumer(buffer).start();
//    new Consumer(buffer).start();
//    new Consumer(buffer).start();
  }

  static class Producer extends Thread {

    BoundedBufferWithStateTracking buffer;

    public Producer(BoundedBufferWithStateTracking buffer) {
      this.buffer = buffer;
    }

    public void run() {
      int num = 0;
      while (true) {
        try {
          Thread.sleep(15);
          Object item = new Integer(++ num);
          System.out.println(this + " waiting to put " + item + " in buffer");
          buffer.put(item);
          System.out.println(this + " done putting in buffer -> " + buffer.size() + " items");
        } catch (InterruptedException e) {
          System.err.println(this + " interrupted");
        }
      }
    }

    public String toString() { return "Producer@" + hashCode() % 100; }
  }

  static class Consumer extends Thread {

    BoundedBufferWithStateTracking buffer;

    public Consumer(BoundedBufferWithStateTracking buffer) {
      this.buffer = buffer;
    }

    public void run() {
      while (true) {
        try {
          Thread.sleep(20);
          System.out.println(this + " waiting to take from buffer");
          Object item = buffer.take();
          System.out.println(this + " got " + item + " from buffer -> " + buffer.size() + " items");
        } catch (InterruptedException e) {
          System.err.println(this + " interrupted");
        }
      }
    }
    public String toString() { return "Consumer@" + hashCode() % 100; }
  }
}

class BoundedBufferWithStateTracking {
  protected final Object[]  array;    // the elements
  protected int putPtr = 0;           // circular indices
  protected int takePtr = 0;
  protected int usedSlots = 0;        // the count

  public BoundedBufferWithStateTracking(int capacity)
   throws IllegalArgumentException {
    if (capacity <= 0) throw new IllegalArgumentException();
    array = new Object[capacity];
  }

  public synchronized int size() { return usedSlots; }

  public int capacity() { return array.length; }

  public synchronized void put(Object x)
   throws InterruptedException {

    while (usedSlots == array.length) // wait until not full
      wait();

    array[putPtr] = x;
    putPtr = (putPtr + 1) % array.length; // cyclically inc

    if (usedSlots++ == 0)              // signal if was empty
      notifyAll();
  }

  public synchronized Object take()
   throws InterruptedException{

    while (usedSlots == 0)           // wait until not empty
      wait();

    Object x = array[takePtr];
    array[takePtr] = null;
    takePtr = (takePtr + 1) % array.length;

    if (usedSlots-- == array.length) // signal if was full
      notifyAll();
    return x;
  }
}
