package edu.njit.cs.saboc.blu.sno.gui.graphframe.initializers;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
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
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.AreaTaxonomyInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.BandTANInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.DisjointAbNInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.PAreaTaxonomyInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.TANInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.TargetAbNInitializer;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNFrameManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNWizardPanel;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.disjointpareataxonomy.configuration.SCTDisjointPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.disjointtan.configuration.SCTDisjointTANConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.pareataxonomy.configuration.SCTPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.tan.configuration.SCTTANConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.gui.gep.panels.targetabn.configuration.SCTTargetAbNConfigurationFactory;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;

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
        
        SCTAbNWizardPanel wizardPanel = new SCTAbNWizardPanel(new SCTFrameManagerAdapter(graphFrame), false);
        wizardPanel.setCurrentRelease(release);
        wizardPanel.setEnabled(true);
        
        return new DerivationSelectionButton(graphFrame, wizardPanel);
    }
    
    @Override
    public GraphFrameInitializer<PAreaTaxonomy, PAreaTaxonomyConfiguration> getPAreaTaxonomyInitializer() {
        
        return new PAreaTaxonomyInitializer(warningManager) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, PAreaTaxonomyConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config); 

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, 
                                (SCTRelease)config.getPAreaTaxonomy().getDerivation().getSourceOntology()));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(PAreaTaxonomy abn, AbNDisplayManager displayManager) {
                return new SCTPAreaTaxonomyConfigurationFactory().createConfiguration(abn, displayManager, frameManager, false);
            }
        };
    }

    @Override
    public GraphFrameInitializer<PAreaTaxonomy, PAreaTaxonomyConfiguration> getAreaTaxonomyInitializer() {
        
        return new AreaTaxonomyInitializer(warningManager) {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, PAreaTaxonomyConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);
                
                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, 
                                (SCTRelease)config.getPAreaTaxonomy().getDerivation().getSourceOntology()));

                return panel;
            }
            
            @Override
            public AbNConfiguration getConfiguration(PAreaTaxonomy abn, AbNDisplayManager displayManager) {
                return new SCTPAreaTaxonomyConfigurationFactory().createConfiguration(abn, displayManager, frameManager, true);
            }
        };
    }

    @Override
    public GraphFrameInitializer<ClusterTribalAbstractionNetwork, TANConfiguration> getTANInitializer() {
        
        return new TANInitializer(warningManager) {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TANConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);
                
                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, 
                                (SCTRelease)config.getAbstractionNetwork().getDerivation().getSourceOntology()));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(ClusterTribalAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTTANConfigurationFactory().createConfiguration(abn, displayManager, frameManager, false);
            }
        };
    }
    
    
    @Override
    public GraphFrameInitializer<ClusterTribalAbstractionNetwork, TANConfiguration> getBandTANInitializer() {
        return new BandTANInitializer(warningManager) {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TANConfiguration config) {
                
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame,
                                (SCTRelease) config.getAbstractionNetwork().getDerivation().getSourceOntology()));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(ClusterTribalAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTTANConfigurationFactory().createConfiguration(abn, displayManager, frameManager, true);
            }
        };
    }
    

    @Override
    public GraphFrameInitializer<TargetAbstractionNetwork, TargetAbNConfiguration> getTargetAbNInitializer() {
        return new TargetAbNInitializer(warningManager) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TargetAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame,
                                (SCTRelease) config.getAbstractionNetwork().getDerivation().getSourceOntology()));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(TargetAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTTargetAbNConfigurationFactory().createConfiguration(abn, displayManager, frameManager);
            }
        };
    }

    @Override
    public GraphFrameInitializer<DisjointAbstractionNetwork, DisjointAbNConfiguration> getDisjointPAreaTaxonomyInitializer() {
        
        return new DisjointAbNInitializer() {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, DisjointAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame,
                                (SCTRelease) config.getAbstractionNetwork().getDerivation().getSourceOntology()));

                return panel;
            }
            
            @Override
            public AbNConfiguration getConfiguration(DisjointAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTDisjointPAreaTaxonomyConfigurationFactory().createConfiguration(abn, displayManager, frameManager);
            }

            @Override
            public AbNExplorationPanelGUIInitializer getExplorationGUIInitializer(DisjointAbNConfiguration config) {
                
                PAreaTaxonomy taxonomy = (PAreaTaxonomy)config.getAbstractionNetwork().getParentAbstractionNetwork();
                
                return new DisjointAbNExplorationPanelInitializer(
                        config,
                        new SCTPAreaTaxonomyConfigurationFactory().createConfiguration(
                                taxonomy, 
                                config.getUIConfiguration().getAbNDisplayManager(), 
                                frameManager,
                                false),
                        
                        (bound, isWeightedAggregated) -> {
                            if (isWeightedAggregated) {
                                DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getWeightedAggregated(bound, isWeightedAggregated);
                                config.getUIConfiguration().getAbNDisplayManager().displayDisjointPAreaTaxonomy(disjointAbN);
                            } else {
                                DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getAggregated(bound);
                                config.getUIConfiguration().getAbNDisplayManager().displayDisjointPAreaTaxonomy(disjointAbN);
                            }
                        }, 
                        warningManager);
            }
        };
    }

    @Override
    public GraphFrameInitializer<DisjointAbstractionNetwork, DisjointAbNConfiguration> getDisjointTANInitializer() {
        return new DisjointAbNInitializer() {
            
            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, DisjointAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame,
                                (SCTRelease) config.getAbstractionNetwork().getDerivation().getSourceOntology()));

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(DisjointAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new SCTDisjointTANConfigurationFactory().createConfiguration(abn, displayManager, frameManager);
            }

            @Override
            public AbNExplorationPanelGUIInitializer getExplorationGUIInitializer(DisjointAbNConfiguration config) {

                ClusterTribalAbstractionNetwork tan = (ClusterTribalAbstractionNetwork) config.getAbstractionNetwork().getParentAbstractionNetwork();

                return new DisjointAbNExplorationPanelInitializer(
                        config,
                        new SCTTANConfigurationFactory().createConfiguration(
                                tan,
                                config.getUIConfiguration().getAbNDisplayManager(),
                                frameManager,
                                false),
                        
                        (bound, isWeightedAggregated) -> {
                            if (isWeightedAggregated) {
                                DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getWeightedAggregated(bound, isWeightedAggregated);
                                config.getUIConfiguration().getAbNDisplayManager().displayDisjointTribalAbstractionNetwork(disjointAbN);

                            } else {
                                DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getAggregated(bound);
                                config.getUIConfiguration().getAbNDisplayManager().displayDisjointTribalAbstractionNetwork(disjointAbN);
                            }
                        },
                        warningManager);
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
}
