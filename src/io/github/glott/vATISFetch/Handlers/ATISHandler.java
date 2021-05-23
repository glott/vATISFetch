package io.github.glott.vATISFetch.Handlers;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    public void fetchATIS(String selectedItem, JTextArea notamFetch)
    {
        if (selectedItem == null) return;
        atis = new String[]{"", ""};

        try
        {
            URL url = new URL(webHandler.getURL(selectedItem));
            JSONParser parser = new JSONParser();
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = "";
            String input;
            while ((input = br.readLine()) != null)
                json += input;

            JSONArray jsonArray = (JSONArray) parser.parse(json);
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                String type = obj.get("type").toString();

                if (type.equals("dep") || type.equals("combined"))
                    atis[0] = obj.get("datis").toString();
                else if (type.equals("arr"))
                    atis[1] = obj.get("datis").toString();
            }

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
            String[] ignore = config[2].split("\t");
            for (String s : ignore)
                for (int i = 0; i < 2; i++)
                    atis[i] = atis[i].replaceAll(s, "");
        }

        if (atis[0].contains("DIGITAL ATIS NOT AVAILABLE"))
        {
            out[0] = "DIGITAL ATIS NOT AVAILABLE";
            out[1] = "";
            out[2] = "D-ATIS NOT AVAIL";
            return out;
        }

        // TODO FIX SINGLE ARR/DEP
        if (config[0].equals("true") && atis[0].contains(config[1]))
        {
            String[] tempNotams = {"", ""};
            for (int i = 0; i < 2; i++)
                tempNotams[i] = atis[i].substring(atis[i].indexOf(config[1]) + config[1].length(), atis[i].indexOf(" ...ADVS YOU"));
            out[1] = tempNotams[0].length() > tempNotams[1].length() ? tempNotams[0] : tempNotams[1];
        }
        System.out.println(atis[0].toString() + "\n" + atis[1].toString());

        for (int i = 0; i < 2; i++)
        {
            int pos = StringUtils.countMatches(atis[i], "ADVS") > 1 ? i + 1 : 1;
            int idx = StringUtils.ordinalIndexOf(atis[i], ")", pos) + 1;

            if (config[0].equals("true") && atis[0].contains(config[1]))
                tempGeneral[i] = atis[i].substring(idx, StringUtils.ordinalIndexOf(atis[i], config[1], pos));
            else
                tempGeneral[i] = atis[i].substring(idx, StringUtils.ordinalIndexOf(atis[i], " ...ADVS YOU", pos));
            tempGeneral[i] = tempGeneral[i].substring(tempGeneral[i].indexOf(". ") + 2);
        }

        if (!tempGeneral[0].equals(tempGeneral[1]))
            out[0] = tempGeneral[0] + tempGeneral[1];
        else
            out[0] = tempGeneral[0];
        out[2] = !atis[1].replaceAll("INFO [A-Z] [0-2][0-9][0-9][0-9]Z", "").equals(atis[1]) ? atis[1].substring(atis[1].indexOf("INFO "), atis[1].indexOf("INFO ") + 12) : "Fetch ATIS";
        return out;
    }
}
