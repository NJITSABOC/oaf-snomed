package edu.njit.cs.saboc.blu.sno.gui.abnselection;

import edu.njit.cs.saboc.blu.sno.localdatasource.load.LoadLocalRelease;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.LocalLoadStateMonitor;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.RF1ReleaseLoader;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.RF2ReleaseLoader;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTReleaseInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Panel that allows a user to open a SNOMED CT release.
 * 
 * @author Chris O
 */
public class LoadReleasePanel extends JPanel {
    
    /**
     * Listener for handling events related to loading a SNOMED CT release
     */
    public interface LocalDataSourceListener {
        public void localDataSourceLoaded(SCTRelease dataSource);
        
        public void dataSourceLoading();

        public void localDataSourceUnloaded();
    }

    /**
     * A worker for monitoring the progress in loading a SNOMED CT release
     */
    private class LoadMonitorTask extends SwingWorker {

        private final LocalLoadStateMonitor stateMonitor;

        public LoadMonitorTask(LocalLoadStateMonitor stateMonitor) {
            this.stateMonitor = stateMonitor;
        }

        @Override
        public Void doInBackground() {

            setProgress(0);

            while (stateMonitor.getOverallProgress() < 100) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {

                }

                setProgress(stateMonitor.getOverallProgress());
            }

            return null;
        }
    }

    private JComboBox chooserVersionBox;
    private JComboBox recentlyOpenedVersionBox;
    private JComboBox localVersionBox = chooserVersionBox;

    private ArrayList<File> chooserReleases = new ArrayList<>();
    private ArrayList<File> recentlyOpenedReleases = new ArrayList<>();
    private ArrayList<File> availableReleases = chooserReleases;

    private final JButton chooserBtn;

    private SCTRelease loadedDataSource = null;

    private JToggleButton loadButton = new JToggleButton("Load");

    private JProgressBar loadProgressBar;

    private final ArrayList<LocalDataSourceListener> dataSourceLoadedListeners = new ArrayList<>();

    public LoadReleasePanel() {
        chooserVersionBox = new JComboBox();
        chooserVersionBox.setBackground(Color.WHITE);
        chooserVersionBox.addItem("Choose a directory");
        chooserVersionBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("hey");
                localVersionBox = chooserVersionBox;
                availableReleases = chooserReleases;
                loadButton.setText("Load from File Opener");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("adios");
            }
        });

        recentlyOpenedVersionBox = new JComboBox();
        recentlyOpenedVersionBox.setBackground(Color.WHITE);
        recentlyOpenedVersionBox.addItem("Recently Opened Release");
        //code that will populate JComboBox and releases list
        recentlyOpenedVersionBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("hi");
                localVersionBox = recentlyOpenedVersionBox;
                availableReleases = recentlyOpenedReleases;
                loadButton.setText("Load from Recently Opened");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("bye");
            }
        });

        chooserBtn = new JButton("Open Folder");

        chooserBtn.addActionListener((ae) -> {
            showReleaseFolderSelectionDialog();
        });

        loadButton.addActionListener( (ae) -> {
            
            if (loadButton.isSelected()) {
                
                if (availableReleases.isEmpty()) {
                    loadButton.setSelected(false);
                    return;
                }

                loadProgressBar.setValue(0);
                loadProgressBar.setString("Detecting Files...");
                loadProgressBar.setStringPainted(true);
                loadProgressBar.setVisible(true);
                
                loadButton.setEnabled(false);
                chooserVersionBox.setEnabled(false);
                recentlyOpenedVersionBox.setEnabled(false);
                chooserBtn.setEnabled(false);
                
                dataSourceLoadedListeners.forEach( (listener) -> {
                   listener.dataSourceLoading();
                });
                
                startLocalReleaseThread();
            } else {
                dataSourceUnloaded();
            }
        });

        loadProgressBar = new JProgressBar(0, 100);
        loadProgressBar.setVisible(false);

        JPanel localReleasePanel = new JPanel();

        localReleasePanel.add(chooserBtn);
        localReleasePanel.add(chooserVersionBox);
        localReleasePanel.add(new JLabel(" OR "));
        localReleasePanel.add(recentlyOpenedVersionBox);
        localReleasePanel.add(loadButton);
        localReleasePanel.add(loadProgressBar);

        this.setLayout(new BorderLayout());
        this.add(localReleasePanel);
    }

    public void addLocalDataSourceLoadedListener(LocalDataSourceListener listener) {
        dataSourceLoadedListeners.add(listener);
    }

    private void loadComplete() {
        loadButton.setText("Unload");
        loadButton.setEnabled(true);

        loadProgressBar.setVisible(false);

        dataSourceLoadedListeners.forEach((LocalDataSourceListener listener) -> {
            listener.localDataSourceLoaded(loadedDataSource);
        });
    }

    private void dataSourceUnloaded() {
        loadButton.setText("Load");
        localVersionBox.setEnabled(true);
        recentlyOpenedVersionBox.setEnabled(true);
        chooserBtn.setEnabled(true);

        this.loadedDataSource = null;

        dataSourceLoadedListeners.forEach( (listener) -> {
            listener.localDataSourceUnloaded();
        });
    }

    private void startLocalReleaseThread() {
        new Thread(() -> {
            try {
                File selectedFile = getSelectedVersion();
                
//                SCTReleaseInfo a = new SCTReleaseInfo(selectedFile, getSelectedVersionName());
                //code that will save "a" using JSON

                final LocalLoadStateMonitor loadMonitor;
                final SCTRelease dataSource;
                
                // Determine which release format the release is in.
                // Currently do this based on the name of the path
                // where the release is located.
                if (selectedFile.getAbsolutePath().contains("RF2Release") || 
                        selectedFile.getAbsolutePath().contains("RF2_Production")) {
                    
                    RF2ReleaseLoader rf2Importer = new RF2ReleaseLoader();
                    
                    loadMonitor = rf2Importer.getLoadStateMonitor();
                    
                    LoadMonitorTask task = new LoadMonitorTask(loadMonitor);
                    
                    task.addPropertyChangeListener( (pce) -> {
                        
                        loadProgressBar.setValue(loadMonitor.getOverallProgress());
                        loadProgressBar.setString(loadMonitor.getProcessName());
                    });
                    
                    task.execute();
                    
                    dataSource = rf2Importer.loadLocalSnomedRelease(selectedFile, 
                            new SCTReleaseInfo(selectedFile, getSelectedVersionName()), 
                            loadMonitor);
                    
                    
                } else {
                    RF1ReleaseLoader importer = new RF1ReleaseLoader();
                    
                    loadMonitor = importer.getLoadStateMonitor();
                    
                    LoadMonitorTask task = new LoadMonitorTask(loadMonitor);
                    
                    task.addPropertyChangeListener((pce) -> {
                        loadProgressBar.setValue(loadMonitor.getOverallProgress());
                        loadProgressBar.setString(loadMonitor.getProcessName());
                    });
                    
                    task.execute();
                    
                    dataSource = importer.loadLocalSnomedRelease(selectedFile, 
                            new SCTReleaseInfo(selectedFile, getSelectedVersionName()), 
                            loadMonitor);
                }
                
                loadedDataSource = dataSource;
                
                SwingUtilities.invokeLater(() -> {
                    loadComplete();
                });
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }).start();
    }

    /**
     * Opens a dialog that allows a user to select a folder that contains one 
     * or more SNOMED CT release folders.
     */
    private void showReleaseFolderSelectionDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = chooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                ArrayList<File> temp = LoadLocalRelease.findReleaseFolders(file);

                if(temp.isEmpty()){
                    JOptionPane.showMessageDialog(
                            null,
                            "SNOMED CT release was not found in the selected directory",
                            "SNOMED CT No Release Found",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    chooserVersionBox.removeAllItems();
                    chooserReleases = temp;
                    ArrayList<String> releaseNames = LoadLocalRelease.getReleaseFileNames(this.chooserReleases);

                    releaseNames.forEach((releaseName) -> {
                        chooserVersionBox.addItem(releaseName);
                    });
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "SNOMED CT release name cannot be properly parsed",
                        "SNOMED CT Release Name Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public File getSelectedVersion() {
        return availableReleases.get(localVersionBox.getSelectedIndex());
    }

    public String getSelectedVersionName() {
        return (String) localVersionBox.getItemAt(localVersionBox.getSelectedIndex());
    }

    public SCTRelease getLoadedDataSource() throws NoSCTDataSourceLoadedException {
        if (loadedDataSource == null) {
            throw new NoSCTDataSourceLoadedException();
        }

        return loadedDataSource;
    }
}
