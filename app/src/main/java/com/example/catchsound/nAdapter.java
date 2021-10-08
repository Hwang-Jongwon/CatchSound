package com.example.catchsound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class nAdapter extends RecyclerView.Adapter<nAdapter.ItemViewHolder> {
    Calendar cal = Calendar.getInstance();

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
        holder.item_Title.setText(todoItems.get(position).getTitle());
        holder.item_Content.setText(todoItems.get(position).getContent());
        holder.item_Date.setText(todoItems.get(position).getDate());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView item_Content;

        private TextView item_Date;

        private TextView item_Title;
        private TextToSpeech tts;


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ItemViewHolder(View itemView) {

            super(itemView);

            item_Title = itemView.findViewById(R.id.item_title);
            item_Content = itemView.findViewById(R.id.item_content);
            item_Date = itemView.findViewById(R.id.item_date);

            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {


                }
            });

            itemView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.activity_edit);

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
                                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.activity_edit);
                                EditText edittitle = dialog.findViewById(R.id.editTitle);
                                EditText editcontent = dialog.findViewById(R.id.editContent);
                                edittitle.setText(ctodoItem.getTitle());
                                editcontent.setText(ctodoItem.getContent());
                                Button btn_save = dialog.findViewById(R.id.button_save);
                                Button btn_cancel = dialog.findViewById(R.id.button_cancel);

                                btn_save.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        String title = edittitle.getText().toString();
                                        String content = editcontent.getText().toString();
                                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                                        String beforeDate = ctodoItem.getDate();
                                        dbHelper.UpdateTodo(title, content, curTime, beforeDate);
                                        ctodoItem.setTitle(title);
                                        ctodoItem.setContent(content);
                                        ctodoItem.setDate(curTime);
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
