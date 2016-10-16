package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;


public class WidgetRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewFactory(this.getApplicationContext(), intent);
    }
}


class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    public ListViewFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {

    }

    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider
        String ticker = "";
        String bidPrice = "";
        String change = "";
        int isUp = 1;
        if (mCursor.moveToPosition(position)) {
            final int symbolColIndex = mCursor.getColumnIndex(QuoteColumns.SYMBOL);
            final int bidPriceColIndex = mCursor.getColumnIndex(QuoteColumns.BIDPRICE);
            final int changeColIndex = mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE);
            final int isUpIndex = mCursor.getColumnIndex(QuoteColumns.ISUP);
            ticker = mCursor.getString(symbolColIndex);
            bidPrice = mCursor.getString(bidPriceColIndex);
            change = mCursor.getString(changeColIndex);
            isUp = mCursor.getInt(isUpIndex);
        }


        final int itemId = R.layout.list_item_quote;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setTextViewText(R.id.stock_symbol, ticker);
        rv.setTextViewText(R.id.bid_price, bidPrice);
        rv.setTextViewText(R.id.change, change);
        if (isUp == 1) {
            rv.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            rv.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }


        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString("ticker", ticker);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.stock_item, fillInIntent);
        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {

        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }
}
