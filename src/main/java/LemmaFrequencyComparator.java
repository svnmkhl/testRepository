import Entity.Lemma;

import java.util.Comparator;

public class LemmaFrequencyComparator implements Comparator<Lemma>
{

    @Override
    public int compare(Lemma lemma1, Lemma lemma2) {
        if(lemma1.getFrequency() > lemma2.getFrequency()) {
            return 1;
        }
        if(lemma1.getFrequency() < lemma2.getFrequency()) {
            return -1;
        }
        return 0;
    }
}
