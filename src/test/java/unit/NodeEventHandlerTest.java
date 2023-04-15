package unit;

import cache.event.NodeEventHandlerImpl;
import cache.model.Node;
import cache.service.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NodeEventHandlerTest {

    @Mock
    private Node node;
    @Mock
    private CacheManager cacheManager;

    @Mock
    private Logger logger;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNodeAdded() {
        try (MockedStatic<Logger> loggerMockedStatic = Mockito.mockStatic(Logger.class)) {
            loggerMockedStatic
                    .when(() -> Logger.getLogger(anyString()))
                    .thenReturn(logger);

            doNothing().when(logger).info(anyString());
            NodeEventHandlerImpl nodeEventHandlerImpl = new NodeEventHandlerImpl(cacheManager);
            nodeEventHandlerImpl.nodeAdded(node);
        }
        verify(logger).info("Node " + node.getNodeId().toString() + " has been added.");
    }

    @Test
    public void testNodeRemoved() {
        try (MockedStatic<Logger> loggerMockedStatic = Mockito.mockStatic(Logger.class)) {
            loggerMockedStatic
                    .when(() -> Logger.getLogger(anyString()))
                    .thenReturn(logger);

            NodeEventHandlerImpl nodeEventHandlerImpl = new NodeEventHandlerImpl(cacheManager);
            nodeEventHandlerImpl.nodeRemoved(node);
        }
        verify(logger).info("Node " + node.getNodeId().toString() + " has been deleted.");
    }

    @Test
    public void testNodeShuttingDown() {
        try (MockedStatic<Logger> loggerMockedStatic = Mockito.mockStatic(Logger.class)) {
            loggerMockedStatic
                    .when(() -> Logger.getLogger(anyString()))
                    .thenReturn(logger);

            NodeEventHandlerImpl nodeEventHandlerImpl = new NodeEventHandlerImpl(cacheManager);
            nodeEventHandlerImpl.nodeShuttingDown(node);
        }
        verify(logger).info("Node " + node.getNodeId().toString() + " has been shut down.");
    }
}
