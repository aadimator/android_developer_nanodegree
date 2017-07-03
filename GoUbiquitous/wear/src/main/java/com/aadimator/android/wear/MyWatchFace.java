package com.aadimator.android.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class MyWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            ConnectionCallbacks, OnConnectionFailedListener, DataListener {
        private static final String WEATHER_PATH = "/weather";
        private static final String KEY_HIGH = "high_temp";
        private static final String KEY_LOW = "low_temp";
        private static final String KEY_ID = "weather_id";


        private String mHighTemp;
        private String mLowTemp;
        private int mWeatherId;


        private GoogleApiClient mGoogleApiClient;

        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;

        boolean mAmbient;
        Calendar mCalendar;

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            }
        };

        // Paint declarations
        Paint mBackgroundPaint;
        Paint mTextTempLowPaint;
        Paint mTextTempHighPaint;
        Paint mTextDatePaint;
        Paint mTextTimePaint;

        // Float offsets
        float mXOffsetDate;
        float mXOffsetTime;
        float mYOffsetDate;
        float mYOffsetTime;
        float mYOffsetWeather;
        float mYOffsetDivider;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            Resources resources = MyWatchFace.this.getResources();

            mGoogleApiClient = new GoogleApiClient
                    .Builder(MyWatchFace.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.colorPrimary));

            mTextTimePaint = new Paint();
            mTextTimePaint = createTextTimePaint(resources.getColor(R.color.digital_text));
            mTextDatePaint = new Paint();
            mTextDatePaint = createTextDatePaint();

            mTextTempLowPaint = new Paint();
            mTextTempLowPaint = createTextTempPaint(resources.getColor(R.color.colorPrimaryLight));
            mTextTempHighPaint = new Paint();
            mTextTempHighPaint = createTextTempPaint(resources.getColor(R.color.digital_text));

            mXOffsetTime = mTextTimePaint.measureText("12:00") / 2;
            mXOffsetDate = mTextDatePaint.measureText("WED, JUN 13, 2016") / 2;

            mYOffsetTime = resources.getDimension(R.dimen.y_offset_time);
            mYOffsetDate = resources.getDimension(R.dimen.y_offset_date);
            mYOffsetDivider = resources.getDimension(R.dimen.y_offset_divider);
            mYOffsetWeather = resources.getDimension(R.dimen.y_offset_weather);

            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextTimePaint(int textColor) {
            Paint paint = new Paint();
            paint.setTextSize(getResources().getDimension(R.dimen.time_text_size));
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        private Paint createTextDatePaint() {
            Paint paint = new Paint();

            paint.setTextSize(getResources().getDimension(R.dimen.date_text_size));
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        private Paint createTextTempPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTextSize(getResources().getDimension(R.dimen.temp_text_size));
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                mGoogleApiClient.connect();
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);


        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;

                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }


        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            // Draw time
            int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);
            String text = String.format("%02d:%02d", hourOfDay, minute);
            canvas.drawText(text, bounds.centerX() - mXOffsetTime, mYOffsetTime, mTextTimePaint);

            // Draw date
            String dayName = mCalendar.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.SHORT,
                    Locale.getDefault()
            );
            String monthName = mCalendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.SHORT,
                    Locale.getDefault()
            );
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            int year = mCalendar.get(Calendar.YEAR);
            String dateText = String.format(
                    "%s, %s %d, %d",
                    dayName.toUpperCase(),
                    monthName.toUpperCase(),
                    dayOfMonth,
                    year
            );
            if (isInAmbientMode()) {
                mTextDatePaint.setColor(getResources()
                        .getColor(R.color.digital_text));
            } else {
                mTextDatePaint.setColor(getResources()
                        .getColor(R.color.colorPrimaryLight));
            }


            canvas.drawText(
                    dateText,
                    bounds.centerX() - mXOffsetDate,
                    mYOffsetDate,
                    mTextDatePaint
            );
            // Draw weather info if high and low temp are available
            if (mHighTemp != null && mLowTemp != null) {
                // Draw divider
                canvas.drawLine(
                        bounds.centerX() - 25,
                        mYOffsetDivider,
                        bounds.centerX() + 25,
                        mYOffsetDivider,
                        mTextDatePaint
                );

                // Draw high and low temperature
                float highTextSize = mTextTempHighPaint.measureText(mHighTemp);

                if (mAmbient) {
                    mTextTempLowPaint.setColor(
                            getResources().getColor(R.color.digital_text)
                    );
                    float lowTextSize = mTextTempLowPaint.measureText(mLowTemp);
                    float xOffset = bounds.centerX() - ((highTextSize + lowTextSize + 20) / 2);
                    canvas.drawText(
                            mHighTemp,
                            xOffset,
                            mYOffsetWeather,
                            mTextTempHighPaint
                    );
                    canvas.drawText(
                            mLowTemp,
                            xOffset + highTextSize + 20,
                            mYOffsetWeather,
                            mTextTempLowPaint
                    );
                } else {
                    mTextTempLowPaint.setColor(getResources()
                            .getColor(R.color.colorPrimaryLight));
                    float xOffset = bounds.centerX() - (highTextSize / 2);
                    canvas.drawText(mHighTemp, xOffset, mYOffsetWeather, mTextTempHighPaint);
                    canvas.drawText(
                            mLowTemp,
                            bounds.centerX() + (highTextSize / 2) + 20,
                            mYOffsetWeather,
                            mTextTempLowPaint);

                    // Draw icon (if not in ambient mode)
                    Drawable b = getResources().getDrawable(
                            getIconResourceForWeatherCondition(mWeatherId)
                    );
                    Bitmap icon = ((BitmapDrawable) b).getBitmap();
                    float scaledWidth = (mTextTempHighPaint.getTextSize() / icon.getHeight()) * icon.getWidth();
                    Bitmap weatherIcon = Bitmap.createScaledBitmap(
                            icon, (int) scaledWidth,
                            (int) mTextTempHighPaint.getTextSize(),
                            true
                    );
                    float iconXOffset = bounds.centerX() - ((highTextSize / 2) + weatherIcon.getWidth() + 30);
                    canvas.drawBitmap(
                            weatherIcon,
                            iconXOffset,
                            mYOffsetWeather - weatherIcon.getHeight(),
                            null
                    );
                }
            }
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, this);
            Log.d("GOOGLE_API_CLIENT", "Connected :)");

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d("GOOGLE_API_CLIENT", "Connection Suspended");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d("GOOGLE_API_CLIENT", "Connection Failed: " + connectionResult.getErrorMessage());
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            for (DataEvent dataEvent : dataEventBuffer) {
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataItem dataItem = dataEvent.getDataItem();
                    if (dataItem.getUri().getPath().compareTo(WEATHER_PATH) == 0) {
                        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                        mHighTemp = dataMap.getString(KEY_HIGH);
                        mLowTemp = dataMap.getString(KEY_LOW);
                        mWeatherId = dataMap.getInt(KEY_ID);
                        Log.d("WATCH_DATA", "\nHigh: " + mHighTemp + "\nLow: " + mLowTemp + "\nID: " + mWeatherId);
                        invalidate();
                    }
                }
            }
        }

        private int getIconResourceForWeatherCondition(int weatherId) {
            if (weatherId >= 200 && weatherId <= 232) {
                return R.drawable.ic_storm;
            } else if (weatherId >= 300 && weatherId <= 321) {
                return R.drawable.ic_light_rain;
            } else if (weatherId >= 500 && weatherId <= 504) {
                return R.drawable.ic_rain;
            } else if (weatherId == 511) {
                return R.drawable.ic_snow;
            } else if (weatherId >= 520 && weatherId <= 531) {
                return R.drawable.ic_rain;
            } else if (weatherId >= 600 && weatherId <= 622) {
                return R.drawable.ic_snow;
            } else if (weatherId >= 701 && weatherId <= 761) {
                return R.drawable.ic_fog;
            } else if (weatherId == 761 || weatherId == 781) {
                return R.drawable.ic_storm;
            } else if (weatherId == 800) {
                return R.drawable.ic_clear;
            } else if (weatherId == 801) {
                return R.drawable.ic_light_clouds;
            } else if (weatherId >= 802 && weatherId <= 804) {
                return R.drawable.ic_cloudy;
            }
            return -1;
        }
    }
}