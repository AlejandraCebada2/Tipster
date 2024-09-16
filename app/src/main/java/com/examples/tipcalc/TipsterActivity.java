package com.examples.tipcalc;

import static com.examples.tipcalc.R.*;

import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TipsterActivity extends Activity {
    final static int DEFAULT_NUM_PEOPLE = 3;

    final static NumberFormat formatter = NumberFormat.getCurrencyInstance();

    // Widgets in the application
    private EditText txtAmount;
    private EditText txtPeople;
    private EditText txtTipOther;
    private RadioGroup rdoGroupTips;
    private Button btnCalculate;
    private Button btnReset;

    private TextView txtTipAmount;
    private TextView txtTotalToPay;
    private TextView txtTipPerPerson;

    // For the id of radio button selected
    private int radioCheckedId = -1;
    private NumberPickerLogic mLogic;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tipster_activity);

        // Access the various widgets by their id in R.java
        txtAmount = (EditText) findViewById(R.id.txtAmount);

        // On app load, the cursor should be in the Amount field
        txtAmount.requestFocus();

        txtPeople = (EditText) findViewById(id.txtPeople);
        txtPeople.setText(Integer.toString(DEFAULT_NUM_PEOPLE));

        txtTipOther = (EditText) findViewById(R.id.txtTipOther);

        rdoGroupTips = (RadioGroup) findViewById(R.id.RadioGroupTips);

        btnCalculate = (Button) findViewById(R.id.btnCalculate);
        // On app load, the Calculate button is disabled
        btnCalculate.setEnabled(false);

        btnReset = (Button) findViewById(R.id.btnReset);

        txtTipAmount = (TextView) findViewById(R.id.txtTipAmount);
        txtTotalToPay = (TextView) findViewById(R.id.txtTotalToPay);
        txtTipPerPerson = (TextView) findViewById(R.id.txtTipPerPerson);

        // On app load, disable the 'Other tip' percentage text field
        txtTipOther.setEnabled(false);

        /*
         * Attach a OnCheckedChangeListener to the radio group to monitor radio
         * buttons selected by user
         */
        rdoGroupTips.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Habilitar o deshabilitar el campo de porcentaje de propina
                if (checkedId == R.id.radioOther) {
                    txtTipOther.setEnabled(true);
                    txtTipOther.requestFocus();
                } else {
                    txtTipOther.setEnabled(false);
                }

                // Actualizar el estado del botón Calcular
                boolean isAmountValid = !txtAmount.getText().toString().trim().isEmpty();
                boolean isPeopleValid = !txtPeople.getText().toString().trim().isEmpty();
                boolean isTipOtherValid = !txtTipOther.getText().toString().trim().isEmpty();

                if (checkedId == R.id.radioOther) {
                    btnCalculate.setEnabled(isAmountValid && isPeopleValid && isTipOtherValid);
                } else {
                    btnCalculate.setEnabled(isAmountValid && isPeopleValid);
                }

                // Actualizar el ID del radio button seleccionado
                radioCheckedId = checkedId;
            }
        });

        /*
         * Attach a KeyListener to the Tip Amount, No. of People and Other Tip Percentage
         * text fields
         */
        txtAmount.setOnKeyListener(mKeyListener);
        txtPeople.setOnKeyListener(mKeyListener);
        txtTipOther.setOnKeyListener(mKeyListener);

        btnCalculate.setOnClickListener(mClickListener);
        btnReset.setOnClickListener(mClickListener);

        /** Create a NumberPickerLogic to handle the + and - keys */
        mLogic = new NumberPickerLogic(txtPeople, 1, Integer.MAX_VALUE);
    }

    /*
     * KeyListener for the Total Amount, No of People and Other Tip Percentage
     * fields. We need to apply this key listener to check for following
     * conditions:
     *
     * 1) If user selects Other tip percentage, then the other tip text field
     * should have a valid tip percentage entered by the user. Enable the
     * Calculate button only when user enters a valid value.
     *
     * 2) If user does not enter values in the Total Amount and No of People,
     * we cannot perform the calculations. Hence enable the Calculate button
     * only when user enters a valid values.
     */
    private OnKeyListener mKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // Actualizar el estado del botón Calcular
            boolean isAmountValid = !txtAmount.getText().toString().trim().isEmpty();
            boolean isPeopleValid = !txtPeople.getText().toString().trim().isEmpty();
            boolean isTipOtherValid = !txtTipOther.getText().toString().trim().isEmpty();

            if (v.getId() == R.id.txtAmount || v.getId() == R.id.txtPeople || v.getId() == R.id.txtTipOther) {
                if (rdoGroupTips.getCheckedRadioButtonId() == R.id.radioOther) {
                    btnCalculate.setEnabled(isAmountValid && isPeopleValid && isTipOtherValid);
                } else {
                    btnCalculate.setEnabled(isAmountValid && isPeopleValid);
                }
            }

            return false; // Permitir que el evento continúe
        }
    };



    /**
     * ClickListener for the Calculate and Reset buttons. Depending on the button
     * clicked, the corresponding method is called.
     */
    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.btnCalculate) {
                calculate();
            } else {
                reset();
            }
        }
    };

    /**
     * Resets the results text views at the bottom of the screen as well as resets
     * the text fields and radio buttons.
     */
    private void reset() {
        txtTipAmount.setText("");
        txtTotalToPay.setText("");
        txtTipPerPerson.setText("");
        txtAmount.setText("");
        txtPeople.setText(Integer.toString(DEFAULT_NUM_PEOPLE));
        txtTipOther.setText("");
        rdoGroupTips.clearCheck();
        rdoGroupTips.check(R.id.radioFifteen);
        btnCalculate.setEnabled(false); // Deshabilitar el botón "Calcular"
        txtAmount.requestFocus();
    }

    public void decrement(View v) {
        mLogic.decrement();
    }

    public void increment(View v) {
        mLogic.increment();
    }

    /**
     * Calculate the tip as per data entered by the user.
     */
    private void calculate() {
        try {
            double billAmount = Double.parseDouble(txtAmount.getText().toString());
            int totalPeople = Integer.parseInt(txtPeople.getText().toString());
            double percentage = 0.0;

            if (billAmount < 1.0) {
                showErrorAlert("Enter a valid Total Amount.", txtAmount.getId());
                return;
            }

            if (totalPeople < 1) {
                showErrorAlert("Enter a valid number of people.", txtPeople.getId());
                return;
            }

            if (radioCheckedId == -1) {
                radioCheckedId = rdoGroupTips.getCheckedRadioButtonId();
            }

            if (radioCheckedId == R.id.radioFifteen) {
                percentage = 15.00;
            } else if (radioCheckedId == R.id.radioTwenty) {
                percentage = 20.00;
            } else if (radioCheckedId == R.id.radioOther) {
                percentage = Double.parseDouble(txtTipOther.getText().toString());
                if (percentage < 1.0) {
                    showErrorAlert("Enter a valid Tip percentage", txtTipOther.getId());
                    return;
                }
            }

            // Calculate the tip amount
            double tipAmount = (billAmount * percentage) / 100;

            // Calculate the total to pay
            double totalToPay = billAmount + tipAmount;

            // Calculate the tip per person
            double tipPerPerson = tipAmount / totalPeople; // Corrected formula

            // Format the results
            txtTipAmount.setText(formatter.format(tipAmount));
            txtTotalToPay.setText(formatter.format(totalToPay));
            txtTipPerPerson.setText(formatter.format(tipPerPerson));
        } catch (NumberFormatException e) {
            // Handle invalid input
            showErrorAlert("Invalid input. Please enter valid values.", txtAmount.getId());
        }
    }


    private void showErrorAlert(String errorMessage, final int fieldId) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setNeutralButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                findViewById(fieldId).requestFocus();
                            }
                        }).show();
    }
}

