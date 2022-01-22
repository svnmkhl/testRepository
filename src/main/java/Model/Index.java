package Model;

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

}