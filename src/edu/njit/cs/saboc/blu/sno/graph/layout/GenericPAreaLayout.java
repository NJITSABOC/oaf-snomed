package edu.njit.cs.saboc.blu.sno.graph.layout;

import SnomedShared.generic.GenericContainerPartition;
import SnomedShared.pareataxonomy.InheritedRelationship;
import SnomedShared.pareataxonomy.InheritedRelationship.InheritanceType;
import edu.njit.cs.saboc.blu.core.graph.BluGraph;
import edu.njit.cs.saboc.blu.core.graph.edges.GraphEdge;
import edu.njit.cs.saboc.blu.core.graph.edges.GraphGroupLevel;
import edu.njit.cs.saboc.blu.core.graph.edges.GraphLevel;
import edu.njit.cs.saboc.blu.core.graph.layout.BluGraphLayout;
import edu.njit.cs.saboc.blu.core.graph.nodes.SinglyRootedNodeEntry;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTArea;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPArea;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTPAreaTaxonomy;
import edu.njit.cs.saboc.blu.sno.abn.pareataxonomy.local.SCTRegion;
import edu.njit.cs.saboc.blu.sno.graph.pareataxonomy.BluArea;
import edu.njit.cs.saboc.blu.sno.graph.pareataxonomy.BluPArea;
import edu.njit.cs.saboc.blu.sno.graph.pareataxonomy.BluRegion;
import edu.njit.cs.saboc.blu.sno.sctdatasource.middlewareproxy.MiddlewareAccessorProxy;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.swing.JLabel;

/**
 *
 * @author Chris
 */
public abstract class GenericPAreaLayout extends BluGraphLayout<SCTArea, BluArea, BluPArea> {

    protected SCTPAreaTaxonomy pareaTaxonomy;
    
    protected boolean isRegionLayout;
   
    protected GenericPAreaLayout(BluGraph graph, SCTPAreaTaxonomy pareaTaxonomy, boolean isRegionLayout) {
        super(graph);
        
        this.isRegionLayout = isRegionLayout;
        this.pareaTaxonomy = pareaTaxonomy;
    }

    public void doLayout() {
        ArrayList<SCTArea> sortedAreas = new ArrayList<SCTArea>();    // Used for generating the graph
        ArrayList<SCTArea> levelAreas = new ArrayList<SCTArea>();     // Used for generating the graph

        ArrayList<SCTArea> tempAreas = pareaTaxonomy.getHierarchyAreas();

        SCTArea lastArea = null;

        Collections.sort(tempAreas, new Comparator<SCTArea>() {    // Sort the areas based on the number of their relationships.

            public int compare(SCTArea a, SCTArea b) {
                if (a.getRelationships() == null || b.getRelationships() == null) {
                    return 0;
                }

                if (a.getRelationships().size() > b.getRelationships().size()) {
                    return 1;
                } else if (a.getRelationships().size() < b.getRelationships().size()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (SCTArea a : tempAreas) {
            if (lastArea != null && lastArea.getRelationships().size() != a.getRelationships().size()) {
                Collections.sort(levelAreas, new Comparator<SCTArea>() {    // Sort the areas based on the number of their relationships.

                    public int compare(SCTArea a, SCTArea b) {
                        if (a.getAllPAreas().size() > b.getAllPAreas().size()) {
                            return 1;
                        } else if (a.getAllPAreas().size() < b.getAllPAreas().size()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

                int c = 0;

                for (c = 0; c < levelAreas.size(); c += 2) {
                    sortedAreas.add(levelAreas.get(c));
                }

                if (levelAreas.size() % 2 == 0) {
                    c = levelAreas.size() - 1;
                } else {
                    c = levelAreas.size() - 2;
                }

                for (; c >= 1; c -= 2) {
                    sortedAreas.add(levelAreas.get(c));
                }

                levelAreas.clear();
            }

            levelAreas.add(a);

            lastArea = a;
        }

        Collections.sort(levelAreas, new Comparator<SCTArea>() {    // Sort the areas based on the number of their relationships.

            public int compare(SCTArea a, SCTArea b) {
                if (a.getAllPAreas().size() > b.getAllPAreas().size()) {
                    return 1;
                } else if (a.getAllPAreas().size() < b.getAllPAreas().size()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        int c = 0;

        for (c = 0; c < levelAreas.size(); c += 2) {
            sortedAreas.add(levelAreas.get(c));
        }

        if (levelAreas.size() % 2 == 0) {
            c = levelAreas.size() - 1;
        } else {
            c = levelAreas.size() - 2;
        }

        for (; c >= 1; c -= 2) {
            sortedAreas.add(levelAreas.get(c));
        }

        lastArea = null;
        layoutGroupContainers = sortedAreas;
    }
    
    public ArrayList<SCTArea> getLayoutAreas() {
        return layoutGroupContainers;
    }

    /**
     * Creates a JPanel representing a pArea, adds it to the graph, and returns it.
     * @param p The data object for this pArea
     * @param parent The region which this pArea is being put inside
     * @param x The x coordinate of the pArea
     * @param y The y coordinate of the pArea
     * @param pAreaX The horizontal index of this pArea within a pArea level (the pArea all the way on the left in a row of pAreas in a region is index 0, etc.)
     * @param pAreaLevel The object representing the pArea level this pArea will be added to.
     * @return A BluPArea object representing the newly created JPanel for this pArea
     */
    protected BluPArea createPAreaPanel(SCTPArea p, BluRegion parent, int x, int y, int pAreaX, GraphGroupLevel pAreaLevel) {
        return PAreaTaxonomyLayoutUtils.createPAreaPanel(graph, p, parent, x, y, pAreaX, pAreaLevel);
    }

    /**
     * Creates a JPanel representing an Area, adds it to the graph, and returns it.
     * @param a The data object representing this area.
     * @param x The x coordinate of the pArea
     * @param y The y coordinate of the pArea
     * @param width The width of this area in pixels.
     * @param height The height of this area in pixels.
     * @param c The background color for this area.
     * @param areaX The horizontal index of this area within a level (the area all the way on the left in a row of areas in a region is index 0, etc.)
     * @param parentLevel The object representing the level this area will be added to.
     * @return A BluArea object representing the newly created JPanel for this area.
     */
    protected BluArea createAreaPanel(SCTArea a, int x, int y, int width, int height, Color c, int areaX, GraphLevel parentLevel) {
        return PAreaTaxonomyLayoutUtils.createAreaPanel(graph, a, x, y, width, height, c, areaX, parentLevel);
    }

    /**
     * Creates a JPanel representing a region, adds it to the graph, and returns it.
     * @param region The data object representing this region.
     * @param regionName The title for this region.
     * @param ap The parent area this region is inside.
     * @param x The x-coordinate where this region is to be positioned.
     * @param y The y-coordinate where this region is to be positioned.
     * @param width The width of this region in pixels.
     * @param height The height of this region in pixels.
     * @param c The background color of this region.
     * @return A BluRegion object representing the newly created JPanel for this region.
     */
    protected BluRegion createRegionPanel(SCTRegion region, String regionName, 
            BluArea ap, int x, int y, int width, int height, Color c, boolean treatRegionAsArea, JLabel regionLabel) {

        return PAreaTaxonomyLayoutUtils.createRegionPanel(graph, region, regionName, ap, x, y, width, height, c, treatRegionAsArea, regionLabel);
    }

     /**
     * Returns an area from a given level in this graph.
     * @param level Specifies the level at which to retrieve the area
     * @param areaX Specifies which area to retrieve in the level (where the first area in the level is index 0)
     * @return The area at that level and horizontal position.
     */
    public BluArea getArea(int level, int areaX) {
        return (BluArea)getConainterAt(level, areaX);
    }

    /**
     * Returns a region inside a given area in this graph.
     * @param level Specifies the level at which to retrieve the area
     * @param areaX Specifies which area to retrieve in the level (where the first area in the level is index 0)
     * @param regionX Specifies which region to retrieve in the area (where, again, the first region in the area is index 0)
     * @return The region at the given position.
     */
    public BluRegion getRegion(int level, int areaX, int regionX) {
        return (BluRegion)getContainerPartitionAt(level, areaX, regionX);
    }

    /**
     * Returns a pArea inside a given region in this graph.
     * @param level Specifies the level at which to retrieve the area
     * @param areaX Specifies which area to retrieve in the level (where the first area in the level is index 0)
     * @param regionX Specifies which region to retrieve in the area (where, again, the first region in the area is 0)
     * @param pAreaY Specifies which level of pAreas to retrieve it from (where the first level is index 0)
     * @param pAreaX Specifies which pArea to retrieve in a given pArea level (where the first pArea in the level is index 0).
     * @return
     */
    public BluPArea getPArea(int level, int areaX, int regionX, int pAreaY, int pAreaX) {
        return (BluPArea)getGroupEntry(level, areaX, regionX, pAreaY, pAreaX);
    }
    
    protected JLabel createRegionLabel(SCTPAreaTaxonomy taxonomy, ArrayList<InheritedRelationship> relationships, String countString, int width, boolean treatAsArea) {
       
        Canvas canvas = new Canvas();
        FontMetrics fontMetrics = canvas.getFontMetrics(new Font("SansSerif", Font.BOLD, 14));
        
        HashMap<Long, String> relAbbrevs;
        
        if(pareaTaxonomy instanceof SCTPAreaTaxonomy) {
            relAbbrevs = pareaTaxonomy.getLateralRelsInHierarchy();
        } else {
            relAbbrevs = MiddlewareAccessorProxy.getProxy().getRelationshipAbbreviations(pareaTaxonomy.getSCTVersion());
        }
        
        String[] entries;

        if (relationships.isEmpty()) {
            entries = new String[]{"\u2205", countString};
        } else {
            entries = new String[relationships.size()];

            int c = 0;

            int longestRelNameWidth = -1;

            for (InheritedRelationship rel : relationships) {
                String relName = relAbbrevs.get(rel.getRelationshipTypeId());

                if (!treatAsArea) {
                    if (rel.getInheritanceType() == InheritanceType.INHERITED) {
                        relName += "*";
                    } else {
                        relName += "+";
                    }
                }

                int relNameWidth = fontMetrics.stringWidth(relName);

                if (relNameWidth > longestRelNameWidth) {
                    longestRelNameWidth = relNameWidth;
                }

                entries[c++] = relName;
            }

            Arrays.sort(entries);
            
            entries = Arrays.copyOf(entries, entries.length + 1);

            entries[entries.length - 1] = countString;

            if (fontMetrics.stringWidth(countString) > longestRelNameWidth) {
                longestRelNameWidth = fontMetrics.stringWidth(countString);
            }

            if (relationships.size() > 1) {
                longestRelNameWidth += fontMetrics.charWidth(',');
            }

            if (!treatAsArea) {
                longestRelNameWidth += fontMetrics.charWidth('+');
            }

            if (longestRelNameWidth > width) {
                width = longestRelNameWidth + 4;
            }

        }
        
        return this.createFittedPartitionLabel(entries, width, fontMetrics);
    }
    
    public JLabel createPartitionLabel(GenericContainerPartition partition, int width) {
        SCTRegion region = (SCTRegion)partition;
        
        String countStr;
        
        if (graph.showingConceptCountLabels()) {
            int conceptCount = pareaTaxonomy.getDataSource().getConceptCountInPAreaHierarchy(pareaTaxonomy, region.getPAreasInRegion());

            if (conceptCount == 1) {
                countStr = "(1 Concept)";
            } else {
                countStr = String.format("(%d Concepts)", conceptCount);
            }
        } else {
            if (region.getPAreasInRegion().size() == 1) {
                countStr = "(1 Partial-area)";
            } else {
                countStr = String.format("(%d Partial-areas)", region.getPAreasInRegion().size());
            }
        }
        
        ArrayList<InheritedRelationship> regionRels = new ArrayList<InheritedRelationship>(region.getPAreasInRegion().get(0).getRelationships());
        
        return this.createRegionLabel(pareaTaxonomy, regionRels, countStr, width, !this.isRegionLayout);
    }
}
