package buildingblocks;

/**
 * This example shows how a bounded counter can be used as a simple
 * signaling mechanism (traffic light or semaphore).
 *
 * The monitor methods wait and notify are not
 * suitable for this because if notify occurs before wait, the thread
 * that invoked the wait is stuck.
 */

public class Semaphore {

  static Object lock = new Object();

  static SimpleBoundedCounter sema = new SimpleBoundedCounter();

  public static void main(String[] args) throws Exception {
    new Worker().start();
    Thread.sleep(3000);
    System.out.println("notifying");
    sema.inc();
//    synchronized (lock) {
//      lock.notify();
//    }
    System.out.println("main done");
  }


  static class Worker extends Thread {

    public void run() {
      try {
        Thread.sleep(3000);
        System.out.println("waiting");
        sema.dec();
//        synchronized (lock) {
//          lock.wait();
//        }
        System.out.println("worker done");
      } catch (InterruptedException e) {
        System.out.println("interrupted");
      }
    }
  }
}
