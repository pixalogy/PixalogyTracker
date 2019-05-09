package com.example.demoApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegistation extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Button  reg;
    private EditText email,pass,cname,caddress,cschool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registation);

        Intent intent = getIntent();
        final String UID = intent.getStringExtra(userid.SEND_TEXT);
       // TextView user = (TextView) findViewById(R.id.userID);

        //user.setText(UID);

        reg=(Button)findViewById(R.id.btnreg);
        email=(EditText)findViewById(R.id.txtemail);
        pass=(EditText)findViewById(R.id.txtpass);
        mAuth=FirebaseAuth.getInstance();
        cname=(EditText)findViewById(R.id.CName);
        caddress=(EditText)findViewById(R.id.CAddress);
        cschool=(EditText)findViewById(R.id.CSchool);



        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent intent = new Intent(CustomerRegistation.this,cusMap.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };



        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String RE=email.getText().toString();
                String RP=pass.getText().toString();

                final String ccname=cname.getText().toString();
                final String ccaddress=caddress.getText().toString();
                final String ccschool=cschool.getText().toString();


                Toast.makeText(CustomerRegistation.this,ccname,Toast.LENGTH_SHORT).show();


                mAuth.createUserWithEmailAndPassword(RE,RP).addOnCompleteListener(CustomerRegistation.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(CustomerRegistation.this,"SignUp Error",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            sendVID sendid = new sendVID();
                            sendid.setVehicleID(UID);
                            sendid.setName(ccname);
                            sendid.setAddress(ccaddress);
                            sendid.setSchool(ccschool);
                            sendid.setPresentStatus("");
                            sendid.setInVehicle("");
                            sendid.setOutVehicle("");
                            String userId= mAuth.getCurrentUser().getUid();





                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            Task<Void> myRef2 = database.getInstance().getReference("users").child("Customers").child(userId).setValue(sendid);
                            Task<Void> myRef23 = database.getInstance().getReference("users").child("vehiclePConnection").child(UID).child(userId).setValue(sendid);




                        }
                    }
                });
            }
        });







    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
