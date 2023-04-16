import cache.utils.ConsoleUtils;
import cache.event.NodeEventHandler;
import cache.event.NodeEventHandlerImpl;
import cache.model.Node;
import cache.model.NodeType;
import cache.service.CacheManager;
import cache.service.NodeManager;
import cache.service.NodeRingManager;

import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final NodeRingManager nodeRingManager = new NodeRingManager();
    private static final CacheManager cacheManager = new CacheManager(nodeRingManager);
    private static final NodeEventHandler nodeEventHandler = new NodeEventHandlerImpl(new NodeManager(nodeRingManager));

    private static final String ENTER_KEY = "Enter key: ";

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
            nodeEventHandler.nodeAdded(node);
        }
        String[] mainOptions = {
                "1 - Add a key-value",
                "2 - Remove a key",
                "3 - Retrieve a key",
                "4 - Send a nodeAdded event",
                "5 - Send a nodeShuttingDown event",
                "6 - Quit"
        };

        int selection;
        do {
            Thread.sleep(2000);
            ConsoleUtils.printMenu(mainOptions);
            Scanner input = new Scanner(System.in);
            selection = input.nextInt();
            subMenu(selection);
        } while (selection != 6);

    }

    private static void subMenu(int selection) {
        try {
            executeActions(selection);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private static void executeActions(int selection){
        switch (selection) {
            case 1:
                addKeyValue();
                break;
            case 2:
                addRemoveKey();
                break;
            case 3:
                retrieveKey();
                break;
            case 4:
                nodeEventHandler.nodeAdded(new Node(UUID.randomUUID(), NodeType.randomNode()));
                break;
            case 5:
                nodeEventHandler.nodeShuttingDown(nodeRingManager.getRandomNode());
                break;
            case 6:
                System.out.print("Exiting...");
                break;
            default:
                System.out.print("Wrong option.");
        }
    }

    private static void addKeyValue() {
        System.out.print(ENTER_KEY);
        String keyOption1 =  getInput();
        System.out.print("Enter value: ");
        String valueOption1 =  getInput();
        cacheManager.cacheKey(keyOption1, valueOption1);
    }

    private static void addRemoveKey() {
        System.out.print(ENTER_KEY);
        String keyOption2 =  getInput();
        cacheManager.invalidateKey(keyOption2);
    }

    private static void retrieveKey() {
        System.out.print(ENTER_KEY);
        String keyOption3 = getInput();
        cacheManager.retrieveKey(keyOption3);
    }

    private static String getInput() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }
}