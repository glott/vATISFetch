package io.github.jhg0.vATISFetch.Handlers;

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
        System.setProperty("jsse.enableSNIExtension", "false");
        try
        {
            URL url = new URL("https://webdatis.arinc.net/cgi-bin/datis/get_datis?station=" + selectedItem + "&sessionId=KXY9E2L&products=DATIS&arrdep=DEP");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String input;
            while ((input = br.readLine()) != null)
                atis[0] += input + " ";
            url = new URL("https://webdatis.arinc.net/cgi-bin/datis/get_datis?station=" + selectedItem + "&sessionId=KXY9E2L&products=DATIS&arrdep=ARR");
            con = (HttpsURLConnection) url.openConnection();
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((input = br.readLine()) != null)
                atis[1] += input + " ";
            br.close();
        } catch (Exception ignored)
        {
            notamFetch.setText("Unable to fetch ATIS!");
        }
    }

    public String[] mergeATIS(String[] config)
    {
        String[] out = {"", ""};
        String[] tempGeneral = {"", ""};

        if (config[3].length() > 1)
        {
            String[] ignore = config[3].split(",");
            for (String s : ignore)
                for (int i = 0; i < 2; i++)
                    atis[i] = atis[i].replaceAll(s, "");
        }

        if (config[1].equals("true") && atis[0].contains(config[2]))
        {
            String[] tempNotams = {"", ""};
            for (int i = 0; i < 2; i++)
                tempNotams[i] = atis[i].substring(atis[i].indexOf(config[2]) + config[2].length(), atis[i].indexOf(" ...ADVS YOU"));
            out[1] = tempNotams[0].length() > tempNotams[1].length() ? tempNotams[0] : tempNotams[1];
        }
        for (int i = 0; i < 2; i++)
        {
            if (config[1].equals("true") && atis[0].contains(config[2]))
                tempGeneral[i] = atis[i].substring(atis[i].indexOf(")") + 1, atis[i].indexOf(config[2]));
            else
                tempGeneral[i] = atis[i].substring(atis[i].indexOf(")") + 1, atis[i].indexOf(" ...ADVS YOU"));
            tempGeneral[i] = tempGeneral[i].substring(tempGeneral[i].indexOf(". ") + 2);
        }
        if (config[0].equals("true"))
            out[0] = tempGeneral[0] + tempGeneral[1];
        else
            out[0] = tempGeneral[0];
        return out;
    }
}
