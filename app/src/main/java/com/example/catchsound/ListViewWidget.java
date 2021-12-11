package com.example.catchsound;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class ListViewWidget extends AppWidgetProvider implements TextToSpeech.OnInitListener{

    private static final String ACTION_BUTTON = "com.example.catchsound.ITEM_CONTENT";

    private Context context1;
    private static TextToSpeech tts;
    /**
     * 위젯의 크기 및 옵션이 변경될 때마다 호출되는 함수
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        여기부분 다 사용할 일 없어져서 주석처리함!
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_view_widget);
        views.setTextViewText(R.id.widget_test_textview, widgetText);




        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * 위젯이 바탕화면에 설치될 때마다 호출되는 함수
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            // RemoteViewsService 실행 등록시키는 함수
            Intent serviceIntent = new Intent(context, MyRemoteViewsService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.list_view_widget);
            widget.setRemoteAdapter(R.id.widget_listview, serviceIntent);
//        클릭이벤트 인텐트 유보.
            Intent toastIntent = new Intent(context, ListViewWidget.class);
            toastIntent.setAction(ListViewWidget.ACTION_BUTTON);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);


            Intent testIntent=new Intent(context, ListViewWidget.class);
            testIntent.setAction("hihi");




            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.widget_listview, toastPendingIntent);


            PendingIntent pi= PendingIntent.getBroadcast(context, 0, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.widget_test_textview,pi);





            //보내기
            appWidgetManager.updateAppWidget(appWidgetIds, widget);
        }


        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }



    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);




        if (intent.getAction().equals(ACTION_BUTTON)) {


            String data= intent.getStringExtra("item_data");
            Toast.makeText(context,data,Toast.LENGTH_SHORT).show();

            Intent speechIntent= new Intent(context, SpeechService.class);
            speechIntent.putExtra(SpeechService.EXTRA_WORD, data);
            context.startService(speechIntent);


        }

        else if(intent.getAction().equals("hihi")){
            //String a= intent.getStringExtra()
            Toast.makeText(context,"굿굿", Toast.LENGTH_SHORT).show();
        }




    }


    @Override
    public void onInit(int status) {

    }
}