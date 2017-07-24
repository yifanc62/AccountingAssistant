package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.BookUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.database.UploadUtils;
import com.cirnoteam.accountingassistant.database.UserUtils;
import com.cirnoteam.accountingassistant.entity.Book;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private List<String> bookName = new ArrayList<>();
    private List<String> record = new ArrayList<>();
    private List<Book> list_books = new ArrayList<>();
    private List<Long> list_book_id = new ArrayList<>();
    private List<Long> list_record_id = new ArrayList<>();
    private ImageButton leftmenu;
    private TextView mDate;
    private Spinner mySpinner;
    private ArrayAdapter<String> bookAdapter;
    private ListView myListView;
    private ArrayAdapter<String> recordAdapter;
    private List<com.cirnoteam.accountingassistant.entity.Record> records = new ArrayList<com.cirnoteam.accountingassistant.entity.Record>();
    private BookUtils bookUtils = new BookUtils(this);
    private UserUtils userUtils = new UserUtils(this);
    private AccountUtils accountUtils = new AccountUtils(this);
    private Animation scaleAnimation;
    private ImageView photo;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_record:
                    Intent intentToRecord = new Intent(MainActivity.this, Record.class);
                    intentToRecord.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToRecord);
                    return true;
                case R.id.navigation_user:
                    Intent intentToUser = new Intent(MainActivity.this, BankCard.class);
                    intentToUser.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToUser);
                    return true;
            }
            return false;

        }

    };

    private void showInputDialog() {
        final EditText editText = new EditText(MainActivity.this);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);

        inputDialog.setTitle("请输入账本名称").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bookAdapter.insert(editText.getText().toString(),0);
                        Long id;
                        if((id = bookUtils.addBook(userUtils.getCurrentUsername(),editText.getText().toString()))!=-1) {
                            accountUtils.addAccount(id,0,1000F,"默认账户");
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            Status.bookid = id;
                            reSetSpinner();
                            for(int i=0;i<list_book_id.size();i++) {
                                if(list_book_id.get(i) == id)
                                    mySpinner.setSelection(i);
                            }
                        }
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //mySpinner.setSelection(bookAdapter.getPosition(bookUtils.getBook(Status.bookid).getName()));
                        //把spinner的当前选项还原为正确选项
                        //do nothing
                    }
                });
        inputDialog.show();
    }
    private void showDeleteDialog() {
        final EditText editText = new EditText(MainActivity.this);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);

        inputDialog.setTitle("是否删除当前账本");
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(bookUtils.deleteBook(Status.bookid))
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        reSetSpinner();
                        mySpinner.setSelection(0);
                        Status.bookid = list_book_id.get(0);
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
        inputDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        Calendar cal = Calendar.getInstance();
        String currentDate = cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH);
        mDate = (TextView) findViewById(R.id.Date);
        mDate.setText(currentDate);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftmenu = (ImageButton) findViewById(R.id.user);
        leftmenu.setImageBitmap(UploadUtils.getImage(userUtils.getCurrentUsername()));
        leftmenu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                TextView username = (TextView)findViewById(R.id.username);
                ImageView imageView = (ImageView)findViewById(R.id.userphoto) ;
                username.setText(userUtils.getCurrentUsername());
                imageView.setImageBitmap(UploadUtils.getImage(userUtils.getCurrentUsername()));
                drawer.openDrawer(Gravity.START);
            }
        });

        //spinner
        mySpinner = (Spinner)findViewById(R.id.spinner_book);
        BookUtils u = new BookUtils(this);
        list_books = u.getAllBooks(userUtils.getCurrentUsername());
        for(Book list_book:list_books){
            bookName.add(list_book.getName());
            list_book_id.add(list_book.getId());
        }
        Status.bookid = list_book_id.get(0);
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookName);

        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(bookAdapter);
        //bookAdapter.add("默认账本");
        bookAdapter.add("＋");

        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == (bookAdapter.getCount()-1)) {
                    showInputDialog();
                    for(int i=0;i<list_book_id.size();i++) {
                        if(list_book_id.get(i) == Status.bookid)
                            mySpinner.setSelection(i);
                    }//还原spinner选项
                    arg0.setVisibility(View.VISIBLE);
                } else{
                    Status.bookid = list_book_id.get(arg2);
                    reSetList();//调用设值方法以刷新
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });

        //list
        reSetList();
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RecordDetail.class);
                intent.putExtra("recordid", list_record_id.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
        reSetSpinner();
        if(Status.bookid != null) {
            for(int i=0;i<list_book_id.size();i++) {
                if(list_book_id.get(i) == Status.bookid)
                    mySpinner.setSelection(i);
            }
        }

        reSetList();//调用设流水列表值方法

        //动画
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        myListView.startAnimation(scaleAnimation);
        LinearLayout Line = (LinearLayout) findViewById(R.id.L1);
        Line.startAnimation(scaleAnimation);
    }

    private void reSetList(){
        list_record_id.clear();
        record.clear();
        RecordUtils recordUtils = new RecordUtils(this);
        records = recordUtils.getLateast4Records(Status.bookid);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        int i = 0;
        for(com.cirnoteam.accountingassistant.entity.Record _record : records) {
            String newRecord = " ";
            newRecord += fm.format(_record.getTime());
            newRecord += " ";
            newRecord += _record.getExpense()?"支出 ":"收入 ";
            newRecord += _record.getAmount();
            newRecord += " ";
            switch (_record.getType()){
                case 0:newRecord += " 一日三餐";
                    break;
                case 1:newRecord += " 购物消费";
                    break;
                case 2:newRecord += " 水电煤气";
                    break;
                case 3:newRecord += " 交通花费";
                    break;
                case 4:newRecord += " 医疗消费";
                    break;
                case 5:newRecord += " 其他支出";
                    break;
                case 6:newRecord += " 经营获利";
                    break;
                case 7:newRecord += " 工资收入";
                    break;
                case 8:newRecord += " 路上捡钱";
                    break;
                case 9:newRecord += " 其他收入";
                    break;
            }
            list_record_id.add(_record.getId());
            record.add(newRecord);
            i++;
            if(i == 4)
                break;
        }
        myListView = (ListView) findViewById(R.id.listview);
        myListView.setDividerHeight(5);
        recordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, record);
        myListView.setAdapter(recordAdapter);
    }

    private void reSetSpinner(){
        list_book_id.clear();
        bookName.clear();

//        第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list
        BookUtils u = new BookUtils(this);
        list_books = u.getAllBooks(userUtils.getCurrentUsername());
        for(Book list_book:list_books){
            bookName.add(list_book.getName());
            list_book_id.add(list_book.getId());
        }
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookName);

        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(bookAdapter);
        //bookAdapter.add("默认账本");
        bookAdapter.add("＋");
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中

        //((BaseAdapter) mySpinner.getAdapter()).notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.change_photo) {
            showChoosePicDialog();
        } else if (id == R.id.change_password) {
            Intent intent = new Intent(this,TransitPassword.class);
            startActivity(intent);
        }else if (id == R.id.log_off) {
            UserUtils userUtils = new UserUtils(this);
            userUtils.logout(userUtils.getCurrentUsername());
            Intent intent = new Intent(this,LogIn.class);
            startActivity(intent);
            Toast.makeText(getApplication(),"注销成功",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        try{
                            setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     */
    protected void setImageToView(Intent data) throws IOException{
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bmp = extras.getParcelable("data");
            photo = (ImageView)findViewById(R.id.userphoto) ;
            photo.setImageBitmap(bmp);
            File userPhoto = changeToFile(bmp,"userphoto.jpg");
            UploadUtils.post(userUtils.getCurrentUsername(),userPhoto);
        }
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     * @param bm
     * @param fileName
     */
    public static File changeToFile(Bitmap bm,String fileName){
        File userphoto = new File(Environment.getExternalStorageDirectory()+fileName);
        if (userphoto.exists()) {
            userphoto.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(userphoto));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userphoto;
    }

    public void toNewRecord(View view) {
        Intent intent = new Intent(this, NewRecord.class);
        startActivity(intent);
    }

    public void toInquire(View view) {
        Intent intent = new Intent(this, Inquire.class);
        startActivity(intent);
    }
    public void toChart(View view){
        Intent intent = new Intent(this,Chart.class);
        startActivity(intent);
    }

    public void toDeleteBook(View view){
        showDeleteDialog();
    }
}
