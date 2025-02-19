package com.example.donotredeem;
public enum MoodType {
    Happy("Happy", R.drawable.hapi),
    Sad("Sad", R.drawable.sad),
    Fear("Fear", R.drawable.fear),
    Angry("Angry", R.drawable.angry),
    Confused("Confused", R.drawable.confused),
    Disgusted("Disgusted", R.drawable.disgust),
    Shameful("Shameful", R.drawable.shame),
    Surprised("Surprised", R.drawable.surprised),
    Shy("Shy", R.drawable.shy),
    Tired("Tired", R.drawable.tired);

    private final String mood; //no moods aside these
    private final int img_id;
    MoodType(String mood, int img_id){
        this.mood = mood;
        this.img_id= img_id;
    }

    public String getMood() {
        return mood;
    }

    public int getImg_id() {
        return img_id;
    }
}

