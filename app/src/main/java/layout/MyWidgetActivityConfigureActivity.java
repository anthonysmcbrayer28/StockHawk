package layout;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.N;

/**
 * The configuration screen for the {@link MyWidgetActivity MyWidgetActivity} AppWidget.
 */
public class MyWidgetActivityConfigureActivity extends RemoteViewsService {

    public final static String LOG_TAG = MyWidgetActivity.class.getSimpleName();

    private final DecimalFormat dollarFormat;
    private final DecimalFormat dollarFormatWithPlus;

    public MyWidgetActivityConfigureActivity() {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockPriceRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class StockPriceRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


        private List<Stock> stocks;
        private Context mContext;
        private int mAppWidgetId;

        private Cursor cursor = null;


        public StockPriceRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            stocks = new ArrayList<Stock>();
        }

        @Override
        public void onDataSetChanged() {
            long identity = Binder.clearCallingIdentity();

            String[] projection = new String[]{
                    Contract.Quote.COLUMN_SYMBOL,
                    Contract.Quote.COLUMN_PRICE,
                    Contract.Quote.COLUMN_ABSOLUTE_CHANGE};

            cursor = getContentResolver().query(Contract.Quote.uri,
                    projection, null, null, null);

            cursor.moveToPosition(-1);

            try {
                while (cursor.moveToNext()) {
                    stocks.add(new Stock(cursor.getString(0),
                            cursor.getFloat(1),
                            cursor.getFloat(2)));
                    Log.d(LOG_TAG, cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + "\n");
                }
            } finally {
                cursor.close();
            }

            Log.d(LOG_TAG, "cursor length: " + cursor.getCount() + "\n" + "stocks length: " + stocks.size());
            Binder.restoreCallingIdentity(identity);
        }


        @Override
        public void onDestroy() {
            stocks.clear();
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(getPackageName(), R.layout.my_widget_activity);

            Log.d("Widgets","Get view at " + position);

            rv.setTextViewText(R.id.widget_stock_list, stocks.get(position).code);
            rv.setTextViewText(R.id.widget_price, dollarFormat.format(stocks.get(position).price));
            rv.setTextViewText(R.id.widget_change, dollarFormatWithPlus.format(stocks.get(position).change));

            Log.d(LOG_TAG, stocks.get(position).code + " " + String.valueOf(stocks.get(position).price) + " " + String.valueOf(stocks.get(position).change) + "\n");
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.my_widget_activity);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    public class Stock {
        String code;
        float price, change;

        public Stock(String code, float price, float change) {
            this.code = code;
            this.price = price;
            this.change = change;
        }
    }
}


