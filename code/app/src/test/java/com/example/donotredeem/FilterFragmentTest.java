package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.FilterFragment;
import com.example.donotredeem.MoodEvent;
import com.example.donotredeem.MoodType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;
import java.util.regex.Pattern;

@RunWith(JUnit4.class)
public class FilterFragmentTest {

    private FilterFragment fragment;
    private final TestMoodEvent currentMonthEvent = new TestMoodEvent("01/" + LocalDate.now().getMonthValue() + "/" + LocalDate.now().getYear());
    private final TestMoodEvent lastMonthEvent = new TestMoodEvent("01/" + LocalDate.now().minusMonths(1).getMonthValue() + "/" + LocalDate.now().getYear());


    static class TestMoodEvent extends MoodEvent {
        private final String date;

        TestMoodEvent(String date) {
            this.date = date;
        }

        @Override
        public String getDate() {
            return date;
        }
    }

    @Before
    public void setUp() throws Exception {
        fragment = new FilterFragment();
    }



    @Test
    public void testMatchesSearch() {
        MoodEvent event = new MoodEvent();
        event.setEmotionalState("Happy");
        event.setPlace("Home");
        event.setTrigger("Family gathering");
        event.setExplainText("Had a great time with family");

        assertTrue(fragment.matchesSearch(event, "happy"));
        assertTrue(fragment.matchesSearch(event, "home"));
        assertTrue(fragment.matchesSearch(event, "gathering"));
        assertTrue(fragment.matchesSearch(event, "great time"));
        assertFalse(fragment.matchesSearch(event, "sad"));
    }

    @Test
    public void testMatchesWholeWord() {
        assertTrue(fragment.matchesWholeWord("Happy birthday", "\\bhappy\\b"));
        assertFalse(fragment.matchesWholeWord("Unhappy day", "\\bhappy\\b"));
        assertFalse(fragment.matchesWholeWord("Happily ever after", "\\bhappy\\b"));
    }


    @Test
    public void testGetMoodForButtonId() throws Exception {
        int[] testIds = {1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010};
        java.lang.reflect.Field field = FilterFragment.class.getDeclaredField("emojiButtonIds");
        field.setAccessible(true);
        field.set(fragment, testIds);

        assertEquals(MoodType.Happy, fragment.getMoodForButtonId(1001));
        assertEquals(MoodType.Sad, fragment.getMoodForButtonId(1002));
        assertNull(fragment.getMoodForButtonId(9999));
    }
}