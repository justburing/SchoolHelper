package com.burning.smile.schoolhelper.addforum;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.burning.smile.schoolhelper.util.chat.AddForumKeyBoard;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;
import com.burning.smile.schoolhelper.util.emoij.Constants;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.ProcessImageView;
import com.burning.smile.schoolhelper.widget.ResizeGridView;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.sj.emoji.EmojiBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by smile on 2017/4/23.
 */
public class AddForumActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener {

    @BindView(R.id.ek_bar)
    AddForumKeyBoard ekBar;


    private static final int UPLOADING = 0;
    private static final int UPLOADED = 1;
    private static final int UPLOADFAILED = 2;
    private static final int FILE_SELECT_CODE = 3;
    private PictrueAdapter pictrueAdapter;
    private List<String> picturePaths;
    private String filePath;
    private AndroidCameraUtil mCameraUtil;
    private File takePhotoFile;
    private List<UploadFile> uploadFiles;
    private UserInfoBean userInfoBean;
    private List<String> imgs;
    private UploadFile mUploadFile;
    private UpProgressListener[] mListener = new UpProgressListener[100];
    private ResizeGridView pictureGv;


    @Override
    protected void init() {
        ButterKnife.bind(this);
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        mCameraUtil = AndroidCameraUtil.getInstance(this);
        picturePaths = new ArrayList<>();
        uploadFiles = new ArrayList<>();
        imgs = new ArrayList<>();
        initEmoticonsKeyBoardBar();
    }

    private void initEmoticonsKeyBoardBar() {
        pictureGv = ekBar.getPictureGv();
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {

            }
        });
        ekBar.getBtnImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picturePaths.size() >= 6) {
                    toast("上传的图片和文件不能超过6个");
                } else {
                    mCameraUtil.choosePhoto();

                }
            }
        });
        ekBar.getBtn_puls().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picturePaths.size() >= 6) {
                    toast("上传的图片和文件不能超过6个");
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "请选择文件,不超过20M"), FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(AddForumActivity.this, "打开文件管理器失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        pictrueAdapter = new PictrueAdapter(this);
        pictureGv.setAdapter(pictrueAdapter);
        pictureGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddForumActivity.this, PhotoViewActivity.class);
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("pics", (ArrayList<String>) picturePaths);
                startActivity(intent);
            }
        });
        pictureGv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pictrueAdapter.deleteItem(position);
                picturePaths.remove(position);
                return false;
            }
        });
        ekBar.getBackLL().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ekBar.getAdd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ekBar.getTitle().getText().toString().equals("") || ekBar.getEtChat().getText().toString().equals("")) {
                    toast("标题和内容是很重要的哦");
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
                    toast("尚有图片或文件未上传完毕或者上传失败,请长按进行删除或者点击进行重传");
                    return;
                } else {
                    addForum();
                }
            }
        });
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                ChatUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        //OnSendImage(((EmoticonEntity)o).getIconUri());
                    }
                } else {
                    String content = null;
                    if (o instanceof EmojiBean) {
                        content = ((EmojiBean) o).emoji;
                    } else if (o instanceof EmoticonEntity) {
                        content = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.forum_add_act;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AndroidCameraUtil.CAMERA_CHOOSEPHOTO:
                    filePath = getFilePathFromAlbum(data);
                    picturePaths.add(filePath);
                    Map map1 = new HashMap();
                    map1.put("url", filePath);
                    map1.put("status", UPLOADING);
                    pictrueAdapter.addItem(map1);
                    upload(filePath, picturePaths.lastIndexOf(filePath));
                    break;
                case FILE_SELECT_CODE:
                    Uri uri = data.getData();
                    String path = getPath(this, uri);
                    picturePaths.add(path);
                    Map map = new HashMap();
                    map.put("url", path);
                    map.put("status", UPLOADING);
                    pictrueAdapter.addItem(map);
                    Log.e("filePath", path);
                    upload(path, picturePaths.lastIndexOf(path));
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

    public String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void upload(String path, final int postion) {
        OkGo.post(AppConfig.BASE_URL + "file/upload")//
                .tag(postion)//
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .headers("X-Auth-Token", userInfoBean.getToken())        // 这里可以上传参数
                .params("gruop", "thread")
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

    public void addForum() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        Map map = new HashMap<>();
        map.put("title", ekBar.getTitle().getText().toString());
        map.put("content", ekBar.getEtChat().getText().toString());
        map.put("group_id", "");
        map.put("imgs", new Gson().toJson(imgs));
        Log.e("error", new Gson().toJson(imgs));
        RetrofitUtil.getRetrofitApiInstance().addForum(userInfoBean.getToken(), map)
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
    public void OnFuncPop(int i) {

    }

    @Override
    public void OnFuncClose() {

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
                        Intent intent = new Intent(AddForumActivity.this, PhotoViewActivity.class);
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
}
