/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.njit.cs.saboc.blu.sno.gui.abnselection;

import edu.njit.cs.saboc.blu.core.abn.PartitionedAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.core.gui.dialogs.LoadStatusDialog;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.SCTInferredPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.sno.localdatasource.concept.SCTConcept;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTReleaseWithStated;
import javax.swing.JFrame;

/**
 *
 * @author Kevyn
 */
public class ShowPAreaTaxonomySelection extends ShowAbNSelection {

    public ShowPAreaTaxonomySelection(JFrame parentFrame, String text, LoadReleasePanel localReleasePanel, DummyConcept root, 
            boolean useStatedRelationships, SCTAbNFrameManager displayFrameListener) {
        super(parentFrame, text, localReleasePanel, root, useStatedRelationships, displayFrameListener);
    }

    @Override
    protected void loadDataSource(boolean doLoad, LoadStatusDialog loadStatusDialog) {
        try {
            SCTRelease dataSource = localReleasePanel.getLoadedDataSource();

            SCTConcept localConcept = dataSource.getConceptFromId(root.getID()).get();

            Hierarchy<SCTConcept> hierarchy;

            if (useStatedRelationships) {
                SCTReleaseWithStated statedDataSource = (SCTReleaseWithStated) dataSource;

                hierarchy = statedDataSource.getStatedHierarchy().getSubhierarchyRootedAt(localConcept);
            } else {
                hierarchy = dataSource.getConceptHierarchy().getSubhierarchyRootedAt(localConcept);
            }

            PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();

            PAreaTaxonomy taxonomy = generator.derivePAreaTaxonomy(
                    new SCTInferredPAreaTaxonomyFactory(dataSource, hierarchy),
                    hierarchy);

            doLater(doLoad, loadStatusDialog, taxonomy);

        } catch (NoSCTDataSourceLoadedException e) {
            // TODO: Show error...
        }
    }

    @Override
    protected void displayFrame(PartitionedAbstractionNetwork taxonomy) {
        displayFrameListener.displayPAreaTaxonomy((PAreaTaxonomy) taxonomy);
    }
}
