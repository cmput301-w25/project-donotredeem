package com.example.donotredeem;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import androidx.test.espresso.matcher.BoundedMatcher;

/**
 * A utility class that provides custom matchers for use in Espresso UI tests.
 */
public class CustomMatchers {

    /**
     * Returns a matcher that checks if an {@link EditText} has text with a length matching the given condition.
     *
     * @param lengthMatcher A matcher that defines the expected text length constraint.
     * @return A matcher that verifies whether the length of the text in an {@link EditText} meets the specified condition.
     */
    public static Matcher<View> withTextLength(final Matcher<Integer> lengthMatcher) {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            /**
             * Checks whether the given {@link EditText} has text that satisfies the specified length constraint.
             *
             * @param editText The {@link EditText} being evaluated.
             * @return {@code true} if the text length matches the given condition, otherwise {@code false}.
             */
            @Override
            protected boolean matchesSafely(EditText editText) {
                String text = editText.getText().toString();
                return lengthMatcher.matches(text.length());
            }

            /**
             * Describes the matcher for debugging and error reporting.
             *
             * @param description The description to be appended with matcher details.
             */
            @Override
            public void describeTo(Description description) {
                description.appendText("with text length: ");
                lengthMatcher.describeTo(description);
            }
        };
    }
}
