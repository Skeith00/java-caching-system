package unit;

import cache.event.NodeEventHandlerImpl;
import cache.model.Node;
import cache.service.NodeManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class NodeEventHandlerTest {

    @Mock
    private Node node;
    @Mock
    private NodeManager nodeManager;

    private NodeEventHandlerImpl nodeEventHandlerImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        nodeEventHandlerImpl = new NodeEventHandlerImpl(nodeManager);
        when(node.getNodeId()).thenReturn(UUID.randomUUID());
    }

    @Test
    public void testNodeAdded() {
        nodeEventHandlerImpl.nodeAdded(node);
        verify(nodeManager).addNode(node);
    }

    @Test
    public void testNodeRemoved() {
        nodeEventHandlerImpl.nodeRemoved(node);
        verify(nodeManager).removeNode(node);
    }

    @Test
    public void testNodeShuttingDown() {
        nodeEventHandlerImpl.nodeRemoved(node);
        verify(nodeManager).removeNode(node);
    }
}
