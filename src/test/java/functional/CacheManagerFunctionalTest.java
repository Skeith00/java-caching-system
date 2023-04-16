package functional;

import cache.exception.EmptyNodeRingFound;
import cache.exception.KeyNotFound;
import cache.service.CacheManager;
import cache.model.Node;
import cache.model.NodeType;
import cache.service.NodeManager;
import cache.service.NodeRingManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class CacheManagerFunctionalTest {

    private CacheManager cacheManager;
    private NodeManager nodeManager;

    @Before
    public void setUp() {
        NodeRingManager nodeRingManager = new NodeRingManager();
        nodeManager = new NodeManager(nodeRingManager);
        cacheManager = new CacheManager(nodeRingManager);
    }

    @Test(expected = EmptyNodeRingFound.class)
    public void testCacheWhenRingEmpty() {
        cacheManager.cacheKey("key1", "value1");
    }

    @Test(expected = EmptyNodeRingFound.class)
    public void testRetrieveWhenRingEmpty() {
        cacheManager.retrieveKey("key1");
    }

    @Test(expected = KeyNotFound.class)
    public void testRetrieveWhenKeyNotPresent() {
        Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
        nodeManager.addNode(node);
        cacheManager.retrieveKey("key1");
    }

    @Test
    public void testCacheAndRetrieve() {
        Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
        nodeManager.addNode(node);
        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        cacheManager.cacheKey("key3", "value3");
        Assert.assertEquals("value1", cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
        Assert.assertEquals("value3", cacheManager.retrieveKey("key3"));
    }

    @Test(expected = EmptyNodeRingFound.class)
    public void testInvalidateWhenRingEmpty() {
        cacheManager.invalidateKey("key1");
    }

    @Test(expected = KeyNotFound.class)
    public void testInvalidateWhenKeyNotPresent() {
        Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
        nodeManager.addNode(node);
        cacheManager.invalidateKey("key1");
    }

    @Test
    public void testInvalidate() {
        Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
        nodeManager.addNode(node);
        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        Assert.assertEquals("value1", cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
        cacheManager.invalidateKey("key1");
        Assert.assertThrows(KeyNotFound.class, () -> cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
    }

}
