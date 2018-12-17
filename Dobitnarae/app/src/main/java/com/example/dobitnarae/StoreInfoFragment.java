package com.example.dobitnarae;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dobitnarae.NaverMapLibrary.NaverMapFragement;

@SuppressLint("ValidFragment")
public class StoreInfoFragment extends Fragment {
    private Store store;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public StoreInfoFragment(Store store) {
        this.store = store;
    }

    public static StoreInfoFragment newInstance(int sectionNumber, Store store) {
        StoreInfoFragment fragment = new StoreInfoFragment(store);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_info, container, false);

        TextView name = (TextView)rootView.findViewById(R.id.content_name);
        name.setText(store.getName());
        TextView intro = (TextView)rootView.findViewById(R.id.content_intro);
        intro.setText(store.getIntro());
        TextView info = (TextView)rootView.findViewById(R.id.content_info);
        info.setText(store.getInform());
        TextView phone = (TextView)rootView.findViewById(R.id.content_phone);
        phone.setText(store.getTel());
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:" + store.getTel();
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
            }
        });
        TextView owner_info = (TextView)rootView.findViewById(R.id.content_owner_info);
        owner_info.setText(store.getAdmin_id());
        TextView address = (TextView)rootView.findViewById(R.id.content_address);
        address.setText(store.getAddress());

        this.getFragmentManager()
                .beginTransaction()
                .replace(R.id.naver_map_layout, NaverMapFragement.newInstance(0, store))
                .commit();

        return rootView;
    }
}
