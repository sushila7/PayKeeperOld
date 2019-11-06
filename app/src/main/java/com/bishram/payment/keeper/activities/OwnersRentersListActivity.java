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
import android.widget.TextView;
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
import static com.bishram.payment.keeper.Constants.FIREBASE_RENTERS_OWNED_PATH;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;


public class OwnersRentersListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonAddORBottom;
    private Button buttonAddORMiddle;

    private ListView listViewORL;

    private LinearLayout layoutEmpty;
    private LinearLayout layoutList;

    private TextView textViewEmptyList;

    private ArrayAdapter<String> arrayAdapterORL;

    DatabaseReference referenceRoot;

    private String mCategory;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_renters_list);

        getIntents();

        initializeViews();

        initializeFirebaseOb();

        setButtonClickListeners();

        setORList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (mCategory) {
            case USER_OWNER:
                setTitle("All Renters");
                readOwnedRentedPath(referenceRoot.child(FIREBASE_OWNERS_RENTED_PATH).child(mUid),
                        getString(R.string.tv_text_list_empty_renter),
                        getString(R.string.btn_text_add_new_renter));
                break;

            case USER_RENTER:
                setTitle("All Owners");
                readOwnedRentedPath(referenceRoot.child(FIREBASE_RENTERS_OWNED_PATH).child(mUid),
                        getString(R.string.tv_text_list_empty_owner),
                        getString(R.string.btn_text_add_new_owner));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_or_list_add_new_middle:

            case R.id.button_or_list_add_new_or_bottom:
                Intent intent = new Intent(OwnersRentersListActivity.this, AddOwnerRenterActivity.class);
                intent.putExtra(KEY_CATEGORY, mCategory);
                intent.putExtra(KEY_UID, mUid);
                startActivity(intent);
                break;
        }
    }

    private void getIntents() {
        mCategory = getIntent().getStringExtra(KEY_CATEGORY);
        mUid = getIntent().getStringExtra(KEY_UID);
    }

    private void initializeViews() {
        buttonAddORBottom = findViewById(R.id.button_or_list_add_new_or_bottom);
        buttonAddORMiddle = findViewById(R.id.button_or_list_add_new_middle);

        listViewORL = findViewById(R.id.list_view_or_list);

        layoutEmpty = findViewById(R.id.linear_or_list_empty_data);
        layoutList = findViewById(R.id.layout_or_list_owners_renters);

        textViewEmptyList = findViewById(R.id.text_view_or_list_empty);

        ArrayList<String> arrayListORL = new ArrayList<>();

        arrayAdapterORL = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayListORL);
    }

    private void initializeFirebaseOb() {
        referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    private void setButtonClickListeners() {
        buttonAddORMiddle.setOnClickListener(this);
        buttonAddORBottom.setOnClickListener(this);
    }

    // Read 'Owner's rented' as well as 'Renter's owned' firebase path
    private void readOwnedRentedPath(DatabaseReference currentDbReference,
                                     final String emptyTextMiddle,
                                     final String btnTextOR) {
        currentDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    arrayAdapterORL.clear();

                    for (DataSnapshot renterSnapshot : dataSnapshot.getChildren()) {
                        OwnerRenterList currentOR = renterSnapshot.getValue(OwnerRenterList.class);

                        assert currentOR != null;
                        arrayAdapterORL.add(String.format("%s\n%s", currentOR.getFullName(), currentOR.getCurrentStatus()));
                    }

                    arrayAdapterORL.notifyDataSetChanged();

                    buttonAddORBottom.setText(btnTextOR);
                    layoutEmpty.setVisibility(View.GONE);
                    layoutList.setVisibility(View.VISIBLE);
                } else {
                    textViewEmptyList.setText(emptyTextMiddle);
                    buttonAddORMiddle.setText(btnTextOR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OwnersRentersListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                textViewEmptyList.setText(getString(R.string.tv_text_server_access_denied));
                buttonAddORMiddle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setORList() {
        listViewORL.setAdapter(arrayAdapterORL);
    }
}
