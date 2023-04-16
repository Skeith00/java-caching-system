package unit;

import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.event.NodeEventHandler;
import cache.model.Node;
import cache.model.NodeType;
import cache.service.CacheManager;
import cache.service.NodeManager;
import cache.service.NodeRingManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class NodeManagerTest {

    @Mock
    private CacheConnector cacheConnector;
    @Mock
    private NodeRingManager nodeRingManager;
    private NodeManager nodeManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        nodeManager = new NodeManager(nodeRingManager);
    }

    @Test
    public void testAddNode() {
        NodeType nodeType = NodeType.randomNode();
        Node node = new Node(UUID.randomUUID(), nodeType);
        nodeManager.addNode(node);
    }

    @Test
    public void testRemoveNode() {
        NodeType nodeType = NodeType.randomNode();
        Node node = new Node(UUID.randomUUID(), nodeType);
        nodeManager.addNode(node);
        nodeManager.removeNode(node);
    }

}
