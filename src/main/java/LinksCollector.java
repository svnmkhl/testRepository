import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveAction;

public class LinksCollector extends RecursiveAction {
    private String parentURL;
    private Elements childAbsoluteURLs = new Elements();
    private Elements childRelativeURLs = new Elements();
    private Elements allChildURLs = new Elements();
    private static ConcurrentSkipListSet<String> urls = new ConcurrentSkipListSet<>();
    private List<LinksCollector> taskList = new ArrayList<>();
    private Document document;

    public LinksCollector(String url) throws IOException {
        parentURL = url;
        document = Jsoup.connect(parentURL).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .maxBodySize(0).referrer("http://www.google.com").ignoreHttpErrors(true).get(); //Подключаемся к корневой странице и получаем тело данной страницы
    }
    @Override
    protected void compute() {
        System.out.println("I am working in thread " + Thread.currentThread().getName());
        urls.add(parentURL);
        urls.add(parentURL);
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
                    LinksCollector linksCollector = new LinksCollector(childURL.absUrl("href"));
                    linksCollector.fork();
                    taskList.add(linksCollector);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (LinksCollector linksCollector : taskList) {
                linksCollector.join();
            }
        }
    }
    public static ConcurrentSkipListSet<String> getUrls () {
        return urls;
    }
}
