package edu.njit.cs.saboc.blu.sno.gui.abnselection;

import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.createanddisplay.CreateAndDisplaySCTNAT;
import edu.njit.cs.saboc.blu.sno.sctdatasource.SCTRelease;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Panel for creating abstraction networks from a SNOMED CT release.
 * 
 * @author Chris O
 */
public class SCTAbNCreationPanel extends JPanel {
    
    private Optional<SCTRelease> optCurrentRelease = Optional.empty();
    
    private final SCTAbNWizardPanel wizardPanel;
    
    private JButton openBrowserBtn;
    
    private final SCTAbNFrameManager frameManager;
    
    private final OAFStateFileManager stateFileManager;
    
    public SCTAbNCreationPanel(
            SCTAbNFrameManager frameManager, 
            OAFStateFileManager stateFileManager) {
        
        super(new BorderLayout());
        
        this.frameManager = frameManager;
        this.stateFileManager = stateFileManager;
        
        this.wizardPanel = new SCTAbNWizardPanel(stateFileManager, frameManager, true);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
                
        JPanel browserPanel = new JPanel(new BorderLayout());
        browserPanel.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);
        browserPanel.add(createConceptBrowserPanel(), BorderLayout.EAST);
                
        centerPanel.add(wizardPanel, BorderLayout.CENTER);
        centerPanel.add(browserPanel, BorderLayout.EAST);

        this.add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        
        this.wizardPanel.setEnabled(value);
        this.openBrowserBtn.setEnabled(value);
    }
    
    public void setCurrentRelease(SCTRelease release) {
        this.optCurrentRelease = Optional.of(release);

        this.wizardPanel.setCurrentRelease(release);
    }

    public void clear() {
       this.optCurrentRelease = Optional.empty();
       
       this.wizardPanel.clear();
    }
    
    public void resetView() {
        
    }
    
    private JPanel createConceptBrowserPanel() {
        JPanel browserPanel = new JPanel(new BorderLayout());
        browserPanel.setPreferredSize(new Dimension(250, -1));
        
        browserPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "Concept Browser"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        openBrowserBtn = new JButton("<html><div align='center'>Open<br>Concept<br>Browser"
                + "<br><b>(NAT)</b>");

        openBrowserBtn.addActionListener( (ae) -> {
            openConceptBrowser();
        });

        openBrowserBtn.setEnabled(false);

        topPanel.add(openBrowserBtn, BorderLayout.CENTER);
        topPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JEditorPane detailsPane = new JEditorPane();
        detailsPane.setContentType("text/html");

        String detailsString = "<html><div align='justify'>The OAF SNOMED CT "
                + "concept browser allows you to browse individual concepts and their "
                + "relationships. <p> This is a \"Hybrid Browser\" that shows text lines in a layout. "
                + "<p> This is the second generation of the <b>NAT (Neighborhood Auditing Tool)</b>.";

        detailsPane.setText(detailsString);

        bottomPanel.add(detailsPane, BorderLayout.CENTER);

        browserPanel.add(topPanel, BorderLayout.NORTH);
        browserPanel.add(bottomPanel, BorderLayout.CENTER);

        return browserPanel;
    }
    
    private void openConceptBrowser() {
        if (!optCurrentRelease.isPresent()) {
            JOptionPane.showMessageDialog(
                    null, 
                    "Please open a SNOMED CT release.",
                    "No Local Release Opened", JOptionPane.ERROR_MESSAGE);

            return;
        }

        CreateAndDisplaySCTNAT createNATDialog = new CreateAndDisplaySCTNAT(frameManager, optCurrentRelease.get());
        createNATDialog.run();
    }
}
