package Entity;

import javax.persistence.*;

@Entity
@Table(name = "_index")
public class Index {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;


    @Column (name = "page_Id")
    private int pageID;

    @Column (name = "lemma_Id")
    private int lemmaID;

    @Column(name = "_rank", columnDefinition = "FLOAT")
    private float rank;

    public Index () {

    }

    public Index (int pageID, int lemmaID, float rank) {
        this.pageID = pageID;
        this.lemmaID = lemmaID;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public int getLemmaID() {
        return lemmaID;
    }

    public void setLemmaID(int lemmaID) {
        this.lemmaID = lemmaID;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}