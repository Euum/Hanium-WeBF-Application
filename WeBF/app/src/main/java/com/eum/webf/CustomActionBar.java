package com.eum.webf;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

public class CustomActionBar {
    private Activity activity;
    private ActionBar actionBar;

    public CustomActionBar(Activity _activity, ActionBar _actionBar){
        this.activity = _activity;
        this.actionBar = _actionBar;
    }

    public TextView setActionBar(String text){
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        View mCustomView = LayoutInflater.from(activity)
                .inflate(R.layout.custom_actionbar, null);
        TextView actionBarText = mCustomView.findViewById(R.id.actionBarText);
        actionBarText.setText(text);
        actionBar.setCustomView(mCustomView,layout);

        return (TextView) mCustomView.findViewById(R.id.logoutBtn);
    }
}
