package com.example.donotredeem;

import android.util.Log;
/**
 * Enum representing various social situations along with their associated image resources.
 *
 * Each social situation has a label and a corresponding drawable image resource id.
 *
 */
public enum SocialSituation {

    Alone("Alone", R.drawable.alone),
    Pair("Pair", R.drawable.pair),
    Crowd("Crowd", R.drawable.crowd);

    private final String label; //no moods aside these
    private final int img_id;

    /**
     * Constructs a SocialSituation enum instance.
     *
     * @param label  the descriptive label of the social situation
     * @param img_id the drawable image resource id for the social situation
     */
    SocialSituation(String label, int img_id) {
        this.label = label;
        this.img_id = img_id;
    }
    /**
     * Retrieves the label for the social situation.
     *
     * @return the label of the social situation
     */
    public String getLabel() {
        return label;
    }
    /**
     * Retrieves the drawable image resource id for the social situation.
     *
     * @return the drawable resource id
     */
    public int getImg_id() {
        return img_id;
    }

    /**
     * Returns the drawable image resource id based on the given situation label.
     *
     * If the input situation does not match any known social situation, or if the situation is null,
     * the method returns -1.
     *
     *
     * @param situation the label of the social situation ("Alone", "Pair", "Crowd")
     * @return the drawable image resource id if a match is found; -1 otherwise
     */
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
