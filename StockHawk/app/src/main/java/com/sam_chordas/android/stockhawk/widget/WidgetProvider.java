package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;


public class WidgetProvider extends AppWidgetProvider {

    public static String CLICK_ACTION = "com.sam_chordas.android.quotelistwidget.CLICKQUOTE";

    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;


    public WidgetProvider() {
        sWorkerThread = new HandlerThread("QuoteWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        final ComponentName cn = new ComponentName(ctx, WidgetProvider.class);
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list);

        final String action = intent.getAction();
        if (action.equals(CLICK_ACTION)) {
            final String ticker = intent.getStringExtra("ticker");

            Intent i = new Intent(ctx, StockDetailActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("ticker", ticker);
            ctx.startActivity(i);
        }
        super.onReceive(ctx, intent);
    }

    private RemoteViews buildLayout(Context context, int appWidgetId) {
        RemoteViews rv;

        final Intent intent = new Intent(context, WidgetRemoteViewService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv = new RemoteViews(context.getPackageName(), R.layout.widget_collection);
        rv.setRemoteAdapter(R.id.widget_list, intent);

        final Intent onClickIntent = new Intent(context, WidgetProvider.class);
        onClickIntent.setAction(WidgetProvider.CLICK_ACTION);
        onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.widget_list, onClickPendingIntent);

        return rv;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews layout = buildLayout(context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
