package edu.njit.cs.saboc.blu.sno.gui.abnselection;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointNode;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.DisjointPArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.tan.Cluster;
import edu.njit.cs.saboc.blu.core.abn.tan.ClusterTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.graph.tan.DisjointCluster;
import edu.njit.cs.saboc.blu.core.gui.frame.OAFMainFrame;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.FrameCreationAction;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.MultiAbNGraphFrame;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.graphframe.SCTDiffPAreaTaxonomyGraphFrame;
import edu.njit.cs.saboc.blu.sno.gui.graphframe.initializers.SCTMultiAbNGraphFrameInitializers;
import edu.njit.cs.saboc.blu.sno.localdatasource.concept.SCTConcept;
import edu.njit.cs.saboc.blu.sno.nat.SCTConceptBrowserDataSource;
import edu.njit.cs.saboc.blu.sno.nat.SCTNATLayout;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import edu.njit.cs.saboc.nat.generic.NATBrowserFrame;
import java.util.Optional;

/**
 * A class for creating and displaying new abstraction networks and NAT
 * frames (and any other kind of frame) in the OAF
 * 
 * @author Chris
 */
public class SCTAbNFrameManager extends AbNDisplayManager  {
    
    private final OAFMainFrame mainFrame;
    
    private final OAFStateFileManager stateFileManager;
    
    private final AbNWarningManager warningManager;
    
    private Optional<SCTRelease> optRelease = Optional.empty();
    
    public SCTAbNFrameManager(
            OAFMainFrame mainFrame, 
            FrameCreationAction fca, 
            OAFStateFileManager stateFileManager,
            AbNWarningManager warningManager) {
        
        super(fca);

        this.stateFileManager = stateFileManager;
        this.mainFrame = mainFrame;
        this.warningManager = warningManager;
    }
    
    public void setCurrentRelease(SCTRelease release) {
        this.optRelease = Optional.of(release);
    }
    
    public void clearCurrentRelease() {
        this.optRelease = Optional.empty();
    }
    
    public OAFMainFrame getMainFrame() {
        return mainFrame;
    }
    
    @Override
    public void displayPAreaTaxonomy(PAreaTaxonomy taxonomy) {
        
        if(!optRelease.isPresent()) {
            return;
        }
        
        SCTRelease release = optRelease.get();
        
        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release, 
                stateFileManager, 
                this, 
                warningManager);
        
        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);

        graphFrame.displayPAreaTaxonomy(taxonomy);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }
        
    
    @Override
    public void displayTribalAbstractionNetwork(ClusterTribalAbstractionNetwork tan) {
        
        if(!optRelease.isPresent()) {
            return;
        }
        
        SCTRelease release = optRelease.get();
        
        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release, 
                stateFileManager, 
                this, 
                warningManager);
        
        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);

        graphFrame.displayTAN(tan);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }

    @Override
    public void displayDisjointPAreaTaxonomy(
            DisjointAbstractionNetwork<DisjointNode<PArea>, PAreaTaxonomy<PArea>, PArea> disjointTaxonomy) {
        
        if(!optRelease.isPresent()) {
            return;
        }
        
        SCTRelease release = optRelease.get();
        
        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release, 
                stateFileManager, 
                this, 
                warningManager);
        
        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);
        
        graphFrame.displayDisjointPAreaTaxonomy(
                (DisjointAbstractionNetwork<DisjointPArea, PAreaTaxonomy<PArea>, PArea>)(DisjointAbstractionNetwork<?, ?, ?>)disjointTaxonomy);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }
    
    @Override
    public void displayDisjointTribalAbstractionNetwork(
            DisjointAbstractionNetwork<DisjointNode<Cluster>, ClusterTribalAbstractionNetwork<Cluster>, Cluster> disjointTAN) {

        if (!optRelease.isPresent()) {
            return;
        }

        SCTRelease release = optRelease.get();

        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release,
                stateFileManager,
                this,
                warningManager);

        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);

        graphFrame.displayDisjointTAN(
                (DisjointAbstractionNetwork<DisjointCluster, ClusterTribalAbstractionNetwork<Cluster>, Cluster>) (DisjointAbstractionNetwork<?, ?, ?>) disjointTAN);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }

    @Override
    public void displayTargetAbstractionNetwork(TargetAbstractionNetwork targetAbN) {
        if (!optRelease.isPresent()) {
            return;
        }

        SCTRelease release = optRelease.get();

        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release,
                stateFileManager,
                this,
                warningManager);

        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);

        graphFrame.displayTargetAbstractionNetwork(targetAbN);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }

    @Override
    public void displayAreaTaxonomy(PAreaTaxonomy taxonomy) {

        if (!optRelease.isPresent()) {
            return;
        }

        SCTRelease release = optRelease.get();

        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release,
                stateFileManager,
                this,
                warningManager);

        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);
        
        graphFrame.displayAreaTaxonomy(taxonomy);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }

    @Override
    public void displayBandTribalAbstractionNetwork(ClusterTribalAbstractionNetwork tan) {

        if (!optRelease.isPresent()) {
            return;
        }

        SCTRelease release = optRelease.get();

        SCTMultiAbNGraphFrameInitializers initializers = new SCTMultiAbNGraphFrameInitializers(
                release,
                stateFileManager,
                this,
                warningManager);

        MultiAbNGraphFrame graphFrame = new MultiAbNGraphFrame(mainFrame, stateFileManager);
        graphFrame.setInitializers(initializers);

        graphFrame.displayBandTAN(tan);

        this.getFrameCreationAction().displayFrame(graphFrame);
    }
    
    @Override
    public void displayDiffPAreaTaxonomy(DiffPAreaTaxonomy diffTaxonomy) {
        SCTDiffPAreaTaxonomyGraphFrame frame = new SCTDiffPAreaTaxonomyGraphFrame(mainFrame, this);
        frame.displayDiffPAreaTaxonomy(diffTaxonomy);
        
        super.getFrameCreationAction().displayFrame(frame);
    }   
    
    public void displayConceptBrowserFrame(SCTConceptBrowserDataSource dataSource, Optional<SCTConcept> focusConcept) {
        
        NATBrowserFrame<SCTConcept> browserFrame = new NATBrowserFrame<>(
            mainFrame,
            stateFileManager,
            dataSource, 
            new SCTNATLayout());
        
        if(focusConcept.isPresent()) {
            browserFrame.nagivateTo(focusConcept.get());
        } else {
            browserFrame.nagivateTo(dataSource.getOntology().getConceptHierarchy().getRoot());
        }
        
        super.getFrameCreationAction().displayFrame(browserFrame);
    }
}
