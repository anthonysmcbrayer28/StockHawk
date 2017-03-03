package layout;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import static android.R.style.Widget;

/**
 * Created by antho_000 on 3/1/2017.
 */
@SuppressLint("NewApi")
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        MyWidgetActivityConfigureActivity dataProvider = new MyWidgetActivityConfigureActivity(
                getApplicationContext(), intent);
        return (RemoteViewsFactory) dataProvider;
    }

}