package com.hamzakhalid.i210704
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.hamzakhalid.integration.R
import org.junit.Before
import org.junit.Test

class Screen2ActivityTest {
    private lateinit var scenario: ActivityScenario<Screen2Activity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(Screen2Activity::class.java)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
    /*
    @Test
    fun testButtonClick(){
        Thread.sleep(3000)
        onView(withId(R.id.LoginBtn)).perform(click())
    }*/
    @Test
    fun testLoginButton() {
        // Enter email and close keyboard
        onView(withId(R.id.editTextEmail)).perform(typeText("hammad@gmail.com"), closeSoftKeyboard())

        // Enter password and close keyboard
        onView(withId(R.id.editTextPassword)).perform(typeText("hammad123"), closeSoftKeyboard())

        // Click on the login button
        onView(withId(R.id.LoginBtn)).perform(click())

    }
}

