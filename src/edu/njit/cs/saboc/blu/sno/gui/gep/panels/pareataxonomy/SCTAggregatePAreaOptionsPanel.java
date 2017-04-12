package edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.aggregate.AggregatePArea;
import edu.njit.cs.saboc.blu.core.abn.tan.TANFactory;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.CreateTANFromSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.ExportSinglyRootedNodeButton;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.HelpButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutNodeDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateAncestorSubtaxonomyButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateExpandedSubtaxonomyButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateRootSubtaxonomyButton;
import edu.njit.cs.saboc.blu.sno.gui.gep.configuration.listener.DisplayPAreaTaxonomyAction;
import edu.njit.cs.saboc.blu.sno.gui.gep.configuration.listener.DisplayTANAction;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;

/**
 *
 * @author Chris O
 */
public class SCTAggregatePAreaOptionsPanel extends NodeOptionsPanel {

    public SCTAggregatePAreaOptionsPanel(SCTPAreaTaxonomyConfiguration config) {

        CreateExpandedSubtaxonomyButton expandedSubtaxonomyBtn = new CreateExpandedSubtaxonomyButton(
                config, new DisplayPAreaTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(expandedSubtaxonomyBtn);
        
        
        CreateAncestorSubtaxonomyButton ancestorSubtaxonomyBtn = new CreateAncestorSubtaxonomyButton(config,
            new DisplayPAreaTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(ancestorSubtaxonomyBtn);
        
        
        CreateTANFromSinglyRootedNodeButton tanBtn = new CreateTANFromSinglyRootedNodeButton(
                new TANFactory(config.getAbstractionNetwork().getDerivation().getSourceOntology()),
                config, 
            new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(tanBtn);
        
        
        CreateRootSubtaxonomyButton rootSubtaxonomyBtn = new CreateRootSubtaxonomyButton(config, 
            new DisplayPAreaTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(rootSubtaxonomyBtn);
        
        
        PopoutNodeDetailsButton popoutBtn = new PopoutNodeDetailsButton("aggregate partial-area", () -> {
            AggregatePArea parea = (AggregatePArea)super.getCurrentNode().get();
            
            NodeDashboardPanel anp = config.getUIConfiguration().createGroupDetailsPanel();
            anp.setContents(parea);

            return anp;
        });
        
        super.addOptionButton(popoutBtn);
        
        
        ExportSinglyRootedNodeButton exportBtn = new ExportSinglyRootedNodeButton(config);
        
        super.addOptionButton(exportBtn);

        HelpButton helpBtn = new HelpButton(config);

        super.addOptionButton(helpBtn);
    }
}