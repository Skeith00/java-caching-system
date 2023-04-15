package functional;

import cache.model.VirtualNode;
import cache.service.CacheManager;
import cache.model.Node;
import cache.model.NodeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class CacheManagerFunctionalTest {

    private final CacheManager cacheManager = new CacheManager(3);
    private final SortedMap<Integer, VirtualNode> nodesCluster = new TreeMap<>();
    private boolean firstTry = true;

    @Before
    public void setUp() {
        if (firstTry) {
            try {
                Field declaredField = CacheManager.class.getDeclaredField("nodesCluster");
                boolean accessible = declaredField.isAccessible();
                declaredField.setAccessible(true);
                declaredField.set(cacheManager, nodesCluster);
                declaredField.setAccessible(accessible);

                UUID uuid = UUID.randomUUID();
                cacheManager.addNode(uuid, NodeType.randomNode());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            firstTry = false;
        }
    }

    @Test
    public void testCacheAndRetrieve() {
        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        cacheManager.cacheKey("key3", "value3");
        Assert.assertEquals("value1", cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
        Assert.assertEquals("value3", cacheManager.retrieveKey("key3"));
    }

    @Test
    public void testInvalidate() {
        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        Assert.assertEquals("value1", cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
        cacheManager.invalidateKey("key1");
        Assert.assertNull(cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
    }

    @Test
    public void addNode() {
        int size = nodesCluster.size();
        UUID uuid = UUID.randomUUID();
        cacheManager.addNode(uuid, NodeType.HAZELCAST);
        Assert.assertEquals(size + CacheManager.NUM_REPLICAS, nodesCluster.size());
    }

    @Test
    public void removeNode() {
        UUID uuid = UUID.randomUUID();
        cacheManager.addNode(uuid, NodeType.HAZELCAST);
        int size = nodesCluster.size();
        cacheManager.removeNode(uuid);
        Assert.assertEquals(size - CacheManager.NUM_REPLICAS, nodesCluster.size());
    }
}
