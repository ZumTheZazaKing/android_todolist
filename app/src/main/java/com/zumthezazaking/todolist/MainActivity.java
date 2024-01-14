package com.zumthezazaking.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    EditText taskInput;
    TextView text;
    ImageButton addBtn;
    ImageButton clearBtn;
    LinearLayout list;
    SharedPreferences myPreferences;
    String tasks = "";

    void loadTasks(String tasks){
        list.removeAllViews();
        String[] taskList = tasks.split(",");
        Arrays.stream(taskList).forEach(task -> {
            taskItem(task,tasks.split(","),this);
        });
    }

    void setDataHolder(String tasks){
        myPreferences = this.getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("tasks",tasks);
        editor.apply();
        loadTasks(tasks);
    }

    @SuppressLint("ResourceAsColor")
    void taskItem(String taskName, String[] tasks,Context context){
        if(!taskName.isEmpty()){

            String[] taskNameSet = taskName.split("<&#!ab>");
            String name = taskNameSet[0];
            String status = taskNameSet[1].trim().replaceAll(",","");

            LinearLayout taskRow = new LinearLayout(context);
            taskRow.setOrientation(LinearLayout.VERTICAL);

            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(30,30,30,30);
            row.setVerticalGravity(Gravity.CENTER_VERTICAL);
            taskRow.addView(row);

            View viewDivider = new View(context);
            viewDivider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().density * 2)));
            viewDivider.setBackgroundColor(Color.BLACK);
            taskRow.addView(viewDivider);

            TextView text = new TextView(context);
            text.setText(name);
            text.setTextColor(Color.BLACK);
            text.setTextSize(18);
            text.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));

            if(status.replaceAll(",","").equals("checked")){
                text.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(tasks));
                        int indexToEdit = list.indexOf(taskName);
                        list.set(indexToEdit,name+"<&#!ab>unchecked");
                        setDataHolder(String.join(",",list));
                    }
                });
            }else{
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(tasks));
                        int indexToEdit = list.indexOf(taskName);
                        list.set(indexToEdit,name+"<&#!ab>checked");
                        setDataHolder(String.join(",",list));
                    }
                });
            }
            row.addView(text);

            if(status.replaceAll("", "").equals("checked")){
                ImageButton deleteBtn = new ImageButton(context);
                deleteBtn.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                deleteBtn.setImageResource(android.R.drawable.ic_delete);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(tasks));
                        int indexToEdit = list.indexOf(taskName);
                        list.remove(indexToEdit);
                        setDataHolder(String.join(",",list));
                    }
                });
                row.addView(deleteBtn);
            }else{
                ImageButton editBtn = new ImageButton(context);
                editBtn.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                editBtn.setImageResource(android.R.drawable.ic_menu_edit);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LinearLayout editView = new LinearLayout(context);
                        editView.setPadding(20,20,20,20);
                        editView.setWeightSum(1f);

                        EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        input.setText(name);
                        input.setPadding(10,25,10,25);
                        input.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));
                        editView.addView(input);


                        new AlertDialog.Builder(context)
                                .setTitle("Edit Task")
                                .setView(editView)
                                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newName = input.getText().toString().trim();
                                        if(!newName.isEmpty()){
                                            String[] tempTasks = tasks;
                                            for (String tempTask : tempTasks) {
                                                String[] taskSet = tempTask.split("<&#!ab>");
                                                if (newName.equals(taskSet[0].trim())) {
                                                    new android.os.Handler().postDelayed(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), "Edit failed. Another task has that name.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                    ,100);
                                                    return;
                                                }
                                            }
                                            ArrayList<String> list = new ArrayList<>(Arrays.asList(tasks));
                                            int indexToEdit = list.indexOf(taskName);
                                            list.set(indexToEdit,newName+"<&#!ab>unchecked");
                                            setDataHolder(String.join(",",list));
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .show();
                    }
                });
                row.addView(editBtn);
            }
            list.addView(taskRow);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = this.getSharedPreferences("myPrefs",MODE_PRIVATE);
        //getApplicationContext().getSharedPreferences("myPrefs", 0).edit().clear().commit(); //uncomment line to delete all data
        setContentView(R.layout.activity_main);

        tasks = myPreferences.getString("tasks","");

        taskInput = (EditText) findViewById(R.id.taskInput);
        text = (TextView) findViewById(R.id.dataHolder);
        addBtn = (ImageButton) findViewById(R.id.addBtn);
        clearBtn = (ImageButton) findViewById(R.id.clearBtn);
        list = (LinearLayout) findViewById(R.id.list);

        text.setText(tasks);
        loadTasks(tasks);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = taskInput.getText().toString().trim();
                if(!task.isEmpty()){
                    String[] tempTasks = tasks.split(",");
                    for (String tempTask : tempTasks) {
                        String[] taskSet = tempTask.split("<&#!ab>");
                        if (task.equals(taskSet[0].trim())) {
                            Toast.makeText(getApplicationContext(), "Task already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    String textToEnter = task+"<&#!ab>unchecked,";
                    text.append(textToEnter);
                    setDataHolder(String.join(",",text.getText().toString()));

                    if(view != null){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Task added",Toast.LENGTH_SHORT).show();
                                }
                            }
                    ,100);

                    taskInput.setText("");
                    taskInput.clearFocus();
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("");

                SharedPreferences.Editor editor = myPreferences.edit();
                editor.putString("tasks","");
                editor.apply();

                list.removeAllViews();

                Toast.makeText(getApplicationContext(),"All Tasks Cleared",Toast.LENGTH_SHORT).show();
            }
        });
    }
}