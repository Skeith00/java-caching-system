package cache.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VirtualNode {
    private final Node node;
    private final List<String> keys = new ArrayList<>();

    public VirtualNode(Node node) {
        this.node = node;
    }

    public NodeType getNodeType() {
        return node.getType();
    }

    public void addKey(String key) {
        keys.add(key);
    }

    public void removeKey(String key) {
        keys.remove(key);
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getNodeUrl() {
        return node.getNodeUrl();
    }

    public UUID getNodeId() {
        return node.getNodeId();
    }

    public Node getNode() {
        return node;
    }
}
