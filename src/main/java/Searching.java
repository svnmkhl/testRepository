import Lemmatizator.Lemmatizator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Searching
{

    public Searching()
    {
    }

    public static HashSet<String> search (String userRequest) throws IOException {
        Lemmatizator lemmatizator = new Lemmatizator();
        HashSet<String> partsOfSpeech = new HashSet<>();
        HashSet<String> resultPartsOfSpeech = new HashSet<>();
        List <String> wordsList = Arrays.asList(Arrays.stream(userRequest.split(" ")).map(string ->
                string.toLowerCase().replaceAll("\\pP", "")).toArray(String[]::new));
        wordsList.forEach(word -> {
            try {
                lemmatizator.getLemms(word.trim().toLowerCase().replaceAll("\\pP", ""));
                System.out.println("*******************************************************************");
                partsOfSpeech.addAll(lemmatizator.partOfLang(word.trim().toLowerCase().replaceAll("\\pP", "")));
                System.out.println(partsOfSpeech);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        partsOfSpeech.forEach(word -> {
            if (word.contains("ПРЕДЛ") || word.contains("ЧАСТ") || word.contains("СОЮЗ")) {
            } else {
                resultPartsOfSpeech.add(word);
            }
        });
        return resultPartsOfSpeech;
    }

}
