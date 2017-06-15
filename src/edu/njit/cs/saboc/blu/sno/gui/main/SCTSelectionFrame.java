package edu.njit.cs.saboc.blu.sno.gui.main;

import edu.njit.cs.saboc.blu.core.gui.frame.AbnSelectionFrameFactory;
import edu.njit.cs.saboc.blu.core.gui.frame.OAFMainFrame;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.DisjointAbNWarningManager;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTAbNFrameManager;
import edu.njit.cs.saboc.blu.sno.gui.abnselection.SCTMainPanel;
import javax.swing.JInternalFrame;

/**
 *
 * @author hl395
 */
public class SCTSelectionFrame implements AbnSelectionFrameFactory {

    private final OAFStateFileManager stateFileManager = new OAFStateFileManager("BLUSNO");
    private final AbNWarningManager warningManager = new DisjointAbNWarningManager();
    
    @Override
    public JInternalFrame createAbNSelectionFrame(OAFMainFrame parentFrame) {
        
        JInternalFrame internalFrame = new JInternalFrame();
        
        internalFrame.setSize(1400, 700);

        SCTAbNFrameManager sctFrameManager = new SCTAbNFrameManager(parentFrame, (frame) -> {
                    parentFrame.addInternalFrame(frame);
                }, stateFileManager, warningManager);

        SCTMainPanel sctMainPanel = new SCTMainPanel(sctFrameManager, stateFileManager);

        internalFrame.add(sctMainPanel);
        internalFrame.setVisible(true);
        
        return internalFrame;
    }
}
