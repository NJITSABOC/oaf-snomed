package edu.njit.cs.saboc.blu.sno.gui.gep.panels;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeList;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTAggregatePArea;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.parea.aggregate.SCTAggregatedPAreaTableModel;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author Chris O
 */
public class SCTAggregatePAreaList extends NodeList<SCTAggregatePArea> {

    public SCTAggregatePAreaList(SCTPAreaTaxonomyConfiguration config) {
        super(new SCTAggregatedPAreaTableModel(config));
    }
    
    @Override
    protected String getBorderText(Optional<ArrayList<SCTAggregatePArea>> entities) {
        if(entities.isPresent()) {
            return String.format("Partial-areas (%d)", entities.get().size());
        } else {
            return "Partial-areas";
        }
    }
}