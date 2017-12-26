package com.lipata.forkauthority.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.lipata.forkauthority.R;

public class LocationQualityView {

    private ImageView mImageView;
    private Context context;
    private int mStatus;

    public LocationQualityView(Context context, ImageView imageView) {
        this.mImageView = imageView;
        this.context = context;
        mStatus = Status.HIDDEN;
    }

    public void setAccuracyCircleStatus(int statusCode) {
        mStatus = statusCode;
        switch (mStatus) {
            case Status.HIDDEN:
                mImageView.setColorFilter(context.getResources().getColor(R.color.material_gray_100));
                break;
            case Status.BEST:
                mImageView.setColorFilter(context.getResources().getColor(R.color.accuracy_BEST));
                break;
            case Status.OK:
                mImageView.setColorFilter(context.getResources().getColor(R.color.accuracy_OK));
                break;
            case Status.BAD:
                mImageView.setColorFilter(context.getResources().getColor(R.color.accuracy_BAD));
                break;
        }
    }

    public void show() {
        mImageView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mImageView.setVisibility(View.GONE);
    }

    public int getStatus() {
        return mStatus;
    }

    public static class Status {
        // Status codes
        public static final int HIDDEN = 0;
        public static final int BEST = 10;
        public static final int OK = 20;
        public static final int BAD = 30;
    }
}


