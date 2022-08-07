package app.apphangman;

public class Player {
    private int points;

    public Player() {
        this.points = 0;
    }


    public int getTotalPoints() {
        return points;
    }

    public void addPoints(double n)
    {
        if (n >= 0.6) points += 5;
        else if (n >= 0.4) points += 10;
        else if (n >= 0.25) points += 15;
        else points = 30;
//        points += n;
    }

    public void subtractPoints() {
        points -= 15;
        if (points < 0) points = 0;
    }
}


