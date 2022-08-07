package app.apphangman;

import java.io.IOException;
import java.util.ArrayList;

public class Dict {
    private ArrayList<String> wordlist;

    public Dict(ArrayList<String> list) throws IOException, InvalidCountException {
        this.wordlist = list;
    }

    public ArrayList<String> getWordlist() {
        return wordlist;
    }

    public int get_no_of_words() {
        return wordlist.size();
    }

    public double[] percentages() {
        double[] result = new double[3];
        int cnt = wordlist.size();
        double six = 0, sev_to_nine = 0, above_ten = 0;
        for(String curr_word: wordlist) {
            if(curr_word.length() == 6) six = six + 1;
            else if(curr_word.length() < 10) sev_to_nine = sev_to_nine + 1;
            else above_ten = above_ten + 1;
        }
        result[0] = 100*six/cnt;
        result[1] = 100*sev_to_nine/cnt;
        result[2] = 100*above_ten/cnt;
        return result;
    }


}

