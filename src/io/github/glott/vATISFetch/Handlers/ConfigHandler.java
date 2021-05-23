package io.github.glott.vATISFetch.Handlers;

import io.github.glott.vATISFetch.Main;
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
        File configDirectory = new File(Main.FETCH_DIR);
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
        runUpdate();
    }

    public void parseConfig(String air, JTextArea generalFetch)
    {
        if (air.equals("NONE")) return;

        JSONParser parser = new JSONParser();
        try
        {
            config = new String[]{"", "", "", ""};
            FileReader fr = new FileReader(Main.FETCH_DIR + File.separator + air + ".json");
            JSONObject jsonObject = (JSONObject) parser.parse(fr);

            config[0] = "" + jsonObject.get("has_notams");
            config[1] = "" + jsonObject.get("notam_start");

            JSONArray ignore = (JSONArray) jsonObject.get("ignore");
            for (Object anIgnore : ignore) config[2] += "" + anIgnore + "\t";

            if (generalFetch != null && generalFetch.getText().contains("parse")) generalFetch.setText("");
            fr.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            if (generalFetch != null) generalFetch.setText("Unable to parse config!");
        }
    }

    public String[] getConfig()
    {
        return this.config;
    }

    private void runUpdate()
    {
        try
        {
            File temp = new File(Main.FETCH_DIR + File.separator + "configs.txt");
            temp.createNewFile();
            FileUtils.copyURLToFile(new URL(Main.URL_BASE + "vaf/configs/configs.txt"), temp);
            Scanner sc = new Scanner(temp);
            while (sc.hasNext())
            {
                String airport = sc.next();
                URL down = new URL(Main.URL_BASE + "vaf/configs/" + airport + ".json");
                File f = new File(Main.FETCH_DIR + File.separator + airport + ".json");
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
