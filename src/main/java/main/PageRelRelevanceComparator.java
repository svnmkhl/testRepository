package main;

import java.util.Comparator;

public class PageRelRelevanceComparator implements Comparator<PageWithRelevance>
{
    @Override
    public int compare(PageWithRelevance page1, PageWithRelevance page2) {
        if(page1.getRelativeRelevance() > page2.getRelativeRelevance()) {
            return -1;
        }
        if(page1.getRelativeRelevance() < page2.getRelativeRelevance()) {
            return 1;
        }
            return 0;
        }
    }