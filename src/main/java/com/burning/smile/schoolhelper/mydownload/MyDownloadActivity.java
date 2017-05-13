package com.burning.smile.schoolhelper.mydownload;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.schoolhelper.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smile on 2017/5/12.
 */
public class MyDownloadActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.refresh)
    Button refresh;

    private List<File> files;
    private ListViewAdapter mAdapter;
    private String path = Environment.getExternalStorageDirectory() + "/download/schoolhelper/";
    private final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    @Override
    protected void init() {
        ButterKnife.bind(this);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter = new ListViewAdapter(this);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(mAdapter.getData().get(position));
            }
        });
        if (getFileList(path).size() != 0) {
            mAdapter.setData(getFileList(path));
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFileList(path).size() != 0) {
                    mAdapter.setData(getFileList(path));
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_download_act;
    }


    public List<File> getFileList(String fileAbsolutePath) {
        List<File> files = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        if (!file.exists()) {
            toast("文件不存在");
        } else {
            File[] subFile = file.listFiles();
            files = Arrays.asList(subFile);
        }
        return files;
    }

    private String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
      /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {
        if (file == null) {
            return;
        }
        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        //跳转

        try {
            startActivity(intent);
        } /*catch(E) {

    } */ catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showDialog(final File file) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_file_dialog, null);
        TextView viewIt = (TextView) dialogView.findViewById(R.id.viewIt);
        TextView deleteIt = (TextView) dialogView.findViewById(R.id.deleteIt);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(this, R.style.dialog_transparent);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.BottomToTop);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        deleteIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        viewIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(file);
                dialog.dismiss();
            }
        });
    }

    class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private List<File> files;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<File> files) {
            this.files = files;
        }

        public List<File> getData() {
            return files;
        }

        @Override
        public int getCount() {
            return files == null ? 0 : files.size();
        }

        @Override
        public File getItem(int position) {
            return files == null ? null : files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download_lv, null);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
                viewHolder.fileName = (TextView) convertView.findViewById(R.id.item_fileName);
                viewHolder.fileTime = (TextView) convertView.findViewById(R.id.item_fileTime);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            File file = files.get(position);
            String type = file.getPath().substring(file.getPath().lastIndexOf(".") + 1, file.getPath().length());
            //String type = getMIMEType(file);
            Log.e("type", type);
            if (type.contains("mp3")) {
                viewHolder.icon.setImageResource(R.mipmap.ic_mp3);
            } else if (type.contains("mp4")) {
                viewHolder.icon.setImageResource(R.mipmap.ic_video);
            } else if (type.contains("doc") || type.contains("xls") || type.contains("ppt")) {
                viewHolder.icon.setImageResource(R.mipmap.ic_doc);
            } else if (type.contains("jpg") || type.contains("png") || type.contains("gif")) {
                viewHolder.icon.setImageResource(R.mipmap.ic_img);
            } else {
                viewHolder.icon.setImageResource(R.mipmap.ic_file);
            }
            viewHolder.fileName.setText(file.getName());
            Date date = new Date(file.lastModified());//文件最后修改时间
            viewHolder.fileTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
            return convertView;
        }

        private class ViewHolder {
            private ImageView icon;
            private TextView fileName;
            private TextView fileTime;
        }
    }
}
