package net.ronaldkoster.resourcepool;

import java.io.ByteArrayInputStream;

public class ByteArrayInputStreamFactory implements PooledResourceFactory {
    
    private final int arraySize; 
    
    public ByteArrayInputStreamFactory(int arraySize) {
        this.arraySize = arraySize;
    }

    @Override
    public PooledResource createResource(ResourcePool pool) {
        return new PooledResource(pool, new ByteArrayInputStream(new byte[arraySize]));
    }
}
