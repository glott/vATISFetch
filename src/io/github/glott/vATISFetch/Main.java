package io.github.glott.vATISFetch;

public class Main
{

    public static String FETCH_DIR = System.getProperty("user.home") + "\\AppData\\Local\\vATIS\\Fetch";

    public static void main(String[] args)
    {
        Display display = new Display();
        display.run();
    }
}
