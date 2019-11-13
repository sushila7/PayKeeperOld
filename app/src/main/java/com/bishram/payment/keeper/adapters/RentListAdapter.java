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
import com.bishram.payment.keeper.models.OwnerRenterList;

import java.util.ArrayList;
import java.util.Locale;

public class RentListAdapter extends ArrayAdapter<OwnerRenterList> {
    public RentListAdapter(@NonNull Context context, ArrayList<OwnerRenterList> ownerRenterList) {
        super(context, 0, ownerRenterList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_owners_renters, parent, false);
        }

        TextView textViewSerialNo = convertView.findViewById(R.id.text_view_li_or_serial_number);
        TextView textViewName = convertView.findViewById(R.id.text_view_li_or_name);
        TextView textViewPhone = convertView.findViewById(R.id.text_view_li_or_mob_number);
        TextView textViewStatus = convertView.findViewById(R.id.text_view_li_or_status);

        OwnerRenterList currentOR = getItem(position);

        assert currentOR != null;
        if (position<9) {
            textViewSerialNo.setText(String.format(Locale.getDefault(),"0%d", position+1));
        } else {
            textViewSerialNo.setText(String.valueOf(position+1));
        }

        textViewName.setText(currentOR.getFullName());
        textViewPhone.setText(currentOR.getPhoneNumber());
        textViewStatus.setText(currentOR.getCurrentStatus());

        return convertView;
    }
}
