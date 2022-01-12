import Lemmatizator.Lemmatizator;
import Model.*;
import org.hibernate.Session;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
        HashSet<Lemma> lemmsAndCounts;
        final File inputFile = new File("src\\main\\resources\\sites.txt");
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FileReader fr = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fr);
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
        LemmaDAO lemmaDAO = new LemmaDAO();
        lemmaDAO.saveMany(lemmsAndCounts);
        session.close();
    }
}

