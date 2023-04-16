package unit;

import cache.model.VirtualNode;
import cache.service.CacheManager;
import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.model.NodeType;
import cache.service.NodeRingManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.SortedMap;

import static org.mockito.Mockito.*;

public class CacheManagerTest {

    @Spy
    private final NodeRingManager nodeRingManager = new NodeRingManager();
    @Mock
    private CacheConnector cacheConnector;
    @Mock
    private VirtualNode virtualNode;
    @Mock
    private SortedMap<Integer, VirtualNode> nodesCluster;
    private CacheManager cacheManager;

    @Before
    public void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        cacheManager = new CacheManager(nodeRingManager);
        Field field = nodeRingManager.getClass().getDeclaredField("nodesCluster");
        field.setAccessible(true);
        field.set(nodeRingManager, nodesCluster);
        when(nodesCluster.headMap(any())).thenReturn(nodesCluster);
        when(nodesCluster.lastKey()).thenReturn(1);
        when(nodesCluster.firstKey()).thenReturn(1);
        when(nodesCluster.get(anyInt())).thenReturn(virtualNode);
        field.setAccessible(false);

        when(virtualNode.getNodeType()).thenReturn(NodeType.REDIS);
        when(virtualNode.getNodeUrl()).thenReturn("http//:localhost:80");
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
        when(cacheConnector.retrieve("http//:localhost:80", "key")).thenReturn("value");
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

}
