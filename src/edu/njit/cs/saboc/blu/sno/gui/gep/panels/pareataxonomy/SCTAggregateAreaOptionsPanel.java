package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Area;
import edu.njit.cs.saboc.blu.core.abn.tan.TANFactory;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.CreateDisjointAbNFromPartitionNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.CreateTANFromPartitionedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutNodeDetailsButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.configuration.listener.DisplayDisjointTaxonomyAction;
import edu.njit.cs.saboc.blu.sno.gui.gep.configuration.listener.DisplayTANAction;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;

/**
 *
 * @author Chris O
 */
public class SCTAggregateAreaOptionsPanel extends NodeOptionsPanel {
    
    public SCTAggregateAreaOptionsPanel(SCTPAreaTaxonomyConfiguration config) {
              
        super.addOptionButton(new CreateDisjointAbNFromPartitionNodeButton(
                config,
                new DisplayDisjointTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager())));

         CreateTANFromPartitionedNodeButton tanBtn = new CreateTANFromPartitionedNodeButton(
                 new TANFactory(),
                 config, new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(tanBtn);
        
        PopoutNodeDetailsButton popoutBtn = new PopoutNodeDetailsButton("aggregate area", () -> {
            Area area = (Area)getCurrentNode().get();
            
            NodeDashboardPanel anp = config.getUIConfiguration().createContainerDetailsPanel();
            anp.setContents(area);

            return anp;
        });
        
        super.addOptionButton(popoutBtn);
    }
}
