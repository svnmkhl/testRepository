import Lemmatizator.Lemmatizator;
import Model.*;
import org.hibernate.Session;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveAction;


public class PageContentScanner extends RecursiveAction {
    private String parentURL;
    private Elements childAbsoluteURLs = new Elements();
    private Elements childRelativeURLs = new Elements();
    private Elements allChildURLs = new Elements();
    private static ConcurrentSkipListSet<String> urls = new ConcurrentSkipListSet<>();
    private List<PageContentScanner> taskList = new ArrayList<>();
    private Document document;
    private List<String> tags;
    private static List<Page>pages = new ArrayList<>();
    private Page page;
    private static int pageId;
    private StringBuilder content = new StringBuilder();

    public PageContentScanner(String url) throws IOException {
        parentURL = url;
        this.tags = tags;
        document = Jsoup.connect(parentURL).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .maxBodySize(0).referrer("http://www.google.com").get(); //Подключаемся к корневой странице и получаем тело данной страницы
    }
    @Override
    protected void compute() {
        System.out.println("I am working in thread " + Thread.currentThread().getName() +  "\n Ссылка: "+ parentURL);
        HashMap<String, Integer> lemmsAndCounts = new HashMap<>();
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        List<String> tags = session.createSQLQuery("SELECT selector FROM _field").list();
        session.close();
        final double titleWeightCoeff = 1.0;
        final double bodyWeightCoeff = 0.8;
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
                           Lemma lemma = new Lemma(entry.getKey(), entry.getValue());
                           LemmaDAO.save(lemma);
                           IndexDAO.save(new Index(page.getId(), lemma.getId(), 1.0f));

                       } else {
                           Lemma lemma = new Lemma(entry.getKey(), entry.getValue());
                           LemmaDAO.update(lemma);
                           IndexDAO.save(new Index(page.getId(), lemma.getId(), 1.0f));
                       }
                    } catch (Exception e) {
                        Lemma lemma = new Lemma(entry.getKey(), entry.getValue());
                        LemmaDAO.save(lemma);
                        IndexDAO.save(new Index(page.getId(), lemma.getId(), 1.0f));
                        continue;
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
                    PageContentScanner pageContentScanner = new PageContentScanner(childURL.absUrl("href"));
                    pageContentScanner.fork();
                    taskList.add(pageContentScanner);
                } catch (HttpStatusException e) {
                    PageDAO.save(new Page(parentURL, e.getStatusCode(), ""));
                    e.printStackTrace();
                    continue;
                } catch (IOException e) {
                    continue;
                }
            }
            for (PageContentScanner pageContentScanner : taskList) {
                pageContentScanner.join();
            }
        }
    }
}
