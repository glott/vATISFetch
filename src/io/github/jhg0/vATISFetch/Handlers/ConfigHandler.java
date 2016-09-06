package io.github.jhg0.vATISFetch.Handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;

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
            for (Object anIgnore : ignore) config[2] += "" + anIgnore + ",";
            if (generalFetch.getText().contains("parse")) generalFetch.setText("");
            fr.close();
            return jsonObject.get("facility") != null && jsonObject.get("facility").equals("ZMA");
        } catch (Exception ex)
        {
            ex.printStackTrace();
            generalFetch.setText("Unable to parse config!");
        }
        return false;
    }

    public String[] getConfig()
    {
        return this.config;
    }
}
