package edu.njit.cs.saboc.blu.sno.gui.gep.painter;

import edu.njit.cs.saboc.blu.core.graph.nodes.PartitionedNodeEntry;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.pareataxonomy.AggregatePAreaTaxonomyPainter;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Chris O
 */
public class SCTAggregateTaxonomyPainter extends AggregatePAreaTaxonomyPainter {
    public void paintPartitionedNodeAtPoint(Graphics2D g2d, PartitionedNodeEntry entry, Point p, double scale) {
        SCTTaxonomyPainter painter = new SCTTaxonomyPainter();
        painter.paintPartitionedNodeAtPoint(g2d, entry, p, scale);
    }
}
