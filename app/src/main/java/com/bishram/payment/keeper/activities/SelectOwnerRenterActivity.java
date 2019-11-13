package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.adapters.RentListAdapter;
import com.bishram.payment.keeper.models.OwnerRenterList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bishram.payment.keeper.Constants.FIREBASE_OWNERS_RENTED_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_RENTERS_OWNED_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_RENT_PAID_RECEIVED;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_OWNER_RENTER_NAME;
import static com.bishram.payment.keeper.Constants.KEY_OWNER_RENTER_UID;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class SelectOwnerRenterActivity extends AppCompatActivity {

    private ListView listViewRentTrans;

    private ArrayList<OwnerRenterList> ownerRenterList;

    private RentListAdapter rentListAdapter;

    private DatabaseReference referenceRoot;

    private String mCategory;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_owner_renter);

        getIntents();

        initializeViews();

        readFirebaseOb();

        setTransList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (mCategory) {
            case USER_OWNER:
                setTitle("Select renter");
                readOwnedRentedPath(referenceRoot.child(FIREBASE_OWNERS_RENTED_PATH).child(mUid));
                break;

            case USER_RENTER:
                setTitle("Select owner");
                readOwnedRentedPath(referenceRoot.child(FIREBASE_RENTERS_OWNED_PATH).child(mUid));
                break;
        }
    }

    private void getIntents() {
        mCategory = getIntent().getStringExtra(KEY_CATEGORY);
        mUid = getIntent().getStringExtra(KEY_UID);
    }

    private void initializeViews() {
        listViewRentTrans = findViewById(R.id.list_view_trans_rent);

        ownerRenterList = new ArrayList<>();

        rentListAdapter = new RentListAdapter(getApplicationContext(), ownerRenterList);
    }

    private void readFirebaseOb() {
        referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    private void setTransList() {
        listViewRentTrans.setAdapter(rentListAdapter);
        addListViewItemListener();
    }

    // Read 'Owner's rented' as well as 'Renter's owned' firebase path
    private void readOwnedRentedPath(DatabaseReference currentDbReference) {
        currentDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    rentListAdapter.clear();

                    for (DataSnapshot renterSnapshot : dataSnapshot.getChildren()) {
                        OwnerRenterList currentOR = renterSnapshot.getValue(OwnerRenterList.class);

                        assert currentOR != null;
                        rentListAdapter.add(currentOR);
                    }

                    rentListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SelectOwnerRenterActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addListViewItemListener() {
        listViewRentTrans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SelectOwnerRenterActivity.this, RentTransactionListActivity.class);
                intent.putExtra(KEY_CATEGORY, mCategory);
                intent.putExtra(KEY_UID, mUid);
                intent.putExtra(KEY_OWNER_RENTER_NAME, ownerRenterList.get(position).getFullName());
                intent.putExtra(KEY_OWNER_RENTER_UID, ownerRenterList.get(position).getPushUid());
                startActivity(intent);
            }
        });
    }
}
