package com.example.imagepro.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagepro.BkashActivity;
import com.example.imagepro.CameraActivity;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;
//import com.example.imagepro.textToSign;
import com.google.android.material.button.MaterialButton;

public class PaymentFragment extends Fragment {


   // Button pay,test;
    Button pay,tst;
    EditText getAmount;
    TextView tv;
    double amount;
    ConstraintLayout theme_payment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_payment.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_payment.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        theme_payment = view.findViewById(R.id.theme_payment);
        pay = view.findViewById(R.id.paymentBtn);
        getAmount = view.findViewById(R.id.paymentText);
//        tst = view.findViewById(R.id.tst);
        tv = view.findViewById(R.id.textView);

//        tst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAmount.setText("");
//            }
//        });

      //  tv.setText(txt);

       // if (amount < 1) {
            // etAmount.setError("You have to pay at least BDT 1. ");
            //etAmount.requestFocus();
            //return;
       // }   // here you need to check internet connection on another condition like if(is_online)
       // else {
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(),"OY PAY", Toast.LENGTH_LONG).show();
                    String txt = getAmount.getText().toString().trim();
                    amount = 0.0;

                    try {
                        amount = Double.parseDouble(txt);   // use try catch so that, if input is invalid, stop taking payment here.
                    } catch (Exception e) {
                        amount = 0.0;
                        Log.e("AmountDebug", "Error parsing amount: "+"..."+txt+"..." + e.getMessage());
                    }
                    if(amount<1)
                    {

                    }
                    else {
                        getAmount.setText("");
                    }
                    //getAmount.setText("");
                    Intent intent = new Intent(getActivity(), BkashActivity.class);
                    // startActivity(new Intent();//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    intent.putExtra("AMOUNT", String.valueOf(amount));  //sent amount to bkash activity
                    startActivity(intent);
                }
            });
        //}
    }



}