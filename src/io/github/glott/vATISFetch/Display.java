package io.github.glott.vATISFetch;

import io.github.glott.vATISFetch.Handlers.ATISHandler;
import io.github.glott.vATISFetch.Handlers.ConfigHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SuppressWarnings({"UnusedDeclaration"})
public class Display
{

    private static JFrame frame;
    private JPanel panel;

    private JComboBox configSelection;

    private JTextArea generalFetch;
    private JTextArea notamFetch;

    private JButton fetchButton;
    private JButton closeButton;
    private JButton importButton;
    private JLabel openConfigLabel;

    private SwingWorker worker;

    private ATISHandler atisHandler;
    private ConfigHandler configHandler;

    public Display()
    {
        atisHandler = new ATISHandler();
        configHandler = new ConfigHandler();

        closeButton.addActionListener(e ->
        {
            if (worker != null) worker.cancel(true);
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });

        configSelection.addActionListener(e ->
        {
            if (configSelection.getSelectedItem() != null)
                configHandler.parseConfig(configSelection.getSelectedItem().toString(), generalFetch);
        });

        fetchButton.addActionListener(e ->
        {
            worker = new SwingWorker<Void, Void>()
            {
                @Override
                protected Void doInBackground() throws Exception
                {
                    atisHandler.fetchATIS(configSelection.getSelectedItem().toString(), notamFetch);
                    String[] out = atisHandler.mergeATIS(configHandler.getConfig());
                    generalFetch.setText(out[0]);
                    notamFetch.setText(out[1]);
                    fetchButton.setText(out[2]);
                    return null;
                }

                @Override
                protected void done()
                {
                }
            };
            worker.execute();
        });

        importButton.addActionListener(e ->
        {
            worker = new SwingWorker<Void, Void>()
            {
                @Override
                protected Void doInBackground() throws Exception
                {
                    importConfigs();
                    return null;
                }

                @Override
                protected void done()
                {
                }
            };
            worker.execute();
        });

        openConfigLabel.setToolTipText(Main.VERSION);

    }

    public void run()
    {
        frame = new JFrame("vATISFetch");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        configHandler.initConfigSelection(configSelection);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void importConfigs()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home") + File.separator + "Downloads"));
        chooser.setFileFilter(new FileNameExtensionFilter("JSON File (*.json)", "json"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(true);
        chooser.showOpenDialog(frame);
        File[] files = chooser.getSelectedFiles();

        File configDirectory = new File(Main.FETCH_DIR);
        if (!configDirectory.exists()) configDirectory.mkdir();
        try
        {
            for (File f : files)
                Files.move(Paths.get(f.getPath()), Paths.get(configDirectory.getPath() + File.separator + f.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ignored)
        {
        }
        configHandler.initConfigSelection(configSelection);
    }

}
