import Comparators.LemmaFrequencyComparator;
import Comparators.PageRelRelevanceComparator;
import DAO.IndexDAO;
import DAO.LemmaDAO;
import Entity.Index;
import Entity.Lemma;
import Entity.Page;
import Lemmatizator.Lemmatizator;
import Pojo.PageWithRelevance;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchingEngine
{
    private static String userRequest;
    public SearchingEngine() {
    }

    public static synchronized List<PageWithRelevance> search (String searchingRequest) throws IOException, ParserConfigurationException {
        userRequest = searchingRequest;
        Lemmatizator lemmatizator = new Lemmatizator();
        HashSet<String> partsOfSpeech = new HashSet<>();
        HashSet<String> resultWordList = new HashSet<>();
        List <String> wordsList = Arrays.asList(Arrays.stream(userRequest.split(" ")).map(string ->
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
        List<PageWithRelevance> resultPageWithRelevanceList = new ArrayList<>();
        for (int i = 0; i < lemmList.size(); i ++) {
            List<Page> currentLemmaPageList = new ArrayList<>(lemmList.get(i).getPages());
            if (i == 0) {
                resultPageList.addAll(currentLemmaPageList);
            } else {
                resultPageList = comparePageLists(resultPageList, currentLemmaPageList);
            }

        }
        if (resultPageList == null) {
            return null;
        } else {
            resultPageWithRelevanceList = calculateRelevance(resultPageList, lemmList);
            resultPageWithRelevanceList.sort(new PageRelRelevanceComparator());
            return resultPageWithRelevanceList;
        }
    }

    private static synchronized List<Page> comparePageLists(List<Page> resultPageList, List<Page> currentLemmaPageList) {
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

    private static synchronized List<PageWithRelevance> calculateRelevance(List<Page> pageList, List<Lemma> lemmaList) throws IOException, ParserConfigurationException {
        List<PageWithRelevance> pageWithRelevanceList = new ArrayList<>();
        for (int i = 0; i < pageList.size(); i ++) {
            float absRelevance = 0.00f;
            for (int j = 0; j < lemmaList.size(); j ++) {
                Index index = IndexDAO.findByPageAndLemmaId(pageList.get(i).getId(), lemmaList.get(j).getId()).get(0);
                absRelevance = absRelevance + index.getRank();
            }
            String content = pageList.get(i).getContent();
            pageWithRelevanceList.add(new PageWithRelevance(pageList.get(i).getPath(), getTitleValue(content), getSnippet(content, userRequest), absRelevance));
        }
        return calculateRelativeRelevance(pageWithRelevanceList);

    }

    private static synchronized List<PageWithRelevance> calculateRelativeRelevance(List<PageWithRelevance> pageWithRelevanceList) {
        float maxRelevance = 0.00f;
        for (PageWithRelevance pageWithRelevance : pageWithRelevanceList) {
            if (pageWithRelevance.getAbsRelevance() > maxRelevance) {
                maxRelevance = pageWithRelevance.getAbsRelevance();
            }
        }
        for (PageWithRelevance pageWithRelevance : pageWithRelevanceList) {
            pageWithRelevance.setRelativeRelevance(pageWithRelevance.getAbsRelevance() / maxRelevance);
        }
        return pageWithRelevanceList;
    }

    private static String getTitleValue (String str) {
        final Pattern pattern = Pattern.compile("<title>(.+?)</title>");
        final Matcher matcher = pattern.matcher(str);
        matcher.find();
        return matcher.group();
    }

    private static synchronized List<String> getSnippet (String content, String wantedText) throws ParserConfigurationException {
        int start = 0;
        int end = 0;
        Document document = Jsoup.parse(content);
        List<String> splittedWantedText = Arrays.asList(wantedText.split(" "));
        List<String> splittedOnSentencesContentLinkedList = new LinkedList<>(Arrays.asList(document.select("body").text().split("[.!?]")));
        List<String> snippet = new ArrayList<>();
        for (int i = 0; i < splittedWantedText.size(); i++) {
            for (int j = 0; j < splittedOnSentencesContentLinkedList.size(); j ++) {
                if (splittedOnSentencesContentLinkedList.get(j).toLowerCase().contains(splittedWantedText.get(i).toLowerCase()) && start == 0) {
                    splittedOnSentencesContentLinkedList = addBoldTextTag(splittedOnSentencesContentLinkedList, splittedWantedText, i, j);
                    start = j;
                    end = j;
                } else if (splittedOnSentencesContentLinkedList.get(j).toLowerCase().contains(splittedWantedText.get(i).toLowerCase()) && start != 0) {
                    splittedOnSentencesContentLinkedList = addBoldTextTag(splittedOnSentencesContentLinkedList, splittedWantedText, i, j);
                    end = j;
                }
            }
        }
        for (int i = start; i <= end; i ++) {
            snippet.add(splittedOnSentencesContentLinkedList.get(i).replaceAll("[\\[\\]]", ""));
        }
        return snippet;
    }

    private static synchronized List<String> addBoldTextTag(List<String> splittedOnSentencesContentLinkedList, List<String> splittedWantedText, int i, int j) {
        String tempStrArrList = splittedOnSentencesContentLinkedList.get(j).trim();
        String newStr = Arrays.stream(tempStrArrList.split(" ")).map(s -> s.equalsIgnoreCase(splittedWantedText.get(i)) ? "<b>" + s + "</b>" : s).collect(Collectors.joining(" "));
        splittedOnSentencesContentLinkedList.set(j, newStr);
        return splittedOnSentencesContentLinkedList;
    }
}
