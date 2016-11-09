package edu.njit.cs.saboc.blu.sno.gui.graphframe;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.graph.BluGraph;
import edu.njit.cs.saboc.blu.core.graph.pareataxonomy.PAreaBluGraph;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.AbNPainter;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.SinglyRootedNodeLabelCreator;
import edu.njit.cs.saboc.blu.core.gui.graphframe.GenericInternalGraphFrame;
import edu.njit.cs.saboc.blu.core.gui.graphframe.buttons.search.PartitionedAbNSearchButton;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;
import edu.njit.cs.saboc.blu.core.gui.gep.AggregateableAbNInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.RelationshipSubtaxonomyPopupButton;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.AggregateSinglyRootedNodeLabelCreator;
import edu.njit.cs.saboc.blu.sno.gui.gep.painter.SCTAggregateTaxonomyPainter;
import edu.njit.cs.saboc.blu.sno.gui.gep.painter.SCTTaxonomyPainter;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyTextConfiguration;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.reports.SCTPAreaTaxonomyReportDialog;
import edu.njit.cs.saboc.blu.sno.gui.graphframe.buttons.GraphOptionsButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PAreaInternalGraphFrame extends GenericInternalGraphFrame {
    
    private final JButton openReportsBtn;
    
    private final PartitionedAbNSearchButton searchButton;
    
    private final GraphOptionsButton optionsButton;
    
    private final SCTDisplayFrameListener displayListener;
    
    private final RelationshipSubtaxonomyPopupButton relationshipSubtaxonomyButton;
    
    private SCTPAreaTaxonomyConfiguration currentConfiguration;
    
    private PAreaTaxonomy currentTaxonomy;

    public PAreaInternalGraphFrame(
            final JFrame parentFrame, 
            final PAreaTaxonomy taxonomy, 
            SCTDisplayFrameListener displayListener) {
        
        super(parentFrame, "SNOMED CT Partial-area Taxonomy");
        
        this.displayListener = displayListener;
        
        this.currentTaxonomy = taxonomy;

        super.setContainerAbNCheckboxText("Show Area Taxonomy");

        openReportsBtn = new JButton("Taxonomy Reports and Metrics");
        openReportsBtn.addActionListener( (ae) -> {
            
            if (currentTaxonomy.isAggregated()) {

            } else {
                SCTPAreaTaxonomyReportDialog reportDialog = new SCTPAreaTaxonomyReportDialog(currentConfiguration);
                reportDialog.showReports(taxonomy);

                reportDialog.setModal(true);
                reportDialog.setVisible(true);
            }
        });

        addReportButtonToMenu(openReportsBtn);
        
        optionsButton = new GraphOptionsButton(parentFrame, this, taxonomy);

        searchButton = new PartitionedAbNSearchButton(parentFrame, new SCTPAreaTaxonomyTextConfiguration(null));
        
        relationshipSubtaxonomyButton = new RelationshipSubtaxonomyPopupButton(parentFrame, 
            (selectedRels) -> {
                PAreaTaxonomy subtaxonomy = currentTaxonomy.getRelationshipSubtaxonomy(selectedRels);
                displayPAreaTaxonomy(subtaxonomy);
            }
        );

        displayPAreaTaxonomy(taxonomy);

        optionsButton.setToolTipText("Click to open the options menu for this graph.");
        searchButton.setToolTipText("Click to search within this graph.");
        
        addToggleableButtonToMenu(optionsButton);
        addToggleableButtonToMenu(searchButton);
        addToggleableButtonToMenu(relationshipSubtaxonomyButton);
    }

    public PAreaBluGraph getGraph() {
        return (PAreaBluGraph)super.getGraph();
    }

    private void updateHierarchyInfoLabel(PAreaTaxonomy<PArea> taxonomy) {

        setHierarchyInfoText(String.format("Areas: %d | Partial-areas: %d | Concepts: %d",
                taxonomy.getAreaTaxonomy().getAreas().size(), 
                taxonomy.getPAreas().size(),
                taxonomy.getSourceHierarchy().size()));
    }

    public final void displayPAreaTaxonomy(PAreaTaxonomy taxonomy) {
        
        this.currentTaxonomy = taxonomy;
        
        Thread loadThread = new Thread(() -> {
            gep.showLoading();
            
            SinglyRootedNodeLabelCreator<PArea> labelCreator;

            AbNPainter abnPainter;

            if (taxonomy.isAggregated()) {
                abnPainter = new SCTAggregateTaxonomyPainter();
                labelCreator = new AggregateSinglyRootedNodeLabelCreator<>();
            } else {
                abnPainter = new SCTTaxonomyPainter();
                labelCreator = new SinglyRootedNodeLabelCreator<>();
            }

            SCTPAreaTaxonomyConfigurationFactory factory = new SCTPAreaTaxonomyConfigurationFactory();

            currentConfiguration = factory.createConfiguration(currentTaxonomy, displayListener);

            BluGraph graph = new PAreaBluGraph(parentFrame, currentTaxonomy, labelCreator, currentConfiguration);
            
            searchButton.initialize(currentConfiguration);
            relationshipSubtaxonomyButton.initialize(currentConfiguration, currentTaxonomy);

            SwingUtilities.invokeLater(() -> {
   
                displayAbstractionNetwork(graph, 
                        abnPainter, 
                        currentConfiguration, 
                        new AggregateableAbNInitializer( (bound) -> {
                            PAreaTaxonomy aggregateTaxonomy = currentTaxonomy.getAggregated(bound);
                            displayPAreaTaxonomy(aggregateTaxonomy);
                        })
                );

                updateHierarchyInfoLabel(currentTaxonomy);
            });
        });

        loadThread.start();
    }
}
