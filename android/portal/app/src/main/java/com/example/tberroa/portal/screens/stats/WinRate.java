package com.example.tberroa.portal.screens.stats;

class WinRate {

    public boolean expanded;
    private int loss;
    private int win;

    public WinRate(boolean winner) {
        if (winner) {
            win = 1;
        } else {
            loss = 1;
        }
    }

    public int played() {
        return win + loss;
    }

    public String ratio() {
        return ((win * 100) / (win + loss)) + "%";
    }

    public void update(boolean winner) {
        if (winner) {
            win++;
        } else {
            loss++;
        }
    }

    public int wins() {
        return win;
    }
}