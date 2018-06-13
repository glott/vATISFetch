package io.github.glott.vATISFetch.Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebHandler
{

    private List<String> exceptions = new ArrayList<>();
    private String externalURL = "";
    private String internalURL = "";
    private String id = "";

    public void init()
    {
        try
        {
            URL url = new URL("http://glott.github.io/vaf/Exceptions.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            if ((input = br.readLine()) != null)
                exceptions = Arrays.asList(input.split(", "));
            br.close();
        } catch (Exception ignored)
        {
        }

        try
        {
            URL url = new URL("http://glott.github.io/vaf/URL.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            if ((input = br.readLine()) != null)
                externalURL = hexToString(input);
            br.close();
        } catch (Exception ignored)
        {
        }

        try
        {
            URL url = new URL("http://glott.github.io/vaf/ID.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            if ((input = br.readLine()) != null)
                id = hexToString(input);
            br.close();
        } catch (Exception ignored)
        {
        }

        internalURL = "http://vatisapi.radarcontact.me/datis.php?station=%ARPT%&arrdep=";
    }

    public String getURL(String airport, boolean configLogic)
    {
        if (externalURL.length() > 0 && id.length() > 0 && exceptions.size() > 0 && exceptions.contains(airport) && configLogic)
            return externalURL.replace("%ID%", id).replace("%ARPT%", airport);
        return internalURL.replace("%ARPT%", airport.toLowerCase());
    }

    private static String hexToString(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
