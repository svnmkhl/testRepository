import Lemmatizator.Lemmatizator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Searching
{

    public Searching()
    {
    }

    public static String search (String userRequest) throws IOException {
        HashSet<String> lemmsInSearchingQuery = new HashSet<>();
        Lemmatizator lemmatizator = new Lemmatizator();
        String[] query = userRequest.trim().split(" ");
        for (String string : query) {
            //lemmsInSearchingQuery.addAll(lemmatizator.getLemms(string));
            
        }


        return null;
    }

}
