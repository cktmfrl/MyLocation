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
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) // 대략적인 위치
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)); // 정확한 위치
    }

    private void requestPermissions() {
        // 이전에 권한 요청을 거부한 경우 true 값 반환
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                                            // && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // 이전에 요청을 거부했지만 "다시 묻지 않음" 확인란을 선택하지 않은 경우
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Requesting permission");
//
//            String title = "권한 설정";
//            String message = "앱을 이용하시려면 필수 권한을 허용해야 합니다. (위치 권한, 정확한 위치)";
//            String okButton = "설정";
//            String cancelButton = "종료";
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 권한이 2개인데 1개만 체크한다는 것 알고 씀..
                //Toast.makeText(MainActivity.this, "Permission was granted.", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "권한이 부여되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                String title = "권한 설정";
                String message = "앱을 이용하시려면 필수 권한을 허용해야 합니다. (위치 권한, 정확한 위치)";
                String okButton = "설정";
                String cancelButton = "닫기"; // "종료";

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
        // 위치 업데이트 요청 여부에 따라 버튼 상태를 업데이트합니다.
//        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
//            Toast.makeText(MainActivity.this, "위치 업데이트 요청 해지", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * 검색 버튼 클릭
     * @param view
     */
    public void searchLocation(View view) {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        String address = mEditInput.getText().toString();

        if ("".equals(address)) {
            Toast.makeText(MainActivity.this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoUtil.getLocationFromName(MainActivity.this,
                address, new GeoUtil.GeoUtilListener() {
                    @Override
                    public void onSuccess(String addr, double lat, double lng) {
                        Log.d(TAG, "lat = " + lat + ", lng = " + lng);

                        String res = "위도 : " + lat + "\n경도 : " + lng;

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
     * 내 위치 가져오기 버튼 클릭
     * @param view
     */
    public void getMyLocation(View view) {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        // 위치 관리자 객체 참조하기
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            String title = "위치 서비스 설정";
            String message = "위치 서비스 설정이 꺼져 있어 현재 위치를 확인할 수 없습니다. 설정을 변경하시겠습니까?";
            String okButton = "설정";
            String cancelButton = "닫기";

            DialogUtil.showDialog(this
                    , title
                    , message
                    , okButton
                    , cancelButton
                    , new DialogUtil.OnClickListener() {
                        @Override
                        public void onOkClicked() {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 위치정보 켜기 화면
                        }

                        @Override
                        public void onCancelClicked() {
                        }
                    }
            );
        } else {
            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
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

    // 현재 위치 가져오기(https://developer.android.com/reference/android/location/LocationListener)
    final LocationListener mGpsLocationListener = new LocationListener() {

        // 위치 변경 시 호출
        public void onLocationChanged(Location location) {
            setRequestLocationButtonsState(false);

            //String provider = location.getProvider(); // 위치정보 (Ex. network, gps)
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도

            String res = "위도 : " + latitude + "\n" + "경도 : " + longitude;

            // 위도, 경도에 해당하는 주소명 가져오기
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

        // 이 메소드는 API 레벨 29에서 더 이상 사용되지 않습니다. 이 콜백은 Android Q 이상에서 호출되지 않습니다.
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged(provider=" + provider + ", status=" + status + ", extras=" + extras + ")");
            setRequestLocationButtonsState(false);
        }

        // 이 수신기가 등록된 공급자가 활성화되면 호출됩니다.
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled(provider=" + provider + ")");
            setRequestLocationButtonsState(false);
        }

        // 이 수신기가 등록된 공급자가 비활성화되면 호출됩니다.
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