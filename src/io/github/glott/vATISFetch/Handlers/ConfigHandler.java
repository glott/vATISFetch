package io.github.glott.vATISFetch.Handlers;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;

public class ConfigHandler
{

    private String[] config = {"", "", "", ""};

    @SuppressWarnings("unchecked, ConstantConditions, ResultOfMethodCallIgnored")
    public void initConfigSelection(JComboBox configSelection)
    {
        File configDirectory = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vATIS\\Fetch");
        if (!(configDirectory.exists() && configDirectory.isDirectory() && configDirectory.listFiles().length > 0))
        {
            configSelection.setEnabled(false);
            configSelection.setFont(configSelection.getFont().deriveFont(Font.BOLD));
            configSelection.addItem("NONE");
            return;
        }
        configSelection.setEnabled(true);
        configSelection.setFont(configSelection.getFont().deriveFont(Font.PLAIN));
        configSelection.removeAllItems();
        for (File f : configDirectory.listFiles())
        {
            if (f.getName().matches("[KPkp][A-Za-z][A-Za-z][A-Za-z].json"))
                configSelection.addItem(f.getName().replaceAll("[^A-Za-z]", "").toUpperCase().replace("JSON", ""));
            else
            {
                f.delete();
                initConfigSelection(configSelection);
                return;
            }
        }
        String update = configSelection.getSelectedItem().toString();
        configSelection.setSelectedItem(null);
        if (parseConfig(update, null))
            runUpdate();
    }

    public boolean parseConfig(String air, JTextArea generalFetch)
    {
        if (air.equals("NONE")) return false;
        JSONParser parser = new JSONParser();
        try
        {
            config = new String[]{"", "", "", ""};
            FileReader fr = new FileReader(System.getProperty("user.home") + "\\AppData\\Roaming\\vATIS\\Fetch\\" + air + ".json");
            Object obj = parser.parse(fr);
            JSONObject jsonObject = (JSONObject) obj;

            config[0] = "" + jsonObject.get("has_notams");
            config[1] = "" + jsonObject.get("notam_start");

            JSONArray ignore = (JSONArray) jsonObject.get("ignore");
            for (Object anIgnore : ignore) config[2] += "" + anIgnore + "\t";
            if (generalFetch != null && generalFetch.getText().contains("parse")) generalFetch.setText("");
            fr.close();
            return jsonObject.get("facility") != null && jsonObject.get("facility").equals("ZXX");
        } catch (Exception ex)
        {
            ex.printStackTrace();
            if (generalFetch != null) generalFetch.setText("Unable to parse config!");
        }
        return false;
    }

    public String[] getConfig()
    {
        return this.config;
    }

    private void runUpdate()
    {
        try
        {
            File temp = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vATIS\\Fetch\\configs.txt");
            temp.createNewFile();
            FileUtils.copyURLToFile(new URL("http://glott.github.io/vaf/configs/configs.txt"), temp);
            Scanner sc = new Scanner(temp);
            while (sc.hasNext())
            {
                String airport = sc.next();
                URL down = new URL("http://glott.github.io/vaf/configs/" + airport + ".json");
                File f = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vATIS\\Fetch\\" + airport + ".json");
                if (f.exists())
                    FileUtils.copyURLToFile(down, f);
            }
            sc.close();
            temp.delete();
        } catch (Exception ignored)
        {
        }

    }
}
