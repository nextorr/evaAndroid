package com.regional.autonoma.corporacion.eva.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.R;

/**
 * Created by nestor on 02-Nov-16.
 * this class contains the definitions to show the score card
 */
public class EvaluationDialogFragment extends DialogFragment {


    private String points;
    private String evaluationResult;
    private String minimumValue;
    private String maximumValue;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_evaluation, null);

        //setting the values for the view
        ((TextView)rootView.findViewById(R.id.textView_dialog_result)).setText(
                evaluationResult
        );
        ((TextView)rootView.findViewById(R.id.textView_dialog_points)).setText(
                points
        );
        ((TextView)rootView.findViewById(R.id.textView_dialog_minPoints)).setText(
                minimumValue
        );
        ((TextView)rootView.findViewById(R.id.textView_dialog_maxPoints)).setText(
                maximumValue
        );

        builder.setView(rootView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    //this is used to set up the inital detail.
    public void setScoreCard (boolean viewed, boolean passed, int obtainedPoints, int maxPoints, Resources strings){
        
        if(viewed){
            evaluationResult = passed ? strings.getString(R.string.quizAproved): strings.getString(R.string.quizFailed);
            points = String.valueOf(obtainedPoints);
        }
        else{
            evaluationResult = strings.getString(R.string.quizPending);
            points = "--";
        }
        minimumValue = String.valueOf((int)(Math.ceil((double)maxPoints*0.6)));
        maximumValue = String.valueOf(maxPoints);
    }

    //use this to update the values on the score card
    public void setScoreCard (boolean passed, int obtainedPoints, Resources strings){

        evaluationResult = passed ? strings.getString(R.string.quizAproved): strings.getString(R.string.quizFailed);
        points = String.valueOf(obtainedPoints);

    }
}
