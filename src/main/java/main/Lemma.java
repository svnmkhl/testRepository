package main;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "lemma")
public class Lemma {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private String lemma;

    private int frequency;

    @ManyToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable (name = "_index", joinColumns = {@JoinColumn (name = "lemma_id")},
            inverseJoinColumns = {@JoinColumn (name = "page_id")})
    private List<Page> pages = new ArrayList<>();

    public Lemma () {

    }

    public Lemma (String name, Integer frequency) {
        this.lemma = name;
        this.frequency = frequency;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages){
        this.pages = pages;
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
