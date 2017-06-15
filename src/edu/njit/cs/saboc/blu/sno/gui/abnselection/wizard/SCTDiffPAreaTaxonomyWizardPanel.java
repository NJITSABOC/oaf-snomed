package edu.njit.cs.saboc.blu.sno.gui.abnselection.wizard;

import edu.njit.cs.saboc.blu.core.gui.panels.abnderivationwizard.AbNDerivationWizardPanel;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.openrelease.LoadReleasePanel;
import edu.njit.cs.saboc.blu.sno.gui.openrelease.LoadReleasePanel.LocalDataSourceListener;
import edu.njit.cs.saboc.blu.sno.gui.openrelease.NoSCTDataSourceLoadedException;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNFrameManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.createanddisplay.CreateAndDisplaySCTDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTReleaseInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

/**
 * Wizard panel for creating SNOMED CT Diff partial-area Taxonomies
 * and Descriptive Delta Diff Partial-area Taxonomies
 * 
 * @author Chris O
 */
public class SCTDiffPAreaTaxonomyWizardPanel extends AbNDerivationWizardPanel {

    private final LoadReleasePanel loadFromReleasePanel;
    
    private final SCTPAreaTaxonomyWizardPanel taxonomyDerivationPanel;
    
    private final SCTAbNFrameManager displayFrameListener;
    
    private Optional<SCTRelease> optRelease = Optional.empty();
    
    private final JCheckBox chkDeriveDescriptiveDelta;
    
    public SCTDiffPAreaTaxonomyWizardPanel(
            OAFStateFileManager stateFileManager,
            SCTAbNFrameManager displayFrameListener) {
        
        this.displayFrameListener = displayFrameListener;
        
        this.loadFromReleasePanel = new LoadReleasePanel(displayFrameListener, stateFileManager);
        this.loadFromReleasePanel.addLocalDataSourceLoadedListener(new LocalDataSourceListener() {

            @Override
            public void localDataSourceLoaded(SCTRelease dataSource) {
                taxonomyDerivationPanel.setEnabled(true);
                
                chkDeriveDescriptiveDelta.setEnabled(canDeriveDescriptiveDelta(dataSource, optRelease.get()));
            }

            @Override
            public void dataSourceLoading() {
                taxonomyDerivationPanel.setEnabled(false);
            }

            @Override
            public void localDataSourceUnloaded() {
                taxonomyDerivationPanel.setEnabled(false);
            }
        });
        this.loadFromReleasePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Select FROM Release"));
        
        this.chkDeriveDescriptiveDelta = new JCheckBox("Include Descriptive Delta");
        
        this.taxonomyDerivationPanel = new SCTPAreaTaxonomyWizardPanel(
                (toRelease, root, availableProperties, selectedProperties, useStatedRelationships) -> {
                    
                    try {
                        SCTRelease fromRelease = loadFromReleasePanel.getLoadedDataSource();
                        
                        CreateAndDisplaySCTDiffPAreaTaxonomy createAndDisplay = new CreateAndDisplaySCTDiffPAreaTaxonomy(
                            "Creating Diff Partial-area Taxonomy",
                            root,
                            availableProperties,
                            selectedProperties, 
                            displayFrameListener,
                            fromRelease,
                            toRelease, 
                            useStatedRelationships,
                            chkDeriveDescriptiveDelta.isEnabled() && chkDeriveDescriptiveDelta.isSelected());

                        createAndDisplay.run();
                        
                    } catch (NoSCTDataSourceLoadedException e) {
                        
                    }
                },
                displayFrameListener);
        
        this.taxonomyDerivationPanel.addOptionComponent(chkDeriveDescriptiveDelta);
        
        this.taxonomyDerivationPanel.setDerivationButtonText("Derive Diff Partial-area Taxonomy");

        this.taxonomyDerivationPanel.setEnabled(false);

        this.setLayout(new BorderLayout());

        this.add(loadFromReleasePanel, BorderLayout.NORTH);
        
        this.add(taxonomyDerivationPanel, BorderLayout.CENTER);
    }
    
    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        
        
    }

    public void initialize(SCTRelease release) {
        super.initialize(release);
        
        optRelease = Optional.of(release);
        
        this.taxonomyDerivationPanel.initialize(release);
    }

    @Override
    public void resetView() {
        this.taxonomyDerivationPanel.resetView();
        
        this.optRelease = Optional.empty();
        
        this.taxonomyDerivationPanel.setEnabled(false);
        this.loadFromReleasePanel.setEnabled(false);
        
        this.chkDeriveDescriptiveDelta.setEnabled(false);
    }
    
    private boolean canDeriveDescriptiveDelta(SCTRelease fromRelease, SCTRelease toRelease) {
        
        if (toRelease.getReleaseInfo().getReleaseFormat() == SCTReleaseInfo.ReleaseFormat.RF2 && 
                fromRelease.getReleaseInfo().getReleaseType() == SCTReleaseInfo.ReleaseType.International && 
                toRelease.getReleaseInfo().getReleaseType() == SCTReleaseInfo.ReleaseType.International) {
            if (fromRelease.getReleaseInfo().getReleaseYear() == toRelease.getReleaseInfo().getReleaseYear()) {
                return fromRelease.getReleaseInfo().getReleaseMonth() == 1 && toRelease.getReleaseInfo().getReleaseMonth() == 7;
            } else {
                if (fromRelease.getReleaseInfo().getReleaseYear() == toRelease.getReleaseInfo().getReleaseYear() - 1) {
                    return fromRelease.getReleaseInfo().getReleaseMonth() == 7 && toRelease.getReleaseInfo().getReleaseMonth() == 1;
                }
            }
        }

        return false;
    }
}