package com.example.alimi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_INSERT = 1000;
    private MemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 제목
        getSupportActionBar().setTitle("Alimi");
        FloatingActionButton fab= (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,
                        MemoActivity.class),REQUEST_CODE_INSERT);

            }
        });
        //메모 목록 화면 작성
        ListView listView=(ListView)findViewById(R.id.memo_list);
        MemoDbHelper dbHelper = MemoDbHelper.getInstance(this);
        Cursor cursor = dbHelper.getReadableDatabase()
                .query(MemoContract.MemoEntry.TABLE_NAME,
                        null, null, null, null, null, null);

        mAdapter = new MemoAdapter(this, cursor);
        listView.setAdapter(mAdapter);
        // 리스트 클릭 시 메모 내용 표시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                // 클릭한 시점의 아이템을 얻음
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                // 커서에서 제목과 내용을 얻음
                String title = cursor.getString(cursor.getColumnIndexOrThrow
                        (MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow
                        (MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));
                // 인텐트에 id와 함께 저장
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);
                // MemoActivity 를 시작
                startActivityForResult(intent,REQUEST_CODE_INSERT);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                final long deleteId = id;
                // 삭제할 것인지 다이얼로그 표시
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("메모 삭제");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db=MemoDbHelper.getInstance(MainActivity.this).getWritableDatabase();
//                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME,
//                                MemoContract.MemoEntry._ID + " = " + deleteId, null);
                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME,
                                MemoContract.MemoEntry._ID + " = " + deleteId, null);

                        if (deletedCount == 0) {
                            Toast.makeText(MainActivity.this, "삭제에 문제가 발생하였습니다",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mAdapter.swapCursor(getMemoCursor());
                            Toast.makeText(MainActivity.this, "메모가 삭제되었습니다",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            }
        });
    }

    private Cursor getMemoCursor(){
        MemoDbHelper dbHelper=MemoDbHelper.getInstance(this);
        return dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME,null,null,null,null,null,MemoContract.MemoEntry._ID+" DESC");
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_CODE_INSERT&&resultCode==RESULT_OK){
            mAdapter.swapCursor(getMemoCursor());
        }
    }

    private static class MemoAdapter extends CursorAdapter {
        public MemoAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = (TextView) view.findViewById(android.R.id.text1);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    // 액션바 오른쪽 메뉴 클릭 시 다음 액티비티로 이동
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu1:
                Intent intent1=new Intent(this,main_menu_set.class);
                startActivity(intent1);
                return true;
            case R.id.menu_action2:
                Intent intent2=new Intent(this,main_menu_help.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

