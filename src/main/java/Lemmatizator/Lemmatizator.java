package Lemmatizator;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public void collectLemmsAndCounts (List<String> pageText) {

        int count = 1;
        for (int i = 0; i < pageText.size(); i ++) {
            try {
                if (pageText.get(i).equals("")) {
                    continue;
                }
                List<String> lemms = getLemms(pageText.get(i).trim().toLowerCase().replaceAll("\\pP", ""));
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
            }catch (WrongCharaterException | IOException ex) {
                continue;
            }
        }
    }
    public static HashMap<String, Integer> getLemmsAndCounts() {
        return lemmsAndCounts;
    }
}
