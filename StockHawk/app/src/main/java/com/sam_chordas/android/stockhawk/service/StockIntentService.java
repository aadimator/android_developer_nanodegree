package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra(getString(R.string.tag_key)).equals(getString(R.string.tag_add))) {
            args.putString(
                    getString(R.string.symbol_key),
                    intent.getStringExtra(getString(R.string.symbol_key))
            );
        }

        try {
            stockTaskService.onRunTask(new TaskParams(
                    intent.getStringExtra(getString(R.string.tag_key)), args));
        } catch (Exception e) {
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            R.string.error_invalid_symbol,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
}
