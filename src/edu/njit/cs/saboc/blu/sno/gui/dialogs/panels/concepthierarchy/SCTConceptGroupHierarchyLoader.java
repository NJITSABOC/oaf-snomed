package edu.njit.cs.saboc.blu.sno.gui.dialogs.panels.concepthierarchy;

import SnomedShared.Concept;
import SnomedShared.generic.GenericConceptGroup;
import edu.njit.cs.saboc.blu.core.gui.dialogs.concepthierarchy.ConceptEntry;
import edu.njit.cs.saboc.blu.core.gui.dialogs.concepthierarchy.NodeConceptHierarchicalViewPanel;
import edu.njit.cs.saboc.blu.core.gui.dialogs.concepthierarchy.NodeConceptHierarchyLoader;
import edu.njit.cs.saboc.blu.sno.datastructure.hierarchy.SCTConceptHierarchy;
import edu.njit.cs.saboc.blu.sno.utils.comparators.ConceptNameComparator;
import java.util.Comparator;

/**
 *
 * @author Chris
 */
public abstract class SCTConceptGroupHierarchyLoader<T extends GenericConceptGroup> extends NodeConceptHierarchyLoader<Concept, SCTConceptHierarchy, T> {
    
    public SCTConceptGroupHierarchyLoader(T group, NodeConceptHierarchicalViewPanel<Concept> panel) {
        super(group, panel);
    }
    
    public Concept getGroupRoot(T group) {
        return group.getRoot();
    }
    
    public Comparator<Concept> getComparator() {
        return new ConceptNameComparator();
    }
    
    public ConceptEntry<Concept> createConceptEntry(Concept c) {
        return new SCTConceptEntry(c);
    }    
}
