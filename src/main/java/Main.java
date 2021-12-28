import Lemmatizator.Lemmatizator;
import Model.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
        HashMap<String, Integer> lemmsAndCounts;
        final File inputFile = new File("src\\main\\resources\\sites.txt");
        final File outputFile = new File("src\\main\\resources\\lemmasFile.txt");
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FileWriter fw = new FileWriter(outputFile.getAbsoluteFile(), true);
        BufferedWriter bw =new BufferedWriter(fw);
        FileReader fr = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fr);
        ConcurrentSkipListSet<String> allUrls;
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<String> tags = session.createSQLQuery("SELECT name FROM field").list();
        session.close();
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


        /*if (!outputFile.exists())
        {
            outputFile.createNewFile();
        }
        try {
            lemmsAndCounts.forEach((key, value) -> {
                try {
                    bw.write(key + " - " + value + "\n");
                } catch (IOException e) {
                  e.printStackTrace();
                }
            });
            bw.flush();
            bw.close();
        } catch (IOException e) {
               e.printStackTrace();
        }*/
    }
}

