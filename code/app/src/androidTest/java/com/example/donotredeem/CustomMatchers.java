package com.example.donotredeem;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import androidx.test.espresso.matcher.BoundedMatcher;

public class CustomMatchers {

    public static Matcher<View> withTextLength(final Matcher<Integer> lengthMatcher) {
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            protected boolean matchesSafely(EditText editText) {
                String text = editText.getText().toString();
                return lengthMatcher.matches(text.length());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text length: ");
                lengthMatcher.describeTo(description);
            }
        };
    }
}
