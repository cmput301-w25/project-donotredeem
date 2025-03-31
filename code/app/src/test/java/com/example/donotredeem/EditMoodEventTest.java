package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.EditMoodEvent;
import com.example.donotredeem.MoodType;
import com.example.donotredeem.SocialSituation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;

/**
 * Unit tests for the {@link EditMoodEvent} fragment's button mapping functionality.
 * This class verifies the correct association between UI button IDs and their corresponding
 * mood types/social situations, including handling of invalid IDs.
 *
 * <p>Key test scenarios include:
 * <ul>
 *     <li>Valid button ID to {@link MoodType} mapping verification</li>
 *     <li>Valid button ID to {@link SocialSituation} mapping verification</li>
 *     <li>Null returns for unrecognized button IDs</li>
 * </ul>
 *
 * <p>Uses reflection to modify private button ID arrays for controlled testing.
 */
@RunWith(JUnit4.class)
public class EditMoodEventTest {

    private EditMoodEvent fragment;

    /**
     * Initializes the {@link EditMoodEvent} fragment before each test method.
     *
     * @throws Exception if fragment instantiation fails
     */
    @Before
    public void setUp() throws Exception {
        fragment = new EditMoodEvent();
    }


    /**
     * Tests valid mood button ID mappings using mock button IDs (1001-1010).
     * Verifies all 10 {@link MoodType} values are correctly mapped in sequence.
     *
     * @throws Exception if reflection field modification fails
     */
    @Test
    public void testGetMoodForButtonId_validIds() throws Exception {
        // Mock emojiButtonIds with test values
        int[] testEmojiIds = {1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010};
        setPrivateField("emojiButtonIds", testEmojiIds);

        assertEquals(MoodType.Happy, fragment.getMoodForButtonId(1001));
        assertEquals(MoodType.Sad, fragment.getMoodForButtonId(1002));
        assertEquals(MoodType.Fear, fragment.getMoodForButtonId(1003));
        assertEquals(MoodType.Angry, fragment.getMoodForButtonId(1004));
        assertEquals(MoodType.Confused, fragment.getMoodForButtonId(1005));
        assertEquals(MoodType.Disgusted, fragment.getMoodForButtonId(1006));
        assertEquals(MoodType.Shameful, fragment.getMoodForButtonId(1007));
        assertEquals(MoodType.Surprised, fragment.getMoodForButtonId(1008));
        assertEquals(MoodType.Shy, fragment.getMoodForButtonId(1009));
        assertEquals(MoodType.Tired, fragment.getMoodForButtonId(1010));
    }

    /**
     * Tests handling of unrecognized mood button IDs.
     * Verifies null return for IDs not present in configured button array.
     *
     * @throws Exception if reflection field modification fails
     */
    @Test
    public void testGetMoodForButtonId_invalidId() throws Exception {
        setPrivateField("emojiButtonIds", new int[]{1001, 1002, 1003});
        assertNull(fragment.getMoodForButtonId(9999));
    }

    /**
     * Tests valid social situation button ID mappings using mock IDs (2001-2004).
     * Verifies all 4 {@link SocialSituation} values are correctly mapped in sequence.
     *
     * @throws Exception if reflection field modification fails
     */
    @Test
    public void testGetSocialSituationForButtonId_validIds() throws Exception {
        // Mock socialButtonIds with test values
        int[] testSocialIds = {2001, 2002, 2003, 2004};
        setPrivateField("socialButtonIds", testSocialIds);

        assertEquals(SocialSituation.Alone, fragment.getSocialSituationForButtonId(2001));
        assertEquals(SocialSituation.Pair, fragment.getSocialSituationForButtonId(2002));
        assertEquals(SocialSituation.Few, fragment.getSocialSituationForButtonId(2003));
        assertEquals(SocialSituation.Crowd, fragment.getSocialSituationForButtonId(2004));
    }

    /**
     * Tests handling of unrecognized social situation button IDs.
     * Verifies null return for IDs not present in configured button array.
     *
     * @throws Exception if reflection field modification fails
     */
    @Test
    public void testGetSocialSituationForButtonId_invalidId() throws Exception {
        setPrivateField("socialButtonIds", new int[]{2001, 2002});
        assertNull(fragment.getSocialSituationForButtonId(9999));
    }

    /**
     * Helper method to inject test values into private fragment fields via reflection.
     * Enables mocking of button ID arrays for controlled testing scenarios.
     *
     * @param fieldName the name of the private field to modify
     * @param value the test value to inject
     * @throws Exception if reflection access/modification fails
     */
    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = EditMoodEvent.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(fragment, value);
    }
}