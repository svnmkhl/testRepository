package Entity;

import javax.persistence.*;

@Entity
@Table (name = "lemma")
public class Lemma {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private String lemma;

    private int frequency;

    public Lemma () {

    }

    public Lemma (String name, Integer frequency) {
        this.lemma = name;
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return lemma;
    }

    public void setName(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setId(int id) {this.id = id;}

}
