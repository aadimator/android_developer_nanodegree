package com.aadimator.bakingapp.utils;

/**
 * Created by aadim on 1/1/2018.
 */

import android.view.View;

public interface OnItemClickListener<MODEL> {

    void onClick(MODEL model, View view);
}
