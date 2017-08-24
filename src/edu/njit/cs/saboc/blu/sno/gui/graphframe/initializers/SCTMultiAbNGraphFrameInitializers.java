package edu.njit.cs.saboc.blu.sno.gui.graphframe.initializers;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.provenance.AbNDerivationParser;
import edu.njit.cs.saboc.blu.core.abn.tan.ClusterTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.AbNExplorationPanelGUIInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.DisjointAbNExplorationPanelInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.AbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.DisjointAbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.configuration.PAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.tan.configuration.TANConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.targetbased.configuration.TargetAbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.buttons.DerivationSelectionButton;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.AbNGraphFrameInitializers;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.GraphFrameInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.MultiAbNGraphFrame;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.TaskBarPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.framestate.FrameState;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.AreaTaxonomyInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.BandTANInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.DisjointAbNInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.PAreaTaxonomyInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.TANInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.TargetAbNInitializer;
import edu.njit.cs.saboc.blu.core.ontology.Ontology;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.abnhistory.SCTDerivationParser;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNFrameManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNWizardPanel;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.disjointpareataxonomy.configuration.SCTDisjointPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.disjointtan.configuration.SCTDisjointTANConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.tan.configuration.SCTTANConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.targetabn.configuration.SCTTargetAbNConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import javax.swing.SwingUtilities;

/**
 *
 * @author Chris O
 */
public class SCTMultiAbNGraphFrameInitializers implements AbNGraphFrameInitializers {
    
    private final SCTRelease release;
    
    private final OAFStateFileManager stateFileManager;
    
    private final SCTAbNFrameManager frameManager;
    
    private final AbNWarningManager warningManager;
    
    public SCTMultiAbNGraphFrameInitializers(
            SCTRelease release, 
            OAFStateFileManager stateFileManager,
            SCTAbNFrameManager frameManager,
            AbNWarningManager warningManager) {
        
        this.release = release;
        this.stateFileManager = stateFileManager;
        this.frameManager = frameManager;
        this.warningManager = warningManager;
    }
    
    private DerivationSelectionButton createDerivationSelectionButton(
            MultiAbNGraphFrame graphFrame,
            SCTRelease release) {
        
        SCTAbNWizardPanel wizardPanel = new SCTAbNWizardPanel(
                stateFileManager,
                new SCTFrameManagerAdapter(graphFrame),
                false);
        
        SwingUtilities.invokeLater(() -> {
            wizardPanel.setCurrentRelease(release);
            wizardPanel.setEnabled(true);
        });

        return new DerivationSelectionButton(graphFrame, wizardPanel);
    }
    
    @Override
    public GraphFrameInitializer<PAreaTaxonomy, PAreaTaxonomyConfiguration> getPAreaTaxonomyInitializer(FrameState frameState) {
        
        return new PAreaTaxonomyInitializer(warningManager, frameState) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, PAreaTaxonomyConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config); 

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(PAreaTaxonomy abn, AbNDisplayManager displayManager) {
                return new SCTPAreaTaxonomyConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager, false);
            }
        };
    }

    @Override
    public GraphFrameInitializer<PAreaTaxonomy, PAreaTaxonomyConfiguration> getAreaTaxonomyInitializer(FrameState frameState) {
        
        return new AreaTaxonomyInitializer(warningManager, frameState) {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, PAreaTaxonomyConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);
                
                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }
            
            @Override
            public AbNConfiguration getConfiguration(PAreaTaxonomy abn, AbNDisplayManager displayManager) {
                return new SCTPAreaTaxonomyConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager, true);
            }
        };
    }

    @Override
    public GraphFrameInitializer<ClusterTribalAbstractionNetwork, TANConfiguration> getTANInitializer(FrameState frameState) {
        
        return new TANInitializer(warningManager, frameState) {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TANConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);
                
                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(ClusterTribalAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTTANConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager, false);
            }
        };
    }
    
    
    @Override
    public GraphFrameInitializer<ClusterTribalAbstractionNetwork, TANConfiguration> getBandTANInitializer(FrameState frameState) {
        return new BandTANInitializer(warningManager, frameState) {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TANConfiguration config) {
                
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(ClusterTribalAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTTANConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager, true);
            }
        };
    }
    

    @Override
    public GraphFrameInitializer<TargetAbstractionNetwork, TargetAbNConfiguration> getTargetAbNInitializer(FrameState frameState) {
        return new TargetAbNInitializer(warningManager, frameState) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TargetAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(TargetAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTTargetAbNConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager);
            }
        };
    }

    @Override
    public GraphFrameInitializer<DisjointAbstractionNetwork, DisjointAbNConfiguration> getDisjointPAreaTaxonomyInitializer(FrameState frameState) {
        
        return new DisjointAbNInitializer() {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, DisjointAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }
            
            @Override
            public AbNConfiguration getConfiguration(DisjointAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTDisjointPAreaTaxonomyConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager);
            }

            @Override
            public AbNExplorationPanelGUIInitializer getExplorationGUIInitializer(DisjointAbNConfiguration config) {
                
                PAreaTaxonomy taxonomy = (PAreaTaxonomy)config.getAbstractionNetwork().getParentAbstractionNetwork();
                
                return new DisjointAbNExplorationPanelInitializer(
                        config,
                        new SCTPAreaTaxonomyConfigurationFactory().createConfiguration(
                                release,
                                taxonomy, 
                                config.getUIConfiguration().getAbNDisplayManager(), 
                                frameManager,
                                false),
                        
                        (ap) -> {
                                frameState.setAggregateProperty(ap);
                                DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getAggregated(frameState.getAggregateProperty());
                                config.getUIConfiguration().getAbNDisplayManager().displayDisjointPAreaTaxonomy(disjointAbN);                           
                        }, 
                        warningManager,
                        frameState);
            }
        };
    }

    @Override
    public GraphFrameInitializer<DisjointAbstractionNetwork, DisjointAbNConfiguration> getDisjointTANInitializer(FrameState frameState) {
        
        return new DisjointAbNInitializer() {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, DisjointAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, release));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(DisjointAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTDisjointTANConfigurationFactory().createConfiguration(
                        release, abn, displayManager, frameManager);
            }

            @Override
            public AbNExplorationPanelGUIInitializer getExplorationGUIInitializer(DisjointAbNConfiguration config) {

                ClusterTribalAbstractionNetwork tan = (ClusterTribalAbstractionNetwork) config.getAbstractionNetwork().getParentAbstractionNetwork();

                return new DisjointAbNExplorationPanelInitializer(
                        config,
                        new SCTTANConfigurationFactory().createConfiguration(
                                release,
                                tan, 
                                config.getUIConfiguration().getAbNDisplayManager(), 
                                frameManager,
                                false),
                        
                        (ap) -> {
                                DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getAggregated(ap);
                                config.getUIConfiguration().getAbNDisplayManager().displayDisjointTribalAbstractionNetwork(disjointAbN);                            
                        },
                        warningManager,
                        frameState);
            }
        };
    }

    @Override
    public OAFRecentlyOpenedFileManager getRecentAbNWorkspaceFiles() {
        
        try {
            
            return stateFileManager.getRecentAbNWorkspaces(release.getReleaseInfo().getReleaseDirectory());
            
        } catch(RecentlyOpenedFileException rofe) {
            
        }
        
        return null;
    }

    @Override
    public Ontology getSourceOntology() {
        return release;
    }

    @Override
    public AbNDerivationParser getAbNParser() {
        return new SCTDerivationParser(release);
    }
}
