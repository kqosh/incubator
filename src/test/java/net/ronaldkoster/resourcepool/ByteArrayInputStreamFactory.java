package net.ronaldkoster.resourcepool;

import java.io.ByteArrayInputStream;

public class ByteArrayInputStreamFactory implements PooledResourceFactory {
    
    private final ResourcePool pool;
    private final int arraySize;

    public ByteArrayInputStreamFactory(ResourcePool pool, int arraySize) {
        this.pool = pool;
        this.arraySize = arraySize;
    }

    @Override
    public PooledResource create() {
        return new PooledResource(pool, new ByteArrayInputStream(new byte[arraySize]));
    }
}
