package cache.utils;

import java.io.IOException;

public class ConsoleUtils {

    private ConsoleUtils() {
    }

    public static void printMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose your option : ");
    }

}
