package edu.njit.cs.saboc.blu.sno.gui.gep.panels.optionbuttons.pareataxonomy;

import edu.njit.cs.saboc.blu.core.gui.dialogs.LoadStatusDialog;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateRootSubtaxonomyButton;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPArea;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTDisplayFrameListener;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfiguration;
import java.util.Optional;
import javax.swing.SwingUtilities;

/**
 *
 * @author Chris O
 */
public class SCTCreateRootSubtaxonomyButton extends CreateRootSubtaxonomyButton {

    private Optional<SCTPArea> currentPArea = Optional.empty();
    
    private final SCTPAreaTaxonomyConfiguration config;
    
    public SCTCreateRootSubtaxonomyButton(SCTPAreaTaxonomyConfiguration config) {
        this.config = config;
    }
    
    public void setCurrentPArea(SCTPArea parea) {
        currentPArea = Optional.ofNullable(parea);
    }
    
    @Override
    public void createSubtaxonomyAction() {
        if (currentPArea.isPresent()) {
            Thread loadThread = new Thread(new Runnable() {

                private LoadStatusDialog loadStatusDialog = null;
                private boolean doLoad = true;

                public void run() {
                    SCTDisplayFrameListener displayListener = config.getUIConfiguration().getDisplayFrameListener();
                    
                    SCTPArea parea = currentPArea.get();

                    loadStatusDialog = LoadStatusDialog.display(null,
                            String.format("Creating %s root subtaxonomy", config.getTextConfiguration().getGroupName(parea)),
                            new LoadStatusDialog.LoadingDialogClosedListener() {

                            @Override
                            public void dialogClosed() {
                                doLoad = false;
                            }
                        });
                    
                    SCTPAreaTaxonomy subtaxonomy = config.getDataConfiguration().getPAreaTaxonomy().getRootSubtaxonomy(parea);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (doLoad) {
                                displayListener.addNewPAreaGraphFrame(subtaxonomy, true);

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
