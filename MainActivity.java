package com.example.manav.pingme;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1;
    private String mUserUid,mUserName = " ";
    private FirebaseDatabase mFirebasDatabase;
    private DatabaseReference mDatabaseRefrence;
    private DatabaseReference mUserRefrence;
    private ChildEventListener mChildEventListener;
    private String mMessage;
    private String mReceiver;
    private FirebaseAuth mFirebaseAuth=FirebaseAuth.getInstance();;
    private FirebaseUser currentUser;
    private FirebaseListAdapter<Message> mAdapter;
    private ListView mListView;



    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebasDatabase = FirebaseDatabase.getInstance();
        mDatabaseRefrence = mFirebasDatabase.getReference().child("Messages");
        mUserRefrence = mFirebasDatabase.getReference().child("Users");

        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 FirebaseUser currentUser= mFirebaseAuth.getCurrentUser();

                if(currentUser != null)
                {
                    mUserName = currentUser.getDisplayName();
                   // storeUserInfo();
                }
                else
                {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);


                }
            }
        };

//        TextView textView = (TextView) findViewById(R.id.inbox_emptyView);
//        textView.setText(mUserName);

//        mListView = (ListView) findViewById(R.id.inbox_listview);
//        TextView textView = (TextView) findViewById(R.id.inbox_emptyView);
//        textView.setText(mUserName);
//        mListView.setEmptyView(textView);
//
//        mAdapter = new FirebaseListAdapter<Message>(this,Message.class,R.layout.inbox_list_item,mDatabaseRefrence.child(mUserName)) {
//            @Override
//            protected void populateView(View v, Message model, int position) {
//                TextView mName;
//                TextView mMessage;
//                mName = (TextView) v.findViewById(R.id.txt_name);
//                mMessage = (TextView) v.findViewById(R.id.txt_messsage);
//                String st = model.getSender();
//                mName.setText(model.getSender());
//                mMessage.setText(model.getMessage());
//            }
//        };
//        mListView.setAdapter(mAdapter);
//        mAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                mListView.setSelection(mAdapter.getCount() - 1);
//            }
//        });
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),"Clicked "+i,Toast.LENGTH_SHORT).show();
//            }
//        });
//
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthUI.getInstance().signOut(this);
            onSignedOutCleanup();
        }

        return super.onOptionsItemSelected(item);
    }
    private void onSignedOutCleanup(){
        detachDatabaseReadListner();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, "Signed In", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in cancelled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void detachDatabaseReadListner()
    {
        if(mChildEventListener!=null)
            mDatabaseRefrence.removeEventListener(mChildEventListener);
        mChildEventListener = null;
    }
    public void showName(){
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserName = currentFirebaseUser.getDisplayName();
        mUserUid = currentFirebaseUser.getUid();
    }
    public void storeUserInfo() {
        final String key = mUserRefrence.push().getKey();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    if (dataSnapshot.getValue().toString().equals(mUserName)) {
                        //FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    }else
                    {
                        dbref.setValue(mUserName);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRefrence = null;
        mFirebasDatabase = null;
        mAdapter.cleanup();
    }

}
