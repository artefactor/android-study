package by.academy.questionnaire.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.academy.questionnaire.R
import by.academy.questionnaire.databinding.CustomDialogBinding

class DialogFragment(val title: String, val infotext: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        this.setStyle(STYLE_NORMAL, R.style.Base_Theme_Questionnaire)
        val dialogView: CustomDialogBinding = CustomDialogBinding.inflate(layoutInflater)
        dialogView.buttonSubmit.setOnClickListener { dialog?.cancel() }
        dialogView.buttonSubmit.text =  getString(R.string.button_yes)
        dialogView.buttonCancel.visibility = GONE
        dialogView.textViewTitle.visibility = GONE
        dialogView.root.setBackgroundColor(resources.getColor(R.color.white))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                    .setView(dialogView.root)
                    .setTitle(title)
                    .setMessage(infotext)
                    .setCancelable(true)
                    .setIcon(R.drawable.ic_noun_questionnaire)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

