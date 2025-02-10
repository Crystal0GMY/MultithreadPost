package client1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class EventGeneratorThread implements Runnable{
  private static final BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(200000);
  private final CountDownLatch latch;

  public EventGeneratorThread(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public void run() {
    int eventCount = 0;
    while (eventCount < 200000) {
      SkierLiftRideEvent event = SkierLiftRideEventGenerator.generateEvent();
      try {
        eventQueue.put(event);
        eventCount++;
        latch.countDown();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  // Restore interrupt flag
        break;
      }
    }
    System.out.println("Event generator thread completed generating events.");
  }

  public static SkierLiftRideEvent getEvent() throws InterruptedException {
    return eventQueue.take();
  }
}
