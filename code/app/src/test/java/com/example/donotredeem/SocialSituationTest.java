package com.example.donotredeem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SocialSituationTest {

    @Test
    public void testEnumValues() {
        // Test that enum values are correct
        assertEquals("Alone", SocialSituation.Alone.getLabel());
        assertEquals(R.drawable.alone, SocialSituation.Alone.getImg_id());

        assertEquals("Pair", SocialSituation.Pair.getLabel());
        assertEquals(R.drawable.pair, SocialSituation.Pair.getImg_id());

        assertEquals("Few", SocialSituation.Few.getLabel());
        assertEquals(R.drawable.few_people, SocialSituation.Few.getImg_id());
    }

    @Test
    public void testGetImageIdBySituation_ValidCases() {
        // Test that correct image ID is returned for valid cases
        assertEquals(R.drawable.alone, SocialSituation.getImageIdBySituation("Alone"));
        assertEquals(R.drawable.pair, SocialSituation.getImageIdBySituation("Pair"));
        assertEquals(R.drawable.few_people, SocialSituation.getImageIdBySituation("Few"));
    }

    @Test
    public void testGetImageIdBySituation_InvalidCases() {
        // Test invalid cases
        assertEquals(-1, SocialSituation.getImageIdBySituation("Unknown"));  // Non-existing situation
        assertEquals(-1, SocialSituation.getImageIdBySituation(""));         // Empty string
        assertEquals(-1, SocialSituation.getImageIdBySituation(null));       // Null case
    }
}
