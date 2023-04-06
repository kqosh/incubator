package net.ronaldkoster.resourcepool;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A resource pool for {@link PooledResource} objects.
 */
public class ResourcePool implements Closeable {
    
    private final PooledResourceFactory factory;
    private final int size;
    private final BlockingQueue<PooledResource> queue;
    private final List<PooledResource> resources = new ArrayList<>();
    
    private final AtomicBoolean isClosing = new AtomicBoolean(false); 

    public ResourcePool(int size, PooledResourceFactory factory) {
        this.size = size;
        this.queue = new ArrayBlockingQueue<>(size, false);
        this.factory = factory;
        init();
    }
    
    private void init() {
        for (int i = 0; i < size; i++) {
            createResource();
        }
    }

    public int getSize() {
        return size;
    }

    private void createResource() {
        var resource = factory.createResource(this);
        resources.add(resource);
        queue.offer(resource);
    }

    /**
     * Return resource to the pool. Is invoked by {@link PooledResource#close()}}. 
     * @throws IOException if the resource was marked as (@link PooledResource#isCorrupted() corrupted} and closing it fails.
     */
    public void returnResource(PooledResource resource) throws IOException {
        if (resource.isCorrupted()) {
            dropResource(resource);
            createResource();
        } else {
            queue.offer(resource);
        }
    }
    
    private void dropResource(PooledResource resource) throws IOException {
        try {
            resources.remove(resource);
            resource.getResource().close();
        } catch (Exception ex) {
            throw new IOException("Failed to close corrupted resource", ex);
        }
    }

    /**
     * Get a resource, waiting if necessary until a resource becomes available. 
     */
    public PooledResource getResource() {
        if (isClosing.get()) {
            throw new IllegalStateException("Pool is shutting down.");
        }
        try {
            return queue.take();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Closes all resources and empties the pool. It does not wait resources to be returned.
     */
    @Override
    public void close() throws IOException {
        isClosing.set(true);
        for (PooledResource resource : resources) {
            queue.remove(resource);
            resource.getResource().close();
        }
    }
}
