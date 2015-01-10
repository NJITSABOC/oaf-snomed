package edu.njit.cs.saboc.blu.sno.conceptbrowser.gui.panels;

import SnomedShared.Concept;
import SnomedShared.OutgoingLateralRelationship;
import SnomedShared.PAreaDetailsForConcept;
import edu.njit.cs.saboc.blu.core.gui.dialogs.AbNLoadStatusDialog;
import edu.njit.cs.saboc.blu.sno.conceptbrowser.FocusConcept;
import edu.njit.cs.saboc.blu.sno.conceptbrowser.SnomedConceptBrowser;
import edu.njit.cs.saboc.blu.sno.graph.PAreaBluGraph;
import edu.njit.cs.saboc.blu.sno.gui.dialogs.ConceptGroupDetailsDialog;
import edu.njit.cs.saboc.blu.sno.gui.graphframe.PAreaInternalGraphFrame;
import edu.njit.cs.saboc.blu.sno.localdatasource.load.InferredRelationshipsRetriever;
import edu.njit.cs.saboc.blu.sno.abn.generator.SCTPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.datastructure.hierarchy.SCTConceptHierarchy;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTDataSource;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTLocalDataSource;
import edu.njit.cs.saboc.blu.sno.sctdatasource.middlewareproxy.MiddlewareAccessorProxy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Chris
 */
public class AbstractionNetworkPanel extends BaseNavPanel {

    private JTabbedPane tabbedPane;
    private BaseNavPanel partialAreaPanel;
    private BaseNavPanel tribalANPanel;
    private PAreaTaxonomyDetailsPanel pareaDetailsPanel = new PAreaTaxonomyDetailsPanel();

    public AbstractionNetworkPanel(final SnomedConceptBrowser mainPanel, final SCTDataSource dataSource) {
        super(mainPanel, dataSource);

        setBackground(mainPanel.getNeighborhoodBGColor());
        setLayout(new BorderLayout());

        partialAreaPanel = new BaseNavPanel(mainPanel, dataSource) {
            public void dataPending() {
                pareaDetailsPanel.displayLabel("Please wait...");
            }

            public void dataEmpty() {
                pareaDetailsPanel.displayLabel("");
            }

            public void dataReady() {
                ArrayList<PAreaDetailsForConcept> details
                        = (ArrayList<PAreaDetailsForConcept>) focusConcept.getConceptList(FocusConcept.Fields.PARTIALAREA);

                if (!details.isEmpty()) {
                    SCTPAreaTaxonomy data;

                    if (dataSource.supportsMultipleVersions()) {
                        String version = MiddlewareAccessorProxy.getProxy().getSnomedVersionAtIndex(
                                focusConcept.getAssociatedBrowser().getLogoPanel().getSelectedVersion());

                        data = MiddlewareAccessorProxy.getProxy().getPAreaHierarchyData(version, details.get(0).getHierarchyRoot());
                    } else {
                        data = ((SCTLocalDataSource)dataSource).getCompleteTaxonomy(details.get(0).getHierarchyRoot());
                    }

                    pareaDetailsPanel.displayDetails(data, details);
                } else {
                    pareaDetailsPanel.displayLabel("SNOMED CT Concept does not belong to any partial-area taxonomies.");
                }
            }
        };

        partialAreaPanel.setLayout(new BorderLayout());
        partialAreaPanel.add(new JScrollPane(pareaDetailsPanel), BorderLayout.CENTER);

        tribalANPanel = new BaseNavPanel(mainPanel, dataSource) {
            public void dataPending() {
            }

            public void dataEmpty() {
            }

            public void dataReady() {
            }
        };

        tribalANPanel.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Partial-Area Details", partialAreaPanel);
        
        if(dataSource instanceof SCTLocalDataSource) {
            final SCTLocalDataSource localDS = (SCTLocalDataSource)dataSource;
            
            JPanel subtaxonomyOptionsPanel = new JPanel(new GridLayout(1, 2, 2, 2));

            JButton descTaxonomyBtn = new JButton("Create Subject Subtaxonomy");
            
            descTaxonomyBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Concept c = focusConcept.getConcept();
                    
                    createAndDisplaySubjectSubtaxonomy(localDS, c);
                }
            });
            
            JEditorPane subjectSubtaxonomyDesc = new JEditorPane();
            subjectSubtaxonomyDesc.setEditable(false);
            subjectSubtaxonomyDesc.setContentType("text/html");
            subjectSubtaxonomyDesc.setText("A <b>subject subtaxonomy</b> is a taxonomy derived using the chosen focus concept and all of its "
                    + "descendants. It summarizes the subhierarchy of classes rooted at the chosen focus concept.");

            JPanel descBtnPanel = new JPanel(new BorderLayout());
            descBtnPanel.setBorder(BorderFactory.createEtchedBorder());
            descBtnPanel.add(descTaxonomyBtn, BorderLayout.NORTH);
            descBtnPanel.add(subjectSubtaxonomyDesc, BorderLayout.CENTER);
            
            subtaxonomyOptionsPanel.add(descBtnPanel);
            
            JButton focusTaxonomyBtn = new JButton("Create Focus Subtaxonomy");
            
            focusTaxonomyBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Concept c = focusConcept.getConcept();

                    createAndDisplayFocusSubtaxonomy(localDS, c);
                }
            });
            
            JEditorPane focusSubtaxonomyDesc = new JEditorPane();
            focusSubtaxonomyDesc.setEditable(false);
            focusSubtaxonomyDesc.setContentType("text/html");
            focusSubtaxonomyDesc.setText("A <b>focus subtaxonomy</b> is a taxonomy derived using the chosen focus concept and all of its "
                    + "ancestors descendants. A focus subtaxonomy summarizes the ancestors and descendants of the chosen concept.");
            
            JPanel focusBtnPanel = new JPanel(new BorderLayout());
            focusBtnPanel.setBorder(BorderFactory.createEtchedBorder());
            focusBtnPanel.add(focusTaxonomyBtn, BorderLayout.NORTH);
            focusBtnPanel.add(focusSubtaxonomyDesc, BorderLayout.CENTER);
            
            subtaxonomyOptionsPanel.add(focusBtnPanel);
            
            tabbedPane.addTab("Create Focus Concept Subtaxonomy", subtaxonomyOptionsPanel);
        }
        
        

        focusConcept.addDisplayPanel(FocusConcept.Fields.TRIBALAN, tribalANPanel);
        focusConcept.addDisplayPanel(FocusConcept.Fields.PARTIALAREA, partialAreaPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void createAndDisplaySubjectSubtaxonomy(final SCTLocalDataSource dataSource, final Concept root) {
        Thread loadThread = new Thread(new Runnable() {
            private AbNLoadStatusDialog loadStatusDialog = null;

            public void run() {
                
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        loadStatusDialog = AbNLoadStatusDialog.display(null);
                    }
                });

                ArrayList<Concept> hierarchy = dataSource.getHierarchiesConceptBelongTo(root);

                SCTConceptHierarchy subhierarchy = (SCTConceptHierarchy) dataSource.getConceptHierarchy().getSubhierarchyRootedAt(root);

                SCTPAreaTaxonomyGenerator generator = new SCTPAreaTaxonomyGenerator(hierarchy.get(0), dataSource, subhierarchy, new InferredRelationshipsRetriever());

                   
                final SCTPAreaTaxonomy taxonomy = generator.derivePAreaTaxonomy();
                               

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mainPanel.getDisplayFrameListener().addNewPAreaGraphFrame(taxonomy, true, false);

                        if (loadStatusDialog != null) {
                            loadStatusDialog.setVisible(false);
                            loadStatusDialog.dispose();
                        }
                    }
                });

            }
        });

        loadThread.start();        
    }
    
    
    private void createAndDisplayFocusSubtaxonomy(final SCTLocalDataSource dataSource, final Concept focusConcept) {
        
        Thread loadThread = new Thread(new Runnable() {
            private AbNLoadStatusDialog loadStatusDialog = null;

            public void run() {
                
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        loadStatusDialog = AbNLoadStatusDialog.display(null);
                    }
                });

                ArrayList<Concept> hierarchy = dataSource.getHierarchiesConceptBelongTo(focusConcept);

                SCTConceptHierarchy conceptHierarchy = new SCTConceptHierarchy(hierarchy.get(0)); // Multi rooted partial-area taxonomy needs research...

                SCTConceptHierarchy descdendants = (SCTConceptHierarchy) dataSource.getConceptHierarchy().getSubhierarchyRootedAt(focusConcept);

                conceptHierarchy.addAllHierarchicalRelationships(descdendants);

                for (Concept hierarchyRoot : hierarchy) {
                    SCTConceptHierarchy ancestors = dataSource.getAncestorHierarchy(dataSource.getConceptHierarchy(), hierarchyRoot, focusConcept);
                    conceptHierarchy.addAllHierarchicalRelationships(ancestors);
                }

                SCTPAreaTaxonomyGenerator generator = new SCTPAreaTaxonomyGenerator(hierarchy.get(0), dataSource, conceptHierarchy, new InferredRelationshipsRetriever());

                final SCTPAreaTaxonomy taxonomy = generator.derivePAreaTaxonomy();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mainPanel.getDisplayFrameListener().addNewPAreaGraphFrame(taxonomy, true, false);

                        if (loadStatusDialog != null) {
                            loadStatusDialog.setVisible(false);
                            loadStatusDialog.dispose();
                        }
                    }
                });

            }
        });

        loadThread.start();        
    }

    private class PAreaTaxonomyDetailsPanel extends JPanel {

        private JLabel detailsLabel = new JLabel();
        private JPanel individualDetailsListPanel = new JPanel();
        
        private SCTPAreaTaxonomy pareaTaxonomy;

        public PAreaTaxonomyDetailsPanel() {
            this.setBackground(Color.WHITE);

            this.setLayout(new BorderLayout());

            this.add(detailsLabel, BorderLayout.NORTH);

            individualDetailsListPanel.setOpaque(false);

            individualDetailsListPanel.setVisible(false);
            
            individualDetailsListPanel.setLayout(new GridBagLayout());
            
            this.add(individualDetailsListPanel, BorderLayout.CENTER);
        }

        public void displayLabel(String text) {
            individualDetailsListPanel.setVisible(false);
            individualDetailsListPanel.removeAll();

            detailsLabel.setVisible(true);
            detailsLabel.setText(text);
        }

        public void displayDetails(SCTPAreaTaxonomy taxonomy, ArrayList<PAreaDetailsForConcept> details) {
            if (details.isEmpty()) {
                displayLabel("");
                return;
            }
            
            this.pareaTaxonomy = taxonomy;

            detailsLabel.setVisible(false);

            // TODO: There are 11 concepts that overlap between two hierarchies.
            PAreaDetailsForConcept firstDetail = details.get(0);

            String labelText = "<html>";
            labelText += "<b>Hierarchy:</b> " + firstDetail.getHierarchyRoot().getName() + "<br>";

            labelText += "<b>Area:</b> ";

            if (firstDetail.getPAreaRelationships().isEmpty()) {
                labelText += "&nbsp;&nbsp; None; root area.<br>";
            } else {
                ArrayList<String> sortedRelNames = new ArrayList<String>();
                
                for (OutgoingLateralRelationship rel : firstDetail.getPAreaRelationships()) {
                    String relName = rel.getRelationship().getName();
                    relName = relName.substring(0, relName.lastIndexOf("(") - 1);

                    sortedRelNames.add(relName);
                }
                
                Collections.sort(sortedRelNames);
                
                for(String relName : sortedRelNames) {
                    labelText += relName + ", ";
                }
                
                labelText = labelText.substring(0, labelText.length() - 2);
            }

            labelText += "<p>Partial-area(s):<p>";
            
            JPanel labelPanel = new JPanel(new BorderLayout());
            labelPanel.setOpaque(true);
            labelPanel.setBackground(Color.WHITE);
            
            labelPanel.add(new JLabel(labelText));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.PAGE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;

            individualDetailsListPanel.add(labelPanel, gbc);
            
            JPanel pareaDetailsPanel = new JPanel(new GridLayout(details.size(), 1));

            for (PAreaDetailsForConcept detail : details) {
                pareaDetailsPanel.add(new PAreaDetailsEntry(detail));
            }
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 1.0;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            
            individualDetailsListPanel.add(new JScrollPane(pareaDetailsPanel), gbc);
            
            individualDetailsListPanel.setVisible(true);
            
            this.repaint();
        }

        private class PAreaDetailsEntry extends JPanel {

            private PAreaDetailsForConcept details;

            public PAreaDetailsEntry(PAreaDetailsForConcept details) {
                this.details = details;
               
                this.setBackground(new Color(250, 250, 250));

                this.setOpaque(false);
                this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

                String labelText = "<html>&nbsp;" + details.getPAreaRoot().getName() + "<br>&nbsp;" + details.getConceptCount() + " concepts   ";

                final JButton btnSummary = new JButton("Details");
                final JButton btnView = new JButton("View");
                
                btnSummary.setMaximumSize(new Dimension(40, 30));
                
                btnSummary.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        btnSummary.setText("Loading...");
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                displayGroupSummary();
                                btnSummary.setText("Details");
                            }
                        });
                    }
                });
                
                btnView.setMaximumSize(new Dimension(40, 30));
                
                btnView.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        btnView.setText("Loading...");
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                displayAN();
                                btnView.setText("View");
                            }
                        });
                    }
                });

                this.add(new JLabel(labelText));
                this.add(btnSummary);
                this.add(Box.createHorizontalStrut(2));
                this.add(btnView);
            }
            
            private void displayGroupSummary() {
                ConceptGroupDetailsDialog dialog = new ConceptGroupDetailsDialog(
                        pareaTaxonomy.getPAreaFromRootConceptId(details.getPAreaRoot().getId()), 
                        pareaTaxonomy,
                        ConceptGroupDetailsDialog.DialogType.PartialArea,
                        mainPanel.getDisplayFrameListener());
            }

            private void displayAN() {
                PAreaInternalGraphFrame igf = mainPanel.getDisplayFrameListener().addNewPAreaGraphFrame(pareaTaxonomy, true, false);

                PAreaBluGraph graph = (PAreaBluGraph) igf.getGraph();

                igf.focusOnComponent(graph.getGroupEntries().get(graph.getPAreaTaxonomy().getPAreaFromRootConceptId(
                        details.getHierarchyRoot().getId()).getId()));
            }
        }
    }
}
