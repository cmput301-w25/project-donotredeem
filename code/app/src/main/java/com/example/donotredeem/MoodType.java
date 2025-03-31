package com.example.donotredeem;

/**
 * Enum representing different mood types along with their associated drawable image resource IDs.
 *
 * Each mood type is defined with a descriptive name and a corresponding drawable resource.
 *
 */
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
    /**
     * Constructs a MoodType enum instance with the specified mood name and image resource.
     *
     * @param mood   the name of the mood
     * @param img_id the drawable image resource ID for the mood
     */
    MoodType(String mood, int img_id){
        this.mood = mood;
        this.img_id= img_id;
    }
    /**
     * Retrieves the mood name.
     *
     * @return the name of the mood
     */
    public String getMood() {
        return mood;
    }
    /**
     * Retrieves the drawable image resource ID for the mood.
     *
     * @return the drawable image resource ID
     */
    public int getImg_id() {
        return img_id;
    }
    /**
     * Returns the drawable image resource ID associated with the specified mood name.
     *
     * The search is case-insensitive and iterates over all available mood types.
     * If no matching mood is found, -1 is returned.
     *
     *
     * @param mood the name of the mood (e.g., "Happy", "Sad", etc.)
     * @return the drawable image resource ID if a match is found; -1 otherwise
     */
    public static int getImageIdByMood(String mood) {
        for (MoodType m : MoodType.values()) {
            if (m.mood.equalsIgnoreCase(mood)) {
                return m.img_id;
            }
        }
        return -1; // Default value if not found (optional: return a default drawable)
    }
}



