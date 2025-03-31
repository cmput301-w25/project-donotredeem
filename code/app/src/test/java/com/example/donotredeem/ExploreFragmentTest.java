package com.example.donotredeem;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import com.example.donotredeem.Fragments.ExploreFragment;
import com.example.donotredeem.Adapters.UserAdapter;

/**
 * Unit tests for the search functionality in the {@link ExploreFragment} fragment.
 * Verifies behavior of the {@code filterList} method under various search scenarios
 * including edge cases and case sensitivity handling.
 *
 * <p>Key test scenarios include:
 * <ul>
 *     <li>Empty search queries clearing results</li>
 *     <li>Partial match filtering with different casing</li>
 *     <li>No-match scenarios returning empty lists</li>
 *     <li>Case-insensitive search operations</li>
 * </ul>
 *
 * <p>Uses reflection to access private fields/methods and Mockito for adapter mocking
 * to test fragment behavior in isolation.
 */
public class ExploreFragmentTest {

    /**
     * Tests empty search query handling.
     * Verifies:
     * <ul>
     *     <li>Non-empty initial display list ("Alice", "Bob")</li>
     *     <li>Empty query clears filtered results</li>
     *     <li>Adapter receives empty filtered list</li>
     * </ul>
     *
     * @throws Exception if reflection access fails
     */
    @Test
    public void filterList_emptyQuery_returnsEmptyList() throws Exception {
        ExploreFragment exploreFragment = new ExploreFragment();
        setPrivateField(exploreFragment, "displaylist", Arrays.asList("Alice", "Bob"));
        setMockAdapter(exploreFragment); // Set mock adapter

        callFilterList(exploreFragment, "");

        List<String> filteredList = getPrivateField(exploreFragment, "filteredList");
        assertTrue("Filtered list should be empty when query is empty", filteredList.isEmpty());
    }

    /**
     * Tests partial match filtering with substring "li".
     * Verifies:
     * <ul>
     *     <li>Initial list ("Alice", "Bob", "Charlie")</li>
     *     <li>Filtered list contains "Alice" and "Charlie"</li>
     *     <li>Exact match count (2 items)</li>
     * </ul>
     *
     * @throws Exception if reflection access fails
     */
    @Test
    public void filterList_matchingQuery_returnsFilteredUsernames() throws Exception {
        ExploreFragment exploreFragment = new ExploreFragment();
        setPrivateField(exploreFragment, "displaylist", Arrays.asList("Alice", "Bob", "Charlie"));
        setMockAdapter(exploreFragment); // Set mock adapter

        callFilterList(exploreFragment, "li");

        List<String> filteredList = getPrivateField(exploreFragment, "filteredList");
        assertEquals("Filtered list should contain 'Alice' and 'Charlie'",
                Arrays.asList("Alice", "Charlie"), filteredList);
    }

    /**
     * Tests case-insensitive matching with query "A".
     * Verifies:
     * <ul>
     *     <li>Initial list contains varied casing ("alice", "Alice", "ALICE")</li>
     *     <li>All case variants are returned</li>
     *     <li>Complete match set (3 items)</li>
     * </ul>
     *
     * @throws Exception if reflection access fails
     */
    @Test
    public void filterList_caseInsensitiveQuery_returnsAllMatches() throws Exception {
        ExploreFragment exploreFragment = new ExploreFragment();
        setPrivateField(exploreFragment, "displaylist", Arrays.asList("alice", "Alice", "ALICE"));
        setMockAdapter(exploreFragment); // Set mock adapter

        callFilterList(exploreFragment, "A");

        List<String> filteredList = getPrivateField(exploreFragment, "filteredList");
        assertEquals("Filtering should be case-insensitive",
                Arrays.asList("alice", "Alice", "ALICE"), filteredList);
    }

    /**
     * Tests no-match scenario with query "z".
     * Verifies:
     * <ul>
     *     <li>Non-matching initial list ("Bob", "Charlie")</li>
     *     <li>Empty result list returned</li>
     *     <li>Adapter receives empty dataset</li>
     * </ul>
     *
     * @throws Exception if reflection access fails
     */
    @Test
    public void filterList_noMatches_returnsEmptyList() throws Exception {
        ExploreFragment exploreFragment = new ExploreFragment();
        setPrivateField(exploreFragment, "displaylist", Arrays.asList("Bob", "Charlie"));
        setMockAdapter(exploreFragment); // Set mock adapter

        callFilterList(exploreFragment, "z");

        List<String> filteredList = getPrivateField(exploreFragment, "filteredList");
        assertTrue("No matches should return empty list", filteredList.isEmpty());
    }

    /**
     * Injects a mock {@link UserAdapter} into the {@link ExploreFragment} fragment
     * using reflection to bypass Android framework dependencies.
     *
     * @param exploreFragment Target fragment instance
     * @throws Exception if field access/modification fails
     */
    private void setMockAdapter(ExploreFragment exploreFragment) throws Exception {
        UserAdapter mockAdapter = Mockito.mock(UserAdapter.class);
        Field adapterField = ExploreFragment.class.getDeclaredField("adapter");
        adapterField.setAccessible(true);
        adapterField.set(exploreFragment, mockAdapter);
    }

    /**
     * Sets private field values in {@link ExploreFragment} fragment via reflection.
     *
     * @param exploreFragment Target fragment instance
     * @param fieldName Name of private field to modify
     * @param value Value to inject
     * @throws Exception if field access/modification fails
     */
    private void setPrivateField(ExploreFragment exploreFragment, String fieldName, Object value) throws Exception {
        Field field = ExploreFragment.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(exploreFragment, value);
    }


    /**
     * Retrieves private field values from {@link ExploreFragment} fragment via reflection.
     *
     * @param <T> Return type of field
     * @param exploreFragment Target fragment instance
     * @param fieldName Name of private field to access
     * @return Current value of the field
     * @throws Exception if field access fails
     */
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(ExploreFragment exploreFragment, String fieldName) throws Exception {
        Field field = ExploreFragment.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(exploreFragment);
    }

    /**
     * Invokes private {@code filterList} method via reflection.
     *
     * @param exploreFragment Target fragment instance
     * @param query Search query to test
     * @throws Exception if method access/invocation fails
     */
    private void callFilterList(ExploreFragment exploreFragment, String query) throws Exception {
        Method method = ExploreFragment.class.getDeclaredMethod("filterList", String.class);
        method.setAccessible(true);
        method.invoke(exploreFragment, query);
    }
}