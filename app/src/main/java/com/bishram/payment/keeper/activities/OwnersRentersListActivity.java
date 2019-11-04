package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.OwnerRenterList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bishram.payment.keeper.Constants.FIREBASE_OWNERS_RENTED_PATH;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.KEY_USER;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class OwnersRentersListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonAddORBottom;
    private Button buttonAddORMiddle;

    private ListView listViewORL;

    private LinearLayout layoutEmpty;
    private LinearLayout layoutList;

    private ArrayAdapter<String> arrayAdapterORL;

    private String stringUser;
    private String ownerRenterUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_renters_list);

        initializeViews();

        setButtonClickListeners();

        setORList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (stringUser) {
            case USER_OWNER:
                readOwnersRentedPath();
                break;

            case USER_RENTER:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_or_list_add_new_or_middle:

            case R.id.button_or_list_add_new_or_bottom:
                Intent intent = new Intent(OwnersRentersListActivity.this, AddOwnerRenterActivity.class);
                intent.putExtra(KEY_USER, stringUser);
                startActivity(intent);
                break;
        }
    }

    private void initializeViews() {
        buttonAddORBottom = findViewById(R.id.button_or_list_add_new_or_bottom);
        buttonAddORMiddle = findViewById(R.id.button_or_list_add_new_or_middle);

        listViewORL = findViewById(R.id.list_view_or_list);

        layoutEmpty = findViewById(R.id.linear_or_list_empty_data);
        layoutList = findViewById(R.id.layout_or_list_owners_renters);

        ArrayList<String> arrayListORL = new ArrayList<>();

        arrayAdapterORL = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayListORL);

        stringUser = getIntent().getStringExtra(KEY_USER);
        ownerRenterUID = getIntent().getStringExtra(KEY_UID);
    }

    private void setButtonClickListeners() {
        buttonAddORMiddle.setOnClickListener(this);
        buttonAddORBottom.setOnClickListener(this);
    }

    // Read 'Owners rented' firebase path
    private void readOwnersRentedPath() {
        DatabaseReference referenceToOwnersRentedPath = FirebaseDatabase.getInstance().getReference().child(FIREBASE_OWNERS_RENTED_PATH).child(ownerRenterUID);

        referenceToOwnersRentedPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    arrayAdapterORL.clear();

                    for (DataSnapshot orSnapshot : dataSnapshot.getChildren()) {
                        OwnerRenterList ownerRenterList = orSnapshot.getValue(OwnerRenterList.class);

                        assert ownerRenterList != null;
                        if (ownerRenterList.getStringNickName() != null) {
                            arrayAdapterORL.add(String.format("%s\n%s", ownerRenterList.getStringNickName(), ownerRenterList.getStringStatus()));
                        } else {
                            arrayAdapterORL.add(String.format("%s\n%s", "Unknown Name", ownerRenterList.getStringStatus()));
                        }

                        arrayAdapterORL.notifyDataSetChanged();
                    }

                    layoutEmpty.setVisibility(View.GONE);
                    layoutList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OwnersRentersListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setORList() {
        listViewORL.setAdapter(arrayAdapterORL);
        layoutList.setVisibility(View.VISIBLE);
    }
}
