package com.example.demoApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class userid extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    public static final String SEND_TEXT = "send";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

       // if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(checkPermission()){
                Toast.makeText(userid.this, "permission Granted",Toast.LENGTH_LONG).show();
                scannerView = new ZXingScannerView(this);
                setContentView(scannerView);
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else {
               requestPermission();
            }

        //}

    }



    private boolean checkPermission(){

     return (ContextCompat.checkSelfPermission(userid.this , CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermissionResult(int requestCode, String permission[],int grantResults[] )
    {
         switch (requestCode)
         {
             case REQUEST_CAMERA :
                 if(grantResults.length>0){
                     boolean camaraAccsepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                     if(camaraAccsepted){
                         Toast.makeText(userid.this, "permission Granted",Toast.LENGTH_LONG).show();
                     }
                     else {
                         Toast.makeText(userid.this, "permission Denied",Toast.LENGTH_LONG).show();

                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                             if(shouldShowRequestPermissionRationale(CAMERA)){
                               displayAlertMessage("Allow Permission for this task", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                      requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);

                                   }
                               });

                               return;
                             }
                         }

                     }
                 }
                 break;
         }
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

            if(checkPermission()){
                if(scannerView==null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                    scannerView.setResultHandler(this);
                    scannerView.startCamera();
                }
            }
            else {

                requestPermission();

            }


    }

    public void displayAlertMessage(String message , DialogInterface.OnClickListener Listener)
    {
        new AlertDialog.Builder(userid.this)
                .setMessage(message)
                .setPositiveButton("OK" , Listener)
                .setNegativeButton("Cancel" , null)
                .create()
                .show();
    }


    @Override
    public void handleResult(final Result result) {
         final String ScanResult = result.getText();
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("QR Code Results..");
         builder.setMessage(ScanResult);
         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 Intent intent = new Intent(userid.this,CustomerRegistation.class);
                 String ScanResult2 = result.getText();
                 intent.putExtra(SEND_TEXT, ScanResult2 );
                 startActivity(intent);

             }
         });
         builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
                 Intent intent = new Intent(userid.this,MainActivity.class);
                 startActivity(intent);
                 finish();
                 return;
             }
         });
        AlertDialog alert = builder.create();
        alert.show();




    }


}
