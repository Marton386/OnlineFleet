package anless.fleetmanagement.core.app.presentation.utils

import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import anless.fleetmanagement.R

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showErrorDialog(error: String): AlertDialog? {
    val alertDialog = showMessageDialog(error)
    alertDialog!!.findViewById<TextView>(R.id.tv_title)?.visibility = View.VISIBLE
    return alertDialog
}

fun Fragment.showMessageDialog(message: String): AlertDialog? {
    val alertDialogBuilder = AlertDialog.Builder(requireContext())
        .setView(R.layout.dialog_message)

    val dialog = alertDialogBuilder.show()

    dialog.findViewById<TextView>(R.id.tv_message)?.setText(message)
    dialog.findViewById<Button>(R.id.btn_ok)?.setOnClickListener {
        dialog.dismiss()
    }

    return dialog
}

fun Fragment.showConfirmDialog(message: String): AlertDialog? {
    val alertDialogBuilder = AlertDialog.Builder(requireContext())
        .setView(R.layout.dialog_confirm)

    val dialog = alertDialogBuilder.show()

    dialog.findViewById<TextView>(R.id.tv_message)?.setText(message)
    dialog.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
        dialog.dismiss()
    }


    return dialog
}