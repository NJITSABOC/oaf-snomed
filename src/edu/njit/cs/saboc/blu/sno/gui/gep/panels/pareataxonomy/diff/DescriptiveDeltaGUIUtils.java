
package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.diff;

import edu.njit.cs.saboc.blu.core.gui.iconmanager.IconManager;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.DescriptiveDelta;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.EditingOperationReport.EditingOperationType;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.derivation.editingoperations.FuzzyParentChange;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.derivation.editingoperations.RelationshipGroupChange;
import edu.njit.cs.saboc.blu.sno.localdatasource.concept.SCTConcept;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 *
 * @author Chris O
 */
public class DescriptiveDeltaGUIUtils {
    public static final ImageIcon getIconForEditingOperation(EditingOperationType type) {
        
        String iconName;
        
        switch (type) {
        
            case AddedParent:
                iconName = "dd_isa_added.png";
                break;
                
            case RemovedParent:
                iconName = "dd_isa_removed.png";
                break;
                
            case ChangedParent:
                iconName = "dd_isa_changed.png";
                break;
                
            case ParentLessRefined:
                iconName = "dd_isa_lessrefined.png";
                break;
                
            case ParentMoreRefined:
                iconName = "dd_isa_morerefined.png";
                break;
                
            case AddedAttributeRelationship:
                iconName = "dd_rel_added.png";
                break;
                
            case RemovedAttributeRelationship:
                iconName = "dd_rel_removed.png";
                break;
                
            case ChangedAttributeRelationship:
                iconName = "dd_rel_changed.png";
                break;
                
            case AttributeRelationshipMoreRefined:
                iconName = "dd_rel_morerefined.png";
                break;
                
            case AttributeRelationshipLessRefined:
                iconName = "dd_rel_lessRefined.png";
                break;
                
            case RelationshipGroupChanged:
                iconName = "dd_group_change.png";
                break;
                
            default:
                return null;
        }
        
        return IconManager.getIconManager().getIcon(iconName);
    }
    
    private static final Color ADDED = new Color(240, 255, 240);
    private static final Color REMOVED = new Color(255, 240, 240);
    private static final Color MODIFIED = new Color(255, 250, 205); // "Lemon Chiffon"
    
    public static Color getOperationColor(EditingOperationType type) {
        switch (type) {
        
            case AddedParent:
            case AddedAttributeRelationship:
                return ADDED;
             
            case RemovedParent:
            case RemovedAttributeRelationship:
                return REMOVED;
                
            case ChangedParent:
            case ParentLessRefined:
            case ParentMoreRefined:
            case ChangedAttributeRelationship:
            case AttributeRelationshipMoreRefined:
            case AttributeRelationshipLessRefined:
            case RelationshipGroupChanged:
                return MODIFIED;
        }
        
        return Color.WHITE;
    }
    
    public static String getParentAddedText(DescriptiveDelta delta, SCTConcept concept) {
        String report = String.format("<b>%s</b> was added as a parent. ", concept.getName());
        
        if(delta.getConceptsAddedToSubhierarchy().contains(concept)) {
            report += "New parent was added to the subhierarchy.";
        } else if(delta.getActivatedConcepts().contains(concept)) {
            report += "New parent is a new concept.";
        }
        
        return report;
    }
    
    public static String getParentRemovedText(DescriptiveDelta delta, SCTConcept concept) {
        String report = String.format("<b>%s</b> was removed as a parent. ", concept.getName());
        
        if(delta.getConceptsRemovedFromSubhierarchy().contains(concept)) {
            report += "Removed parent was removed from the subhierarchy.";
        } else if(delta.getRetiredConcepts().contains(concept)) {
            report += "Removed parent was retired.";
        }
        
        return report;
    }
    
    public static String getParentChangedText(DescriptiveDelta delta, FuzzyParentChange parentChange) {
        
        String oldParentState = "";
        
        if(delta.getRetiredConcepts().contains(parentChange.getOriginalParent())) {
            oldParentState = " (a retired concept)";
        } else if(delta.getConceptsRemovedFromSubhierarchy().contains(parentChange.getOriginalParent())) {
            oldParentState = " (a concept removed from the subhierarchy)";
        }

        if (parentChange.getPotentialNewParents().size() == 1) {

            SCTConcept newParent = parentChange.getPotentialNewParents().iterator().next();
            
            String desc = String.format("Parent <b>%s</b>%s was changed to <b>%s</b>",
                    parentChange.getOriginalParent().getName(), 
                    oldParentState,
                    newParent.getName());
            
            if(delta.getActivatedConcepts().contains(newParent)) {
                return " (a newly activated concept).";
            } else if(delta.getConceptsAddedToSubhierarchy().contains(newParent)) {
                return " (a concept added to the subhierarchy)";
            }
            
            return desc + ".";
            
        } else {
            ArrayList<String> possibleParentChanges = new ArrayList<>();

            parentChange.getPotentialNewParents().forEach((parent) -> {

            });
        }
        
        return "[EDITING OPERATION TEXT NOT SET]";
    }
    
    public static String getGroupChangeText(RelationshipGroupChange groupChange) {
        String originalGroupDescription = "";
        
        if(groupChange.getOriginalRelGroup() == 0) {
            originalGroupDescription = "(ungrouped) ";
        }
        
        String relName = String.format("A <b>%s</b> attribute relationship with a target of <b>%s</b> moved from Group %d %sto",
                groupChange.getRelType().getName(),
                groupChange.getTarget().getName(),
                groupChange.getOriginalRelGroup(), 
                originalGroupDescription);
        
        if(groupChange.getPotentialNewRelGroups().size() == 1) {
            return String.format("%s Group %d.", relName, groupChange.getPotentialNewRelGroups().iterator().next());
        } else {
            ArrayList<Integer> potentialGroups = new ArrayList<>(groupChange.getPotentialNewRelGroups());
            
            String potentialGroupStr = potentialGroups.toString();
            potentialGroupStr = potentialGroupStr.substring(1, potentialGroupStr.length() - 1);
            
            return String.format("%s one of the following Groups: %s.", relName, potentialGroupStr);
        }
    }
}
