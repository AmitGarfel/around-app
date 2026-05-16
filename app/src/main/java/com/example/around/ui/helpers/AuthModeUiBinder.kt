package com.example.around.ui.helpers

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.around.ui.formatters.AuthUiFormatter

object AuthModeUiBinder {

    fun bind(
        ui: AuthUiFormatter.AuthModeUi,
        titleText: TextView,
        subtitleText: TextView,
        switchModeText: TextView,
        actionButton: Button,
        firstNameInput: EditText,
        lastNameInput: EditText,
        confirmPasswordInput: EditText
    ) {
        titleText.text = ui.title
        subtitleText.text = ui.subtitle
        switchModeText.text = ui.switchText
        actionButton.text = ui.actionText

        firstNameInput.setVisible(ui.showNameFields)
        lastNameInput.setVisible(ui.showNameFields)
        confirmPasswordInput.setVisible(ui.showConfirmPassword)
    }

    // Toggles view visibility based on flag
    private fun View.setVisible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}