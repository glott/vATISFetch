package io.github.jhg0.vATISFetch.Handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

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
        configSelection.setSelectedItem(null);
    }

    public void parseConfig(String air, JTextArea generalFetch)
    {
        JSONParser parser = new JSONParser();
        try
        {
            config = new String[]{"", "", "", ""};
            Object obj = parser.parse(new FileReader(System.getProperty("user.home") + "\\AppData\\Roaming\\vATIS\\Fetch\\" + air + ".json"));
            JSONObject jsonObject = (JSONObject) obj;

            config[0] = "" + jsonObject.get("dep_arr");
            config[1] = "" + jsonObject.get("has_notams");
            config[2] = "" + jsonObject.get("notam_start");

            JSONArray ignore = (JSONArray) jsonObject.get("ignore");
            for (Object anIgnore : ignore) config[3] += "" + anIgnore + ",";
            if (generalFetch.getText().contains("parse")) generalFetch.setText("");
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
            generalFetch.setText("Unable to parse config!");
        }
    }

    public String[] getConfig()
    {
        return this.config;
    }
}
