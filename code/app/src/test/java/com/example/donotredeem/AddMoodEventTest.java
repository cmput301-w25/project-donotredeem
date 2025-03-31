package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.AddMoodEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import java.lang.reflect.Field;
/**
 * Unit tests for the {@link AddMoodEvent} fragment.
 * This test class verifies the correct functionality of methods related to mapping button IDs to mood types,
 * social situations, and retrieving button IDs based on mood names. It also includes tests for handling invalid inputs.
 *
 * <p>Key functionalities tested include:
 * <ul>
 *     <li>Retrieving the correct {@link MoodType} for valid and invalid button IDs</li>
 *     <li>Retrieving the correct {@link SocialSituation} for valid and invalid button IDs</li>
 *     <li>Mapping mood names to their corresponding button resource IDs</li>
 * </ul>
 *
 * <p>This class uses reflection to modify private fields in {@link AddMoodEvent} for testing purposes.
 */


@RunWith(MockitoJUnitRunner.class)
public class AddMoodEventTest {

    private AddMoodEvent fragment;

    /**
     * Initializes the {@link AddMoodEvent} fragment before each test method.
     */
    @Before
    public void setUp() throws Exception {
        fragment = new AddMoodEvent();
    }

    /**
     * Tests that valid button IDs return the correct {@link MoodType} values.
     * This test injects mock button IDs into the fragment and verifies the expected mood mappings.
     *
     * @throws Exception if reflection fails to set private fields
     */
    @Test
    public void testGetMoodForButtonId_validIds_returnsCorrectMoods() throws Exception {
        // Mock emojiButtonIds with test values
        int[] testEmojiIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        setPrivateField("emojiButtonIds", testEmojiIds);

        assertEquals(MoodType.Happy, fragment.getMoodForButtonId(1));
        assertEquals(MoodType.Sad, fragment.getMoodForButtonId(2));
        assertEquals(MoodType.Fear, fragment.getMoodForButtonId(3));
        assertEquals(MoodType.Angry, fragment.getMoodForButtonId(4));
        assertEquals(MoodType.Confused, fragment.getMoodForButtonId(5));
        assertEquals(MoodType.Disgusted, fragment.getMoodForButtonId(6));
        assertEquals(MoodType.Shameful, fragment.getMoodForButtonId(7));
        assertEquals(MoodType.Surprised, fragment.getMoodForButtonId(8));
        assertEquals(MoodType.Shy, fragment.getMoodForButtonId(9));
        assertEquals(MoodType.Tired, fragment.getMoodForButtonId(10));
    }

    /**
     * Tests that an invalid button ID returns {@code null} when retrieving a mood.
     *
     * @throws Exception if reflection fails to set private fields
     */
    @Test
    public void testGetMoodForButtonId_invalidId_returnsNull() throws Exception {
        setPrivateField("emojiButtonIds", new int[]{1, 2, 3});
        assertNull(fragment.getMoodForButtonId(999));
    }
    /**
     * Tests that valid button IDs return the correct {@link SocialSituation} values.
     *
     * @throws Exception if reflection fails to set private fields
     */
    @Test
    public void testGetSocialSituationForButtonId_validIds_returnsCorrectSocial() throws Exception {
        int[] testSocialIds = {11, 12, 13, 14};
        setPrivateField("socialButtonIds", testSocialIds);

        assertEquals(SocialSituation.Alone, fragment.getSocialSituationForButtonId(11));
        assertEquals(SocialSituation.Pair, fragment.getSocialSituationForButtonId(12));
        assertEquals(SocialSituation.Few, fragment.getSocialSituationForButtonId(13));
        assertEquals(SocialSituation.Crowd, fragment.getSocialSituationForButtonId(14));
    }

    /**
     * Tests that an invalid button ID returns {@code null} when retrieving a social situation.
     *
     * @throws Exception if reflection fails to set private fields
     */
    @Test
    public void testGetSocialSituationForButtonId_invalidId_returnsNull() throws Exception {
        setPrivateField("socialButtonIds", new int[]{11, 12});
        assertNull(fragment.getSocialSituationForButtonId(999));
    }

    /**
     * Tests that valid mood names return their corresponding button resource IDs.
     * This test assumes predefined resource IDs (e.g., {@code R.id.emoji_happy}) are correctly mapped.
     */
    @Test
    public void testGetButtonIdForMood_validMoods_returnsCorrectId() {
        // Assumes mood names map correctly to their respective button IDs
        assertEquals(R.id.emoji_happy, fragment.getButtonIdForMood("happy"));
        assertEquals(R.id.emoji_sad, fragment.getButtonIdForMood("sad"));
        assertEquals(R.id.emoji_fear, fragment.getButtonIdForMood("fear"));
        assertEquals(R.id.emoji_angry, fragment.getButtonIdForMood("angry"));
        assertEquals(R.id.emoji_confused, fragment.getButtonIdForMood("confused"));
        assertEquals(R.id.emoji_disgusted, fragment.getButtonIdForMood("disgusted"));
        assertEquals(R.id.emoji_shameful, fragment.getButtonIdForMood("shameful"));
        assertEquals(R.id.emoji_surprised, fragment.getButtonIdForMood("surprised"));
        assertEquals(R.id.emoji_shy, fragment.getButtonIdForMood("shy"));
        assertEquals(R.id.emoji_tired, fragment.getButtonIdForMood("tired"));
    }

    /**
     * Tests that an invalid mood name returns {@code -1} when retrieving a button ID.
     */
    @Test
    public void testGetButtonIdForMood_invalidMood_returnsNegativeOne() {
        assertEquals(-1, fragment.getButtonIdForMood("unknown"));
    }
    /**
     * Helper method to set private fields in {@link AddMoodEvent} using reflection.
     * This is used to inject mock button IDs for testing.
     *
     * @param fieldName the name of the private field to modify
     * @param value the value to assign to the field
     * @throws Exception if reflection fails to access or modify the field
     */
    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = AddMoodEvent.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(fragment, value);
    }
}