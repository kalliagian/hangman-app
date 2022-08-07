package app.apphangman;

import java.io.IOException;
import java.text.DecimalFormat;


public class Game {
    static String imagePath = "src/main/images/img_";
    GameDictionary dict;
    Player player;
    int wrongGuesses;
    int rightGuesses;
    int imageNo;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Class constructor specifying the dictionary that will be used.
     *
     * @param dict reference to the Dict object that will be used as a dictionary for the game
     */
    public Game(Dict dict){
        this.dict = new GameDictionary(dict);
        this.player = new Player();
        this.wrongGuesses = 0;
        this.rightGuesses = 0;
        this.imageNo = 0;
    }

    /**
     * Changes this player's points, this Game's wrong and right guesses counter
     * and image number based on the outcome of the guess of the player.
     * Calls updateDictionary method of this dict in order to update this
     * dict's potential words and characters.
     * <p>
     * Position must be in the range of this dict's chosen word length.
     * Letter must be one of the potential letters in this dict's potentialChars in the specified position.
     *
     * @param letter the guessed character
     * @param position the guessed position of the guessed character
     */
    public void makeGuess(char letter, int position) {

        double outcome = dict.updateDictionary(letter, position);

        if (outcome == 0) {
            player.subtractPoints();
            wrongGuesses += 1;
            imageNo += 1;
        }
        else if (outcome > 0) {
            player.addPoints(outcome);
            rightGuesses += 1;
        }
    }

    /**
     * Returns this game's state.
     * 1 indicates that the player lost, 2 indicates that the player won and 0 indicates that the game hasn't finished.
     *
     * @return the int that indicates the state of this game
     */
    public int checkGameState() {
        if (wrongGuesses == 6) return 1;  //lost
        if (dict.allFound()) return 2;  //won
        return 0;
    }

    /**
     * Returns the image path of the image that indicates how many wrong guesses the player has made so far.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath + imageNo + ".png";
    }

    /**
     * Returns the percentage of correct guesses the player has made.
     *
     * @return percentage
     */
    public String getSuccessRate() {
        int totalGuesses = rightGuesses + wrongGuesses;
        if (totalGuesses != 0)
            return String.valueOf(df.format(rightGuesses * 100.0 / (rightGuesses + wrongGuesses)));
        else return " ";
    }
}
