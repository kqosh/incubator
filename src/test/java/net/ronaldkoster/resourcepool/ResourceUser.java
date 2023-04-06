package net.ronaldkoster.resourcepool;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ResourceUser implements Runnable{

    private final String name;
    private final CountDownLatch latch;
    private final ResourcePool pool;
    private final long durationInMillis;
    
    public ResourceUser(String name, CountDownLatch latch, ResourcePool pool, long durationInMillis) {
        this.name = name;
        this.latch = latch;
        this.pool = pool;
        this.durationInMillis = durationInMillis;
    }

    @Override
    public void run() {
        try (var resource = pool.getResource()) {
            printTime("t0");
            Thread.sleep(durationInMillis);
        } catch (Exception ex) {
            throw new RuntimeException("name=" + name, ex);
        }
        printTime("t1");
        latch.countDown();
    }
    
    private void printTime(String label) {
        System.out.println(String.format("%s %s=%s", name, label, System.currentTimeMillis()));
    }
}
