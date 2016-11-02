package com.dishcuss.foodies.ExtraHelpingClasses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dishcuss.foodies.R;

/**
 * Created by Naeem Ibrahim on 7/19/2016.
 */
public class Intro3 extends Fragment {

    ImageView imageViewLogo,imageViewBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // inflate the root view of the fragment
        View fragmentView = inflater.inflate(R.layout.login_intro_fragment, container, false);
        imageViewLogo=(ImageView)fragmentView.findViewById(R.id.intro_image_logo);
        imageViewBar=(ImageView)fragmentView.findViewById(R.id.intro_image_bar);
        imageViewLogo.setImageResource(R.drawable.intro3_logo);
        imageViewBar.setImageDrawable(null);
        imageViewBar.setImageResource(R.drawable.ic_bell);
        return fragmentView;
    }
}
