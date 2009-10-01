package buildingblocks;

abstract class ReadWrite {
  protected int activeReaders = 0;  // threads executing read
  protected int activeWriters = 0;  // always zero or one

  protected int waitingReaders = 0; // threads not yet in read
  protected int waitingWriters = 0; // same for write

  protected abstract void doRead(); // implement in subclasses
  protected abstract void doWrite();

  public void read() throws InterruptedException {
    beforeRead();
    try     { doRead(); }
    finally { afterRead(); }
  }

  public void write() throws InterruptedException {
    beforeWrite();
    try     { doWrite(); }
    finally { afterWrite(); }
  }
  protected boolean allowReader() {
    return waitingWriters == 0 && activeWriters == 0;
  }

  protected boolean allowWriter() {
    return activeReaders == 0 && activeWriters == 0;
  }

  protected synchronized void beforeRead()
   throws InterruptedException {
    ++waitingReaders;
    while (!allowReader()) {
      try { wait(); }
      catch (InterruptedException ie) {
        --waitingReaders; // roll back state
        throw ie;
      }
    }
    --waitingReaders;
    ++activeReaders;
  }

  protected synchronized void afterRead()  {
    --activeReaders;
    notifyAll();
  }

  protected synchronized void beforeWrite()
   throws InterruptedException {
    ++waitingWriters;
    while (!allowWriter()) {
      try { wait(); }
      catch (InterruptedException ie) {
        --waitingWriters;
        throw ie;
      }
    }
    --waitingWriters;
    ++activeWriters;
  }

  protected synchronized void afterWrite() {
    --activeWriters;
    notifyAll();
  }
}


class MyReadWrite extends ReadWrite {
    int value;
    public MyReadWrite(int value) { this.value = value; }
    public void doRead() { System.out.println(Thread.currentThread() + ": " + value); }
    public void doWrite() { value ++; }

    public static void main(String[] args) {
        final int NUM = 3;
        final MyReadWrite r = new MyReadWrite(10);
        Thread[] threads = new Thread[NUM];
        for (int k = 0; k < NUM; k ++) {
            final int i = k;
            threads[k] = new Thread(new Runnable() {
                public void run() {
                    try {
                        r.write();
                        Thread.sleep(0);
                        r.read();
                        Thread.sleep(0);
                        r.read();
                        Thread.sleep(0);
                        r.write();
                        Thread.sleep(0);
                        r.read();
                        Thread.sleep(0);
                        r.read();
                        Thread.sleep(0);
                        r.write();
                        Thread.sleep(0);
                        r.read();
                        Thread.sleep(0);
                        r.read();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
        }
        for (int k = 0; k < NUM; k ++) {
            threads[k].start();
        }
    }
}

