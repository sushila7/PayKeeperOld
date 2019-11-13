package com.bishram.payment.keeper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.RentPaidReceived;

import java.util.ArrayList;

public class PaymentListAdapter extends ArrayAdapter<RentPaidReceived> {
    public PaymentListAdapter(@NonNull Context context, ArrayList<RentPaidReceived> arrayListPayment) {
        super(context, 0, arrayListPayment);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_payment, parent, false);
        }

        TextView textViewDatePaid = convertView.findViewById(R.id.text_view_li_ppr_date);
        TextView textViewAmount = convertView.findViewById(R.id.text_view_li_ppr_amount);

        RentPaidReceived currentPayment = getItem(position);

        assert currentPayment != null;
        textViewDatePaid.setText(currentPayment.getDateOfPayment());
        textViewAmount.setText(String.format("Rs. %s/-", currentPayment.getAmountPaidReceived()));

        return convertView;
    }
}
