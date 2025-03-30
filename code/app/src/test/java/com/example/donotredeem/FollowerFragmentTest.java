package com.example.donotredeem.Fragments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class FollowerFragmentTest {

    private static final String TEST_USERNAME = "testUser";

    @Before
    public void setUp() {
        // No setup that requires Android framework classes
    }

    @Test
    public void testFragmentCanBeInstantiated() {
        // Simply test that we can create an instance without errors
        FollowerFragment fragment = new FollowerFragment();
        assertNotNull("Fragment should not be null", fragment);
    }
}