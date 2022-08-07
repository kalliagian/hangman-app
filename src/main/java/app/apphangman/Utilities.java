package app.apphangman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utilities {
    public static void createDict(String description, String dictionaryID) throws UndersizeException, UnbalancedException {
        HashSet<String> dict_list;
        dict_list = dictionaryWords(description);
        try {
            File myObj = new File("medialab/hangman_" + dictionaryID + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("A file with this name already exists.");
                return;
            }
            try {
                FileWriter myWriter = new FileWriter("medialab/hangman_" + dictionaryID + ".txt");
                for(String i : dict_list) {
                    myWriter.write(i+"\n");
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static HashSet<String> dictionaryWords(String description) throws UndersizeException, UnbalancedException {
        String[] words = description.split("\\W+");
        HashSet<String> set = new HashSet<>();
        HashSet<String> bigwordsset = new HashSet<>();
        int count = words.length;
        for(int i = 0; i < count; i++) {
            int word_length = words[i].length();
            if(word_length >= 6) {
                words[i] = words[i].toUpperCase();
                set.add(words[i]);
                if(word_length >= 9) bigwordsset.add(words[i]);
            }
        }
        if(set.size() < 20) throw new UndersizeException();
        if(bigwordsset.size()*1.0/set.size() < 0.2) throw new UnbalancedException();
        return set;
    }

    public static ArrayList<String> readFile(String path) throws IOException
    {
        ArrayList<String> l = new ArrayList<String>();

        try
        {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while(line != null)
            {
                l.add(line);
                line = br.readLine();
            }

            br.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Could not find the specified dictionary id!");
            e.printStackTrace();
        }

        return l;
    }
}
