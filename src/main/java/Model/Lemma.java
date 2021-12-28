package Model;

import javax.persistence.*;

@Entity
@Table (name = "lemma")
public class Lemma {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int frequency;

    public String getName() {
        return name;
    }

    public void setName(String lemma) {
        this.name = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
