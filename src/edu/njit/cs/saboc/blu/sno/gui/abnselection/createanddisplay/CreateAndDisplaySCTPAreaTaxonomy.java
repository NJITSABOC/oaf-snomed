package edu.njit.cs.saboc.blu.sno.gui.abnselection.createanddisplay;

import edu.njit.cs.saboc.blu.core.gui.dialogs.AbNCreateAndDisplayDialog;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.SCTInferredPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.SCTInheritableProperty;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNFrameManager;
import edu.njit.cs.saboc.blu.sno.localdatasource.concept.SCTConcept;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import java.util.Set;

/**
 *
 * @author Kevyn
 */
public class CreateAndDisplaySCTPAreaTaxonomy extends AbNCreateAndDisplayDialog<PAreaTaxonomy> {

    private final SCTConcept selectedRoot;
    
    private final SCTRelease release;
    
    private final Set<SCTInheritableProperty> availableProperties;
    private final Set<SCTInheritableProperty> selectedProperties;

    public CreateAndDisplaySCTPAreaTaxonomy(
            String text, 
            SCTConcept selectedRoot, 
            Set<SCTInheritableProperty> availableProperties,
            Set<SCTInheritableProperty> selectedProperties,
            SCTAbNFrameManager displayFrameListener, 
            SCTRelease release) {
        
        super(text, displayFrameListener);
        
        this.selectedRoot = selectedRoot;
        
        this.release = release;
        this.availableProperties = availableProperties;
        this.selectedProperties = selectedProperties;
    }

    @Override
    protected void displayAbN(PAreaTaxonomy taxonomy) {
        super.getDisplayFrameListener().displayPAreaTaxonomy(taxonomy);
    }

    @Override
    protected PAreaTaxonomy deriveAbN() {
        Hierarchy<SCTConcept> conceptHierarchy = release.getConceptHierarchy().getSubhierarchyRootedAt(selectedRoot);
        
        PAreaTaxonomyFactory factory = new SCTInferredPAreaTaxonomyFactory(release, conceptHierarchy);

        PAreaTaxonomyGenerator taxonomyGenerator = new PAreaTaxonomyGenerator();
        PAreaTaxonomy taxonomy = taxonomyGenerator.derivePAreaTaxonomy(factory, conceptHierarchy);
        
        if(availableProperties.equals(selectedProperties)) {
            return taxonomy;
        } else {
            return taxonomy.getRelationshipSubtaxonomy(selectedProperties);
        }
    }
}
