package com.example.scheduleapp;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import static androidx.test.espresso.Espresso.onView;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterVehicleActivityTest{
    @Test
    public void test_isActivityInView(){
        ActivityScenario<RegisterVehicleActivity> activityScenario = ActivityScenario.launch(RegisterVehicleActivity.class);
        onView(withId(R.id.registerVehicle)).check(matches(isDisplayed()));
    }
    @Test
    public void test_visibility_title_button(){
        ActivityScenario<RegisterVehicleActivity> activityScenario = ActivityScenario.launch(RegisterVehicleActivity.class);
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
        onView(withId(R.id.registerVehicleButton)).check(matches(isDisplayed()));
    }
    @Test
    public void test_isTitleTextDisplayed(){
        ActivityScenario<RegisterVehicleActivity> activityScenario = ActivityScenario.launch(RegisterVehicleActivity.class);
        onView(withId(R.id.textView)).check(matches(withText(R.string.Please_Enter_Vehicle_Information)));
    }

}