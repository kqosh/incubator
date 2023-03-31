package net.ronaldkoster.resourcepool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourcePoolTest {
    
//    @Tes
    @Test
    void qqqq() {
        var pool = new ResourcePool(3, new ByteArrayInputStreamFactory(4));
        Assertions.assertEquals(4, pool.getSize());
        //qqqq more
    }
}
