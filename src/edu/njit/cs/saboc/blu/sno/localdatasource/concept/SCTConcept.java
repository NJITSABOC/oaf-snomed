package edu.njit.cs.saboc.blu.sno.localdatasource.concept;

import edu.njit.cs.saboc.blu.core.ontology.Concept;
import java.util.HashSet;
import java.util.Set;

/**
 * A SNOMED CT concept with all of its attributes
 * @author Chris
 */
public class SCTConcept extends Concept<Long> {
    
    private static final String UNSET_FSN = "FSN_NOT_SET";

    private final Set<Description> descList = new HashSet<>();
    private final Set<AttributeRelationship> lateralRelationships = new HashSet<>();
    
    private final boolean isActive;
    private final boolean isPrimitive;
    
    private String fullySpecifiedName = SCTConcept.UNSET_FSN;

    public SCTConcept(long id, boolean isPrimitive, boolean isActive) {
        super(id);
        
        this.isPrimitive = isPrimitive;
        this.isActive = isActive;
    }
    
    public boolean isPrimitive() {
        return isPrimitive;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setLateralRelationships(Set<AttributeRelationship> rels) {
        this.lateralRelationships.clear(); 
        this.lateralRelationships.addAll(rels);
    }

    public void setDescriptions(Set<Description> descriptions) {
        this.descList.clear();
        this.descList.addAll(descriptions);
        
        for(Description d : descList) {
            if(d.getDescriptionType() == 3) {
                this.fullySpecifiedName = d.getTerm();
                break;
            }
        }
    }

    public Set<AttributeRelationship> getAttributeRelationships() {
        return lateralRelationships;
    }
    
    public Set<Description> getDescriptions() {
        return descList;
    }
    
    @Override
    public String getName() {
        return fullySpecifiedName;
    }

    @Override
    public String getIDAsString() {
        return getID().toString();
    }
}
