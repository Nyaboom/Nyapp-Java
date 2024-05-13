package com.nin0dev.nyaboom.nyapp;

public class Yapper {
    public static void error(String text) {
        System.out.println("\u001B[31m[ERROR] " + text + "\u001B[0m");
    }
    public static void warning(String text) {
        System.out.println("\u001B[33m[WARN] " + text + "\u001B[0m");
    }

    public static void info(String text) {
        System.out.println("\u001B[34m[INFO] \u001B[0m" + text);
    }
}
