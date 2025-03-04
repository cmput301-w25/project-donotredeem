package com.example.donotredeem;

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
}
