package io.github.glott.vATISFetch.Handlers;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATISHandler
{

    private String[] atis = {"", ""};
    private WebHandler webHandler;

    public ATISHandler()
    {
        webHandler = new WebHandler();
        webHandler.init();
    }

    @SuppressWarnings("unused")
    private static int regexIndexOf(String regex, String s)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher.start() : -1;
    }

    public void fetchATIS(String selectedItem, JTextArea notamFetch, boolean configLogic)
    {
        if (selectedItem == null) return;
        atis = new String[]{"", ""};
        System.setProperty("jsse.enableSNIExtension", "false");
        try
        {
            URL url = new URL(webHandler.getURL(selectedItem, configLogic) + "dep");
            BufferedReader br;
            if (url.toString().contains("https"))
            {
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else
                br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            while ((input = br.readLine()) != null)
                atis[0] += input + " ";
            url = new URL(webHandler.getURL(selectedItem, configLogic) + "arr");
            if (url.toString().contains("https"))
            {
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else
                br = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((input = br.readLine()) != null)
                atis[1] += input + " ";
            br.close();
            atis[0] = atis[0].replaceAll("\\s+", " ");
            atis[1] = atis[1].replaceAll("\\s+", " ");
        } catch (Exception ex)
        {
            ex.printStackTrace();
            notamFetch.setText("Unable to fetch ATIS!");
        }
    }

    public String[] mergeATIS(String[] config)
    {
        String[] out = {"", "", ""};
        String[] tempGeneral = {"", ""};

        if (config[2].length() > 1)
        {
            String[] ignore = config[2].split(",");
            for (String s : ignore)
                for (int i = 0; i < 2; i++)
                    atis[i] = atis[i].replaceAll(s, "");
        }

        if (config[0].equals("true") && atis[0].contains(config[1]))
        {
            String[] tempNotams = {"", ""};
            for (int i = 0; i < 2; i++)
                tempNotams[i] = atis[i].substring(atis[i].indexOf(config[1]) + config[1].length(), atis[i].indexOf(" ...ADVS YOU"));
            out[1] = tempNotams[0].length() > tempNotams[1].length() ? tempNotams[0] : tempNotams[1];
        }
        for (int i = 0; i < 2; i++)
        {
            if (config[0].equals("true") && atis[0].contains(config[1]))
                tempGeneral[i] = atis[i].substring(atis[i].indexOf(")") + 1, atis[i].indexOf(config[1]));
            else
                tempGeneral[i] = atis[i].substring(atis[i].indexOf(")") + 1, atis[i].indexOf(" ...ADVS YOU"));
            tempGeneral[i] = tempGeneral[i].substring(tempGeneral[i].indexOf(". ") + 2);
        }
        if (!atis[0].equals(atis[1]))
            out[0] = tempGeneral[0] + tempGeneral[1];
        else
            out[0] = tempGeneral[0];
        out[2] = !atis[1].replaceAll("INFO [A-Z] [0-2][0-9][0-9][0-9]Z", "").equals(atis[1]) ? atis[1].substring(atis[1].indexOf("INFO "), atis[1].indexOf("INFO ") + 12) : "Fetch ATIS";
        return out;
    }
}
