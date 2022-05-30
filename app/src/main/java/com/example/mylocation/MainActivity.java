package com.example.mylocation;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private TextView mTextResult;
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        mTextResult = findViewById(R.id.text_result);
        mEditInput = findViewById(R.id.edit_input);
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    public void searchLocation(View view) {
        EditText editText = findViewById(R.id.edit_input);
        String str = editText.getText().toString();

        final Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> list = geocoder.getFromLocationName(str, 10);

            if (list != null) {
                String city = "";
                String country = "";
                if (list.size() == 0) {
                    mTextResult.setText("올바른 주소를 입력해주세요.");
                } else {
                    Address address = list.get(0);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();

                    //mTextResult.setText("lat : " + lat + ", lng : " + lng);
                    mTextResult.setText("lat : " + lat + ", lng : " + lng + "\nlist = " + list);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getLocation(View view) {
        if (!checkPermissions()) {
            requestPermissions();
        }

        // 위치 관리자 객체 참조하기
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 위치정보 설정 Intent
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 위치정보 켜기 화면

            // TODO 다음 링크 참고하여 개발하기
            // https://stickode.tistory.com/350
        } else {
            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            setRequestLocationButtonsState(true);

            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    mGpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    mGpsLocationListener);
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

    // java.lang.IllegalArgumentException: latitude is out of range of [-90.000000, 90.000000] (too high)
    // longitude=127.1929369, latitude=37.5620068
    private void getAddress(double latitude, double longitude) {
        Log.d(TAG, "reverseGeocoding : latitude=" + latitude + ", longitude=" + longitude);

        //GeoPoint point = new GeoPoint(latitude, longitude);

        final Geocoder geocoder = new Geocoder(this); // getApplicationContext()); // (this.getContext());
        List<Address> citylist = null;
        try {
            citylist = geocoder.getFromLocation(latitude, longitude, 10);

            if (citylist != null) {
                if (citylist.size() == 0) {
                    Log.e(TAG, "reverseGeocoding : 해당 도시 없음111");
                } else {
                    String city = citylist.get(0).getAdminArea();
                    String country = citylist.get(0).getCountryName();
                    Log.d(TAG, "reverseGeocoding : citylist = " + citylist);
                    mEditInput.setText(country + " " + city);
                    mTextResult.setText(citylist.toString());


                }
            } else {
                Log.d(TAG, "reverseGeocoding : citylist is NULL");
            }

        } catch (IOException e) {
            Log.e(TAG, "reverseGeocoding : 해당 도시 없음22");
            e.printStackTrace();
        }


    }

    // https://developer.android.com/reference/android/location/LocationListener
    final LocationListener mGpsLocationListener = new LocationListener() {
        // 위치가 변경되면 호출됩니다.
        public void onLocationChanged(Location location) {
            setRequestLocationButtonsState(false);

            // 위지청보 처리 작업 구현
            String provider = location.getProvider(); // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
            //mTextResult.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
            StringBuilder sb = new StringBuilder();
            sb.append("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
            sb.append("\n==============\n");
            sb.append("onLocationChanged(location=" + location + ")");
            mTextResult.setText(sb.toString());

            getAddress(latitude, longitude);
        }

        // 이 메소드는 API 레벨 29에서 더 이상 사용되지 않습니다. 이 콜백은 Android Q 이상에서 호출되지 않습니다.
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged(provider=" + provider + ", status=" + status + ", extras=" + extras + ")");
            setRequestLocationButtonsState(false);

            StringBuilder sb = new StringBuilder();
            sb.append(mTextResult.getText());
            sb.append("\n==============\n");
            sb.append("onStatusChanged(provider=" + provider + ", status=" + status + ", extras=" + extras + ")");
            mTextResult.setText(sb.toString());
        }

        // 이 수신기가 등록된 공급자가 활성화되면 호출됩니다.
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled(provider=" + provider + ")");
            setRequestLocationButtonsState(false);

            StringBuilder sb = new StringBuilder();
            sb.append(mTextResult.getText());
            sb.append("\n==============\n");
            sb.append("onProviderEnabled(provider=" + provider + ")");
            mTextResult.setText(sb.toString());
        }

        // 이 수신기가 등록된 공급자가 비활성화되면 호출됩니다.
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled(provider=" + provider + ")");
            setRequestLocationButtonsState(false);

            StringBuilder sb = new StringBuilder();
            sb.append(mTextResult.getText());
            sb.append("\n==============\n");
            sb.append("onProviderDisabled(provider=" + provider + ")");
            mTextResult.setText(sb.toString());
        }

    };

    private void setRequestLocationButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            findViewById(R.id.iv_location).setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.iv_location).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}