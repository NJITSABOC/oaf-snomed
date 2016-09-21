package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.diff;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.diff.DiffPAreaPanel;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.diff.configuration.SCTDiffPAreaTaxonomyConfiguration;

/**
 *
 * @author Chris O
 */
public class SCTDescriptiveDiffPAreaPanel extends DiffPAreaPanel {
    
    private final SCTDiffPAreaDeltaConceptPanel descriptiveDeltaConceptPanel;
    
    public SCTDescriptiveDiffPAreaPanel(SCTDiffPAreaTaxonomyConfiguration configuration) {
        super(configuration);
        
        this.descriptiveDeltaConceptPanel = new SCTDiffPAreaDeltaConceptPanel(configuration);

        super.addInformationTab(descriptiveDeltaConceptPanel, "Descriptive Delta Details");
    }
}
