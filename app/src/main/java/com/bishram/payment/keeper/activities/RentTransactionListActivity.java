package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.adapters.PaymentListAdapter;
import com.bishram.payment.keeper.models.RentPaidReceived;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bishram.payment.keeper.Constants.FIREBASE_OWNER_RENTER_ALL_PAYMENT;
import static com.bishram.payment.keeper.Constants.FIREBASE_RENT_PAID_RECEIVED;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_OWNER_RENTER_NAME;
import static com.bishram.payment.keeper.Constants.KEY_OWNER_RENTER_UID;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class RentTransactionListActivity extends AppCompatActivity {

    private Button buttonAddTransaction;

    private ListView listViewRentTrans;

    private ArrayList<RentPaidReceived> arrayList;
    private PaymentListAdapter adapter;

    private DatabaseReference referenceRoot;

    private String mCategory;
    private String mORName;
    private String mORUid;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_transaction_list);

        getIntents();

        initializeViewOb();

        initializeFirebaseOb();

        setListView();

        buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RentTransactionListActivity.this, AddPaymentPRActivity.class);

                intent.putExtra(KEY_CATEGORY, mCategory);
                intent.putExtra(KEY_OWNER_RENTER_NAME, mORName);
                intent.putExtra(KEY_OWNER_RENTER_UID, mORUid);
                intent.putExtra(KEY_UID, mUid);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (mCategory) {
            case USER_OWNER:
                setTitle(mORName + "'s Payments");
                readOwnedRentedPath(referenceRoot.child(FIREBASE_RENT_PAID_RECEIVED).child(mUid).child(mORUid).child(FIREBASE_OWNER_RENTER_ALL_PAYMENT));
                break;

            case USER_RENTER:
                setTitle("My Payments");
                readOwnedRentedPath(referenceRoot.child(FIREBASE_RENT_PAID_RECEIVED).child(mUid).child(mORUid).child(FIREBASE_OWNER_RENTER_ALL_PAYMENT));
                break;
        }
    }

    private void getIntents() {
        mCategory = getIntent().getStringExtra(KEY_CATEGORY);
        mORName = getIntent().getStringExtra(KEY_OWNER_RENTER_NAME);
        mORUid = getIntent().getStringExtra(KEY_OWNER_RENTER_UID);
        mUid = getIntent().getStringExtra(KEY_UID);
    }

    private void initializeViewOb() {
        buttonAddTransaction = findViewById(R.id.button_trans_add_transaction);

        listViewRentTrans = findViewById(R.id.list_view_or_list);

        arrayList = new ArrayList<>();

        adapter = new PaymentListAdapter(getApplicationContext(), arrayList);
    }

    private void initializeFirebaseOb() {
        referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    private void setListView() {
        listViewRentTrans.setAdapter(adapter);

        listViewItemListener();
    }

    private void listViewItemListener() {
        listViewRentTrans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RentTransactionListActivity.this, String.valueOf(position+1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readOwnedRentedPath(final DatabaseReference orFbReference) {
        orFbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();

                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot paySnapshot : dataSnapshot.getChildren()) {
                        RentPaidReceived rentPaidReceived = paySnapshot.getValue(RentPaidReceived.class);
                        arrayList.add(rentPaidReceived);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RentTransactionListActivity.this, orFbReference.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentTransactionListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
