package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.parea.aggregate;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.AbstractNodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.AbstractNodePanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutNodeDetailsButton;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTAggregatePArea;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPArea;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.SCTPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.SCTCreateExpandedSubtaxonomyButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.SCTCreateTANFromAggregatePAreaButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.SCTExportAggregatePAreaButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.SCTOpenBrowserButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.SCTCreateRootSubtaxonomyButton;
import java.util.Optional;

/**
 *
 * @author Chris O
 */
public class SCTAggregatePAreaOptionsPanel extends AbstractNodeOptionsPanel<SCTAggregatePArea> {
    
    private Optional<SCTPArea> selectedPArea = Optional.empty();
    
    private final SCTPAreaTaxonomyConfiguration config;
    
    private final SCTOpenBrowserButton btnNAT;
    
    private final SCTCreateExpandedSubtaxonomyButton expandedSubtaxonomyBtn;
    
    private final SCTCreateTANFromAggregatePAreaButton tanBtn;
    
    private final SCTCreateRootSubtaxonomyButton rootSubtaxonomyBtn;
    
    private final PopoutNodeDetailsButton popoutBtn;
    
    private final SCTExportAggregatePAreaButton exportBtn;
    
    public SCTAggregatePAreaOptionsPanel(SCTPAreaTaxonomyConfiguration config) {
        this.config = config;

        btnNAT = new SCTOpenBrowserButton(config.getPAreaTaxonomy().getDataSource(),
                "aggregate partial-area", config.getDisplayListener());

        super.addOptionButton(btnNAT);
        
        expandedSubtaxonomyBtn = new SCTCreateExpandedSubtaxonomyButton(config);
        
        super.addOptionButton(expandedSubtaxonomyBtn);
        
        tanBtn = new SCTCreateTANFromAggregatePAreaButton(config);
        
        super.addOptionButton(tanBtn);
        
        rootSubtaxonomyBtn = new SCTCreateRootSubtaxonomyButton(config);
        
        super.addOptionButton(rootSubtaxonomyBtn);
        
        popoutBtn = new PopoutNodeDetailsButton("aggregate partial-area") {

            @Override
            public AbstractNodePanel getCurrentDetailsPanel() {
                AbstractNodePanel anp = config.getGEPConfiguration().createGroupDetailsPanel();
                anp.setContents(selectedPArea.get());
                
                return anp;
            }
        };
        
        super.addOptionButton(popoutBtn);
        
        exportBtn = new SCTExportAggregatePAreaButton(config);
        
        super.addOptionButton(exportBtn);
    }
    
    @Override
    public void enableOptionsForGroup(SCTAggregatePArea parea) {
        if(parea.getAggregatedGroups().isEmpty()) {
            expandedSubtaxonomyBtn.setEnabled(false);
        } else {
            expandedSubtaxonomyBtn.setEnabled(true);
        }
        
        if(config.getPAreaTaxonomy().getDescendantGroups(parea).isEmpty()) {
            rootSubtaxonomyBtn.setEnabled(false);
        } else { 
            rootSubtaxonomyBtn.setEnabled(true);
        }
        
        if(parea.getConceptCount() > 1) {
            if(config.getAggregatedPAreaHierarchy(parea).getChildren(parea.getRoot()).size() > 1) {
                tanBtn.setEnabled(true);
            } else {
                tanBtn.setEnabled(false);
            }
        } else {
            tanBtn.setEnabled(false);
        }
    }

    @Override
    public void setContents(SCTAggregatePArea parea) {
        selectedPArea = Optional.of(parea);
        
        btnNAT.setCurrentRootConcept(parea.getRoot());
        
        exportBtn.setCurrentPArea(parea);
        
        expandedSubtaxonomyBtn.setCurrentPArea(parea);
        
        tanBtn.setCurrentPArea(parea);
        
        rootSubtaxonomyBtn.setCurrentPArea(parea);
        
        this.enableOptionsForGroup(parea);
    }
    
    public void clearContents() {
        selectedPArea = Optional.empty();
        
        btnNAT.setCurrentRootConcept(null);
        
        exportBtn.setCurrentPArea(null);
        
        expandedSubtaxonomyBtn.setCurrentPArea(null);
        
        tanBtn.setCurrentPArea(null);
        
        rootSubtaxonomyBtn.setCurrentPArea(null);
        
        this.enableOptionsForGroup(null);
    }
}