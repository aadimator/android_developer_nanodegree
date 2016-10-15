package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StockDetailActivity extends AppCompatActivity {

    private View errorMessage;
    private View progressCircle;
    private LineChartView lineChart;

    private boolean isLoaded = false;
    private String companyTicker;
    private String companyName;
    private ArrayList<String> labels;
    private ArrayList<Float> values;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorMessage = findViewById(R.id.graphError);
        progressCircle = findViewById(R.id.prog_circle);
        lineChart = (LineChartView) findViewById(R.id.linechart);


        companyTicker = getIntent().getStringExtra(MyStocksActivity.TICKER_KEY);
        if (savedInstanceState == null) {
            downloadStockDetails();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isLoaded) {
            outState.putString(getString(R.string.company_name_key), companyName);
            outState.putStringArrayList(getString(R.string.labels_key), labels);

            float[] valuesArray = new float[values.size()];
            for (int i = 0; i < valuesArray.length; i++) {
                valuesArray[i] = values.get(i);
            }
            outState.putFloatArray(getString(R.string.values_key), valuesArray);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(
                getString(R.string.company_name_key))
                ) {
            isLoaded = true;

            companyName = savedInstanceState.getString(getString(R.string.company_name_key));
            labels = savedInstanceState.getStringArrayList(getString(R.string.labels_key));
            values = new ArrayList<>();

            float[] valuesArray = savedInstanceState.getFloatArray(getString(R.string.values_key));
            for (float f : valuesArray) {
                values.add(f);
            }
            onDownloadCompleted();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return false;
        }
    }


    private void downloadStockDetails() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://chartapi.finance.yahoo.com/instrument/1.0/" +
                        companyTicker +
                        "/chartdata;type=quote;range=5y/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) { //on Success
                    try {

                        String result = response.body().string();
                        if (result.startsWith("finance_charts_json_callback( ")) {
                            result = result.substring(29, result.length() - 2);
                        }


                        JSONObject object = new JSONObject(result);
                        companyName = object.getJSONObject("meta").getString("Company-Name");
                        labels = new ArrayList<>();
                        values = new ArrayList<>();
                        JSONArray series = object.getJSONArray("series");
                        for (int i = 0; i < series.length(); i++) {
                            JSONObject seriesItem = series.getJSONObject(i);
                            SimpleDateFormat srcFormat = new SimpleDateFormat("yyyyMMdd");
                            String date = android.text.format.DateFormat.
                                    getMediumDateFormat(getApplicationContext()).
                                    format(srcFormat.parse(seriesItem.getString("Date")));
                            labels.add(date);
                            values.add(Float.parseFloat(seriesItem.getString("close")));
                        }

                        onDownloadCompleted();
                    } catch (Exception e) {
                        onDownloadFailed();
                        e.printStackTrace();
                    }
                } else {
                    onDownloadFailed();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                onDownloadFailed();
            }
        });
    }

    private void onDownloadCompleted() {
        StockDetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(companyName);

                progressCircle.setVisibility(View.GONE);
                errorMessage.setVisibility(View.GONE);

                LineSet dataset = new LineSet();
                dataset.setColor(getColor(R.color.white));

                for (int i = 0; i < labels.size(); i++) {
                    dataset.addPoint(new Point(labels.get(i), values.get(i)));
                }

                dataset.setSmooth(true);
                lineChart.addData(dataset);
                lineChart.setYAxis(false);
                lineChart.setXAxis(false);
                lineChart.setXLabels(AxisRenderer.LabelPosition.NONE);
                lineChart.setYLabels(AxisRenderer.LabelPosition.NONE);

                lineChart.show();

                lineChart.setVisibility(View.VISIBLE);

                isLoaded = true;
            }
        });
    }

    private void onDownloadFailed() {
        StockDetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lineChart.setVisibility(View.GONE);
                progressCircle.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                setTitle(getString(R.string.error_title));
            }
        });
    }
}
