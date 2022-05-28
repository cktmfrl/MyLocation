package com.example.mylocationexam;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private TextView textResult;
    private EditText editInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermissions()) {
            requestPermissions();
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    public void searchLocation(View view) {
    }

    public void getLocation(View view) {
        if (!checkPermissions()) {
            requestPermissions();
        }

        // 위치 관리자 객체 참조하기
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 위치정보 설정 Intent
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            // TODO 다음 링크 참고하여 개발하기
            // https://stickode.tistory.com/350
        }
    }

    private boolean checkPermissions() {
//        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // https://developer.android.com/training/location/permissions?hl=ko
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) // 대략적인 위치
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) // 정확한 위치
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION));
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION); // 이전에 권한 요청을 거부한 경우 true 값 반환
//
//        // Provide an additional rationale to the user. This would happen if the user denied the
//        // request previously, but didn't check the "Don't ask again" checkbox.
//        // 이것은 사용자가 이전에 요청을 거부했지만 "다시 묻지 않음" 확인란을 선택하지 않은 경우 발생합니다.
        if (shouldProvideRationale) {
            Log.i(TAG, "Requesting permission shouldProvideRationale"); // 권한 요청
//            Log.i(TAG, "Displaying permission rationale to provide additional context."); // 추가 컨텍스트를 제공하기 위해 권한 근거를 표시합니다.
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle("권한 설정")
                    //.setMessage("권한 거절로 인해 일부 기능이 제한됩니다.")
                    .setMessage("앱을 이용하시려면 필수 권한을 허용해야 합니다. (위치 권한, 정확한 위치)")
                    .setPositiveButton("권한 설정", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("종료",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                    .create()
                    .show();
        } else {
            Log.i(TAG, "Requesting permission requestPermissions"); // 권한 요청
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(MainActivity.this, "Permission was granted.", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(MainActivity.this, "'정확한 위치' 사용 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "위치 사용 설정을 켜주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // 위치 업데이트 요청 여부에 따라 버튼 상태를 업데이트합니다.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            //setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES, false));
            Toast.makeText(MainActivity.this, "위치 업데이트 요청 해지", Toast.LENGTH_SHORT).show();
        }
    }
}