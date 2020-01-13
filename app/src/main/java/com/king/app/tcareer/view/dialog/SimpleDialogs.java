package com.king.app.tcareer.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;

/**
 * @desc
 * @auth 景阳
 * @time 2018/3/23 0023 20:20
 */

public class SimpleDialogs {

    public interface OnDialogActionListener {
        void onOk(String name);
    }

    public void openInputDialog(Context context, String msg, final OnDialogActionListener listener) {
        openInputDialog(context, msg, null, listener);
    }

    public void openInputDialog(Context context, String msg, String initialText, final OnDialogActionListener listener) {
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(40, 10, 40, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        EditText edit = new EditText(context);
        edit.setLayoutParams(params);
        if (!TextUtils.isEmpty(initialText)) {
            edit.setText(initialText);
        }
        layout.addView(edit);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if (msg == null) {
            dialog.setMessage("input content");
        }
        else {
            dialog.setMessage(msg);
        }
        dialog.setView(layout);

        final EditText folderEdit = edit;
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folderName = folderEdit.getText().toString();
                listener.onOk(folderName);
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }

    public void showWarningActionDialog(Context context, String msg, String okText, String neutralText,
                                        final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warning);
        builder.setMessage(msg);
        builder.setPositiveButton(okText, listener);
        if (neutralText != null) {
            builder.setNeutralButton(neutralText, listener);
        }
        builder.setNegativeButton(R.string.cancel, listener);
        builder.show();
    }

    public void showWarningActionDialog(Context context, String msg, String positiveText, String neutralText, String negativeText,
                                        final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warning);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveText, listener);
        if (neutralText != null) {
            builder.setNeutralButton(neutralText, listener);
        }
        builder.setNegativeButton(negativeText, listener);
        builder.show();
    }

}
