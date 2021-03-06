package com.example.mylocation;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mylocation.util.DialogUtil;
import com.example.mylocation.util.GeoUtil;
import com.example.mylocation.view.FirstFragment;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_CODE = 34;

//    private static final String[] REQUEST_PERMESSIONS = {Manifest.permission.ACCESS_FINE_LOCATION
//                                                        , Manifest.permission.ACCESS_COARSE_LOCATION
//                                                        , Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private static final String[] REQUEST_PERMESSIONS = {Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION};

    private EditText mEditInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermissions()) {
            requestPermissions();
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        mEditInput = findViewById(R.id.edit_input);
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

    private boolean checkPermissions() {
        // https://developer.android.com/training/location/permissions?hl=ko
        Log.d(TAG, "checkPermissions() ACCESS_FINE_LOCATION = " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d(TAG, "checkPermissions() ACCESS_COARSE_LOCATION = " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
        Log.d(TAG, "checkPermissions() ACCESS_BACKGROUND_LOCATION = " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION));

//        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION));
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) // ???????????? ??????
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)); // ????????? ??????
    }

    private void requestPermissions() {
        // ????????? ?????? ????????? ????????? ?????? true ??? ??????
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                                            // && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // ????????? ????????? ??????????????? "?????? ?????? ??????" ???????????? ???????????? ?????? ??????
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Requesting permission");
//
//            String title = "?????? ??????";
//            String message = "?????? ?????????????????? ?????? ????????? ???????????? ?????????. (?????? ??????, ????????? ??????)";
//            String okButton = "??????";
//            String cancelButton = "??????";
//
//            DialogUtil.showDialog(this
//                    , title
//                    , message
//                    , okButton
//                    , cancelButton
//                    , new DialogUtil.OnClickListener() {
//                        @Override
//                        public void onOkClicked() {
//                            ActivityCompat.requestPermissions(MainActivity.this, REQUEST_PERMESSIONS, REQUEST_PERMISSIONS_CODE);
//                        }
//
//                        @Override
//                        public void onCancelClicked() {
//                            finish();
//                        }
//                    }
//            );
//        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this, REQUEST_PERMESSIONS, REQUEST_PERMISSIONS_CODE);
//        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "onRequestPermissionResult");

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // ????????? 2????????? 1?????? ??????????????? ??? ?????? ???..
                //Toast.makeText(MainActivity.this, "Permission was granted.", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            } else {
                String title = "?????? ??????";
                String message = "?????? ?????????????????? ?????? ????????? ???????????? ?????????. (?????? ??????, ????????? ??????)";
                String okButton = "??????";
                String cancelButton = "??????"; // "??????";

                DialogUtil.showDialog(this
                        , title
                        , message
                        , okButton
                        , cancelButton
                        , new DialogUtil.OnClickListener() {
                            @Override
                            public void onOkClicked() {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelClicked() {
                                //finish();
                            }
                        }
                );


            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // ?????? ???????????? ?????? ????????? ?????? ?????? ????????? ?????????????????????.
//        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
//            Toast.makeText(MainActivity.this, "?????? ???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * ?????? ?????? ??????
     * @param view
     */
    public void searchLocation(View view) {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        String address = mEditInput.getText().toString();

        if ("".equals(address)) {
            Toast.makeText(MainActivity.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoUtil.getLocationFromName(MainActivity.this,
                address, new GeoUtil.GeoUtilListener() {
                    @Override
                    public void onSuccess(String addr, double lat, double lng) {
                        Log.d(TAG, "lat = " + lat + ", lng = " + lng);

                        String res = "?????? : " + lat + "\n?????? : " + lng;

                        GeoUtil.getFromLocation(MainActivity.this, lat, lng, new GeoUtil.GeoUtilListener() {
                            @Override
                            public void onSuccess(String addr, double lat, double lng) {
                                FirstFragment currentLocationFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_current_location);
                                currentLocationFragment.setCurrentLocation(res, addr);
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * ??? ?????? ???????????? ?????? ??????
     * @param view
     */
    public void getMyLocation(View view) {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        // ?????? ????????? ?????? ????????????
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            String title = "?????? ????????? ??????";
            String message = "?????? ????????? ????????? ?????? ?????? ?????? ????????? ????????? ??? ????????????. ????????? ?????????????????????????";
            String okButton = "??????";
            String cancelButton = "??????";

            DialogUtil.showDialog(this
                    , title
                    , message
                    , okButton
                    , cancelButton
                    , new DialogUtil.OnClickListener() {
                        @Override
                        public void onOkClicked() {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // ???????????? ?????? ??????
                        }

                        @Override
                        public void onCancelClicked() {
                        }
                    }
            );
        } else {
            // ??????????????? ????????? ??????, ???????????? ???????????????.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            setRequestLocationButtonsState(true);

            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mGpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mGpsLocationListener);
        }
    }

    // ?????? ?????? ????????????(https://developer.android.com/reference/android/location/LocationListener)
    final LocationListener mGpsLocationListener = new LocationListener() {

        // ?????? ?????? ??? ??????
        public void onLocationChanged(Location location) {
            setRequestLocationButtonsState(false);

            String provider = location.getProvider(); // ???????????? (Ex. network, gps)
            double longitude = location.getLongitude(); // ??????
            double latitude = location.getLatitude(); // ??????

            String res = "?????? : " + latitude + "\n" + "?????? : " + longitude  + "\n" + provider;

            // ??????, ????????? ???????????? ????????? ????????????
            GeoUtil.getFromLocation(MainActivity.this,
                    latitude, longitude, new GeoUtil.GeoUtilListener() {
                        @Override
                        public void onSuccess(String addr, double lat, double lng) {
                            FirstFragment currentLocationFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_current_location);
                            currentLocationFragment.setCurrentLocation(res, addr);
                            mEditInput.setText(addr);
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        // ??? ???????????? API ?????? 29?????? ??? ?????? ???????????? ????????????. ??? ????????? Android Q ???????????? ???????????? ????????????.
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged(provider=" + provider + ", status=" + status + ", extras=" + extras + ")");
            setRequestLocationButtonsState(false);
        }

        // ??? ???????????? ????????? ???????????? ??????????????? ???????????????.
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled(provider=" + provider + ")");
            setRequestLocationButtonsState(false);
        }

        // ??? ???????????? ????????? ???????????? ?????????????????? ???????????????.
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled(provider=" + provider + ")");
            setRequestLocationButtonsState(false);
        }

    };

    private void setRequestLocationButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            findViewById(R.id.iv_my_location).setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.iv_my_location).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

}