package com.burning.smile.schoolhelper.util.chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.widget.ResizeGridView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.PageSetEntity;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.EmoticonsFuncView;
import sj.keyboard.widget.EmoticonsIndicatorView;
import sj.keyboard.widget.EmoticonsToolBarView;
import sj.keyboard.widget.FuncLayout;

public class AddForumKeyBoard extends AutoHeightLayout implements EmoticonsFuncView.OnEmoticonsPageViewListener,
        EmoticonsToolBarView.OnToolBarItemClickListener, EmoticonsEditText.OnBackKeyClickListener, FuncLayout.OnFuncChangeListener {

    public final int APPS_HEIGHT = 256;

    public static final int FUNC_TYPE_PTT = 1;
    public static final int FUNC_TYPE_PTV = 2;
    public static final int FUNC_TYPE_IMAGE = 3;
    public static final int FUNC_TYPE_CAMERA = 4;
    public static final int FUNC_TYPE_HONGBAO = 5;
    public static final int FUNC_TYPE_EMOTICON = 6;
    public static final int FUNC_TYPE_PLUG = 7;

    protected LayoutInflater mInflater;

    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;
    protected ChatEmoticonsToolBarView mEmoticonsToolBarView;

    protected boolean mDispatchKeyEventPreImeLock = false;

    @BindView(R.id.view)
    LinearLayout view;
    @BindView(R.id.et_chat)
    EmoticonsEditText etChat;
    @BindView(R.id.btn_image)
    ImageView btn_image;
    @BindView(R.id.btn_plus)
    ImageView btn_plus;
    @BindView(R.id.btn_emoticon)
    ImageView btn_emoticon;
    @BindView(R.id.ly_kvml)
    FuncLayout lyKvml;
    @BindView(R.id.pictureGv)
    ResizeGridView pictureGv;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.add)
    TextView add;
    @BindView(R.id.toolbarLL)
    LinearLayout toolbarLL;

    public AddForumKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.view_keyboard_add_forum, this);
        ButterKnife.bind(this, view);
        initView();
        initFuncView();
    }

    protected void initView() {
        etChat.setOnBackKeyClickListener(this);
    }

    protected void initFuncView() {
        initEmoticonFuncView();
        initEditView();
    }

    protected void initEmoticonFuncView() {
        View keyboardView = inflateFunc();
        lyKvml.addFuncView(FUNC_TYPE_EMOTICON, keyboardView);
        mEmoticonsFuncView = ((EmoticonsFuncView) findViewById(com.keyboard.view.R.id.view_epv));
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) findViewById(com.keyboard.view.R.id.view_eiv));
        mEmoticonsToolBarView = ((ChatEmoticonsToolBarView) findViewById(com.keyboard.view.R.id.view_etv));
        mEmoticonsFuncView.setOnIndicatorListener(this);
        mEmoticonsToolBarView.setOnToolBarItemClickListener(this);
        lyKvml.setOnFuncChangeListener(this);
    }

    protected View inflateFunc() {
        return mInflater.inflate(R.layout.view_func_emoticon_qq, null);
    }

    protected void initEditView() {
        etChat.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!etChat.isFocused()) {
                    etChat.setFocusable(true);
                    etChat.setFocusableInTouchMode(true);
                    pictureGv.clearFocus();
                }
                lyKvml.hideAllFuncView();
                resetIcon();
                return false;
            }
        });
        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    add.setTextColor(ContextCompat.getColor(getContext(), R.color.color_text2));
                } else {
                    add.setTextColor(ContextCompat.getColor(getContext(), R.color.text_grey));
                }
            }
        });

    }

    public void setAdapter(PageSetAdapter pageSetAdapter) {
        if (pageSetAdapter != null) {
            ArrayList<PageSetEntity> pageSetEntities = pageSetAdapter.getPageSetEntityList();
            if (pageSetEntities != null) {
                for (PageSetEntity pageSetEntity : pageSetEntities) {
                    mEmoticonsToolBarView.addToolItemView(pageSetEntity);
                }
            }
        }
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    public void addFuncView(int type, View view) {
        lyKvml.addFuncView(type, view);
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
        lyKvml.hideAllFuncView();
        resetIcon();
    }

    public void resetIcon() {
        btn_emoticon.setImageResource(R.drawable.qq_skin_aio_panel_emotion);
    }

    protected void toggleFuncView(int key) {
        lyKvml.toggleFuncView(key, isSoftKeyboardPop(), etChat);
    }

    @Override
    public void onFuncChange(int key) {
        resetIcon();
        switch (key) {
            case FUNC_TYPE_EMOTICON:
                btn_emoticon.setImageResource(R.mipmap.qq_skin_aio_panel_emotion_press);
                break;
        }
    }

    protected void setFuncViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lyKvml.getLayoutParams();
        params.height = height;
        lyKvml.setLayoutParams(params);
        super.OnSoftPop(height);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        lyKvml.updateHeight(height);
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        lyKvml.setVisibility(true);
        onFuncChange(lyKvml.DEF_KEY);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        if (lyKvml.isOnlyShowSoftKeyboard()) {
            reset();
        } else {
            onFuncChange(lyKvml.getCurrentFuncKey());
        }
    }

    public void addOnFuncKeyBoardListener(FuncLayout.OnFuncKeyBoardListener l) {
        lyKvml.addOnKeyBoardListener(l);
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    @Override
    public void onToolBarItemClick(PageSetEntity pageSetEntity) {
        mEmoticonsFuncView.setCurrentPageSet(pageSetEntity);
    }

    @Override
    public void onBackKeyClick() {
        if (lyKvml.isShown()) {
            mDispatchKeyEventPreImeLock = true;
            reset();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false;
                    return true;
                }
                if (lyKvml.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    public EmoticonsEditText getEtChat() {
        return etChat;
    }

    public EmoticonsFuncView getEmoticonsFuncView() {
        return mEmoticonsFuncView;
    }

    public EmoticonsIndicatorView getEmoticonsIndicatorView() {
        return mEmoticonsIndicatorView;
    }

    public EmoticonsToolBarView getEmoticonsToolBarView() {
        return mEmoticonsToolBarView;
    }


    @OnClick(R.id.btn_emoticon)
    void btn_emoticon() {
        toggleFuncView(FUNC_TYPE_EMOTICON);
        setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
    }


    public ImageView getBtnImage() {
        return btn_image;
    }

    public ImageView getBtn_puls() {
        return btn_plus;
    }

    public LinearLayout getView() {
        return view;
    }

    public ResizeGridView getPictureGv() {
        return pictureGv;
    }

    public EditText getTitle() {
        return title;
    }

    public LinearLayout getBackLL() {
        return backLL;
    }

    public TextView getAdd() {
        return add;
    }
}
