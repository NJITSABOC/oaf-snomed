package edu.njit.cs.saboc.blu.sno.gui.openrelease;

import edu.njit.cs.saboc.blu.core.utils.toolstate.RecentlyOpenedFile;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNFrameManager;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.LoadLocalRelease;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.LocalLoadStateMonitor;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.RF1ReleaseLoader;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.RF2ReleaseLoader;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTReleaseInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

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

    private JComboBox<String> chooseReleaseBox;
    private ArrayList<File> chooserReleases;
    
    private final JButton btnDownloadRelease;
    
    private final JButton btnOpenRecentRelease;
    
    private final JButton btnOpenReleaseFolder;

    private Optional<SCTRelease> loadedDataSource = Optional.empty();

    private JToggleButton loadButton = new JToggleButton("Load");

    private JProgressBar loadProgressBar;

    private final ArrayList<LocalDataSourceListener> dataSourceLoadedListeners = new ArrayList<>();

    private final SCTAbNFrameManager frameManager;
    
    private final OAFStateFileManager stateFileManager;

    public LoadReleasePanel(
            SCTAbNFrameManager frameManager, 
            OAFStateFileManager stateFileManager) {
        
        this.frameManager = frameManager;
        this.stateFileManager = stateFileManager;
         
        loadButton.setEnabled(false);
        
        chooseReleaseBox = new JComboBox();
        chooseReleaseBox.setBackground(Color.WHITE);
        chooseReleaseBox.setEnabled(false);
        chooseReleaseBox.setPreferredSize(new Dimension(300, 24));
        
        chooserReleases = new ArrayList<>();
        
        btnDownloadRelease = new JButton("Download Release");
        btnDownloadRelease.addActionListener( (ae) -> {
            openBrowserDownloadRelease();
        });
        btnDownloadRelease.setToolTipText("Download Current Interational RF2 Release from NLM UTS");
        
        btnOpenRecentRelease = new JButton("Select Recently Opened Release");
        btnOpenRecentRelease.addActionListener( (ae) -> {
            displayRecentReleaseMenu();
        });
        
        btnOpenReleaseFolder = new JButton("Select Folder with SNOMED CT Release(s)");

        btnOpenReleaseFolder.addActionListener((ae) -> {
            showReleaseFolderSelectionDialog();
        });
        
        if (!releaseHistoryAvailable()) {
            btnOpenRecentRelease.setEnabled(false);
            btnOpenRecentRelease.setToolTipText("An error occured when opening "
                    + "the Recent SNOMED CT Releases configuration file...");
        }

        loadButton.addActionListener((ae) -> {
            
            if (loadButton.isSelected()) {
                loadProgressBar.setValue(0);
                loadProgressBar.setString("Detecting Files...");
                loadProgressBar.setStringPainted(true);
                loadProgressBar.setVisible(true);
                
                loadButton.setEnabled(false);
                
                chooseReleaseBox.setEnabled(false);
                btnOpenReleaseFolder.setEnabled(false);
                btnOpenRecentRelease.setEnabled(false);
                
                dataSourceLoadedListeners.forEach( (listener) -> {
                   listener.dataSourceLoading();
                });
                
                startLocalReleaseThread();
            } else {

                int answer = JOptionPane.showOptionDialog(
                        null,
                        "Are you sure you want to unload the current release?",
                        "Unload current release?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new Object[]{"Yes", "No"},
                        "Yes");
                
                if(answer == JOptionPane.YES_OPTION) {
                    dataSourceUnloaded();
                } else {
                    loadButton.setSelected(true);
                }
            }
        });

        loadProgressBar = new JProgressBar(0, 100);
        loadProgressBar.setVisible(false);
        loadProgressBar.setPreferredSize(new Dimension(250, 16));

        JPanel localReleasePanel = new JPanel();

        localReleasePanel.add(btnDownloadRelease);
        localReleasePanel.add(btnOpenRecentRelease);
        localReleasePanel.add(btnOpenReleaseFolder);
        localReleasePanel.add(chooseReleaseBox);
        localReleasePanel.add(loadButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.add(loadProgressBar);
        
        this.setLayout(new BorderLayout());
        
        this.add(localReleasePanel, BorderLayout.CENTER);
        this.add(statusPanel, BorderLayout.EAST);
    }

    public void addLocalDataSourceLoadedListener(LocalDataSourceListener listener) {
        dataSourceLoadedListeners.add(listener);
    }

    private void loadComplete() {
        loadButton.setText("Unload");
        loadButton.setEnabled(true);

        loadProgressBar.setVisible(false);

        dataSourceLoadedListeners.forEach((listener) -> {
            listener.localDataSourceLoaded(this.loadedDataSource.get());
        });
    }

    private void dataSourceUnloaded() {
        loadButton.setText("Load");
        
        chooseReleaseBox.setEnabled(true);
                
        btnOpenRecentRelease.setEnabled(this.stateFileManager.isInitialized());
        
        btnOpenReleaseFolder.setEnabled(true);

        loadedDataSource = Optional.empty();

        dataSourceLoadedListeners.forEach( (listener) -> {
            listener.localDataSourceUnloaded();
        });
        
        if(frameManager != null) {
            frameManager.getMainFrame().closeFrames(frameManager.getMainFrame().getContentFrames());
        }
    }
    
    private boolean releaseHistoryAvailable() {
        return stateFileManager.isInitialized();
    }
    
    private void displayRecentReleaseMenu() {
        
        if(!releaseHistoryAvailable()) {
            return;
        }
        
        ArrayList<RecentlyOpenedFile> recentReleases;
        
        try {
            recentReleases = stateFileManager.getRecentlyOpenedOntologiesManager().getRecentlyOpenedFiles(5);
        } catch(RecentlyOpenedFileException rofe) {
            return;
        }
        
        JPopupMenu recentReleaseMenu = new JPopupMenu();
        
        if(recentReleases.isEmpty()) {
            JMenuItem emptyHistoryItem = new JMenuItem(
                    String.format("<html><font size = '4' color = 'red'><b>%s</b></font>",
                               "No recently opened releases found"));
            
            recentReleaseMenu.add(emptyHistoryItem);
            
        } else {
            recentReleases.forEach((release) -> {
                ArrayList<File> releaseFile = new ArrayList<>(Collections.singletonList(release.getFile()));
                ArrayList<String> releaseNames = LoadLocalRelease.getReleaseFileNames(releaseFile);

                String releaseName = releaseNames.get(0);

                SimpleDateFormat dateFormatter = new SimpleDateFormat();
                String lastOpenedStr = dateFormatter.format(release.getDate());

                JMenuItem item = new JMenuItem(
                        String.format("<html><font size = '4' color = 'blue'><b>%s</b></font> (Last opened: %s)",
                                releaseName,
                                lastOpenedStr));

                item.addActionListener((ae) -> {
                    setReleaseList(new ArrayList<>(Collections.singleton(release.getFile())));
                });

                recentReleaseMenu.add(item);
            });

            recentReleaseMenu.add(new JSeparator());

            JMenuItem clearHistoryItem = new JMenuItem(""
                    + "<html><font size = '4' color = 'red'>"
                    + "Clear recently opened release history</font>");
            
            clearHistoryItem.addActionListener((ae) -> {

                int answer = JOptionPane.showOptionDialog(
                        null,
                        "The history of recently opened SNOMED CT releases will be deleted."
                        + "\nAre you sure you want to clear the history?",
                        "Recently Opened Releases: Clear History?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new Object[]{"Yes", "No"},
                        "No");
                
                if(answer == JOptionPane.YES_OPTION) {
                    try {
                        stateFileManager.getRecentlyOpenedOntologiesManager().eraseHistory();
                    } catch (RecentlyOpenedFileException rofe) {
                        
                    }
                }
            });

            recentReleaseMenu.add(clearHistoryItem);
        }
        
        recentReleaseMenu.show(this,
                btnOpenRecentRelease.getX() + 20,
                btnOpenRecentRelease.getY() + 20);
    }

    private void startLocalReleaseThread() {
        new Thread(() -> {
            
            try {
                File selectedFile = getSelectedVersion();

                final LocalLoadStateMonitor loadMonitor;
                final SCTRelease dataSource;
                
                // Determine which release format the release is in.
                // Currently do this based on the name of the path
                // where the release is located.
                if (selectedFile.getAbsolutePath().contains("RF2Release") || 
                        selectedFile.getAbsolutePath().contains("RF2_Production")) {
                    
                    RF2ReleaseLoader rf2Importer = new RF2ReleaseLoader(stateFileManager);
                    
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
                    RF1ReleaseLoader importer = new RF1ReleaseLoader(stateFileManager);
                    
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
                
                loadedDataSource = Optional.of(dataSource);
                
                try {
                    
                    if(stateFileManager.isInitialized()) {
                        stateFileManager.getRecentlyOpenedOntologiesManager().
                                addOrUpdateRecentlyOpenedFile(selectedFile);
                    }
                } catch (RecentlyOpenedFileException rofe) {
                    rofe.printStackTrace();
                }
                
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
        
        chooseReleaseBox.setEnabled(false);
        loadButton.setEnabled(false);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = chooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                ArrayList<File> releaseFolders = LoadLocalRelease.findReleaseFolders(file);

                if(releaseFolders.isEmpty()){
                    JOptionPane.showMessageDialog(
                            null,
                            "SNOMED CT release was not found in the selected directory",
                            "SNOMED CT No Release Found",
                            JOptionPane.WARNING_MESSAGE);
                }
                
                setReleaseList(releaseFolders);
                
            } catch (Exception e) {
                e.printStackTrace();
                
                JOptionPane.showMessageDialog(
                        null,
                        "SNOMED CT release name cannot be properly parsed",
                        "SNOMED CT Release Name Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            setReleaseList(chooserReleases);
        }
    }
    
    private void setReleaseList(ArrayList<File> releaseFolders) {
        chooseReleaseBox.removeAllItems();
        chooserReleases = releaseFolders;
        
        if(releaseFolders.isEmpty()) {
            return;
        }

        ArrayList<String> releaseNames = LoadLocalRelease.getReleaseFileNames(chooserReleases);

        releaseNames.forEach( (releaseName) -> {
            chooseReleaseBox.addItem(releaseName);
        });

        chooseReleaseBox.setEnabled(true);
        loadButton.setEnabled(true);
    }

    public File getSelectedVersion() {
        return chooserReleases.get(chooseReleaseBox.getSelectedIndex());
    }

    public String getSelectedVersionName() {
        return (String) chooseReleaseBox.getItemAt(chooseReleaseBox.getSelectedIndex());
    }

    public SCTRelease getLoadedDataSource() throws NoSCTDataSourceLoadedException {
        if (!loadedDataSource.isPresent()) {
            throw new NoSCTDataSourceLoadedException();
        }

        return loadedDataSource.get();
    }
    
    private void openBrowserDownloadRelease() {

        String url = "https://www.nlm.nih.gov/healthit/snomedct/international.html";

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));

            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }

    }
}
