package com.example.donotredeem;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.moodhistory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(JUnit4.class)
public class MoodHistoryTest {

    private moodhistory fragment;

    @Before
    public void setUp() {
        fragment = new moodhistory();
    }

    @Test
    public void testParseStringToDate_ValidFormat() {
        LocalDate date = fragment.parseStringToDate("25/12/2023");
        assertEquals(2023, date.getYear());
        assertEquals(12, date.getMonthValue());
        assertEquals(25, date.getDayOfMonth());
    }

    @Test
    public void testParseStringToDate_LeapYear() {
        LocalDate date = fragment.parseStringToDate("29/02/2020");
        assertEquals(2020, date.getYear());
        assertEquals(2, date.getMonthValue());
        assertEquals(29, date.getDayOfMonth());
    }


    @Test
    public void testParseStringToTime_ValidWithoutSeconds() {
        LocalTime time = fragment.parseStringToTime("23:59");
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        assertEquals(0, time.getSecond());
    }

    @Test
    public void testParseStringToTime_ValidWithSeconds() {
        LocalTime time = fragment.parseStringToTime("12:34:56");
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        assertEquals(56, time.getSecond());
    }

    @Test
    public void testParseStringToTime_Midnight() {
        LocalTime time = fragment.parseStringToTime("00:00");
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }
}

