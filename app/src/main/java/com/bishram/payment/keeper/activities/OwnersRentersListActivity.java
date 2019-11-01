package com.bishram.payment.keeper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bishram.payment.keeper.R;

public class OwnersRentersListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonAddORMiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_renters_list);

        initializeViews();

        setButtonClickListeners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_or_list_add_new_or_middle:
                startActivity(new Intent(OwnersRentersListActivity.this, AddOwnerRenterActivity.class));
                break;
        }
    }

    private void initializeViews() {
        buttonAddORMiddle = findViewById(R.id.button_or_list_add_new_or_middle);
    }

    private void setButtonClickListeners() {
        buttonAddORMiddle.setOnClickListener(this);
    }
}
