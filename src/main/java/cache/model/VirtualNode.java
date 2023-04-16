package cache.model;

import java.util.*;

public class VirtualNode {
    private final Node node;
    private final int hash;

    private final Set<String> keys = new HashSet<>();

    public VirtualNode(int hash, Node node) {
        this.hash = hash;
        this.node = node;
    }

    public NodeType getNodeType() {
        return node.getType();
    }

    public void addKey(String key) {
        keys.add(key);
    }

    public void addKeys(Set<String> key) {
        keys.addAll(key);
    }

    public void removeKey(String key) {
        keys.remove(key);
    }
    public void removeKeys() {
        keys.clear();
    }

    public Set<String> getKeys() {
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

    public int getVirtualNodeHash() {
        return hash;
    }
}
