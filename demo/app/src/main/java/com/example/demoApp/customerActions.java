package com.example.demoApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class customerActions extends AppCompatActivity {
      ToggleButton t;
    public String  userid ;
    private String vehiid3="null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_actions);


        t= (ToggleButton) findViewById(R.id.presentsw);



        t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    Task<Void> myRef2 = database.getInstance().getReference("users").child("vehiclePConnection").child(vehiid3).child(userid).child("presentStatus").setValue("YES");


                }
                else
                {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    Task<Void> myRef2 = database.getInstance().getReference("users").child("vehiclePConnection").child(vehiid3).child(userid).child("presentStatus").setValue("NO");




                }
            }
        });







    }




    private void showdata(DataSnapshot dataSnapshot) {

        getvehicleID vi = new getvehicleID();
        vi.setVehicleID(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getVehicleID());
        vi.setName(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getName());
        vi.setAddress(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getAddress());
        vi.setInVehicle(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getInVehicle());
        vi.setOutVehicle(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getOutVehicle());
        vi.setSchool(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getSchool());
        
        vehiid3 = vi.getVehicleID();
        //String vehiidN = vi.getName();
       // String vehiidA = vi.getAddress();
        //String vehiidIV = vi.getInVehicle();
       // String vehiidOV = vi.getOutVehicle();
        //String vehiidS = vi.getSchool();
        
       // Toast.makeText(customerActions.this, vehiid3,Toast.LENGTH_LONG).show();

      // presentStatus present = new presentStatus();

      // present.setPresentStatus("yes");
      // present.setAddress(vehiidA);
       //present.setName(vehiidN);
      // present.setInVehicle(vehiidIV);
       //present.setOutVehicle(vehiidOV);
      // present.setSchool(vehiidS);


    }

    @Override
    protected void onStart() {
        super.onStart();

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference getb1 = FirebaseDatabase.getInstance().getReference();
        getb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showdata(dataSnapshot);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference getb2 = FirebaseDatabase.getInstance().getReference();
        getb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkData(dataSnapshot);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //t.setChecked(true);
    }

    private void checkData(DataSnapshot dataSnapshot) {

        presentStatus present = new presentStatus();

        present.setPresentStatus(dataSnapshot.child("users").child("vehiclePConnection").child(vehiid3).child(userid).getValue(presentStatus.class).getPresentStatus());

      String ps = present.getPresentStatus();


        if(ps.equals("YES")){
            t.setChecked(true);
        }
        else if(ps.equals("NO")){
            t.setChecked(false);
        }
    }
}
