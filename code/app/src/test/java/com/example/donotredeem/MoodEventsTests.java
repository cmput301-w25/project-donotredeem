//package com.example.donotredeem;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MoodEventsTests {
//
//    private MoodEvent mood;
//    private List<String> moodList;
//
//    @Before
//    public void setUp(){
//        // Setting up the initial data before each tests.
//        moodList = new ArrayList<>();
//        mood = new MoodEvent(
//                "Happy",
//                "01/01/2025",
//                "12:12",
//                "University",
//                "None",
//                "party",
//                "happy new year",
//                "family picture"
//        );
//
//    }
//     @Test
//    public void testGettersAndSetters(){
//        mood.setEmotionalState("Sad");
//        mood.setDate("02/01/2025");
//        mood.setTime("01:00");
//        mood.setPlace("home");
//        mood.setSituation("tired");
//        mood.setTrigger("work");
//        mood.setExplainText("Lonely");
//        mood.setExplainPicture("Alone");
//
//        assertEquals("Sad", mood.getEmotionalState());
//        assertEquals("02/01/2025", mood.getDate());
//        assertEquals("01:00", mood.getTime());
//        assertEquals("home", mood.getPlace());
//        assertEquals("tired", mood.getSituation());
//        assertEquals("work", mood.getTrigger());
//        assertEquals("Lonely", mood.getExplainText());
//        assertEquals("Alone", mood.getExplainPicture());
//
//
//
//     }
//
//}
