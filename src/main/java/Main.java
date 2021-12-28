import Lemmatizator.Lemmatizator;
import org.hibernate.Session;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import Model.Lemma;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
        HashMap<String, Integer> lemmsAndCounts;
        final File inputFile = new File("src\\main\\resources\\sites.txt");
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FileReader fr = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fr);
        List<Lemma> lemmsList = new ArrayList<>();
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        List<String> tags = session.createSQLQuery("SELECT name FROM field").list();
        try {
            String line = reader.readLine();
            while (line != null) {
                try {
                    forkJoinPool.invoke(new PageContentScanner(line, tags));
                    line = reader.readLine();
                }catch (SSLHandshakeException e) {
                    continue;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lemmsAndCounts = Lemmatizator.getLemmsAndCounts();
        for (Map.Entry<String, Integer> entry : lemmsAndCounts.entrySet()) {
            lemmsList.add(new Lemma(entry.getKey(), entry.getValue()));
        }
        LemmaDAO lemmaDAO = new LemmaDAO();
        lemmaDAO.saveMany(lemmsList);
        session.close();
    }
}

