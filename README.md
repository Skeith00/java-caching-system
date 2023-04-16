# java-caching-system
Java Distributed Cache Challenge

This project implements a distributed caching mechanism using a consistent hashing algorithm for storing and retrieving data.

**Features**

The system consists of the following components:

**CacheManager and NodeManagers**

These services interact with the NodeRingManager. The CacheManager handles all key-value operations, while the NodeManager handles all node operations.

**NodeEventHandler**

This component interacts with the CacheManager to trigger node operations based on events. These events are simulated and used in the console menu as a means of interacting with the NodeEventHandler.

**Connectors**

The CacheConnectorFactory provides an interface to acquire different connectors based on the node type. The GenericConnector is the only connector with logic. The remaining connectors are just dummies that inherit from the GenericConnector. All cache entries reside within each of the connectors, simulating a realistic approach where all key-values would live in remote servers, and specific clients/connectors are used to interact with them.

**Usage**

To use the system, follow these steps:

1. Open a terminal and navigate to the root directory of the project.
2. Run the command `./gradlew build` to compile the Java classes, run the tests, and build the JAR under build/libs.
Alternatively, you can run the command `./gradlew jar` to only compile the Java classes and build the JAR.
3. Finally, run the command `java -jar build/libs/java-caching-system.jar` to start the system.

Testing
To test the system, run the command ./gradlew test. This will execute the unit tests included in the project.