package edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy.disjoint;

import edu.njit.cs.saboc.blu.core.gui.dialogs.LoadStatusDialog;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.CreateTANButton;
import edu.njit.cs.saboc.blu.sno.abn.disjointpareataxonomy.DisjointPartialArea;
import edu.njit.cs.saboc.blu.sno.abn.tan.TANGenerator;
import edu.njit.cs.saboc.blu.sno.abn.tan.local.SCTTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.disjointpareataxonomy.configuration.SCTDisjointPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTLocalDataSource;
import java.util.Optional;
import javax.swing.SwingUtilities;

/**
 *
 * @author Chris O
 */
public class SCTCreateTANFromDisjointPAreaButton extends CreateTANButton {
    private Optional<DisjointPartialArea> currentPArea = Optional.empty();
    
    private final SCTDisjointPAreaTaxonomyConfiguration config;
    
    public SCTCreateTANFromDisjointPAreaButton(SCTDisjointPAreaTaxonomyConfiguration config) {
        super("partial-area");
        
        this.config = config;
    }
    
    public void setCurrentPArea(DisjointPartialArea parea) {
        currentPArea = Optional.ofNullable(parea);
    }
    
    @Override
    public void deriveTANAction() {
        if (currentPArea.isPresent()) {
            Thread loadThread = new Thread(new Runnable() {

                private LoadStatusDialog loadStatusDialog = null;

                public void run() {
                    SCTDisplayFrameListener displayListener = config.getUIConfiguration().getDisplayListener();
                    
                    DisjointPartialArea parea = currentPArea.get();

                    loadStatusDialog = LoadStatusDialog.display(null,
                            String.format("Creating %s Tribal Abstraction Network (TAN)", config.getTextConfiguration().getGroupName(parea)));
                    
                    SCTTribalAbstractionNetwork tan = 
                            TANGenerator.createTANFromConceptHierarchy(
                            config.getDataConfiguration().getDisjointPAreaTaxonomy().getSCTVersion(), 
                            parea.getConceptHierarchy(),
                            (SCTLocalDataSource)config.getDataConfiguration().getDisjointPAreaTaxonomy().getDataSource());

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            
                            displayListener.addNewClusterGraphFrame(tan, true, true);

                            loadStatusDialog.setVisible(false);
                            loadStatusDialog.dispose();
                        }
                    });
                }
            });

            loadThread.start();
        }
    }
}
