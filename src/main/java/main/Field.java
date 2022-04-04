package main;

import javax.persistence.*;

@Entity
@Table(name = "_field")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String selector;

    @Column(name = "weight", columnDefinition = "FLOAT")
    private float weight;

    public Field () {

    }

    public Field(String name, String selector, float weight) {
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }
}
