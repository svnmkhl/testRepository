import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.ConnectException;
import java.util.concurrent.ForkJoinPool;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
        final File inputFile = new File("src\\main\\resources\\sites.txt");
        FileReader fr = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fr);
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

