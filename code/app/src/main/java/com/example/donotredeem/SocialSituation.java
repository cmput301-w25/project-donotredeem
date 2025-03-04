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


    public static int getImageIdBySituation(String label) {
        for (SocialSituation situation : SocialSituation.values()) {
            if (situation.getLabel().equalsIgnoreCase(label)) {
                return situation.getImg_id();
            }
        }
        // Log or handle invalid situation gracefully
        Log.e("SocialSituation", "No matching situation found for label: " + label);
        return -1;  // Return a fallback value or handle error
    }

}
