package net.ronaldkoster.resourcepool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ResourcePoolTest {
    
    @Test
    void corruptedResourceAreDrpoped() throws IOException {
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
    
    //qqqq test 4th will blocking await
}
