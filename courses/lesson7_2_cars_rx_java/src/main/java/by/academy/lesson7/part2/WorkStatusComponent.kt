package by.academy.lesson7.part2

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import by.academy.lesson7.part2.R

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
        workStatusRadioGroup.setOnCheckedChangeListener { _, _ -> setWorkStatusByIndex(getStatus()) }
    }

    internal fun setWorkStatus(status: Int) {
        setWorkStatusByIndex(status)
        workStatusRadioGroup.check(getStatusButtonId(status))
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
        val color = ContextCompat.getColor(wStatusRadio.context, INACTIVE_COLOR)
        wStatusRadio.compoundDrawableTintList = ColorStateList.valueOf(color)
        wStatusRadio.setTextColor(color)
    }

    private fun lightOn(wStatusRadio: RadioButton, colorCode: Int) {
        val color = ContextCompat.getColor(wStatusRadio.context, colorCode)
        wStatusRadio.compoundDrawableTintList = ColorStateList.valueOf(color)
        wStatusRadio.setTextColor(color)
    }

    fun getStatus(): Int = when (workStatusRadioGroup.checkedRadioButtonId) {
        wStatusInProgress.id -> WS_IN_PROGRESS
        wStatusCompleted.id -> WS_COMPLETED
        wStatusInPending.id -> WS_PENDING
        else -> WS_IN_PROGRESS
    }

    fun getStatusButtonId(status: Int): Int = when (status) {
        WS_IN_PROGRESS -> wStatusInProgress.id
        WS_COMPLETED -> wStatusCompleted.id
        WS_PENDING -> wStatusInPending.id
        else -> wStatusInProgress.id
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


