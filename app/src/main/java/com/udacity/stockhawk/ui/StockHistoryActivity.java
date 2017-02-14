package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by antho_000 on 2/13/2017.
 */

public class StockHistoryActivity extends Activity {
    private static final String LOG_TAG = StockHistoryActivity.class.getSimpleName();
    private final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    View rootView;
    TextView titleView;
    LineChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        Intent intent = getIntent();
        String stockCode = intent.getStringExtra(MainActivity.STOCK_CODE_STRING);

        rootView = this.findViewById(android.R.id.content);
        titleView = (TextView) rootView.findViewById(R.id.stock_code_view);
        chart = (LineChart) rootView.findViewById(R.id.chart);

        titleView.setText(stockCode);

        // Get history data
        String[] projection = new String[]{Contract.Quote.COLUMN_PRICE, Contract.Quote.COLUMN_HISTORY};
        Cursor c = this.getContentResolver().query(Contract.Quote.makeUriForStock(stockCode),
                projection, null, null, null);
        if (c.moveToFirst() && c.getCount() >= 1) {
            TextView priceView = (TextView) rootView.findViewById(R.id.stock_price_view);
            priceView.setText(dollarFormat.format(c.getFloat(0)));
            plotStockHistory(c.getString(1));
        }
    }

    private void plotStockHistory(String data){
        List<Entry> entries = new ArrayList<Entry>();
        String[] tokens = data.split("\n+");
        for (int i = tokens.length - 1; i > 0; i--) {
            String[] xy = tokens[i].split(" *, *");
            entries.add(new Entry(Float.valueOf(xy[0]), Float.valueOf(xy[1])));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.WHITE);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        // Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new XAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5, true);
        xAxis.setTextSize(10);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setLabelCount(5, true);
        yAxis.setTextSize(14);

        chart.getAxisLeft().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setHighlightPerDragEnabled(false);
    }

    public class XAxisValueFormatter implements IAxisValueFormatter {

        private SimpleDateFormat mFormat;

        public XAxisValueFormatter() {
            mFormat = new SimpleDateFormat("MM/dd/yy");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mFormat.format(value);
        }
    }
}
