package com.dishcuss.foodie.hub.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dishcuss.foodie.hub.Activities.HomeActivity;
import com.dishcuss.foodie.hub.R;

/**
 * Created by Naeem Ibrahim on 7/19/2016.
 */
public class LoginIntroFragment extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_LOGO_ID = "logoId";
    private static final String ARG_BAR_ID = "barId";

    private int layoutResId,logoId,barId;
    ImageView imageViewLogo,imageViewBar;
    TextView skipLogin;




    public static LoginIntroFragment newInstance(int layoutResId, int logo, int bar) {
        LoginIntroFragment intro = new LoginIntroFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putInt(ARG_LOGO_ID, logo);
        args.putInt(ARG_BAR_ID, bar);

        intro.setArguments(args);
        return intro;
    }



    public LoginIntroFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)
                                  && getArguments().containsKey(ARG_LOGO_ID)
                                  && getArguments().containsKey(ARG_BAR_ID))
        {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
            logoId = getArguments().getInt(ARG_LOGO_ID);
            barId = getArguments().getInt(ARG_BAR_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView=inflater.inflate(layoutResId, container, false);

        imageViewLogo=(ImageView)fragmentView.findViewById(R.id.intro_image_logo);
        imageViewBar=(ImageView)fragmentView.findViewById(R.id.intro_image_bar);
        skipLogin=(TextView) fragmentView.findViewById(R.id.skip_login_tv);

        imageViewLogo.setImageResource(logoId);
        imageViewBar.setImageDrawable(null);
        imageViewBar.setImageResource(barId);

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Skip Login",Toast.LENGTH_LONG).show();

                Intent intent=new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return fragmentView;
    }
}
