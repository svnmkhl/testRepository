import DAO.HibernateSessionFactoryCreator;
import DAO.LemmaDAO;
import Entity.Lemma;
import Entity.Page;
import Lemmatizator.Lemmatizator;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SearchingEngine
{

    public SearchingEngine() {
    }

    public static List<Page> search (String searchingRequest) throws IOException {
        Lemmatizator lemmatizator = new Lemmatizator();
        HashSet<String> partsOfSpeech = new HashSet<>();
        HashSet<String> resultWordList = new HashSet<>();
        List <String> wordsList = Arrays.asList(Arrays.stream(searchingRequest.split(" ")).map(string ->
                string.toLowerCase().replaceAll("\\pP", "")).toArray(String[]::new));

        //Определяем часть речи для каждого слова нашего запроса попутно очищая слова от ненужных служебных символов
        wordsList.forEach(word -> {
            try {
                partsOfSpeech.addAll(lemmatizator.partOfLang(word.trim().toLowerCase().replaceAll("\\pP", "")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Отфильтровываем предлоги, частицы, союзы и междометия и формируем окончательный список (имён) лемм из слов нашего запроса
        System.out.println(partsOfSpeech + "\n");
        partsOfSpeech.forEach(word -> {
            if (word.contains("ПРЕДЛ") || word.contains("ЧАСТ") || word.contains("СОЮЗ") || word.contains("МЕЖД")) {

            } else {
                resultWordList.add(word.substring(0, word.indexOf("|")));
            }
        });

        //Получаем окончательный список лемм (объектов) по их именам, полученным выше
        List<Lemma> lemmList = new ArrayList<>();
        for (String word : resultWordList) {
            List <Lemma> temp = LemmaDAO.findByName(word);
            //Здесь ещё нужно будет добавить критерий фильтрации лемм, которые встречаются на слишком большом количестве страниц. Эти леммы не нужно включать в результат поиска
            if (!temp.isEmpty()) {
                lemmList.addAll(temp);
            } else {
                continue;
            }
        }

        lemmList.sort(new LemmaFrequencyComparator());

        List<Page> resultPageList = new ArrayList<>();
        for (int i = 0; i < lemmList.size(); i ++) {
            List<Page> currentLemmaPageList = new ArrayList<>(lemmList.get(i).getPages());
            if (i == 0) {
                resultPageList.addAll(currentLemmaPageList);
            } else {
                resultPageList = comparePageLists(resultPageList, currentLemmaPageList);
            }

        }
        return resultPageList;
    }

    private static List<Page> comparePageLists(List<Page> resultPageList, List<Page> currentLemmaPageList) {
        List<Page> result = new ArrayList<>();
        for (int i = 0; i < resultPageList.size(); i ++) {
            for (int j = 0; j < currentLemmaPageList.size(); j ++) {
                if (resultPageList.get(i).getPath().equals(currentLemmaPageList.get(j).getPath())) {
                    result.add(currentLemmaPageList.get(j));
                }
            }
        }
        return result;
    }
}
