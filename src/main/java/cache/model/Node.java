package cache.model;

import java.util.Random;
import java.util.UUID;

public class Node {
    private final UUID nodeId;
    private final String hostname;
    private final int port;
    private final NodeType type;

    public Node(UUID nodeId, String hostname, int port, NodeType type) {
        this.nodeId = nodeId;
        this.hostname = hostname;
        this.port = port;
        this.type = type;
    }

    public Node(UUID nodeId, NodeType type) {
        this.nodeId = nodeId;
        this.hostname = "http://localhost";
        this.port = new Random().nextInt(Integer.MAX_VALUE); ;
        this.type = type;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public NodeType getType() {
        return type;
    }

    public String getNodeUrl() {
        return getHostname() + ":" + getPort();
    }
}
