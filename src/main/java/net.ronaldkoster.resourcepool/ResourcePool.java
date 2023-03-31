package net.ronaldkoster.resourcepool;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ResourcePool implements Closeable {
    
    private final PooledResourceFactory factory;
    private final int size;
    private final BlockingQueue<PooledResource> queue;
    private final List<PooledResource> resources = new ArrayList<>();

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
    
    public void returnResource(PooledResource resource) {
        if (resource.isCorrupted()) {
            dropResource(resource);
            createResource();
        } else {
            queue.offer(resource);
        }
    }
    
    private void dropResource(PooledResource resource) {
        resources.remove(resource);
        try {
            resource.close();
        } catch (Exception ex) {
            System.out.println("Failed to close corrupted resource");
            ex.printStackTrace();
        }
    }
    
    public PooledResource getResource() {
        try {
            return queue.take();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        for (PooledResource resource : resources) {
            getResource().getResource().close();
        }
    }

    private void closeResource() {
        var resource = getResource();
        resources.remove(resource);
        resource.close();
    }
}
