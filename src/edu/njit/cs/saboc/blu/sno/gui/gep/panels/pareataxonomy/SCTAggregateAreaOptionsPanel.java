package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy;

import edu.njit.cs.saboc.blu.core.abn.node.Node;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Area;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.AbstractNodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutNodeDetailsButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.SCTCreateDisjointTaxonomyButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.SCTCreateTANFromAreaButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Chris O
 */
public class SCTAggregateAreaOptionsPanel extends AbstractNodeOptionsPanel {
    
    private Optional<Area> selectedArea = Optional.empty();
    
    private final SCTPAreaTaxonomyConfiguration config;
    
    private final SCTCreateDisjointTaxonomyButton disjointTaxonomyBtn;
    
    private final SCTCreateTANFromAreaButton tanBtn;
    
    private final PopoutNodeDetailsButton popoutBtn;

    public SCTAggregateAreaOptionsPanel(SCTPAreaTaxonomyConfiguration config) {
        this.config = config;
        
        this.disjointTaxonomyBtn = new SCTCreateDisjointTaxonomyButton(config);
        
        super.addOptionButton(disjointTaxonomyBtn);

        this.tanBtn = new SCTCreateTANFromAreaButton(config);
        
        super.addOptionButton(tanBtn);
        
        popoutBtn = new PopoutNodeDetailsButton("aggregate area", () -> {
            NodeDashboardPanel anp = config.getUIConfiguration().createContainerDetailsPanel();
            anp.setContents(selectedArea.get());

            return anp;
        });
        
        super.addOptionButton(popoutBtn);
    }

    @Override
    public void enableOptionsForNode(Node node) {
        
        Area area = (Area)node;
        
        if (area.getPAreas().size() > 2) {
            Set<PArea> pareas = area.getPAreas();

            boolean tanPossible = false;

            for (PArea parea : pareas) {
                if (parea.getConceptCount() > 1) {
                    tanPossible = true;
                    break;
                }
            }

            tanBtn.setEnabled(tanPossible);
        } else {
            tanBtn.setEnabled(false);
        }
    }

    @Override
    public void setContents(Node node) {
        Area area = (Area)node;
        
        selectedArea = Optional.of(area);
        
        disjointTaxonomyBtn.setCurrentArea(area);
        tanBtn.setCurrentArea(area);
        
        this.enableOptionsForNode(area);
    }
    
    public void clearContents() {
        selectedArea = Optional.empty();
        
        disjointTaxonomyBtn.setCurrentArea(null);
        tanBtn.setCurrentArea(null);
    }
}