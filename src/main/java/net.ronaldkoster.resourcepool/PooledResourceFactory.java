package net.ronaldkoster.resourcepool;

public interface PooledResourceFactory {

    PooledResource createResource(ResourcePool pool);
}
