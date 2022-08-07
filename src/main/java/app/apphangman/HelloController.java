package app.apphangman;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.lang.Character;
import java.util.ArrayList;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HelloController {
    @FXML
    private TextField dictionaryIDField;
    @FXML
    private TextField loaddictionaryIDField;
    @FXML
    private TextField openlibraryIDField;

    @FXML
    private TextField letterField;
    @FXML
    private TextField positionField;

    @FXML
    private Label createprogressLabel;
    @FXML
    private Label loadprogressLabel;

    @FXML
    private Label availwordsLabel;
    @FXML
    private Label totalpointsLabel;
    @FXML
    private Label percentagesuccesLabel;
    @FXML
    private Label wordLabel;
    @FXML
    private Label wonorlostLabel;
    @FXML
    private Label guessprogressLabel;
    @FXML
    private ImageView hangmanImage;
    @FXML
    private TextArea charsTextArea;
    @FXML
    private Button guessButton;

    @FXML
    private Label word1Label, word2Label, word3Label, word4Label, word5Label;
    @FXML
    private Label noofguesses1Label, noofguesses2Label, noofguesses3Label, noofguesses4Label, noofguesses5Label;
    @FXML
    private Label winner1Label, winner2Label, winner3Label, winner4Label, winner5Label;
    @FXML
    private Label sixletterLabel;
    @FXML
    private Label sevennineletterLabel;
    @FXML
    private Label tenplusletterLabel;
//    @FXML
//    private Label {...}Label;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    static ArrayList<String> five_words;
    static ArrayList<String> five_cnt_guesses;
    static ArrayList<String> five_winners;
    static boolean start = true;

    static boolean playing = false;

    static Dict dictionary;
    static Game game;
    private final String REST_URL = "https://openlibrary.org/works/";

    public void initialize() {
        if(start) {  //once the first window opens initialize the lists with the info of the 5 last games
            five_words = new ArrayList<>();
            five_cnt_guesses = new ArrayList<>();
            five_winners = new ArrayList<>();
            start = false;
            guessButton.setDisable(true);
        }
        if (dictionary != null && sixletterLabel != null && sevennineletterLabel != null && tenplusletterLabel != null) { //show dictionary percentages once that window opens
            sixletterLabel.setText(String.valueOf(df.format(dictionary.percentages()[0])));
            sevennineletterLabel.setText(String.valueOf(df.format(dictionary.percentages()[1])));
            tenplusletterLabel.setText(String.valueOf(df.format(dictionary.percentages()[2])));
        }
        if (five_words != null && word1Label != null) { //show info on 5 last games once that window opens
            setLabels();
        }
    }

    @FXML
    protected void onLoadMenuClick(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("load-popup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 522, 377);
        stage.setTitle("Load Dictionary");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onCreateMenuClick(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("create-popup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 522, 377);
        stage.setTitle("Create Dictionary");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onStartButtonClick(ActionEvent event) throws IOException, InvalidCountException {
        if (dictionary != null) {
            game = new Game(dictionary);
            wonorlostLabel.setText("");
            guessprogressLabel.setText(" ");
            availwordsLabel.setText(String.valueOf(dictionary.get_no_of_words()));
            totalpointsLabel.setText(String.valueOf(game.player.getTotalPoints()));
            percentagesuccesLabel.setText(game.getSuccessRate());
            wordLabel.setText(game.dict.getWordcurrentstate());
            hangmanImage.setImage(new Image(new File(game.getImagePath()).toURI().toString()));
            printPotentialLetters();
            guessButton.setDisable(false);
            playing = true;
        }
        else
            guessprogressLabel.setText("You have to Load a Dictionary first.");
    }

    @FXML
    protected void onGuessButtonClick(ActionEvent event) throws IOException {
        guessprogressLabel.setText(" ");

        String letter = letterField.getText();
        String position = positionField.getText(); //Integer.parseInt(position);

        if (letter.isEmpty() || position.isEmpty()) {
            guessprogressLabel.setText("Please fill in both fields.");
            return;
        }
        if (!isNumeric(position)){
            guessprogressLabel.setText("Please fill in a valid number. (see the list on the right)");
            return;
        }
        //Please only submit a capital letter in the letter field
        //Please submit a number 1-word.length in the position field
        int position_num = Integer.parseInt(position);
        if (position_num < 0 || position_num > game.dict.getWord().length()){
            guessprogressLabel.setText("Please fill in a valid number. (see the list on the right)");
            return;
        }
        if (letter.length() > 1 || !game.dict.getPotentialChars().get(position_num).containsKey(letter.charAt(0))){
            guessprogressLabel.setText("Please fill in a valid letter. (see the list on the right)");
            return;
        }
        game.makeGuess(letter.charAt(0), position_num);

        int gameState = game.checkGameState();
//        hangmanImage.setImage(new Image(new File(game.getImagePath()).toURI().toString())); //an lathos

        if (gameState == 0) {
            totalpointsLabel.setText(String.valueOf(game.player.getTotalPoints()));
            percentagesuccesLabel.setText(game.getSuccessRate());
            wordLabel.setText(game.dict.getWordcurrentstate());
            hangmanImage.setImage(new Image(new File(game.getImagePath()).toURI().toString()));
            wordLabel.setText(game.dict.getWordcurrentstate());

            printPotentialLetters();
        }
        else if(gameState == 1) {
            wonorlostLabel.setText("YOU LOST :(");
            wordLabel.setText(game.dict.getWordcurrentstate());
            totalpointsLabel.setText(String.valueOf(game.player.getTotalPoints()));
            hangmanImage.setImage(new Image(new File(game.getImagePath()).toURI().toString())); //////
            percentagesuccesLabel.setText(game.getSuccessRate());
            guessButton.setDisable(true);

            five_winners.add("computer");
            five_words.add(game.dict.getWord());
            five_cnt_guesses.add(String.valueOf(game.rightGuesses + game.wrongGuesses));
            if(five_winners.size() > 5) five_winners.remove(0);
            if(five_words.size() > 5) five_words.remove(0);
            if(five_cnt_guesses.size() > 5) five_cnt_guesses.remove(0);
            playing = false;
        }
        else {
            wonorlostLabel.setText("YAY, YOU WON!");
            totalpointsLabel.setText(String.valueOf(game.player.getTotalPoints()));
            percentagesuccesLabel.setText(game.getSuccessRate()); /////
            guessButton.setDisable(true);

            five_winners.add("player");
            five_words.add(game.dict.getWord());
            five_cnt_guesses.add(String.valueOf(game.rightGuesses + game.wrongGuesses));
            if(five_winners.size() > 5) five_winners.remove(0);
            if(five_words.size() > 5) five_words.remove(0);
            if(five_cnt_guesses.size() > 5) five_cnt_guesses.remove(0);
            playing = false;
        }
    }

    @FXML
    protected void onExitButtonClick(ActionEvent event) throws IOException {
//ok??
/*        Stage stage = (Stage) loadprogressLabel.getScene().getWindow();
        stage.close();*/
        Platform.exit();
    }

    @FXML
    protected void onRoundsButtonClick(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rounds-popup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 522, 377);
        stage.setTitle("Load Dictionary");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onSolutionButtonClick(ActionEvent event) throws IOException {
//ok?
        if(playing) {
            guessButton.setDisable(true);
            wonorlostLabel.setText("YOU LOST :(");
            wordLabel.setText(game.dict.getWordSolved());

            five_winners.add("computer");
            five_words.add(game.dict.getWord());
            five_cnt_guesses.add(String.valueOf(game.rightGuesses + game.wrongGuesses));
            if (five_winners.size() > 5) five_winners.remove(0);
            if (five_words.size() > 5) five_words.remove(0);
            if (five_cnt_guesses.size() > 5) five_cnt_guesses.remove(0);
            playing = false;
        }
    }

    @FXML
    protected void onLoadButtonClick(ActionEvent event) throws InvalidCountException{

        loadprogressLabel.setText(" ");

        String dictionaryID = loaddictionaryIDField.getText();

        if (dictionaryID.isEmpty()) {
            createprogressLabel.setText("Please fill in the field.");
            return;
        }

        String path = "medialab/hangman_" + dictionaryID + ".txt";

        System.out.print("Searching for the book in medialab file...");


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
            dictionary = new Dict(l);
            System.out.println(dictionary.getWordlist());
            Stage stage = (Stage) loadprogressLabel.getScene().getWindow();
            stage.close();
        }
        catch(FileNotFoundException e)
        {
            loadprogressLabel.setText("File not found.");
            System.out.println("Could not find the specified dictionary id!");
            e.printStackTrace();
        }
        catch(IOException e)
        {
            System.out.println("IO Exception");
            e.printStackTrace();
        }

    }

    @FXML
    protected void onDictionaryButtonClick(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dict_info-popup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 522, 377);
        stage.setTitle("Dictionary Info");
        stage.setResizable(false);
        stage.setScene(scene);
/*        if (dictionary != null) {
            System.out.println(dictionary.percentages()[0]);
            selectedwordLabel.setText(String.valueOf(dictionary.percentages()[0]));
            noofguessesLabel.setText(String.valueOf(dictionary.percentages()[1]));
            winnerLabel.setText(String.valueOf(dictionary.percentages()[2]));
            System.out.println(dictionary.percentages());
        }*/
        stage.show();
    }

    @FXML
    protected void onCreateButtonClick(ActionEvent event) {

        createprogressLabel.setText(" ");

        String dictionaryID = dictionaryIDField.getText();
        String open_libraryID = openlibraryIDField.getText();

        if (dictionaryID.isEmpty() || open_libraryID.isEmpty()) {
            createprogressLabel.setText("Please fill in both fields.");
            return;
        }

        for (int i = 0; i < dictionaryID.length(); i++) {
            if ((Character.isLetterOrDigit(dictionaryID.charAt(i)) == false)) {
                createprogressLabel.setText("DICTIONARY-ID must contain only letters or digits.");
                return;
            }
        }
        System.out.print("Searching for the book...");

        try {
            URL obj = new URL(formatSearchRequest(open_libraryID));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            getDescription(response.toString(), dictionaryID);

        } catch (NoDescriptionException e) {
            createprogressLabel.setText("The specific book does not have description. Choose another.");
        }
        catch (UndersizeException e) {
            createprogressLabel.setText("The specific book description has less than 20 words. Choose another.");
        }
        catch (UnbalancedException e) {
            createprogressLabel.setText("Word lengths are unbalanced. Choose another book.");
        }
        catch (Exception e) {
            System.out.println(e.toString());
            createprogressLabel.setText("Could not find book with inserted Open Library ID.");
        }

    }



    private void getDescription(String response, String dictID) throws NoDescriptionException, UndersizeException, UnbalancedException{
        System.out.println(response);

        JSONParser parser = new JSONParser();

        try{
            Object obj = parser.parse(response);
            JSONObject response_object = (JSONObject)obj;
            createprogressLabel.setText("Creating dictionary...");
            System.out.print("Creating dictionary...");

            if(response_object.containsKey("description")) {
                String description;
                if((response_object.get("description")).getClass().equals(String.class)) {
                    description = (String)response_object.get("description");
                }
                else {
                    description = (String) (((JSONObject) response_object.get("description")).get("value"));
                }
                Utilities.createDict(description, dictID);

            }
            else{
                throw new NoDescriptionException();
            }
            createprogressLabel.setText("done");

//            posterView.setImage(new Image((String)response_object.get("Poster")));

        }
        catch (ParseException e) {
            System.out.println(e.toString());
            createprogressLabel.setText(e.toString());
        }

//        createprogressLabel.setText("Dictionary created successfully!");
    }

    private void printPotentialLetters() {
        List<Map<Character, Integer>> chars = game.dict.getPotentialChars();
        int cnt = game.dict.getNoPotentialWords();
        int i;
        StringBuilder charsText = new StringBuilder("");
        for(i = 0; i < chars.size(); i++) {
            charsText.append("letter in position " + i + ":\n");
            for(Character j: chars.get(i).keySet()) {
                charsText.append(j + " - " + df.format(chars.get(i).get(j)*1.0/cnt) + ", ");
            }
            charsText.append("\n\n");
        }
        charsText.setLength(charsText.length() - 3);
        charsTextArea.setText(charsText.toString());
    }

    private void setLabels() {
        int my_size = five_words.size();
        if(my_size >= 1 ) {
            word1Label.setText(five_words.get(0));
            noofguesses1Label.setText(five_cnt_guesses.get(0));
            winner1Label.setText(five_winners.get(0));
        }
        if(my_size >= 2 ) {
            word2Label.setText(five_words.get(1));
            noofguesses2Label.setText(five_cnt_guesses.get(1));
            winner2Label.setText(five_winners.get(1));
        }
        if(my_size >= 3 ) {
            word3Label.setText(five_words.get(2));
            noofguesses3Label.setText(five_cnt_guesses.get(2));
            winner3Label.setText(five_winners.get(2));
        }
        if(my_size >= 4 ) {
            word4Label.setText(five_words.get(3));
            noofguesses4Label.setText(five_cnt_guesses.get(3));
            winner4Label.setText(five_winners.get(3));
        }
        if(my_size == 5){
            word5Label.setText(five_words.get(4));
            noofguesses5Label.setText(five_cnt_guesses.get(4));
            winner5Label.setText(five_winners.get(4));
        }
    }

    private static boolean isNumeric(String str){
        return str != null && str.matches("[0-9]+");
    }

    private String formatSearchRequest(String str) {
        String url = REST_URL+str+".json";
        return url; //.replace(" ", "+");
    }
}