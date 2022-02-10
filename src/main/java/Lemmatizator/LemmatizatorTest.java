package Lemmatizator;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LemmatizatorTest {
    public static void main(String[] args) throws IOException {
       String text = "Попробуйте передать на вход программы несколько разных текстов и" +
               "проверьте, верно ли выдаётся список лемм Лемма леммы с количествами.";
       //List<String> wordsList = Arrays.asList(text.split(" "));
       Lemmatizator lemmatizator = new Lemmatizator();

       List <String> wordsList = Arrays.asList(Arrays.stream(text.split(" ")).map(string ->
               string.toLowerCase().replaceAll("\\pP", "")).toArray(String[]::new));
       wordsList.forEach(word -> {
           try {
               lemmatizator.getLemms(word.trim().toLowerCase().replaceAll("\\pP", ""));
               lemmatizator.partOfLang(word.trim().toLowerCase().replaceAll("\\pP", ""));
               System.out.println("*******************************************************************");
           } catch (IOException e) {
               e.printStackTrace();
           }
       });
       //Lemmatizator.getLemmsAndCounts(wordsList);*/
    }
}
