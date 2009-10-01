package buildingblocks;

/**
 * A barrier allows several threads to wait for each other at a specified point.
 */

public class Barrier {
  public static void main(String[] args) throws Exception {
    new Driver().compute(new Problem() {{ size = 7; }});
  }
}

class CyclicBarrier {

  protected final int parties;
  protected int count;     // parties currently being waited for
  protected int resets = 0;  // times barrier has been tripped

  CyclicBarrier(int c) { count = parties = c; }

  synchronized int barrier() throws InterruptedException {
    int index = --count;
    if (index > 0) {        // not yet tripped
      int r = resets;       // wait until next reset

      do { wait(); } while (resets == r);

    }
    else {                 // trip
      count = parties;     // reset count for next time
      ++resets;
      notifyAll();         // cause all other parties to resume
      System.out.println("everybody has arrived");
    }

    return index;
  }
}

class Segment implements Runnable  {            // Code sketch
  final CyclicBarrier bar; // shared by all segments
  final int i;
  Segment(int i, CyclicBarrier b) { bar = b; this.i = i; }

  void update() {
    try {
      Thread.sleep(1000);
      System.out.println(i);
//      System.out.println(i);
//      Thread.sleep(1);
//      System.out.println(i);
    } catch (InterruptedException e) { }
    Thread.yield();
  }

  public void run() {
    // ...
    try {
      for (int i = 0; i < 10 /* iterations */; ++i) {
        update();
        bar.barrier();
      }
    }
    catch (InterruptedException ie) {}
    // ...
  }
}

class Problem { int size; }

class Driver {
  // ...
  int granularity = 1;
  void compute(Problem problem) throws Exception {
    int n = problem.size / granularity;
    CyclicBarrier barrier = new CyclicBarrier(n);
    Thread[] threads = new Thread[n];

    // create
    for (int i = 0; i < n; ++i)
      threads[i] = new Thread(new Segment(i, barrier));

    // trigger
    for (int i = 0; i < n; ++i) threads[i].start();

    // await termination
    for (int i = 0; i < n; ++i) threads[i].join();
  }
}
