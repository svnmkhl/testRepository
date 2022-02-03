package Entity;

import javax.persistence.*;

@Entity
@Table(name = "_index")
public class Index {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "page_id")
    private int pageId;

    @Column(name = "lemma_id")
    private int lemmaId;

    @Column(name = "_rank", columnDefinition = "FLOAT")
    private float rank;

    public Index () {

    }

    public Index (int pageId, int lemmaId, float rank) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public int getPageId() {
        return pageId;
    }

    public int getLemmaId() {
        return lemmaId;
    }

    public float getRank() {
        return rank;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}