package by.academy.lesson8.part2.helper

import android.content.res.ColorStateList
import android.os.Build
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import by.academy.lesson8.part2.R

const val WS_IN_PROGRESS = 0
const val WS_COMPLETED = 2
const val WS_PENDING = 4

private const val WS_IN_PROGRESS_COLOR = R.color.yellow
private const val WS_COMPLETED_COLOR = R.color.green
private const val WS_PENDING_COLOR = R.color.red

private const val INACTIVE_COLOR = R.color.purple_700

@RequiresApi(Build.VERSION_CODES.M)
class WorkStatusComponent(
        private val workStatusRadioGroup: RadioGroup,
        private val wStatusInProgress: RadioButton,
        private val wStatusCompleted: RadioButton,
        private val wStatusInPending: RadioButton,
) {
    constructor(radioGroup: RadioGroup) :
            this(
                    radioGroup,
                    radioGroup.findViewById(R.id.workStatusInProgress),
                    radioGroup.findViewById(R.id.workStatusCompleted),
                    radioGroup.findViewById(R.id.workStatusPending)

            )

    fun init() {
        workStatusRadioGroup.setOnCheckedChangeListener { _, _ -> setWorkStatusByIndex(getStatusByButtonId()) }
    }

    internal fun setWorkStatus(status: Int) {
        setWorkStatusByIndex(status)
        workStatusRadioGroup.check(getButtonIdByStatus(status))
    }

    private fun setWorkStatusByIndex(checkedIndex: Int) {
        val status = checkedIndex;
        when (status) {
            WS_IN_PROGRESS -> {
                lightOn(wStatusInProgress, WS_IN_PROGRESS_COLOR)
                lightOFF(wStatusCompleted)
                lightOFF(wStatusInPending)
            }
            WS_COMPLETED -> {
                lightOFF(wStatusInProgress)
                lightOn(wStatusCompleted, WS_COMPLETED_COLOR)
                lightOFF(wStatusInPending)
            }
            WS_PENDING -> {
                lightOFF(wStatusInProgress)
                lightOFF(wStatusCompleted)
                lightOn(wStatusInPending, WS_PENDING_COLOR)
            }
        }
    }

    private fun lightOFF(wStatusRadio: RadioButton) {
        val color = ContextCompat.getColor(wStatusRadio.context, INACTIVE_COLOR)
        wStatusRadio.compoundDrawableTintList = ColorStateList.valueOf(color)
        wStatusRadio.setTextColor(color)
    }

    private fun lightOn(wStatusRadio: RadioButton, colorCode: Int) {
        val color = ContextCompat.getColor(wStatusRadio.context, colorCode)
        wStatusRadio.compoundDrawableTintList = ColorStateList.valueOf(color)
        wStatusRadio.setTextColor(color)
    }

    fun getStatusByButtonId(): Int = when (workStatusRadioGroup.checkedRadioButtonId) {
        R.id.workStatusInProgress -> WS_IN_PROGRESS
        R.id.workStatusCompleted -> WS_COMPLETED
        R.id.workStatusPending -> WS_PENDING
        else -> WS_IN_PROGRESS
    }

    private fun getButtonIdByStatus(status: Int): Int = when (status) {
        WS_IN_PROGRESS -> R.id.workStatusInProgress
        WS_COMPLETED -> R.id.workStatusCompleted
        WS_PENDING -> R.id.workStatusPending
        else -> R.id.workStatusInProgress
    }

    companion object {
        fun getColorByStatus(status: Int): Int =
                when (status) {
                    WS_IN_PROGRESS -> WS_IN_PROGRESS_COLOR
                    WS_COMPLETED -> WS_COMPLETED_COLOR
                    WS_PENDING -> WS_PENDING_COLOR
                    else -> WS_IN_PROGRESS_COLOR
                }
    }
}


