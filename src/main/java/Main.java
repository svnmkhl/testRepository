import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.ConnectException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
        final File inputFile = new File("src\\main\\resources\\sites.txt");
        FileReader fr = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fr);
        System.out.println("Введите команду: \nsearch - поиск информации по базе данных; \nupdate database" +
                " обновить базу поиска");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();

        if (command.equals("search")){
            //Поиск
            scanner = new Scanner(System.in);
            String query = scanner.nextLine();
            Searching.search(query);

        } else if(command.equals("update database")) {
            //Обновление базы данных
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            try {
                String line = reader.readLine();
                while (line != null) {
                    try {
                        forkJoinPool.invoke(new DataBaseUpdater(line));
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

