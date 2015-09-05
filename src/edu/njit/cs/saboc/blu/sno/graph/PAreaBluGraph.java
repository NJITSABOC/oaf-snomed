package edu.njit.cs.saboc.blu.sno.graph;

import SnomedShared.pareataxonomy.InheritedRelationship;
import edu.njit.cs.saboc.blu.core.graph.BluGraph;
import edu.njit.cs.saboc.blu.core.graph.ShowHideGroupEntryListener;
import edu.njit.cs.saboc.blu.core.gui.dialogs.ContainerResize;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.GroupEntryLabelCreator;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPArea;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTRegion;
import edu.njit.cs.saboc.blu.sno.graph.layout.NoRegionsLayout;
import edu.njit.cs.saboc.blu.sno.graph.layout.RegionsLayout;
import edu.njit.cs.saboc.blu.sno.graph.layout.SCTGraphLayoutFactory;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;
import edu.njit.cs.saboc.blu.sno.gui.gep.listeners.SCTPAreaTaxonomyGEPConfiguration;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.SCTPAreaTaxonomyConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Chris
 */
public class PAreaBluGraph extends BluGraph {
    
    private SCTDisplayFrameListener displayListener;
    
    private final SCTPAreaTaxonomyGEPConfiguration config;

    public PAreaBluGraph(
            JFrame parentFrame, 
            SCTPAreaTaxonomy hierarchyData, 
            boolean areaGraph,
            SCTDisplayFrameListener displayListener, 
            GroupEntryLabelCreator<SCTPArea> labelCreator,
            SCTPAreaTaxonomyGEPConfiguration config) {
        
        super(hierarchyData, areaGraph, hierarchyData.getDataSource().isLocalDataSource(), labelCreator);
        
        this.displayListener = displayListener;
        this.config = config;

        if (areaGraph) {
            layout = SCTGraphLayoutFactory.createNoRegionsPAreaLayout(this);
            ((NoRegionsLayout) layout).doLayout(showConceptCountLabels);

        } else {
            layout = SCTGraphLayoutFactory.createRegionsPAreaLayout(this);
            ((RegionsLayout) layout).doLayout(showConceptCountLabels);
        }
    }
    
    public SCTPAreaTaxonomyGEPConfiguration getGEPConfiguration() {
        return config;
    }

    public SCTPAreaTaxonomy getPAreaTaxonomy() {
        return (SCTPAreaTaxonomy)getAbstractionNetwork();
    }
    
    public SCTDisplayFrameListener getDisplayFrameListener() {
        return displayListener;
    }
}
