package edu.njit.cs.saboc.blu.sno.gui.graphframe.buttons;

import SnomedShared.pareataxonomy.InheritedRelationship;
import SnomedShared.pareataxonomy.PAreaSummary;
import edu.njit.cs.saboc.blu.core.gui.graphframe.buttons.PopupToggleButton;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.LocalPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.gui.graphframe.PAreaInternalGraphFrame;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.InferredRelationshipsRetriever;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTLocalDataSource;
import edu.njit.cs.saboc.blu.sno.sctdatasource.middlewareproxy.MiddlewareAccessorProxy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Chris
 */
public class RelationshipSelectionButton extends PopupToggleButton {

    public RelationshipSelectionButton(Frame parent, final PAreaInternalGraphFrame igf, final PAreaTaxonomy data) {

        super(parent, "Create Subtaxonomy");

        final HashMap<JCheckBox, Long> selectedRelationships = new HashMap<JCheckBox, Long>();
        
        final HashMap<Long, String> lateralRels = data.getLateralRelsInHierarchy();
                
        final JPanel cbPanel = new JPanel();

        cbPanel.setBackground(Color.WHITE);
        cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.Y_AXIS));

        ArrayList<Long> relIds = new ArrayList<Long>();

        for (Long key : lateralRels.keySet()) {
            relIds.add(key);
        }

        Collections.sort(relIds, new Comparator<Long>() {
            public int compare(Long a, Long b) {
                return lateralRels.get(a).compareToIgnoreCase(lateralRels.get(b));
            }
        });

        for (Long key : relIds) {
            String relName = lateralRels.get(key);
            JCheckBox cb = new JCheckBox(relName);
            cb.setSelected(false);
            cb.setBackground(Color.WHITE);

            selectedRelationships.put(cb, key);

            cbPanel.add(cb);
        }

        PAreaSummary rootPArea = data.getRootPArea();

        for(Long selectedRelId : data.getLateralRelsInHierarchy().keySet()) {
            for(Entry<JCheckBox, Long> entry : selectedRelationships.entrySet()) {
                if(selectedRelId.equals(entry.getValue())) {
                    entry.getKey().setSelected(true);
                }
            }
        }

        for(JCheckBox cb : selectedRelationships.keySet()) {
            for(InheritedRelationship ir : rootPArea.getRelationships()) {
                if(ir.getRelationshipTypeId() == selectedRelationships.get(cb)) {
                    cb.setEnabled(false);
                }
            }
        }

        JPanel popupPanel = new JPanel(new BorderLayout());

        popupPanel.setBorder(BorderFactory.createEtchedBorder());

        popupPanel.add(new JLabel("<html>Show only areas in this hierarchy containing "
                + "atleast one<br> of the following relationships:"), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(cbPanel);

        scrollPane.setPreferredSize(new Dimension(200, 350));

        popupPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton checkAll = new JButton("Check All");
        checkAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(JCheckBox cb : selectedRelationships.keySet()) {
                    if(cb.isEnabled()) {
                        cb.setSelected(true);
                    }
                }
            }
        });

        JButton uncheckAll = new JButton("Uncheck All");
        uncheckAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (JCheckBox cb : selectedRelationships.keySet()) {
                    if(cb.isEnabled()) {
                        cb.setSelected(false);
                    }
                }
            }
        });

        buttonPanel.add(checkAll);
        buttonPanel.add(uncheckAll);

        JButton generateButton = new JButton("Display Subtaxonomy");
        
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ArrayList<Long> rels = new ArrayList<Long>();

                for(JCheckBox cb : selectedRelationships.keySet()) {
                    if(cb.isSelected()) {
                        rels.add(selectedRelationships.get(cb));
                    }
                }
                
                PAreaTaxonomy hierarchyTaxonomy;
                
                if(data instanceof LocalPAreaTaxonomy) {
                    SCTLocalDataSource dataSource = (SCTLocalDataSource)((LocalPAreaTaxonomy)data).getSCTDataSource();
                    
                    PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();

                    hierarchyTaxonomy = generator.createPAreaTaxonomy(data.getSNOMEDHierarchyRoot(), dataSource, new InferredRelationshipsRetriever());
                } else {
                    hierarchyTaxonomy = MiddlewareAccessorProxy.getProxy().getPAreaHierarchyData(
                        data.getVersion(), data.getSNOMEDHierarchyRoot());
                }

                if(rels.size() != selectedRelationships.keySet().size()) {
                    hierarchyTaxonomy = hierarchyTaxonomy.getRelationshipSubtaxonomy(rels);
                }
                
                System.out.println(hierarchyTaxonomy.getRootPArea().getRoot().getName() + " | " + hierarchyTaxonomy.getSNOMEDHierarchyRoot().getName());

                if(hierarchyTaxonomy.getRootPArea().getRoot().getId() != hierarchyTaxonomy.getSNOMEDHierarchyRoot().getId()) {
                    hierarchyTaxonomy = hierarchyTaxonomy.getRootSubtaxonomy(data.getRootPArea());
                }

                if(rels.size() != selectedRelationships.keySet().size()) {
                    hierarchyTaxonomy = hierarchyTaxonomy.getImplicitRelationshipSubtaxonomy();
                }

                igf.replaceInternalFrameDataWith(hierarchyTaxonomy,
                        igf.getGraph().getIsAreaGraph(),
                        igf.getGraph().showingConceptCountLabels(), null);
            }
        });

        buttonPanel.add(generateButton);

        popupPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setPopupContent(popupPanel);
    }
}
