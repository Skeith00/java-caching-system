package unit;

import cache.service.CacheManager;
import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.event.NodeEventHandler;
import cache.model.Node;
import cache.model.NodeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class CacheManagerTest {

    @Mock
    private CacheConnector cacheConnector;
    @Mock
    private NodeEventHandler nodeEventHandler;

    @InjectMocks
    private CacheManager cacheManager = new CacheManager(3);

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCacheKeyValue() {
        try (MockedStatic<CacheConnectorFactory> factoryMockedStatic = Mockito.mockStatic(CacheConnectorFactory.class)) {
            factoryMockedStatic
                    .when(() -> CacheConnectorFactory.getConnector(any(NodeType.class)))
                    .thenReturn(cacheConnector);

            cacheManager.cacheKey("key", "value");
        }
        verify(cacheConnector, times(1)).cache(anyString(), eq("key"), eq("value"));
    }

    @Test
    public void testRetrieve() {
        when(cacheConnector.retrieve(anyString(), anyString())).thenReturn("value");
        try (MockedStatic<CacheConnectorFactory> factoryMockedStatic = Mockito.mockStatic(CacheConnectorFactory.class)) {
            factoryMockedStatic
                    .when(() -> CacheConnectorFactory.getConnector(any(NodeType.class)))
                    .thenReturn(cacheConnector);

            String result = cacheManager.retrieveKey("key");
            Assert.assertEquals("value", result);
        }
        verify(cacheConnector, times(1)).retrieve(anyString(), eq("key"));
    }

    @Test
    public void testInvalidate() {
        try (MockedStatic<CacheConnectorFactory> factoryMockedStatic = Mockito.mockStatic(CacheConnectorFactory.class)) {
            factoryMockedStatic
                    .when(() -> CacheConnectorFactory.getConnector(any(NodeType.class)))
                    .thenReturn(cacheConnector);

            cacheManager.invalidateKey("key");
        }
        verify(cacheConnector, times(1)).invalidate(anyString(), eq("key"));
    }

    @Test
    public void testAddNode() {
        NodeType nodeType = NodeType.randomNode();
        Node node = new Node(UUID.randomUUID(), nodeType);
        cacheManager.addNode(node.getNodeId(), nodeType);
    }

    @Test
    public void testRemoveNode() {
        NodeType nodeType = NodeType.randomNode();
        Node node = new Node(UUID.randomUUID(), nodeType);
        cacheManager.addNode(node.getNodeId(), nodeType);
        cacheManager.removeNode(node.getNodeId());
    }

}
