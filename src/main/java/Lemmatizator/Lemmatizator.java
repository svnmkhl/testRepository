package Lemmatizator;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.util.*;

public class Lemmatizator {
    private static LuceneMorphology luceneMorph;
    private static HashMap<String, Integer> lemmsAndCounts = new HashMap<>();

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

    public void collectLemmsAndCounts (HashSet<String> uniqueWordsFromPageText) {
        List<String> uniqueWordsFromPageTextList = new ArrayList<>(uniqueWordsFromPageText);
        int count = 1;
        for (int i = 0; i < uniqueWordsFromPageTextList.size(); i ++) {
            try {
                if (uniqueWordsFromPageTextList.get(i).equals("")) {
                    continue;
                }
                List<String> lemms = getLemms(uniqueWordsFromPageTextList.get(i).trim().toLowerCase().replaceAll("\\pP", ""));
                synchronized (lemmsAndCounts)
                {
                    lemms.forEach(lemma -> {
                        if (!lemmsAndCounts.keySet().contains(lemma)) {
                            lemmsAndCounts.put(lemma, count);
                        } else {
                            int newCount = lemmsAndCounts.get(lemma) + 1;
                            lemmsAndCounts.put(lemma, newCount);
                        }
                    });
                }
            } catch (WrongCharaterException | IOException ex) {
                continue;
            }
        }
    }
    public static HashMap<String, Integer> getLemmsAndCounts() {
        return lemmsAndCounts;
    }
}
