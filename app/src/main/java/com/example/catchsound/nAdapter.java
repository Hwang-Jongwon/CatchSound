package com.example.catchsound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class nAdapter extends RecyclerView.Adapter<nAdapter.ItemViewHolder> {

    private Context context;

    private com.example.catchsound.DBHelper dbHelper;

    private ArrayList<com.example.catchsound.TodoItem> todoItems;

    public nAdapter(ArrayList<com.example.catchsound.TodoItem> todoItems, Context context) {
        this.todoItems = todoItems;
        this.context = context;
        dbHelper = new com.example.catchsound.DBHelper(context);
    }

    public void addItem(com.example.catchsound.TodoItem _item) {
        this.todoItems.add(0, _item);
        notifyItemInserted(0);
    }

    public int getItemCount() {
        return todoItems.size();
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.item_Content.setText(todoItems.get(position).getContent());
        holder.item_Date.setText(todoItems.get(position).getDate());
        holder.item_Flag.setText(todoItems.get(position).getFlag());
        holder.item_Star.setBackgroundResource(todoItems.get(position).getStar());
        holder.item_Tag.setText(todoItems.get(position).getTag());
        holder.item_Use.setText(todoItems.get(position).getUse());
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView item_Content;

        private TextView item_Date;

        private ImageView item_Star;

        private TextToSpeech tts;

        private TextView item_Flag;

        private TextView item_Tag;

        private TextView item_Use;




        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ItemViewHolder(View itemView) {

            super(itemView);

            item_Content = itemView.findViewById(R.id.item_content);
            item_Date = itemView.findViewById(R.id.item_date);
            item_Star = itemView.findViewById(R.id.star);
            item_Flag = itemView.findViewById(R.id.item_flag);
            item_Tag = itemView.findViewById(R.id.item_tag);
            item_Use = itemView.findViewById(R.id.item_use);

            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {


                }
            });

            item_Star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curPos = getAdapterPosition();
                    com.example.catchsound.TodoItem ctodoItem = todoItems.get(curPos);
                    String flag = item_Flag.getText().toString();
                    String editcontent = item_Content.getText().toString();
                    String edittag = item_Tag.getText().toString();
                    String edituse = item_Use.getText().toString();
                    int usenum = Integer.parseInt(edituse);
                    edituse = String.valueOf(usenum);

                    if(flag == "0"){
                        flag = "1";
                        item_Star.setBackgroundResource(R.drawable.star_full);
                        String content = editcontent;
                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                        String beforeDate = ctodoItem.getDate();
                        String tag = edittag;
                        String use = edituse;
                        dbHelper.UpdateTodo(content, curTime, beforeDate, flag, R.drawable.star_full, tag, use);
                        ctodoItem.setFlag(flag);
                        ctodoItem.setDate(curTime);
                        ctodoItem.setStar(R.drawable.star_full);
                        notifyDataSetChanged();
                    }
                    else{
                        flag = "0";
                        item_Star.setBackgroundResource(R.drawable.star_empty);
                        String content = editcontent;
                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                        String beforeDate = ctodoItem.getDate();
                        String tag = edittag;
                        String use = edituse;
                        dbHelper.UpdateTodo(content, curTime, beforeDate, flag, R.drawable.star_empty, tag, use);
                        ctodoItem.setFlag(flag);
                        ctodoItem.setDate(curTime);
                        ctodoItem.setStar(R.drawable.star_empty);
                        notifyDataSetChanged();
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.custom_dialog);

                int curPos = getAdapterPosition();
                com.example.catchsound.TodoItem ctodoItem = todoItems.get(curPos);
                String flag = item_Flag.getText().toString();
                String editcontent = item_Content.getText().toString();
                String edittag = item_Tag.getText().toString();
                String edituse = item_Use.getText().toString();
                int usenum = Integer.parseInt(edituse);
                usenum += 1;
                edituse = String.valueOf(usenum);

                String content = editcontent;
                String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                String beforeDate = ctodoItem.getDate();
                String tag = edittag;
                String use = edituse;
                dbHelper.UpdateTodo(content, curTime, beforeDate, flag, R.drawable.star_full, tag, use);
                ctodoItem.setDate(curTime);
                ctodoItem.setUse(use);
                notifyDataSetChanged();

                AudioManager volumeControl = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if(volumeControl.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
                    Toast.makeText(context, "소리가 꺼져있어요!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "음성 재생중입니다!", Toast.LENGTH_SHORT).show();

                tts.setPitch((float) 0.8);
                tts.setSpeechRate((float) 0.9);
                tts.speak(item_Content.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,"id1");

            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View param2View) {
                    int curPos = getAdapterPosition();
                    com.example.catchsound.TodoItem ctodoItem = todoItems.get(curPos);

                    String[] options = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("원하는 작업을 선택하세요.");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                Dialog dialog = new Dialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.custom_dialog);
                                EditText editcontent = dialog.findViewById(R.id.editContent);
                                EditText edittag = dialog.findViewById(R.id.editTag);
                                editcontent.setText(ctodoItem.getContent());
                                edittag.setText(ctodoItem.getTag());
                                Button btn_save = dialog.findViewById(R.id.button_save);
                                Button btn_cancel = dialog.findViewById(R.id.button_cancel);

                                btn_save.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        String content = editcontent.getText().toString();
                                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                                        String beforeDate = ctodoItem.getDate();
                                        String flag = ctodoItem.getFlag();
                                        String tag = edittag.getText().toString();
                                        int star = ctodoItem.getStar();
                                        String use = ctodoItem.getUse();
                                        dbHelper.UpdateTodo(content, curTime, beforeDate, flag, star, tag, use);
                                        ctodoItem.setFlag(flag);
                                        ctodoItem.setContent(content);
                                        ctodoItem.setDate(curTime);
                                        ctodoItem.setTag(tag);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                btn_cancel.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            } else if (position == 1) {
                                String str = ctodoItem.getDate();
                                dbHelper.DeleteTodo(str);
                                todoItems.remove(curPos);
                                notifyItemRemoved(curPos);
                            }

                        }
                    });
                    builder.show();
                    return true;
                }

            });




        }

    }
}
