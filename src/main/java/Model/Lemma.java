package Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "lemma")
public class Lemma {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private String lemma;

    private int frequency;

    @ManyToMany (cascade = CascadeType.ALL)
    @JoinTable (name = "index", joinColumns = {@JoinColumn (name = "page_id")}, inverseJoinColumns =
            {@JoinColumn (name = "lemma_id")})
    private List<Page> pages;

    public Lemma () {

    }

    public Lemma (String name, Integer frequency) {
        this.lemma = name;
        this.frequency = frequency;
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

    public List<Page> getPages() {
        return pages;
    }

    public void addPage (List<Page> pages) {
        this.pages.addAll(pages);
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

}
