package com.example.alimi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alimi.MemoContract;
import com.example.alimi.MemoDbHelper;
import com.example.alimi.memo_location;

import java.util.Calendar;

public class MemoActivity extends AppCompatActivity {
    private EditText mTitleEditText;
    private EditText mContentsEditText;
    private long mMemoId=-1;

    Switch switch1;
    Switch switch2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        getSupportActionBar().setTitle("Alimi");

        //switch 버튼 실행
        switch1=(Switch)findViewById(R.id.switch1);
        switch2=(Switch)findViewById(R.id.switch2);
        CheckState();
        switch1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CheckState();
            }
        });
        switch2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CheckState();
            }
        });

        // 반복 스피너
        final TextView editText6=(TextView)findViewById(R.id.editText6);
        Spinner spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText6.setText("반복 : "+parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 알림음 스피너
        final TextView editText7=(TextView)findViewById(R.id.editText7);
        Spinner spinner3=(Spinner)findViewById(R.id.spinner3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText7.setText("알림음 : "+parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        mTitleEditText = (EditText) findViewById(R.id.title_edit);
        mContentsEditText = (EditText) findViewById(R.id.contents_edit);
        Intent intent=getIntent();
        if (intent != null) {
            mMemoId = intent.getLongExtra("id", -1);
            String title = intent.getStringExtra("title");
            String contents = intent.getStringExtra("contents");
            mTitleEditText.setText(title);
            mContentsEditText.setText(contents);
        }
    }

    //switch 버튼 실행
    private void CheckState() {
        TextView editText6=(TextView)findViewById(R.id.editText6);
        TextView editText7=(TextView)findViewById(R.id.editText7);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        Button btn1=(Button)findViewById(R.id.btn1);
        Button btn2=(Button)findViewById(R.id.btn2);
        Button btn3=(Button)findViewById(R.id.btn3);
        Spinner spinner2=(Spinner)findViewById(R.id.spinner2);

        if(switch1.isChecked()){
            btn3.setText("위치");
        }
        else{
            btn3.setText("");
        }
        if(switch2.isChecked()){
            textView2.setText("알림");
            editText6.setText("반복");
            editText7.setText("알림음");
            btn1.setText("TIME");
            btn2.setText("DATE");
        }
        else{
            textView2.setText("");
            editText6.setText("");
            editText7.setText("");
            btn1.setText("");
            btn2.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        // DB 에 저장하는 처리
        String title = mTitleEditText.getText().toString();
        String contents = mContentsEditText.getText().toString();


        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);

        SQLiteDatabase db = MemoDbHelper.getInstance(this).getWritableDatabase();
        if(mMemoId==-1){
            long newRowId=db.insert(MemoContract.MemoEntry.TABLE_NAME,null,contentValues);

            if (newRowId == -1) {
                Toast.makeText(this, "저장에 문제가 발생하였습니다",
                        Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "메모가 저장되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }else{
            int count = db.update(MemoContract.MemoEntry.TABLE_NAME,contentValues,MemoContract.MemoEntry._ID+" = "+mMemoId,null);

            if (count == 0) {
                Toast.makeText(this, "수정에 문제가 발생하였습니다",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "메모가 수정되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }
        super.onBackPressed();
    }

    // time/date 버튼
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }

    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //위치 다른 액티비티로 이동
    public void onClick_01(View v){
        Intent intent_01=new Intent(getApplicationContext(), memo_location.class);
        startActivity(intent_01);
    }

}
