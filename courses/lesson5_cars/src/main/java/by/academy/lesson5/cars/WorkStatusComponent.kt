package by.academy.lesson5.cars

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi

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
        private val resources: Resources
) {

    fun init() {
        workStatusRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val checkedIndex: Int =
                    workStatusRadioGroup.indexOfChild(
                            workStatusRadioGroup.findViewById(checkedId))
            setWorkStatusByIndex(checkedIndex)
        }
    }

    internal fun setWorkStatus(status: Int) {
        setWorkStatusByIndex(status)
        workStatusRadioGroup.check(status)
    }

    private fun setWorkStatusByIndex(checkedIndex: Int) {
        when (checkedIndex) {
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
        val color = resources.getColor(INACTIVE_COLOR)
        wStatusRadio.compoundDrawableTintList = ColorStateList.valueOf(color)
        wStatusRadio.setTextColor(color)
    }

    private fun lightOn(wStatusRadio: RadioButton, colorCode: Int) {
        val color = resources.getColor(colorCode)
        wStatusRadio.compoundDrawableTintList = ColorStateList.valueOf(color)
        wStatusRadio.setTextColor(color)
    }

    fun getStatus(): Int {
        when (workStatusRadioGroup.checkedRadioButtonId) {
            wStatusInProgress.id -> return WS_IN_PROGRESS
            wStatusCompleted.id -> return WS_COMPLETED
            wStatusInPending.id -> return WS_PENDING
        }
        return WS_IN_PROGRESS
    }

    companion object {
        fun statusColor(status: Int): Int =
                when (status) {
                    WS_IN_PROGRESS -> WS_IN_PROGRESS_COLOR
                    WS_COMPLETED -> WS_COMPLETED_COLOR
                    WS_PENDING -> WS_PENDING_COLOR
                    else -> WS_IN_PROGRESS_COLOR
                }
    }
}


