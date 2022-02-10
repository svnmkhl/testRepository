package Lemmatizator;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class LemmatizatorTest {
    public static void main(String[] args) throws IOException {
       String text = "Попробуйте не передать на вход программы несколько разных текстов и" +
               "проверьте, верно ли выдаётся список лемм Лемма леммы с количествами.";
       //List<String> wordsList = Arrays.asList(text.split(" "));
       Lemmatizator lemmatizator = new Lemmatizator();
       HashSet<String> partsOfSpeech = new HashSet<>();
       HashSet<String> resultPartsOfSpeech = new HashSet<>();
       List <String> wordsList = Arrays.asList(Arrays.stream(text.split(" ")).map(string ->
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
        resultPartsOfSpeech.forEach(word -> System.out.println(word));
       //Lemmatizator.getLemmsAndCounts(wordsList);*/
    }
}
