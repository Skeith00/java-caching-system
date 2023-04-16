package functional;

import cache.connector.CacheConnectorFactory;
import cache.exception.KeyNotFound;
import cache.model.Node;
import cache.model.NodeType;
import cache.model.VirtualNode;
import cache.service.CacheManager;
import cache.service.NodeManager;
import cache.service.NodeRingManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class NodeManagerFunctionalTest {

    private NodeManager nodeManager;
    private CacheManager cacheManager;

    @Before
    public void setUp() {
        NodeRingManager nodeRingManager = new NodeRingManager();
        nodeManager = new NodeManager(nodeRingManager);
        cacheManager = new CacheManager(nodeRingManager);
    }

    @Test
    public void addNode() {
        try {
            cacheManager.cacheKey("key1", "value1");
            Assert.fail("Node Ring should be empty so no key-values shouldn't be stored.");
        } catch (Exception e) {
            Assert.assertEquals("Node Cluster is empty.", e.getMessage());
        }
        Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
        nodeManager.addNode(node);

        try {
            cacheManager.cacheKey("key1", "value1");
        } catch (Exception e) {
            Assert.fail("A Node is present in the ring so key-values should be stored.");
        }
    }

    @Test
    public void removeNode() {
        Node node = new Node(UUID.randomUUID(), NodeType.randomNode());

        try {
            nodeManager.removeNode(node);
            Assert.fail("Node Ring should be empty and therefore this Node not present.");
        } catch (Exception e) {
            Assert.assertEquals("Node " + node.getNodeId() + " not found.", e.getMessage());
        }

        nodeManager.addNode(node);

        try {
            nodeManager.removeNode(node);
        } catch (Exception e) {
            Assert.fail("A Node is present in the ring so should have been removed.");
        }
    }


    @Test
    public void testKeyTransfersWhenRemoving() {
        UUID uuid1 = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID uuid2 = UUID.fromString("123e4569-e89b-42d3-a556-556742440000");
        Node node1 = new Node(uuid1, "http://redis", 6379, NodeType.REDIS);
        Node node2 = new Node(uuid2, "http://hazelcast", 54327, NodeType.HAZELCAST);
        nodeManager.addNode(node1);
        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        Assert.assertEquals("value1", cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
        nodeManager.addNode(node2);
        nodeManager.removeNode(node1);
        Assert.assertEquals("value1", cacheManager.retrieveKey("key1"));
        Assert.assertEquals("value2", cacheManager.retrieveKey("key2"));
    }

}
