package id.vantura.ftptest;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends Activity {

    Button selector, upload;
    TextView filePath;
    final int PICK_FILE = 1;

    // Define this parameters as required
//    String FTPHost = "178.128.90.183";
//    String user = "remotetango";
//    String pass = "27uc87xis";
    String FTPHost = "ftp.vantura.id";
    String user = "mediapaud@vantura.id";
    String pass = "Gedawang123";

    final int PORT = 21;
    String filename;
    Context activity;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0){
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        activity = this;
        selector = (Button) findViewById(R.id.selectfile);
        upload = (Button) findViewById(R.id.upload);
        selector.setOnClickListener(new SelectFile());
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new UploadFile().execute("/sdcard/Pictures/Instagram/a.jpg",
                        FTPHost, user, pass);
                System.out.println("clicked");

            }
        });

    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which == -2){
                                                Toast.makeText(activity, "ERRR", Toast.LENGTH_LONG).show();
                                            }else{
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                                }
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private class UploadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            FTPClient client = new FTPClient();
            try {
                client.connect(params[1], PORT);
                client.login(params[2], params[3]);
                client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                client.storeFile("quotezzz.jpg", new FileInputStream(new File(params[0])));
                return "sukses";

            } catch (Exception e) {

                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String respon) {
            if (respon.equals("sukses")){
                Toast.makeText(activity, "File Sent", Toast.LENGTH_LONG).show();
                System.out.println(respon);
            }else{
                Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
                System.out.println("error -> "+respon);
            }
        }

    }

    private class SelectFile implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("file/*");
            startActivityForResult(i, PICK_FILE);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE && data.getData() != null) {
            filePath.setText(data.getData().getPath());
            filename = data.getData().getLastPathSegment();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
