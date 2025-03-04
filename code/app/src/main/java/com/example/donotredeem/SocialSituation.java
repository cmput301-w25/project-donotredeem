package com.example.donotredeem;

import android.util.Log;

public enum SocialSituation {

    Alone("Alone", R.drawable.alone),
    Pair("Pair", R.drawable.pair),
    Crowd("Crowd", R.drawable.crowd);

    private final String label; //no moods aside these
    private final int img_id;

    SocialSituation(String label, int img_id) {
        this.label = label;
        this.img_id = img_id;
    }

    public String getLabel() {
        return label;
    }

    public int getImg_id() {
        return img_id;
    }


    public static int getImageIdBySituation(String situation) {
        if (situation == null) return -1;

        switch (situation) {
            case "Alone":
                return R.drawable.alone;
            case "Pair":
                return R.drawable.pair;
            case "Crowd":
                return R.drawable.crowd;
            // Add more cases here
            default:
                return -1;  // Return -1 for invalid or unrecognized situations
        }
    }

}
