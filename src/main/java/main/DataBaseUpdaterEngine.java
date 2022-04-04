package main;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveAction;


public class DataBaseUpdaterEngine extends RecursiveAction {
    private String parentURI;
    private Elements childAbsoluteURIs = new Elements();
    private Elements childRelativeURLs = new Elements();
    private Elements allChildURIs = new Elements();
    private static ConcurrentSkipListSet<String> urls = new ConcurrentSkipListSet<>();
    private List<DataBaseUpdaterEngine> taskList = new ArrayList<>();
    private Document document;
    private List<String> tags = new ArrayList<>();
    private static List<Page>pages = new ArrayList<>();
    private Page page;
    private static int pageId;
    private StringBuilder content = new StringBuilder();
    private static int pageCounter;
    private Index index;

    public DataBaseUpdaterEngine(String URI) throws IOException {
        pageCounter ++;
        parentURI = URI;
        document = Jsoup.connect(parentURI).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .maxBodySize(0).referrer("http://www.google.com").get(); //Подключаемся к корневой странице и получаем тело данной страницы
    }
    @Override
    protected void compute() {
        System.out.println("I am working in thread " + Thread.currentThread().getName() +  "\n Ссылка: "+ parentURI + "\n Страница №: " + pageCounter);
        HashMap<String, Integer> lemmsAndCounts;
        HashMap<Lemma, Float> lemmsAndRanks = new HashMap<>();
        Lemma lemma;
        final float titleWeightCoeff = 1.0f;
        final float bodyWeightCoeff = 0.8f;
        //Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        tags.add("title");
        tags.add("body");
       /* List<String> tags = session.createSQLQuery("SELECT selector FROM _field").list();
        session.close();*/
        //HashMap<Integer, Integer> indexes = new HashMap<>();
        //content.append(document.select("title") + " ").append(document.select("body"));
        urls.add(parentURI);
        page = new Page(parentURI, document.connection().response().statusCode(), document.toString());
        for (String tag : tags) {
            String contentByTag = document.select(tag).text();
            try {
                Lemmatizator lemmatizator = new Lemmatizator(contentByTag);
                lemmatizator.collectLemmsAndCounts();
                lemmsAndCounts = lemmatizator.getLemmsAndCounts();
                for (Map.Entry<String, Integer> entry : lemmsAndCounts.entrySet()) {
                    lemma = new Lemma(entry.getKey(), 1);
                    if (!lemmsAndRanks.containsKey(lemma)) {
                        if (tag.equals("title")) {
                            lemmsAndRanks.put(lemma, titleWeightCoeff * entry.getValue());
                        } else {
                            lemmsAndRanks.put(lemma, bodyWeightCoeff * entry.getValue());
                        }
                    } else {
                        lemmsAndRanks.replace(lemma, titleWeightCoeff * entry.getValue(),
                                titleWeightCoeff * entry.getValue() + bodyWeightCoeff * entry.getValue());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PageDAO.save(page);
        List<Index> indexes = new ArrayList<>();
        for (Map.Entry<Lemma, Float> entry : lemmsAndRanks.entrySet()) {
            if(LemmaDAO.findByName(entry.getKey().getName()).size() == 0 || LemmaDAO.findByName(entry.getKey().getName()) == null) {
                lemma = entry.getKey();
                LemmaDAO.save(lemma);
                indexes.add(new Index(page.getId(), lemma.getId(), entry.getValue()));

            } else {
                lemma = LemmaDAO.updateFrequency(entry.getKey());
                indexes.add(new Index(page.getId(), lemma.getId(), entry.getValue()));
            }
        }
        IndexDAO.saveMany(indexes);
        childAbsoluteURIs = document.select("a[href*= " + parentURI + "]").select("a[href$=/]"); //Получаем дочерние абсолютные ссылки. Закрывается косой чертой, потому что ссылки могут быть на изображения и заканчиваться символами.
        childRelativeURLs = document.select("a[href^=/]").select("a[href$=/]"); //Получаем дочерние относительные ссылки. Закрывается косой чертой, потому что ссылки могут быть на изображения и заканчиваться символами.

        if (childAbsoluteURIs != null) {
            allChildURIs.addAll(childAbsoluteURIs);
        }
        if (childRelativeURLs != null) {
            allChildURIs.addAll(childRelativeURLs);
        }
        if (allChildURIs != null) {
            for (Element childURL : allChildURIs) {
                if (urls.contains(childURL.absUrl("href"))) {
                    continue;
                }
                urls.add(childURL.absUrl("href"));
                try {
                    DataBaseUpdaterEngine dataBaseUpdater = new DataBaseUpdaterEngine(childURL.absUrl("href"));
                    dataBaseUpdater.fork();
                    taskList.add(dataBaseUpdater);
                } catch (HttpStatusException e) {
                    PageDAO.save(new Page(parentURI, e.getStatusCode(), ""));
                    e.printStackTrace();
                    continue;
                } catch (IOException e) {
                    continue;
                }
            }
            for (DataBaseUpdaterEngine pageContentScanner : taskList) {
                pageContentScanner.join();
            }
        }
    }
}
