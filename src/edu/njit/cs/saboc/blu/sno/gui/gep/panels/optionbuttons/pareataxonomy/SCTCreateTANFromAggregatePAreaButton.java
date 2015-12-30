package edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy;

import edu.njit.cs.saboc.blu.core.gui.dialogs.LoadStatusDialog;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.CreateTANButton;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTAggregatePArea;
import edu.njit.cs.saboc.blu.sno.abn.tan.SCTTribalAbstractionNetworkGenerator;
import edu.njit.cs.saboc.blu.sno.abn.tan.local.SCTTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTLocalDataSource;
import java.util.Optional;
import javax.swing.SwingUtilities;

/**
 *
 * @author Den
 */
public class SCTCreateTANFromAggregatePAreaButton extends CreateTANButton {
    
    private Optional<SCTAggregatePArea> currentPArea = Optional.empty();
    
    private final SCTPAreaTaxonomyConfiguration config;
    
    public SCTCreateTANFromAggregatePAreaButton(SCTPAreaTaxonomyConfiguration config) {
        super("aggregate partial-area");
        
        this.config = config;
    }
    
    public void setCurrentPArea(SCTAggregatePArea parea) {
        currentPArea = Optional.ofNullable(parea);
    }
    
    @Override
    public void deriveTANAction() {
        if (currentPArea.isPresent()) {
            Thread loadThread = new Thread(new Runnable() {

                private LoadStatusDialog loadStatusDialog = null;
                private boolean doLoad = true;

                public void run() {
                    SCTDisplayFrameListener displayListener = config.getUIConfiguration().getDisplayFrameListener();
                    
                    SCTAggregatePArea parea = currentPArea.get();

                    loadStatusDialog = LoadStatusDialog.display(null,
                            String.format("Creating %s Tribal Abstraction Network (TAN)", config.getTextConfiguration().getGroupName(parea)),
                            new LoadStatusDialog.LoadingDialogClosedListener() {

                            @Override
                            public void dialogClosed() {
                                doLoad = false;
                            }
                        });
                    
                    SCTTribalAbstractionNetworkGenerator generator = new SCTTribalAbstractionNetworkGenerator(
                            config.getTextConfiguration().getGroupName(parea),
                            (SCTLocalDataSource)config.getDataConfiguration().getPAreaTaxonomy().getDataSource());
                    
                    SCTTribalAbstractionNetwork tan = generator.createTANFromConceptHierarchy(
                            config.getDataConfiguration().getAggregatedPAreaHierarchy(parea));
                    

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (doLoad) {
                                displayListener.addNewClusterGraphFrame(tan, true, true);

                                loadStatusDialog.setVisible(false);
                                loadStatusDialog.dispose();
                            }
                        }
                    });
                }
            });

            loadThread.start();
        }
    }
}