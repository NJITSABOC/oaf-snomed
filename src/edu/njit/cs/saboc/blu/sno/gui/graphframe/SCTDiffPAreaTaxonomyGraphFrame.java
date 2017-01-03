package edu.njit.cs.saboc.blu.sno.gui.graphframe;

import edu.njit.cs.saboc.blu.core.abn.diff.ModifiedNode;
import edu.njit.cs.saboc.blu.core.abn.diff.change.ChangeState;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.graph.AbstractionNetworkGraph;
import edu.njit.cs.saboc.blu.core.graph.pareataxonomy.diff.DiffPAreaTaxonomyGraph;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.SinglyRootedNodeLabelCreator;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.pareataxonomy.DiffTaxonomyPainter;
import edu.njit.cs.saboc.blu.core.gui.graphframe.GenericInternalGraphFrame;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.diff.configuration.SCTDiffPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.diff.configuration.SCTDiffPAreaTaxonomyConfigurationFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Chris O
 */
public class SCTDiffPAreaTaxonomyGraphFrame extends GenericInternalGraphFrame<DiffPAreaTaxonomy> {

    private final SCTDisplayFrameListener displayListener;
    
    public SCTDiffPAreaTaxonomyGraphFrame(
            JFrame parentFrame,
            SCTDisplayFrameListener displayListener) {

        super(parentFrame, "SNOMED CT Diff Partial-area Taxonomy");
        
        this.displayListener = displayListener;

        this.setTitle("TEST DIFF FRAME");
    }
    
    private void updateHierarchyInfoLabel(DiffPAreaTaxonomy data) {
        setHierarchyInfoText("*** SCT DIFF TAXONOMY UI IN DEVELOPMENT ***");
    }

    public final void replaceInternalFrameDataWith(DiffPAreaTaxonomy data) {
        
        Thread loadThread = new Thread(() -> {
            getAbNExplorationPanel().showLoading();
            
            SinglyRootedNodeLabelCreator<DiffPArea> labelCreator = new SinglyRootedNodeLabelCreator<DiffPArea>() {

                public String getCountStr(DiffPArea parea) {
                    
                    int num;
                    
                    if(parea.getPAreaState() == ChangeState.Removed) {
                        num =  0;
                    } else if(parea.getPAreaState() == ChangeState.Modified) {
                        PArea after = (PArea)((ModifiedNode)parea.getDiffNode()).getToNode();
                        
                        num = after.getConceptCount();
                    } else {
                        num = parea.getConceptCount();
                    }
                    
                    return String.format("(%d)", num);
                }
                
                
            };
            
            SCTDiffPAreaTaxonomyConfigurationFactory configFactory = new SCTDiffPAreaTaxonomyConfigurationFactory();
            SCTDiffPAreaTaxonomyConfiguration config = configFactory.createConfiguration(data, displayListener);

            AbstractionNetworkGraph newGraph = new DiffPAreaTaxonomyGraph(getParentFrame(), data, labelCreator, config);
            
            SwingUtilities.invokeLater(() -> {
                displayAbstractionNetwork(newGraph, new DiffTaxonomyPainter(), config);

                updateHierarchyInfoLabel(data);
            });
        });
        
        loadThread.start();
    }
}