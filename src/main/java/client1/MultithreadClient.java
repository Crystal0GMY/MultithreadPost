package client1;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class MultithreadClient {
  private static final String SERVER_URL = "http://18.237.181.22:8080/Assignment1_war";
  private static final int TOTAL_REQUESTS = 200000;
  private static final int NUMBER_OF_THREADS = 200;
  private static final int REQUEST_PER_THREAD = 1000;
  private static final AtomicInteger successfulRequests = new AtomicInteger(0);
  private static final AtomicInteger failedRequests = new AtomicInteger(0);

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    Thread eventGeneratorThread = new Thread(new EventGeneratorThread());
    eventGeneratorThread.start();

    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    List<Future<Void>> futures = new ArrayList<>();

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < NUMBER_OF_THREADS; i++) {
      HTTPClientThread clientThread = new HTTPClientThread(SERVER_URL, successfulRequests, failedRequests, REQUEST_PER_THREAD);
      futures.add(executorService.submit(clientThread));
    }

    for (Future<Void> future : futures) {
      future.get();
    }

    eventGeneratorThread.join();

    executorService.shutdown();

    long endTime = System.currentTimeMillis();
    long wallTime = endTime - startTime;

    System.out.println("This is the Part 1 Output: ");
    System.out.println("Number of Threads used: " + NUMBER_OF_THREADS);
    System.out.println("Total requests sent: " + TOTAL_REQUESTS);
    System.out.println("Successful requests: " + successfulRequests.get());
    System.out.println("Failed requests: " + failedRequests.get());
    System.out.println("Total wall time: " + wallTime + " ms");
    System.out.println("Throughput: " + (TOTAL_REQUESTS / (wallTime / 1000.0)) + " requests per second");

    LatencyAnalyzer.analyzeResults("request_logs.csv", TOTAL_REQUESTS);
  }
}
