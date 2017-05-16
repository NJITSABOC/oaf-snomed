package edu.njit.cs.saboc.blu.sno.gui.abnselection;

import edu.njit.cs.saboc.blu.core.utils.recentlyopenedfile.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import edu.njit.cs.saboc.blu.core.utils.recentlyopenedfile.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.openrelease.LoadReleasePanel;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * The main panel of the SNOMED CT module of the OAF
 * 
 * @author Chris O
 */
public class SCTMainPanel extends JPanel {
    
    private final LoadReleasePanel loadReleasePanel;
    
    private final SCTAbNCreationPanel abnCreationPanel;
    
    private OAFStateFileManager stateFileManager;
    
    public SCTMainPanel(SCTAbNFrameManager frameManager) {
        
        super(new BorderLayout());
        
        try {
            this.stateFileManager = new OAFStateFileManager("BLUSNO");
        } catch(RecentlyOpenedFileException rofe) {
            this.stateFileManager = null;
        }
        
        loadReleasePanel = new LoadReleasePanel(frameManager, stateFileManager);
        
        loadReleasePanel.addLocalDataSourceLoadedListener(new LoadReleasePanel.LocalDataSourceListener() {
            
            @Override
            public void localDataSourceLoaded(SCTRelease dataSource) {
                setEnabled(true);

                abnCreationPanel.setCurrentRelease(dataSource);
                
                abnCreationPanel.setEnabled(true);
            }

            @Override
            public void localDataSourceUnloaded() {
                setEnabled(false);
                
                abnCreationPanel.setEnabled(false);
                abnCreationPanel.clear();
            }

            @Override
            public void dataSourceLoading() {
                
            }
        });

        this.add(loadReleasePanel, BorderLayout.NORTH);
        
        abnCreationPanel = new SCTAbNCreationPanel(frameManager, stateFileManager);
        abnCreationPanel.setEnabled(false);
        
        this.add(abnCreationPanel, BorderLayout.CENTER);
    }
}
