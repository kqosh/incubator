package net.ronaldkoster.resourcepool;

import java.io.Closeable;
import java.io.IOException;

/**
 * Decorator around {@link Closeable} that is part of a pool.
 */
public class PooledResource implements Closeable {
    
    private final ResourcePool pool;
    private final Closeable resource;
    
    private boolean corrupted;

    public PooledResource(ResourcePool pool, Closeable resource) {
        this.pool = pool;
        this.resource = resource;
    }

    public Closeable getResource() {
        return resource;
    }

    /**
     * Returns this resource to the pool.
     */
    @Override
    public void close() throws IOException {
        pool.returnResource(this);
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    /**
     * When you suspect a resource has become corrupted, for example you have accidentally invoked {@code close()} on
     * the underlying resource, set this field to true before {@link #close() returning} it to the resource pool. The pool
     * will then drop the resource and add a newly created one to the pool instead of this resource. 
     */
    public void setCorrupted(boolean corrupted) {
        this.corrupted = corrupted;
    }
}
