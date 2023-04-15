package cache.event;

import cache.model.Node;
import cache.model.VirtualNode;
import cache.service.CacheManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.SortedMap;
import java.util.logging.Logger;

public class NodeEventHandlerImpl implements NodeEventHandler, PropertyChangeListener {

    private final Logger logger = Logger.getLogger(NodeEventHandlerImpl.class.getName());
    private final CacheManager manager;
    private SortedMap<Integer, VirtualNode> nodesCluster;


    public NodeEventHandlerImpl(CacheManager manager) {
        this.manager = manager;
        manager.addPropertyChangeListener(this);
    }

    @Override
    public void nodeAdded(Node node) {
        logger.info(String.format("Node %s has been added.", node.getNodeId().toString()));
    }

    @Override
    public void nodeRemoved(Node node) {
        logger.info(String.format("Node %s has been deleted.", node.getNodeId().toString()));
    }

    @Override
    public void nodeShuttingDown(Node node) {
        logger.info(String.format("Node %s has been shut down.", node.getNodeId().toString()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        nodesCluster = (SortedMap<Integer, VirtualNode>) evt.getOldValue();
        switch (propertyName) {
            case "addNode":
                nodeAdded((Node) evt.getNewValue());
                break;
            case "removeNode":
                nodeRemoved((Node) evt.getNewValue());
                break;
            default:
                logger.warning(String.format("Uknown event %s.", propertyName));
        }
    }
}
