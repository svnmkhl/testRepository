package Pojo;

import java.io.IOException;
import java.util.List;

public class PageWithRelevance {
    private String uri;
    float relativeRelevance;
    String titleText;
    List<String> snippet;
    float absRelevance;

    public PageWithRelevance(String uri, String titleText, List<String> snippet, float absRelevance) throws IOException {
        this.uri = uri;
        this.absRelevance = absRelevance;
        this.titleText = titleText;
        this.snippet = snippet;

    }

    public float getAbsRelevance() {
        return absRelevance;
    }

    public float getRelativeRelevance() {
        return relativeRelevance;
    }

    public void setRelativeRelevance(float relativeRelevance) {
        this.relativeRelevance = relativeRelevance;
    }

    public String getUri() {
        return uri;
    }
}
