
package edu.njit.cs.saboc.blu.sno.gui.gep.panels.disjointpareataxonomy.configuration;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;

/**
 *
 * @author Chris O
 */
public class SCTDisjointPAreaTaxonomyConfigurationFactory {
    public SCTDisjointPAreaTaxonomyConfiguration createConfiguration(
            DisjointAbstractionNetwork<PAreaTaxonomy, PArea> disjointTaxonomy, 
            SCTDisplayFrameListener displayListener) {
        
        SCTDisjointPAreaTaxonomyConfiguration disjointConfiguration = new SCTDisjointPAreaTaxonomyConfiguration(disjointTaxonomy);
        disjointConfiguration.setUIConfiguration(new SCTDisjointPAreaTaxonomyUIConfiguration(disjointConfiguration, displayListener));
        disjointConfiguration.setTextConfiguration(new SCTDisjointPAreaTaxonomyTextConfiguration(disjointTaxonomy));
        
        return disjointConfiguration;
    }
}
