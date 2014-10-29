package edu.njit.cs.saboc.blu.sno.utils.filterable.entry;

import SnomedShared.Concept;
import SnomedShared.SearchResult;

import edu.njit.cs.saboc.blu.core.utils.filterable.list.Filterable;
import edu.njit.cs.saboc.blu.sno.conceptbrowser.Options;

/**
 *
 * @author Chris
 */
public class FilterableSearchEntry extends Filterable {
    private SearchResult entry;
    private Options options;

    public FilterableSearchEntry(Options options, SearchResult entry) {
        this.entry = entry;
        this.options = options;
    }

    public Concept getNavigableConcept() {
        return options.getDataSource().getConceptFromId(entry.getConceptId());
    }

    public String getInitialText() {
        return String.format("<html>%s &nbsp;<font color='blue'>{%s}</font> " +
                "<font color='purple'>--%s</font>", entry.getTerm(),
                entry.getFullySpecifiedName(), Long.toString(entry.getConceptId()));
    }

    public String getFilterText(String filter) {
        return String.format("<html>%s &nbsp; <font color='blue'>{%s}</font> " +
                "<font color='purple'>--%s</font>", filter(entry.getTerm(), filter),
                filter(entry.getFullySpecifiedName(), filter),
                filter(Long.toString(entry.getConceptId()), filter));
    }
}
