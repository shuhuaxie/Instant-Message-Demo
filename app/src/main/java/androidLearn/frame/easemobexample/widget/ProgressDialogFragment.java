package androidLearn.frame.easemobexample.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import androidLearn.frame.easemobexample.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;


import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProgressDialogFragment extends DialogFragment {

  public static final String ARGUMENT_TEXT = "argument_text";

  @InjectView(R.id.loading_text) TextView mTextView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.progress_dialog, container, false);
    ButterKnife.inject(this, rootView);
    mTextView.setText(getArguments().getString(ARGUMENT_TEXT));
    return rootView;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  public void setText(String text) {
    if (mTextView != null) {
      mTextView.setText(text);
    }
  }
}
