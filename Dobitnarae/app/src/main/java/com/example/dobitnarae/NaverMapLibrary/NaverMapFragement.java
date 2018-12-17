package com.example.dobitnarae.NaverMapLibrary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dobitnarae.R;
import com.example.dobitnarae.Store;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

@SuppressLint("ValidFragment")
public class NaverMapFragement extends Fragment{
    private NMapController mMapController;
    private NMapView mMapView;// 지도 화면 View
    private NMapContext mMapContext;
    private final String CLIENT_ID = "kq6ZsHG_bYYKmox3mPqw";// 애플리케이션 클라이언트 아이디 값
    private NMapOverlayManager mapOverlayManager;
    private NMapResourceProvider nMapResourceProvider;

    private Store store;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private NaverMapFragement(Store store){
        this.store = store;
    }

    public static NaverMapFragement newInstance(int sectionNumber, Store store) {
        NaverMapFragement fragment = new NaverMapFragement(store);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void setMarker(){
        int markerID = NMapPOIflagType.PIN;
        NMapPOIdata poIdata = new NMapPOIdata(2, nMapResourceProvider);
        poIdata.beginPOIdata(2);
        poIdata.addPOIitem(store.getLongitude(), store.getLatitude(),null, markerID, 0);
        poIdata.endPOIdata();
        NMapPOIdataOverlay poIdataOverlay = mapOverlayManager.createPOIdataOverlay(poIdata, null);
        poIdataOverlay.showAllPOIdata(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_naver_map, container, false);

        mMapView = (NMapView)rootView.findViewById(R.id.mapView);
        mMapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mMapContext.setupMapView(mMapView);

        mMapController = mMapView.getMapController();
        mMapController.setMapCenter(new NGeoPoint(store.getLongitude(), store.getLatitude()), 12);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext =  new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onStart(){
        super.onStart();

        nMapResourceProvider = new NMapViewerResourceProvider(getActivity());
        mapOverlayManager = new NMapOverlayManager(getActivity(), mMapView, nMapResourceProvider);

        setMarker();

        mMapContext.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }
    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }
}
