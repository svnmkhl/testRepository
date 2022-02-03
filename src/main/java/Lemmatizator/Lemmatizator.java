package Lemmatizator;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.util.*;

public class Lemmatizator {
    private static LuceneMorphology luceneMorph;
    private HashMap<String, Integer> lemmsAndCounts = new HashMap<>();
    private String textToLemms;

    static {
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Lemmatizator() throws IOException {}

    public Lemmatizator(String textToLemms) throws IOException{
        this.textToLemms = textToLemms;
    }


    public List<String> getLemms(String text) throws IOException {
        List<String> wordBaseForms =
                luceneMorph.getNormalForms(text);
        return wordBaseForms;
    }

    public List<String> partOfLang() throws IOException {
        List<String> wordBaseForms =
                luceneMorph.getMorphInfo(textToLemms);
        return wordBaseForms;
    }

    public void collectLemmsAndCounts () {
        HashSet<String> setOfUniqueWordsFromPageText = new HashSet<>(Arrays.asList(textToLemms.toLowerCase().replaceAll("\\pP", " ").split(" ")));
        List<String> listOfUniqueWordsFromPageText = new ArrayList<>(setOfUniqueWordsFromPageText);
        final int DEFAULT_LEMMA_FREQUENCY = 1;
        for (int i = 0; i < listOfUniqueWordsFromPageText.size(); i ++) {
            try {
                if (listOfUniqueWordsFromPageText.get(i).equals("") || listOfUniqueWordsFromPageText.get(i).equals(" ")) {
                    continue;
                }

                List<String> lemms = getLemms(listOfUniqueWordsFromPageText.get(i).trim());
                lemms.forEach(lemma -> {
                    if (!lemmsAndCounts.keySet().contains(lemma)) {
                        lemmsAndCounts.put(lemma, DEFAULT_LEMMA_FREQUENCY);
                    } else {
                        int newCount = lemmsAndCounts.get(lemma) + 1;
                        lemmsAndCounts.put(lemma, newCount);
                    }
                });
            } catch (WrongCharaterException | IOException ex) {
                continue;
            }
        }
    }
    public HashMap<String, Integer> getLemmsAndCounts() {
        return lemmsAndCounts;
    }
}
