package com.example.donotredeem;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.Explore;
import com.example.donotredeem.Fragments.UserAdapter;

public class ExploreTest {

    @Test
    public void filterList_emptyQuery_returnsEmptyList() throws Exception {
        Explore explore = new Explore();
        setPrivateField(explore, "displaylist", Arrays.asList("Alice", "Bob"));
        setMockAdapter(explore); // Set mock adapter

        callFilterList(explore, "");

        List<String> filteredList = getPrivateField(explore, "filteredList");
        assertTrue("Filtered list should be empty when query is empty", filteredList.isEmpty());
    }

    @Test
    public void filterList_matchingQuery_returnsFilteredUsernames() throws Exception {
        Explore explore = new Explore();
        setPrivateField(explore, "displaylist", Arrays.asList("Alice", "Bob", "Charlie"));
        setMockAdapter(explore); // Set mock adapter

        callFilterList(explore, "li");

        List<String> filteredList = getPrivateField(explore, "filteredList");
        assertEquals("Filtered list should contain 'Alice' and 'Charlie'",
                Arrays.asList("Alice", "Charlie"), filteredList);
    }

    @Test
    public void filterList_caseInsensitiveQuery_returnsAllMatches() throws Exception {
        Explore explore = new Explore();
        setPrivateField(explore, "displaylist", Arrays.asList("alice", "Alice", "ALICE"));
        setMockAdapter(explore); // Set mock adapter

        callFilterList(explore, "A");

        List<String> filteredList = getPrivateField(explore, "filteredList");
        assertEquals("Filtering should be case-insensitive",
                Arrays.asList("alice", "Alice", "ALICE"), filteredList);
    }

    @Test
    public void filterList_noMatches_returnsEmptyList() throws Exception {
        Explore explore = new Explore();
        setPrivateField(explore, "displaylist", Arrays.asList("Bob", "Charlie"));
        setMockAdapter(explore); // Set mock adapter

        callFilterList(explore, "z");

        List<String> filteredList = getPrivateField(explore, "filteredList");
        assertTrue("No matches should return empty list", filteredList.isEmpty());
    }

    // Helper method to set a mock UserAdapter
    private void setMockAdapter(Explore explore) throws Exception {
        UserAdapter mockAdapter = Mockito.mock(UserAdapter.class);
        Field adapterField = Explore.class.getDeclaredField("adapter");
        adapterField.setAccessible(true);
        adapterField.set(explore, mockAdapter);
    }

    // Existing reflection helpers
    private void setPrivateField(Explore explore, String fieldName, Object value) throws Exception {
        Field field = Explore.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(explore, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Explore explore, String fieldName) throws Exception {
        Field field = Explore.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(explore);
    }

    private void callFilterList(Explore explore, String query) throws Exception {
        Method method = Explore.class.getDeclaredMethod("filterList", String.class);
        method.setAccessible(true);
        method.invoke(explore, query);
    }
}