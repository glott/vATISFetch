package io.github.jhg0.vATISFetch;

import io.github.jhg0.vATISFetch.Handlers.WebHandler;

public class Main
{

    public static void main(String[] args)
    {
        WebHandler webHandler = new WebHandler();
        webHandler.init();
        Display display = new Display();
        display.run();
    }
}
