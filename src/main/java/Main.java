import Pojo.PageWithRelevance;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ConnectException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException {
        final File inputFile = new File("src\\main\\resources\\sites.txt");
        FileReader fr = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fr);
        System.out.println("Введите команду: \nsearch - поиск информации по базе данных; \nupdate database - " +
                " обновить базу поиска");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();

        if (command.equals("search")){

            //ПОИСК

            scanner = new Scanner(System.in);
            String userRequest = scanner.nextLine();
            List<PageWithRelevance> searchingResult = SearchingEngine.search(userRequest);
            if (searchingResult.isEmpty()) {
                System.out.println("No result");
            } else {
                searchingResult.forEach(page -> System.out.println(page.getUri()));
            }
        } else if(command.equals("update database")) {

            //ОБНОВЛЕНИЕ БАЗЫ ДАННЫХ

            ForkJoinPool forkJoinPool = new ForkJoinPool();
            try {
                String line = reader.readLine();
                while (line != null) {
                    try {
                        forkJoinPool.invoke(new DataBaseUpdaterEngine(line));
                        line = reader.readLine();
                    }catch (SSLHandshakeException e) {
                        continue;
                    }
                    catch (ConnectException e) {
                        continue;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

