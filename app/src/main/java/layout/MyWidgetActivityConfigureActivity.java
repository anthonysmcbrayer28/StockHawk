package layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration screen for the {@link MyWidgetActivity MyWidgetActivity} AppWidget.
 */
@SuppressLint("NewApi")
public class MyWidgetActivityConfigureActivity implements RemoteViewsFactory {
    List mCollections = new ArrayList();

    Context mContext = null;

    public MyWidgetActivityConfigureActivity(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        mView.setTextViewText(android.R.id.text1, (CharSequence) mCollections.get(position));
        mView.setTextColor(android.R.id.text1, Color.BLACK);

        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(MyWidgetActivity.ACTION_TOAST);
        final Bundle bundle = new Bundle();
        bundle.putString(MyWidgetActivity.EXTRA_STRING,
                (String) mCollections.get(position));
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(android.R.id.text1, fillInIntent);
        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        mCollections.clear();
        for (int i = 1; i <= 10; i++) {
            mCollections.add("ListView item " + i);
        }
    }

    @Override
    public void onDestroy() {

    }

}



