package com.example.mylocation.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoUtil {

    private static final String TAG = GeoUtil.class.getSimpleName();

    public interface GeoUtilListener {
        void onSuccess(String addr, double lat, double lng);

        void onError(String message);
    }

    /**
     * 주소의 좌표 조회
     * @param context
     * @param city
     * @param listener
     */
    public static void getLocationFromName(Context context, final String city, final GeoUtilListener listener) {
        Geocoder geocoder = new Geocoder(context);

        // TODO : "CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views." 에러 수정 (java.io.IOException: grpc failed 방지를 위해 비동기 작업 처리)
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocationName(city, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                double lat = address.getLatitude();
                double lng = address.getLongitude();
                listener.onSuccess(null, lat, lng);
            } else {
                listener.onError("주소 결과가 없습니다");
            }
        } catch (IOException e) {
            listener.onError(e.getMessage());
        }
//            }
//        }).start();

    }

    /**
     * 좌표의 주소 조회
     * @param context
     * @param latitude
     * @param longitude
     * @param listener
     */
    public static void getFromLocation(Context context, final double latitude, final double longitude, final GeoUtilListener listener) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // TODO : "CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views." 에러 수정 (java.io.IOException: grpc failed 방지를 위해 비동기 작업 처리)
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0); // countryCode=KR,countryName=대한민국,admin=시/도,sub-admin=null,locality=시,thoroughfare=동,postalCode=번지,feature=상세주소-
                //String addrStr = address.getAddressLine(0); // 전체 주소 (Ex. 대한민국 ㅇㅇ도 ㅇㅇ시 ㅇㅇ동 12-34 5층)

                String sido = address.getAdminArea(); // 시/도 (Ex. 서울시, 경기도, ...)
                String gugun = address.getLocality(); // 구/군
                String dong = address.getThoroughfare(); // 동
                String result = sido + " " + gugun + " " + dong;
                //Log.d(TAG, result);

                listener.onSuccess(result, 0, 0);
            } else {
                listener.onError("현재위치에서 검색된 주소 결과가 없습니다");
            }
        } catch (IOException e) {
            listener.onError("Geocoder 서비스 사용불가"); // 네트워크 문제
        } catch (IllegalArgumentException e) {
            listener.onError("잘못된 GPS 좌표");
        }
//            }
//        }).start();
        
    }

}
