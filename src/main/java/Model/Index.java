package Model;

import javax.persistence.*;

@Entity
@Table(name = "lemma")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int pageId;

    private int lemmaId;

    private int rank;

    public Index () {

    }

}