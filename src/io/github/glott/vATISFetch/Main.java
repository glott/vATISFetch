package io.github.glott.vATISFetch;

public class Main
{

    public static String FETCH_DIR = System.getProperty("user.home") + "\\AppData\\Local\\vATIS\\Fetch";
    public static String URL_BASE = "https://joshglott.com/";
    public static String VERSION = "v1.3.8";

    public static void main(String[] args)
    {
        Display display = new Display();
        display.run();
    }
}
