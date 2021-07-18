package org.github.examples;

public class CommandLineApp {

    public void hello(boolean shouldPrint, String who) {
        if (shouldPrint) {
            System.out.println("Hello " + who);
        }
    }

}
