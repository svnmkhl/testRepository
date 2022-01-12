package Lemmatizator;

import Model.Lemma;
import Model.Page;
import Model.PageDAO;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.util.*;

public class Lemmatizator {
    private static LuceneMorphology luceneMorph;
    private static HashSet<Lemma> lemmsAndCounts = new HashSet<>();

    static {
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Lemmatizator() throws IOException {}


    public static List<String> getLemms(String word) throws IOException {
        List<String> wordBaseForms =
                luceneMorph.getNormalForms(word);
        return wordBaseForms;
    }

    public static List<String> partOfLang(String word) throws IOException {
        List<String> wordBaseForms =
                luceneMorph.getMorphInfo(word);
        return wordBaseForms;
    }

    public synchronized void collectLemmsAndCounts (Page page) {
        HashSet<String> setOfUniqueWordsFromPageText = new HashSet<>(Arrays.asList(page.getContent().toLowerCase().replaceAll("\\pP", " ").split(" ")));
        List<String> listOfUniqueWordsFromPageText = new ArrayList<>(setOfUniqueWordsFromPageText);
        int defaultLemmaFrequency = 1;
        for (int i = 0; i < listOfUniqueWordsFromPageText.size(); i ++) {
            try {
                if (listOfUniqueWordsFromPageText.get(i).equals("") || listOfUniqueWordsFromPageText.get(i).equals(" ")) {
                    continue;
                }
                List<String> lemmsList = getLemms(listOfUniqueWordsFromPageText.get(i).trim());
                List<Lemma> lemmaObjectList = new ArrayList<>();
                lemmsList.forEach(word ->
                        {
                            Lemma lemma = new Lemma(word, defaultLemmaFrequency);
                            List<Page> pages = new ArrayList<>();
                            pages.add(page);
                            lemma.setPages(pages);
                            lemmaObjectList.add(lemma);
                        });
                if (lemmsAndCounts.isEmpty()) {
                    lemmsAndCounts.addAll(lemmaObjectList);
                } else {
                    for (int j = 0; j < lemmaObjectList.size(); j ++) {
                        for (Lemma lemma : lemmsAndCounts) {
                            if (lemmaObjectList.get(j).getName().equals(lemma.getName())) {
                                lemma.setFrequency(lemma.getFrequency() + 1);
                                lemma.addPage(lemmaObjectList.get(j).getPages());
                            } else {
                                lemmsAndCounts.add(lemmaObjectList.get(j));
                            }
                        }
                    }
                }
            } catch (WrongCharaterException | IOException ex) {
                continue;
            }
        }
    }
    public static HashSet<Lemma> getLemmsAndCounts() {
        return lemmsAndCounts;
    }
}
