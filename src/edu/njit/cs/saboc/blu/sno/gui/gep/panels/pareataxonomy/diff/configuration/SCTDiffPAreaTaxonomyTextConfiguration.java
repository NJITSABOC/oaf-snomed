package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.diff.configuration;

import edu.njit.cs.saboc.blu.core.abn.diff.change.ChangeState;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Area;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.diff.configuration.DiffPAreaTaxonomyTextConfiguration;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.diffpareataxonomy.SCTDescriptiveDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.DescriptiveDelta;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.EditingOperationReport;
import edu.njit.cs.saboc.blu.sno.localdatasource.concept.SCTConcept;
import edu.njit.cs.saboc.blu.sno.localdatasource.concept.SCTEntityNameConfiguration;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Chris O
 */
public class SCTDiffPAreaTaxonomyTextConfiguration extends DiffPAreaTaxonomyTextConfiguration {

    public SCTDiffPAreaTaxonomyTextConfiguration(DiffPAreaTaxonomy taxonomy) {
        super(new SCTEntityNameConfiguration(), taxonomy);
    }

    @Override
    public String getNodeHelpDescription(PArea parea) {
        return "[SCT DIFF PAREA HELP DESCRIPTION]";
    }
    
    @Override
    public String getAbNSummary() {
        
        String baseSummary = super.getAbNSummary();
        
        if(super.getPAreaTaxonomy() instanceof SCTDescriptiveDiffPAreaTaxonomy) {
            SCTDescriptiveDiffPAreaTaxonomy diffTaxonomy = (SCTDescriptiveDiffPAreaTaxonomy)super.getPAreaTaxonomy();
            
            DescriptiveDelta delta = diffTaxonomy.getDescriptiveDelta();
            
            
            int retiredConcepts = delta.getRetiredConcepts().size();
            int activatedConcepts = delta.getActivatedConcepts().size();
            int removedFromSubhierarchyConcepts = delta.getConceptsRemovedFromSubhierarchy().size();
            int addedToSubhierchyConcepts = delta.getConceptsAddedToSubhierarchy().size();
            
            int editingOperations = 0;
            int editedConcepts = 0;
            
            int inferredChanges = 0;
            int conceptsWithInferredChange = 0;
            
            for(Entry<SCTConcept, EditingOperationReport> entry : delta.getStatedEditingOperations().entrySet()) {
                editedConcepts++;
                editingOperations += entry.getValue().getNumberOfOperationsApplied();
            }
            
            for (Entry<SCTConcept, EditingOperationReport> entry : delta.getInferredChanges().entrySet()) {
                conceptsWithInferredChange++;
                inferredChanges += entry.getValue().getNumberOfOperationsApplied();
            }
            
            StringBuilder metricsStr = new StringBuilder();
            
            metricsStr.append("<b>Descriptive delta metrics:</b><p>");
            metricsStr.append(String.format("# Edited Concepts: %d<br>", editedConcepts));
            metricsStr.append(String.format("# Editing Operations: %d<br>", editingOperations));
            metricsStr.append(String.format("# Stated Delta Entries: %d<br>", delta.getStatedDeltaEntryCount()));
            metricsStr.append("<p>");
            
            metricsStr.append(String.format("# Changed Concepts: %d<br>", conceptsWithInferredChange));
            metricsStr.append(String.format("# Inferred Changes: %d<br>", inferredChanges));
            metricsStr.append(String.format("# Inferred Delta Entries: %d<br>", delta.getInferredDeltaEntryCount()));
            metricsStr.append("<p>");
            
            metricsStr.append(String.format("# Concepts Retired: %d<br>", retiredConcepts));
            metricsStr.append(String.format("# Concepts Activated: %d<br>", activatedConcepts));
            metricsStr.append(String.format("# Active concepts removed from subhierarchy: %d<br>", removedFromSubhierarchyConcepts));
            metricsStr.append(String.format("# Active concepts added to subhierarchy: %d<br>", addedToSubhierchyConcepts));
            metricsStr.append("<p>");
            metricsStr.append("<p>");
            
            metricsStr.append("<b>Visual semantic delta metrics:</b><p>");
            
            Set<SCTConcept> editedConceptsInUnmodifiedPArea = new HashSet<>();
            Set<SCTConcept> changedConceptsInUnmodifiedPArea = new HashSet<>();

            for(DiffPArea diffPArea : diffTaxonomy.getPAreas()) {
                if(diffPArea.getPAreaState() == ChangeState.Unmodified) {
                    diffPArea.getConcepts().forEach( (concept) -> {
                        SCTConcept sctConcept = (SCTConcept)concept;
                        
                        if(delta.getStatedEditingOperations().containsKey(sctConcept)) {
                            editedConceptsInUnmodifiedPArea.add(sctConcept);
                        }
                        
                        if(delta.getInferredChanges().containsKey(sctConcept)) {
                            changedConceptsInUnmodifiedPArea.add(sctConcept);
                        }
                    });
                }
            }
            
            metricsStr.append(String.format("# Edited concepts in unmodified partial-area: %d<br>", editedConceptsInUnmodifiedPArea.size()));
            metricsStr.append(String.format("# Changed concepts in unmodified partial-area: %d<br>", changedConceptsInUnmodifiedPArea.size()));
            
            return String.format("%s<p>%s", baseSummary, metricsStr.toString());
        } else {
            return baseSummary;
        }
    }
    
    @Override
    public String getAbNHelpDescription() {
        return "[SCT DIFF PAREA TAXONOMY HELP INFORMATION]";
    }
}
