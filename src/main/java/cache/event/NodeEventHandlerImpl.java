package cache.event;

import cache.model.Node;
import cache.model.VirtualNode;
import cache.service.CacheManager;
import cache.service.NodeManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.SortedMap;
import java.util.logging.Logger;

public class NodeEventHandlerImpl implements NodeEventHandler {

    private final Logger logger = Logger.getLogger(NodeEventHandlerImpl.class.getName());;
    private final NodeManager manager;

    public NodeEventHandlerImpl(NodeManager manager) {
        this.manager = manager;
    }

    @Override
    public void nodeAdded(Node node) {
        logger.info("Node " + node.getNodeId() + " has been added.");
        manager.addNode(node);
    }

    @Override
    public void nodeRemoved(Node node) {
        logger.info("Node " + node.getNodeId() + " has been deleted.");
        //manager.removeNode(node);
    }

    @Override
    public void nodeShuttingDown(Node node) {
        logger.info("Node " + node.getNodeId().toString() + " has been shut down.");
        manager.removeNode(node);
    }

}
