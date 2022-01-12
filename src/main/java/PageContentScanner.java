import Lemmatizator.Lemmatizator;
import Model.Page;
import Model.PageDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    private StringBuilder content = new StringBuilder();

    public PageContentScanner(String url, List<String> tags) throws IOException {
        parentURL = url;
        this.tags = tags;
        document = Jsoup.connect(parentURL).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .maxBodySize(0).referrer("http://www.google.com").get(); //Подключаемся к корневой странице и получаем тело данной страницы
    }
    @Override
    protected void compute() {
        System.out.println("I am working in thread " + Thread.currentThread().getName() +  "\n Ссылка: "+ parentURL);
        urls.add(parentURL);
        childAbsoluteURLs = document.select("a[href*= " + parentURL + "]").select("a[href$=/]"); //Получаем дочерние абсолютные ссылки. Закрывается косой чертой, потому что ссылки могут быть на изображения и заканчиваться символами.
        childRelativeURLs = document.select("a[href^=/]").select("a[href$=/]"); //Получаем дочерние относительные ссылки. Закрывается косой чертой, потому что ссылки могут быть на изображения и заканчиваться символами.
        for(String tag : tags) {
            content.append((document.select(tag).text()));
        }
        page = new Page(parentURL, document.connection().response().statusCode(), content.toString());
        try {
            new Lemmatizator().collectLemmsAndCounts(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    PageContentScanner pageContentScanner = new PageContentScanner(childURL.absUrl("href"), tags);
                    pageContentScanner.fork();
                    taskList.add(pageContentScanner);
                } catch (IOException e) {
                    PageDAO pageDAO = new PageDAO();
                    pageDAO.save(page);
                    e.printStackTrace();
                    continue;
                }
            }
            for (PageContentScanner pageContentScanner : taskList) {
                pageContentScanner.join();
            }
        }
    }
}
