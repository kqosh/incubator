package net.ronaldkoster.resourcepool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ResourcePoolTest {
    
    @Test
    void corruptedResourceAreDropped() throws IOException {
        var pool = new ResourcePool(3, new ByteArrayInputStreamFactory(4));
        Assertions.assertEquals(3, pool.getSize());
        for (int i = 0; i < 2; i++) {
            pool.getResource();
        }
        final var lastResource = pool.getResource();
        lastResource.setCorrupted(true);
        pool.returnResource(lastResource);

        final var nextResource = pool.getResource();
        Assertions.assertNotEquals(lastResource, nextResource);
        
        pool.close();
    }
    
    @Test
    void assertBlockingWait() throws InterruptedException, IOException {
        var resourcePoolSize = 3;
        var threadPoolSize = resourcePoolSize + 1;
        var latch = new CountDownLatch(threadPoolSize);
        var jobDuration = 50;

        long t0, t1;
        var threadPool = Executors.newFixedThreadPool(threadPoolSize);
        try {
            try (var resourcePool = new ResourcePool(resourcePoolSize, new ByteArrayInputStreamFactory(4))) {
                t0 = System.currentTimeMillis();
                
                // last job will have to wait for at least one job to have finished 
                for (int i = 0; i < threadPoolSize; i++) {
                    threadPool.execute(new ResourceUser("job#" + i, latch, resourcePool, jobDuration));
                }

                boolean allFinished = latch.await(200, TimeUnit.MILLISECONDS);
                t1 = System.currentTimeMillis();
                Assertions.assertTrue(allFinished);
            }
        } finally {
            threadPool.shutdown();
        }

        // 3 jobs ran parallel, 1 had to wait, so total duration should exceed 2 * jobDuration
        Assertions.assertTrue(t1 - t0 > 2L * jobDuration);
    }
}
