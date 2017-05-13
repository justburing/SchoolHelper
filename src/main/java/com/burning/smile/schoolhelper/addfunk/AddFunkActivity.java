package com.burning.smile.schoolhelper.addfunk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.bean.UploadFile;
import com.burning.smile.androidtools.tools.AndroidCameraUtil;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.photoshower.PhotoViewActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.UpProgressListener;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.ProcessImageView;
import com.burning.smile.schoolhelper.widget.ResizeGridView;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/4/22.
 */
public class AddFunkActivity extends BaseActivity {
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.toolbarLL)
    LinearLayout toolbarLL;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.takePhoto)
    ImageView takePhoto;
    @BindView(R.id.choosePicture)
    ImageView choosePicture;
    @BindView(R.id.pictureGv)
    ResizeGridView pictureGv;
    @BindView(R.id.price)
    EditText price;
    @BindView(R.id.priceUnit)
    TextView priceUnit;
    @BindView(R.id.chooseMoneyUnit)
    LinearLayout chooseMoneyUnit;
    @BindView(R.id.category)
    TextView category;
    @BindView(R.id.chooseCategory)
    LinearLayout chooseCategory;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.chooseAddress)
    LinearLayout chooseAddress;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.add)
    Button add;

    private static final int UPLOADING = 0;
    private static final int UPLOADED = 1;
    private static final int UPLOADFAILED = 2;
    private PictrueAdapter pictrueAdapter;
    private List<String> picturePaths;
    private String filePath;
    private AndroidCameraUtil mCameraUtil;
    private File takePhotoFile;
    private List<UploadFile> uploadFiles;
    private UserInfoBean userInfoBean;
    private List<String> imgs;
    private String uri = "";
    private int dialogSelectedPositon = -1;
    private int dialogSelectedPositon1 = 1;
    private UpProgressListener[] mListener = new UpProgressListener[100];

    @Override
    protected void init() {
        ButterKnife.bind(this);
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        mCameraUtil = AndroidCameraUtil.getInstance(this);
        picturePaths = new ArrayList<>();
        uploadFiles = new ArrayList<>();
        imgs = new ArrayList<>();
        pictrueAdapter = new PictrueAdapter(this);
        pictureGv.setAdapter(pictrueAdapter);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.funk_add_act;
    }


    @OnClick({R.id.backLL, R.id.takePhoto, R.id.choosePicture, R.id.chooseMoneyUnit, R.id.chooseCategory, R.id.chooseAddress, R.id.add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.takePhoto:
                if (picturePaths.size() < 8) {
                    takePhotoFile = new File(AndroidCameraUtil.dir, "schoolhelper_" + System.currentTimeMillis() + ".jpg");
                    filePath = takePhotoFile.getPath();
                    mCameraUtil.takePhoto(takePhotoFile);
                } else {
                    toast("照片不能超过8张");
                }
                break;
            case R.id.choosePicture:
                if (picturePaths.size() < 8) {
                    mCameraUtil.choosePhoto();
                } else {
                    toast("照片不能超过8张");
                }
                break;
            case R.id.chooseMoneyUnit:
                break;
            case R.id.chooseCategory:
                View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_popmenu, null);
                TextView dialog_canel = (TextView) rootView.findViewById(R.id.dialog_cancel);
                TextView dialog_confirm = (TextView) rootView.findViewById(R.id.dialog_confirm);
                final ListView dialog_lv = (ListView) rootView.findViewById(R.id.dialog_lv);
                final DialogAdapter adapter = new DialogAdapter(this);
                final List<String> data = new ArrayList<>();
                //1玩偶；2图书
                data.add("玩偶");
                data.add("图书");
                data.add("其他");
                adapter.setData(data);
                dialog_lv.setAdapter(adapter);
                adapter.setSelectedPosition(dialogSelectedPositon);
                final AlertDialog dialog = new AlertDialog.Builder(this).setView(rootView).create();
                // dialog.setCancelable(true);
                dialog.getWindow().setLayout(getScreenWidth(),
                        getScreenHeight() / 3);
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setWindowAnimations(R.style.BottomToTop);
                dialog.show();
                dialog_canel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category.setText(data.get(adapter.getSelectedPosition() == -1 ? 0 : adapter.getSelectedPosition()));
                        dialog.dismiss();
                    }
                });
                dialog_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialogSelectedPositon = position;
                        adapter.setSelectedPosition(position);
                    }
                });
                break;
            case R.id.chooseAddress:
                View rootView1 = LayoutInflater.from(this).inflate(R.layout.dialog_popmenu, null);
                TextView dialog_canel_1 = (TextView) rootView1.findViewById(R.id.dialog_cancel);
                TextView dialog_confirm_1 = (TextView) rootView1.findViewById(R.id.dialog_confirm);
                final ListView dialog_lv_1 = (ListView) rootView1.findViewById(R.id.dialog_lv);
                final DialogAdapter adapter1 = new DialogAdapter(this);
                final List<String> data1 = new ArrayList<>();
                //1玩偶；2图书
                data1.add("生活一区");
                data1.add("生活二区");
                data1.add("生活三区");
                adapter1.setData(data1);
                dialog_lv_1.setAdapter(adapter1);
                adapter1.setSelectedPosition(dialogSelectedPositon1);
                final AlertDialog dialog1 = new AlertDialog.Builder(this).setView(rootView1).create();
                // dialog.setCancelable(true);
                dialog1.getWindow().setLayout(getScreenWidth(),
                        getScreenHeight() / 3);
                dialog1.getWindow().setGravity(Gravity.CENTER);
                dialog1.getWindow().setWindowAnimations(R.style.BottomToTop);
                dialog1.show();
                dialog_canel_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                dialog_confirm_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        address.setText(data1.get(adapter1.getSelectedPosition() == -1 ? 0 : adapter1.getSelectedPosition()));
                        dialog1.dismiss();
                    }
                });
                dialog_lv_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialogSelectedPositon1 = position;
                        adapter1.setSelectedPosition(position);
                    }
                });
                break;
            case R.id.add:
                if (imgs == null || imgs.size() == 0) {
                    toast("至少一张图片,照片更能展示物品的价值哦");
                    return;
                } else {
                    if (price.getText().toString().equals("")) {
                        toast("发布前记得定个价格哦");
                        return;
                    }
                    if (title.getText().toString().equals("") && content.getText().toString().equals("")) {
                        toast("一个精简的标题或者内容是很重要的");
                        return;
                    }
                    if (category.getText().toString().equals("选择分类")) {
                        toast("别忘了选择物品分类哦");
                        return;
                    }
                    boolean isUploadFinished = true;
                    List<Map<String, Object>> picData = pictrueAdapter.getData();
                    for (Map map : picData) {
                        if ((Integer) map.get("status") != UPLOADED) {
                            isUploadFinished = false;
                        }
                    }
                    if (!isUploadFinished) {
                        toast("尚有图片未上传完毕或者上传失败,请长按进行删除或者点击进行重传");
                        return;
                    } else {
                        addFunk();
                    }
                }
                break;
        }
    }

    public void upload(String path, final int postion) {
        OkGo.post(AppConfig.BASE_URL + "file/upload")//
                .tag(postion)//
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .headers("X-Auth-Token", userInfoBean.getToken())        // 这里可以上传参数
                .params("gruop", "goods")
                .params("file", new File(path))   // 可以添加文件上传
                //  .params("file2", new File("filepath2")) 	// 支持多文件同时添加上传
                // .addFileParams("key", List<File> files)	// 这里支持一个key传多个文件
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OkGo.getInstance().cancelTag(postion);
                        //上传成功
                        toast("文件" + (postion + 1) + "上传成功");
                        Log.e("callbackString", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        imgs.add(jsonObject.getString("uri"));
                        pictrueAdapter.getData().get(postion).put("status", UPLOADED);
                        pictrueAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        pictrueAdapter.getData().get(postion).put("status", UPLOADFAILED);
                        pictrueAdapter.notifyDataSetChanged();
                        if (e instanceof MalformedJsonException) {
                            toast("上传图片或文件失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            toast("文件" + (postion + 1) + "上传失败");
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        Log.e("progress", currentSize + ">>>" + totalSize + ">>>" + progress + ">>>" + networkSpeed);
                        mListener[postion].update((int) (progress * 100));
                    }
                });
    }

    public void uploadFile(final UploadFile uploadFile) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//表单类型
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), uploadFile.getmFile());
        builder.addFormDataPart(uploadFile.getmType(), uploadFile.getmName(), imageBody);//第一个参数是后台接收图片流的参数名
        builder.addFormDataPart("group", "goods");
        RequestBody body = builder.build();
        RetrofitUtil.getRetrofitApiInstance().uploadFile(userInfoBean.getToken(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof MalformedJsonException) {
                            toast("上传图片失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            toast(e.toString());
                        }
                        for (Map map : pictrueAdapter.getData()) {
                            if (map.get("url").toString().equals(uploadFile.getmName())) {
                                map.put("status", UPLOADFAILED);
                                pictrueAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        imgs.add(jsonObject.getString("uri"));
                        Log.e("imgs.size()", imgs.size() + "");
                        for (Map map : pictrueAdapter.getData()) {
                            if (map.get("url").toString().equals(uploadFile.getmName())) {
                                map.put("status", UPLOADED);
                                pictrueAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
        ;

    }

    public void addFunk() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        Map map = new HashMap<>();
        map.put("title", title.getText().toString());
        map.put("body", content.getText().toString());
        map.put("category_id", category.getText().equals("玩偶") ? "1" : (category.getText().equals("图书") ? "2" : "3"));
        map.put("price", price.getText().toString());
        map.put("imgs", new Gson().toJson(imgs));
        RetrofitUtil.getRetrofitApiInstance().addFunk(userInfoBean.getToken(), map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("发布失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            toast(e.toString());
                        }
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        toast("发布成功,请刷新");
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AndroidCameraUtil.CAMERA_TAKEPHOTO:
                    picturePaths.add(filePath);
                    Map map = new HashMap();
                    map.put("url", filePath);
                    map.put("status", UPLOADING);
                    pictrueAdapter.addItem(map);
                    upload(filePath, picturePaths.lastIndexOf(filePath));
                    break;
                case AndroidCameraUtil.CAMERA_CHOOSEPHOTO:
                    filePath = getFilePathFromAlbum(data);
                    picturePaths.add(filePath);
                    Map map1 = new HashMap();
                    map1.put("url", filePath);
                    map1.put("status", UPLOADING);
                    pictrueAdapter.addItem(map1);
                    upload(filePath, picturePaths.lastIndexOf(filePath));
                    break;

            }
        }
    }


    public String getFilePathFromAlbum(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor query = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        assert query != null;
        query.moveToFirst();
        int columnIndex = query.getColumnIndex(filePathColumns[0]);
        String picturePath = query.getString(columnIndex);
        query.close();
        return picturePath;
    }


    class PictrueAdapter extends BaseAdapter {
        private Context mContext;
        private List<Map<String, Object>> data;

        public PictrueAdapter(Context context) {
            this.mContext = context;
            data = new ArrayList<>();
        }

        public void setData(List<Map<String, Object>> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public List<Map<String, Object>> getData() {
            return data;
        }

        public void addItem(Map<String, Object> imgUrl) {
            data.add(imgUrl);
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            data.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return data == null ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_forum_photo_gv, null);
                viewHolder.imageView = (ProcessImageView) convertView.findViewById(R.id.item_img);
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.deleteIcon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            mListener[position] = viewHolder.imageView;
            final Map<String, Object> map = data.get(position);
            final String imgUrl = map.get("url").toString();
            if (imgUrl.endsWith(".jpg") || imgUrl.endsWith(".png") || imgUrl.endsWith(".gif")) {
                if (imgUrl.contains("http")) {
                    Glide.with(mContext).load(imgUrl).into(viewHolder.imageView);
                } else {
                    Glide.with(mContext).load(new File(imgUrl)).error(R.mipmap.ic_launcher).crossFade().into(viewHolder.imageView);
                }
            } else {
                Glide.with(mContext).load(R.mipmap.ic_file).crossFade().into(viewHolder.imageView);
            }
            final int status = (int) map.get("status");
            switch (status) {
                case UPLOADING:
                    viewHolder.iv_delete.setVisibility(View.GONE);
                    break;
                case UPLOADED:
                    viewHolder.iv_delete.setVisibility(View.VISIBLE);
                    break;
                case UPLOADFAILED:
                    viewHolder.iv_delete.setVisibility(View.GONE);
                    break;
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    picturePaths.remove(position);
                    imgs.remove(position);
                    data.remove(position);
                    notifyDataSetChanged();
                }
            });
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status == UPLOADED) {
                        List<String> urls = new ArrayList<String>();
                        for (String s : picturePaths) {
                            if (s.endsWith(".jpg") || s.endsWith(".png") || s.endsWith(".gif")) {
                                urls.add(s);
                            }
                        }
                        Intent intent = new Intent(AddFunkActivity.this, PhotoViewActivity.class);
                        intent.putExtra("position", position);
                        intent.putStringArrayListExtra("pics", (ArrayList<String>) urls);
                        startActivity(intent);
                    } else if (status == UPLOADFAILED) {
                        OkGo.getInstance().cancelTag(position);
                        upload(map.get("url").toString(), position);
                    }
                }
            });
            notifyDataSetChanged();
            return convertView;
        }

        class ViewHolder {
            private ProcessImageView imageView;
            private ImageView iv_delete;
        }
    }

    class DialogAdapter extends BaseAdapter {
        private List<String> mData;
        private Context mContext;
        private int selectedPosition = -1;// 选中的位置

        public DialogAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<String> data) {
            mData = data;
            notifyDataSetChanged();
        }

        public List<String> getData() {
            return mData;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public String getItem(int position) {
            if (mData.get(position) != null) {
                return mData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new MyViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                convertView = mInflater.inflate(R.layout.item_dialog_lv, null);
                viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyViewHolder) convertView.getTag();
            }
            // set item values to the viewHolder:
            String s = mData.get(position);
            if (s != null) {
                viewHolder.tv.setText(s);
            }
            if (selectedPosition == position) {
                convertView.setBackgroundColor(Color.parseColor("#E7E7E7"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);

            }
            return convertView;
        }

        class MyViewHolder {
            private TextView tv;
        }
    }

}
