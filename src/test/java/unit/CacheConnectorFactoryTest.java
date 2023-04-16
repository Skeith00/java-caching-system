package unit;

import cache.connector.*;
import cache.exception.NodeTypeNotFound;
import cache.model.NodeType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CacheConnectorFactoryTest {

    @Test
    public void testGetHazelcastConnector() {
        // Test getting the Hazelcast connector
        CacheConnector result = CacheConnectorFactory.getConnector(NodeType.HAZELCAST);
        Assert.assertTrue(result instanceof HazelcastConnector);
    }

    @Test
    public void testGetMemcacheConnector() {
        // Test getting the Memcache connector
        CacheConnector result = CacheConnectorFactory.getConnector(NodeType.MEMCACHE);
        Assert.assertTrue(result instanceof MemcacheConnector);
    }

    @Test
    public void testGetRedisConnector() {
        // Test getting the Redis connector
        CacheConnector result = CacheConnectorFactory.getConnector(NodeType.REDIS);
        Assert.assertTrue(result instanceof RedisConnector);
    }

    @Test(expected = NodeTypeNotFound.class)
    public void testInvalidConnector() {
        // Test getting an invalid connector
        CacheConnectorFactory.getConnector(null);
        Assert.fail("Expected an Exception to be thrown");
    }
}
