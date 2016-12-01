package edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.diffpareataxonomy;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.AreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.explain.InheritablePropertyChanges;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.sno.descriptivedelta.DescriptiveDelta;

/**
 *
 * @author Chris O
 */
public class SCTDescriptiveDiffAreaTaxonomy extends DiffAreaTaxonomy {
    
    private final DescriptiveDelta descriptiveDelta;
    
    public SCTDescriptiveDiffAreaTaxonomy(
            SCTDescriptiveDiffPAreaTaxonomyFactory diffFactory,
            InheritablePropertyChanges ontDifferences,
            DescriptiveDelta descriptiveDelta, 
            AreaTaxonomy fromAreaTaxonomy,
            AreaTaxonomy toAreaTaxonomy,
            Hierarchy<DiffArea> areaHierarchy) {
        
        super(diffFactory, ontDifferences, fromAreaTaxonomy, toAreaTaxonomy, areaHierarchy);
        
        this.descriptiveDelta = descriptiveDelta;
    }
    
    public DescriptiveDelta getDescriptiveDelta() {
        return descriptiveDelta;
    }
    
    @Override
    public SCTDescriptiveDiffPAreaTaxonomyFactory getDiffFactory() {
        return (SCTDescriptiveDiffPAreaTaxonomyFactory)super.getDiffFactory();
    }
}
