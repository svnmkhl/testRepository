package Lemmatizator;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LemmatizatorTest {
    public static void main(String[] args) throws IOException {
       String text = "Попробуйте передать на вход программы несколько разных текстов и" +
               "проверьте, верно ли выдаётся список лемм Лемма леммы с количествами.";
       //List<String> wordsList = Arrays.asList(text.split(" "));
       List <String> wordsList = Arrays.asList(Arrays.stream(text.split(" ")).map(string ->
               string.toLowerCase().replaceAll("\\pP", "")).toArray(String[]::new));
       /*wordsList.forEach(word -> {
           try {
               Lemmatizator.getLemms(word.trim().toLowerCase().replaceAll("\\pP", ""));
               Lemmatizator.partOfLang(word.trim().toLowerCase().replaceAll("\\pP", ""));
               System.out.println("*******************************************************************");
           } catch (IOException e) {
               e.printStackTrace();
           }
       });
       //Lemmatizator.getLemmsAndCounts(wordsList);*/
    }
}
