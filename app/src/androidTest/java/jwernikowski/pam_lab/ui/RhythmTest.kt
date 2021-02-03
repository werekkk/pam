package jwernikowski.pam_lab.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.utils.ErrorText
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import java.lang.Integer.max

@LargeTest
class RhythmTest {

    @get:Rule
    val activityRule = ActivityTestRule<MainActivity>(
        MainActivity::class.java)

    @Test
    fun addingRhythmsWorks() {
        val rhythmName = "test rhythm"
        // open the RhythmDetailsActivity
        onView(withId(R.id.navigation_rhythms)).perform(click())
        onView(withId(R.id.add_rhythm_fab)).perform(click())
        // type rhythm name
        onView(withId(R.id.nameEditText)).perform(typeText(rhythmName))
        // type too high tempo
        val enterTempo = activityRule.activity.getString(R.string.enter_tempo)
        onView(withId(R.id.tempoEditText))
            .perform(clearText())
            .check(matches(hasErrorText(enterTempo)))
            .perform(typeText("999"))
            .check(matches(hasErrorText(ErrorText.tempoOutOfRange(activityRule.activity))))
        // open the meter dialog
        onView(withId(R.id.meterEditText))
            .perform(click())
            .inRoot(isDialog())
        onView(withId(R.id.measureText))
            .check(matches(withText(Meter.default().measure.toString())))
        onView(withId(R.id.lengthText))
            .check(matches(withText(Meter.default().length.toString())))
        // change the meter to 2/3
        val adjustment = AdjustMeterProps(Meter.default(), Meter(2, 3))
        repeat(adjustment.measureUp) { run{ onView(withId(R.id.measureUpBtn)).perform(click())}}
        repeat(adjustment.measureDown) { run{ onView(withId(R.id.measureDownBtn)).perform(click())}}
        repeat(adjustment.lengthUp) { run{ onView(withId(R.id.lengthUpBtn)).perform(click())}}
        repeat(adjustment.lengthDown) { run{ onView(withId(R.id.lengthDownBtn)).perform(click())}}
        // check if the meter is 2/3
        onView(withId(R.id.measureText))
            .check(matches(withText("2")))
        onView(withId(R.id.lengthText))
            .check(matches(withText("3")))
        // close the dialog
        onView(isRoot())
            .perform(pressBack())
        // check if the meter is 2/3
        onView(withId(R.id.meterEditText))
            .check(matches(withText(Meter(2, 3).toString())))
        // set the rhythm
        onView(allOf(
            isDescendantOfA(withId(R.id.rhythmDesigner)),
            withTagValue(`is`("WOOD_0"))
        ))
            .perform(click())
        onView(allOf(
            isDescendantOfA(withId(R.id.rhythmDesigner)),
            withTagValue(`is`("TRIANGLE_2"))
        ))
            .perform(click())
        onView(allOf(
            isDescendantOfA(withId(R.id.rhythmDesigner)),
            withTagValue(`is`("WOOD_4"))
        ))
            .perform(click())
        // add rhythm
        onView(withId(R.id.add))
            .perform(click())
        // wait and check if saved
        onView(withText(rhythmName))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.tempoEditText))
            .check(matches(withText("300")))
        // delete
        onView(withId(R.id.delete))
            .perform(click())
    }

    data class AdjustMeterProps(private val given: Meter, private val expected: Meter) {
        val measureUp: Int = max(0, expected.measure - given.measure)
        val measureDown: Int = max(0, given.measure - expected.measure)
        val lengthUp: Int = max(0, expected.length - given.length)
        val lengthDown: Int = max(0, given.length - expected.length)
    }

}