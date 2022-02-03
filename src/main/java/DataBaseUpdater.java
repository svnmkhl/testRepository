import Entity.Index;
import Entity.Lemma;
import Lemmatizator.Lemmatizator;
import DAO.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveAction;


public class DataBaseUpdater extends RecursiveAction {
    private String parentURL;
    private Elements childAbsoluteURLs = new Elements();
    private Elements childRelativeURLs = new Elements();
    private Elements allChildURLs = new Elements();
    private static ConcurrentSkipListSet<String> urls = new ConcurrentSkipListSet<>();
    private List<DataBaseUpdater> taskList = new ArrayList<>();
    private Document document;
    private List<String> tags = new ArrayList<>();
    private static List<Page>pages = new ArrayList<>();
    private Page page;
    private static int pageId;
    private StringBuilder content = new StringBuilder();

    public DataBaseUpdater(String url) throws IOException {
        parentURL = url;
        document = Jsoup.connect(parentURL).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .maxBodySize(0).referrer("http://www.google.com").get(); //Подключаемся к корневой странице и получаем тело данной страницы
    }
    @Override
    protected void compute() {
        System.out.println("I am working in thread " + Thread.currentThread().getName() +  "\n Ссылка: "+ parentURL);
        HashMap<String, Integer> lemmsAndCounts;
        //Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        tags.add("title");
        tags.add("body");
       /* List<String> tags = session.createSQLQuery("SELECT selector FROM _field").list();
        session.close();*/
        Lemma lemma;
        HashMap<Integer, Integer> indexes = new HashMap<>();
        content.append(document.select("title").text() + " ").append(document.select("body").text());
        urls.add(parentURL);
        page = new Page(parentURL, document.connection().response().statusCode(), content.toString());
        PageDAO.save(page);
        for (String tag : tags) {
            String contentByTag = document.select(tag).text();
            try {
                Lemmatizator lemmatizator = new Lemmatizator(contentByTag);
                lemmatizator.collectLemmsAndCounts();
                lemmsAndCounts = lemmatizator.getLemmsAndCounts();
                for (Map.Entry<String, Integer> entry : lemmsAndCounts.entrySet()) {
                    try {
                       if(LemmaDAO.findByName(entry.getKey()).size() == 0 || LemmaDAO.findByName(entry.getKey()) == null) {
                           lemma = new Lemma(entry.getKey(), 1);
                           LemmaDAO.save(lemma);

                       } else {
                           lemma = new Lemma(entry.getKey(), 1);
                           lemma = LemmaDAO.updateFrequency(lemma);
                       }
                    } catch (Exception e) {
                        lemma = new Lemma(entry.getKey(), 1);
                        LemmaDAO.save(lemma);
                        continue;
                    }
                    final float titleWeightCoeff = 1.0f;
                    final float bodyWeightCoeff = 0.8f;
                    int lemmaInTitleFrequency = 0;
                    int lemmaInBodyFrequency= 0;
                    float rankIfTitle = 0;
                    float rankIfBody = 0;
                    Index indexIfTitle;
                    Index indexIfBody;
                    if(tag.equals("title")) {
                        lemmaInTitleFrequency = entry.getValue();
                        rankIfTitle = titleWeightCoeff * (float) lemmaInTitleFrequency;
                        indexIfTitle = new Index(page.getId(), lemma.getId(), rankIfTitle);
                        indexes.put(page.getId(), lemma.getId());
                        IndexDAO.save(indexIfTitle);
                    } else if (tag.equals("body")) {
                        lemmaInBodyFrequency = entry.getValue();
                        rankIfBody = bodyWeightCoeff * (float) lemmaInBodyFrequency;
                        indexIfBody = new Index(page.getId(), lemma.getId(), rankIfBody);
                        if (indexes.entrySet().contains(Map.entry(page.getId(), lemma.getId()))){
                            IndexDAO.updateRank(indexIfBody);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        childAbsoluteURLs = document.select("a[href*= " + parentURL + "]").select("a[href$=/]"); //Получаем дочерние абсолютные ссылки. Закрывается косой чертой, потому что ссылки могут быть на изображения и заканчиваться символами.
        childRelativeURLs = document.select("a[href^=/]").select("a[href$=/]"); //Получаем дочерние относительные ссылки. Закрывается косой чертой, потому что ссылки могут быть на изображения и заканчиваться символами.

        if (childAbsoluteURLs != null) {
            allChildURLs.addAll(childAbsoluteURLs);
        }
        if (childRelativeURLs != null) {
            allChildURLs.addAll(childRelativeURLs);
        }
        if (allChildURLs != null) {
            for (Element childURL : allChildURLs) {
                if (urls.contains(childURL.absUrl("href"))) {
                    continue;
                }
                urls.add(childURL.absUrl("href"));
                try {
                    DataBaseUpdater dataBaseUpdater = new DataBaseUpdater(childURL.absUrl("href"));
                    dataBaseUpdater.fork();
                    taskList.add(dataBaseUpdater);
                } catch (HttpStatusException e) {
                    PageDAO.save(new Page(parentURL, e.getStatusCode(), ""));
                    e.printStackTrace();
                    continue;
                } catch (IOException e) {
                    continue;
                }
            }
            for (DataBaseUpdater pageContentScanner : taskList) {
                pageContentScanner.join();
            }
        }
    }
}
