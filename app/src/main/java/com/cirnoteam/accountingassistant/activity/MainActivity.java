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
import android.support.design.widget.BottomSheetBehavior;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.BookUtils;
import com.cirnoteam.accountingassistant.database.DirtyUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.database.UploadUtils;
import com.cirnoteam.accountingassistant.database.UserUtils;
import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.entity.Book;
import com.cirnoteam.accountingassistant.json.AccountReqEntity;
import com.cirnoteam.accountingassistant.json.BookReqEntity;
import com.cirnoteam.accountingassistant.json.RecordReqEntity;
import com.cirnoteam.accountingassistant.json.RemoteIdRespEntity;
import com.cirnoteam.accountingassistant.json.Response;
import com.cirnoteam.accountingassistant.json.SyncAccount;
import com.cirnoteam.accountingassistant.json.SyncBook;
import com.cirnoteam.accountingassistant.json.SyncRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    //private Book book = new Book();
    private BookUtils bookUtils = new BookUtils(this);
    private UserUtils userUtils = new UserUtils(this);
    private AccountUtils accountUtils = new AccountUtils(this);
    private DirtyUtils dirtyUtils = new DirtyUtils(this);
    private RecordUtils recordUtils = new RecordUtils(this);
    private Animation scaleAnimation, flyAnimation;
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
        editText.setMaxLines(6);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);

        inputDialog.setTitle("请输入账本名称").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bookAdapter.insert(editText.getText().toString(),0);
                        Long id;
                        if ((id = bookUtils.addBook(userUtils.getCurrentUsername(), editText.getText().toString())) != -1) {
                            accountUtils.addAccount(id, 0, 1000F, "默认账户");
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            Status.bookid = id;
                            reSetSpinner();
                            for (int i = 0; i < list_book_id.size(); i++) {
                                if (list_book_id.get(i) == id)
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
                        if (bookUtils.deleteBook(Status.bookid))
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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mDate = (TextView) findViewById(R.id.Date);
        String strdate = df.format(cal.getTime());
        mDate.setText(strdate);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftmenu = (ImageButton) findViewById(R.id.user);
        leftmenu.setImageURI(Uri.fromFile(new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/userphoto.png")));
        leftmenu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                TextView username = (TextView) findViewById(R.id.username);
                ImageView imageView = (ImageView) findViewById(R.id.userphoto);
                username.setText(userUtils.getCurrentUsername());
                if (UploadUtils.getImage(userUtils.getCurrentUsername()) != null) ;
                imageView.setImageBitmap(UploadUtils.getImage(userUtils.getCurrentUsername()));
                drawer.openDrawer(Gravity.START);
            }
        });

        //spinner
        mySpinner = (Spinner) findViewById(R.id.spinner_book);
        BookUtils u = new BookUtils(this);
        list_books = u.getAllBooks(userUtils.getCurrentUsername());
        for (Book list_book : list_books) {
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
                if (arg2 == (bookAdapter.getCount() - 1)) {
                    showInputDialog();
                    for (int i = 0; i < list_book_id.size(); i++) {
                        if (list_book_id.get(i) == Status.bookid)
                            mySpinner.setSelection(i);
                    }//还原spinner选项
                    arg0.setVisibility(View.VISIBLE);
                } else {
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
        if (Status.bookid != null) {
            for (int i = 0; i < list_book_id.size(); i++) {
                if (list_book_id.get(i) == Status.bookid)
                    mySpinner.setSelection(i);
            }
        }

        reSetList();//调用设流水列表值方法

        //动画
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        flyAnimation = AnimationUtils.loadAnimation(this, R.anim.flyin);
        myListView.startAnimation(scaleAnimation);
        TableLayout tab = (TableLayout) findViewById(R.id.main_jinkuang);
        tab.startAnimation(scaleAnimation);
        TextView textView1 = (TextView) findViewById(R.id.main_lab1);
        TextView textView2 = (TextView) findViewById(R.id.main_lab2);
        textView1.startAnimation(flyAnimation);
        textView2.startAnimation(flyAnimation);
    }

    private void reSetList() {
        list_record_id.clear();
        record.clear();
        RecordUtils recordUtils = new RecordUtils(this);
        records = recordUtils.getLateast4Records(Status.bookid);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        int i = 0;
        for (com.cirnoteam.accountingassistant.entity.Record _record : records) {
            String newRecord = " ";
            newRecord += fm.format(_record.getTime());
            newRecord += " ";
            newRecord += _record.getExpense() ? "支出 " : "收入 ";
            newRecord += _record.getAmount();
            newRecord += " ";
            switch (_record.getType()) {
                case 0:
                    newRecord += " 一日三餐";
                    break;
                case 1:
                    newRecord += " 购物消费";
                    break;
                case 2:
                    newRecord += " 水电煤气";
                    break;
                case 3:
                    newRecord += " 交通花费";
                    break;
                case 4:
                    newRecord += " 医疗消费";
                    break;
                case 5:
                    newRecord += " 其他支出";
                    break;
                case 6:
                    newRecord += " 经营获利";
                    break;
                case 7:
                    newRecord += " 工资收入";
                    break;
                case 8:
                    newRecord += " 路上捡钱";
                    break;
                case 9:
                    newRecord += " 其他收入";
                    break;
            }
            list_record_id.add(_record.getId());
            record.add(newRecord);
            i++;
            if (i == 4)
                break;
        }
        myListView = (ListView) findViewById(R.id.listview);
        myListView.setDividerHeight(5);
        recordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, record);
        myListView.setAdapter(recordAdapter);

        TextView income = (TextView) findViewById(R.id.main_income);
        TextView outcome = (TextView) findViewById(R.id.main_outcome);
        TextView remain = (TextView) findViewById(R.id.main_remain);
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.set(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        float amount_in = 0;
        float amount_out = 0;
        List<com.cirnoteam.accountingassistant.entity.Record> list_records = recordUtils.searchRecord(Status.bookid, startDate, endDate, null, null, null, null, null, null);
        for (com.cirnoteam.accountingassistant.entity.Record list_record : list_records) {
            if (list_record.getExpense())
                amount_out += list_record.getAmount();
            else
                amount_in += list_record.getAmount();
        }
        income.setText(String.valueOf(amount_in));
        outcome.setText(String.valueOf(amount_out));
        AccountUtils accountUtils = new AccountUtils(this);
        List<Account> accounts = accountUtils.getAccountsOfBook(Status.bookid);
        float book_remain = 0;
        for (Account account : accounts) {
            book_remain = account.getBalance();
        }
        remain.setText(String.valueOf(book_remain));
    }

    private void reSetSpinner() {
        list_book_id.clear();
        bookName.clear();

//        第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list
        BookUtils u = new BookUtils(this);
        list_books = u.getAllBooks(userUtils.getCurrentUsername());
        for (Book list_book : list_books) {
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
            Intent intent = new Intent(this, TransitPassword.class);
            startActivity(intent);
        } else if (id == R.id.log_off) {
            UserUtils userUtils = new UserUtils(this);
            userUtils.logout(userUtils.getCurrentUsername());
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
            Toast.makeText(getApplication(), "注销成功", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.sync) {
            sync();
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
        String[] items = {"选择本地照片", "拍照"};
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        try {
                            setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                        } catch (IOException e) {
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
     */
    protected void setImageToView(Intent data) throws IOException {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bmp = extras.getParcelable("data");
            photo = (ImageView) findViewById(R.id.userphoto);
            photo.setImageBitmap(bmp);
            final File userPhoto = changeToFile(bmp, "userphoto.png");
//            new Thread() {
//                public void run() {
//                    try {
//                        UploadUtils.post(getApplicationContext(), userUtils.getCurrentUsername(), userPhoto);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();

        }
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public File changeToFile(Bitmap bm, String fileName) {
        File userphoto = new File(getApplicationContext().getFilesDir().getAbsolutePath() + fileName);
        if (userphoto.exists()) {
            userphoto.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(userphoto));
            bm.compress(Bitmap.CompressFormat.PNG, 90, bos);
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

    public void toChart(View view) {
        Intent intent = new Intent(this, Chart.class);
        startActivity(intent);
    }

    public void toDeleteBook(View view) {
        showDeleteDialog();
    }

    public void sync() {
        AlertDialog load = new AlertDialog.Builder(this).create();
        load.setMessage("数据处理中，请勿进行其他操作");
        load.show();
        new Thread() {
            public void run() {
                syncNewAddedBookByPost();
                //syncModifyBookByPost();
                //syncDeleteBookByPost();
                syncNewAddedAccountByPost();
                //syncModifyAccountByPost();
                //syncDeleteAccountByPost();
                syncNewAddedRecordByPost();
                //syncModifyRecordByPost();
                //syncDeleteRecordByPost();
                getNewAddedBookByPost();
                //getModifyBookByPost();
                //getDeleteBookByPost();
                getNewAddedAccountByPost();
                //getModifyAccountByPost();
                //getDeleteAccountByPost();
                getNewAddedRecordByPost();
                //getModifyRecordByPost();
                //getDeleteRecordByPost();
                dirtyUtils.deleteAllDirty(userUtils.getCurrentUsername());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();

    }

    public void syncNewAddedBookByPost() {

        try {
            String spec = "http://cirnoteam.varkarix.com/sync/add/book";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncBook> newAddedBooks = dirtyUtils.getAllNewAddedBooks(userUtils.getCurrentUsername());
            BookReqEntity bookReqEntity = new BookReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("add").setBooks(newAddedBooks);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(bookReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() == 200) {
                    RemoteIdRespEntity entity = mapper.readValue(mapper.readTree(result).get("entity").toString(), RemoteIdRespEntity.class);
                    Map<Long, Long> map = entity.getIdMap();
                    for (Map.Entry<Long, Long> entry : map.entrySet()) {
                        Long id = entry.getKey();
                        Long remoteId = entry.getValue();
                        bookUtils.setBookRemoteId(id, remoteId);
                    }
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            Log.e("book", e.getMessage());
            e.printStackTrace();
        }
    }

    public void syncModifyBookByPost() {
        try {

            String spec = "http://cirnoteam.varkarix.com/sync/modify/book";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncBook> modifyBooks = dirtyUtils.getAllModifiedBooks(userUtils.getCurrentUsername());
            BookReqEntity bookReqEntity = new BookReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("modify").setBooks(modifyBooks);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(bookReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncDeleteBookByPost() {
        try {

            String spec = "http://cirnoteam.varkarix.com/sync/delete/book";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncBook> deleteBooks = dirtyUtils.getAllDeletedBooks(userUtils.getCurrentUsername());
            BookReqEntity bookReqEntity = new BookReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("delete").setBooks(deleteBooks);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(bookReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncNewAddedAccountByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/sync/add/account";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncAccount> newAddedAccounts = dirtyUtils.getAllNewAddedAccounts(userUtils.getCurrentUsername());
            AccountReqEntity accountReqEntity = new AccountReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("add").setAccounts(newAddedAccounts);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(accountReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() == 200) {
                    RemoteIdRespEntity entity = mapper.readValue(mapper.readTree(result).get("entity").toString(), RemoteIdRespEntity.class);
                    Map<Long, Long> map = entity.getIdMap();
                    for (Map.Entry<Long, Long> entry : map.entrySet()) {
                        Long id = entry.getKey();
                        Long remoteId = entry.getValue();
                        accountUtils.setAccountRemoteId(id, remoteId);
                    }
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncModifyAccountByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/sync/modify/account";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncAccount> modifyAccounts = dirtyUtils.getAllModifiedAccounts(userUtils.getCurrentUsername());
            AccountReqEntity accountReqEntity = new AccountReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("modify").setAccounts(modifyAccounts);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(accountReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncDeleteAccountByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/sync/delete/account";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncAccount> deleteAccounts = dirtyUtils.getAllDeletedAccounts(userUtils.getCurrentUsername());
            AccountReqEntity accountReqEntity = new AccountReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("delete").setAccounts(deleteAccounts);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(accountReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncNewAddedRecordByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/sync/add/record";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncRecord> newAddedRecords = dirtyUtils.getAllNewAddedRecords(userUtils.getCurrentUsername());
            RecordReqEntity accountReqEntity = new RecordReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("add").setRecords(newAddedRecords);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(accountReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() == 200) {
                    RemoteIdRespEntity entity = mapper.readValue(mapper.readTree(result).get("entity").toString(), RemoteIdRespEntity.class);
                    Map<Long, Long> map = entity.getIdMap();
                    for (Map.Entry<Long, Long> entry : map.entrySet()) {
                        Long id = entry.getKey();
                        Long remoteId = entry.getValue();
                        recordUtils.setRecordRemoteId(id, remoteId);
                    }
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncModifyRecordByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/sync/modify/record";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncRecord> modifyRecords = dirtyUtils.getAllModifiedRecords(userUtils.getCurrentUsername());
            RecordReqEntity accountReqEntity = new RecordReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("modify").setRecords(modifyRecords);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(accountReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncDeleteRecordByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/sync/delete/record";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            List<SyncRecord> deleteRecords = dirtyUtils.getAllDeletedRecords(userUtils.getCurrentUsername());
            RecordReqEntity accountReqEntity = new RecordReqEntity().setToken(userUtils.getCurrentToken()).setUuid(userUtils.getCurrentDeviceUuid()).setType("delete").setRecords(deleteRecords);
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(accountReqEntity);

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNewAddedBookByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/add/book";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=add";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() == 200) {
                    BookReqEntity entity = mapper.readValue(mapper.readTree(result).get("entity").toString(), BookReqEntity.class);
                    List<SyncBook> list_syncbooks = entity.getBooks();
                    for (SyncBook list_syncbook : list_syncbooks) {
                        bookUtils.addSyncBook(list_syncbook);
                    }
                }
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getModifyBookByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/modify/book";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=modify";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDeleteBookByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/delete/book";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=delete";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNewAddedAccountByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/add/account";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=add";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() == 200) {
                    AccountReqEntity entity = mapper.readValue(mapper.readTree(result).get("entity").toString(), AccountReqEntity.class);
                    List<SyncAccount> list_syncaccounts = entity.getAccounts();
                    for (SyncAccount list_syncaccount : list_syncaccounts) {
                        accountUtils.addSyncAccount(list_syncaccount);
                    }
                }
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getModifyAccountByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/modify/account";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=modify";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDeleteAccountByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/delete/account";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=delete";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNewAddedRecordByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/add/record";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=add";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() == 200) {
                    RecordReqEntity entity = mapper.readValue(mapper.readTree(result).get("entity").toString(), RecordReqEntity.class);
                    List<SyncRecord> list_syncrecords = entity.getRecords();
                    for (SyncRecord list_syncrecord : list_syncrecords) {
                        recordUtils.addSyncRecord(list_syncrecord);
                    }
                }
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getModifyRecordByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/modify/record";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=modify";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDeleteRecordByPost() {
        try {
            String spec = "http://cirnoteam.varkarix.com/get/delete/record";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            ObjectMapper mapper = new ObjectMapper();
            String data = "token=" + URLEncoder.encode(userUtils.getCurrentToken(), "UTF-8")
                    + "&uuid=" + URLEncoder.encode(userUtils.getCurrentDeviceUuid(), "UTF-8")
                    + "&type=delete";

            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                final Response response = mapper.readValue(result, Response.class);
                if (response.getCode() != 200) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
