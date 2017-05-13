package com.burning.smile.schoolhelper.addexpress;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.util.LoadingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by smile on 2017/3/14.
 */
public class AddExpressActivity extends BaseActivity implements AddExpressContract.View {
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.bigExpress)
    RadioButton bigExpress;
    @BindView(R.id.middleExpress)
    RadioButton middleExpress;
    @BindView(R.id.smallExpress)
    RadioButton smallExpress;
    @BindView(R.id.expressType)
    RadioGroup expressType;
    @BindView(R.id.partOne)
    RadioButton partOne;
    @BindView(R.id.partTwo)
    RadioButton partTwo;
    @BindView(R.id.partNewsStand)
    RadioButton partNewsStand;
    @BindView(R.id.partMarket)
    RadioButton partMarket;
    @BindView(R.id.expressAddress)
    RadioGroup expressAddress;
    @BindView(R.id.expressCoin)
    EditText expressCoin;
    @BindView(R.id.isUrgent)
    CheckBox isUrgent;
    @BindView(R.id.expressTitle)
    EditText expressTitle;
    @BindView(R.id.expressCotnent)
    EditText expressCotnent;


    private AddExpressContract.Presenter mPresenter;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        new AddExpressPresenter(this).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.express_add_act;
    }


    @OnClick({R.id.backLL, R.id.add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.add:
                //包裹规格（1:小包裹，2：中包裹，3：大包裹）
                String type;
                if (expressType.getCheckedRadioButtonId() == R.id.bigExpress) {
                    type = "3";
                } else if (expressType.getCheckedRadioButtonId() == R.id.middleExpress) {
                    type = "2";
                } else {
                    type = "1";
                }
                String offer = expressCoin.getText().toString();
                String title = expressTitle.getText().toString();
                String detail = expressCotnent.getText().toString();
                //是否加急0否1是
                String is_urgent = isUrgent.isChecked() ? "2" : "1";
                mPresenter.postExpress(title, detail, offer, type, is_urgent);
                break;
        }
    }

    @Override
    public void showLoadingView() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
    }

    @Override
    public void dimissLoadingView() {
        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
    }

    @Override
    public void showPostSuccess(String msg) {
        toast(msg);
        finish();
    }

    @Override
    public void showPostFailure(String msg) {
        toast(msg);
    }

    @Override
    public void showExpressTypeError(String msg) {
        toast(msg);
    }

    @Override
    public void showExpressAddressError(String msg) {
        toast(msg);
    }

    @Override
    public void showExpressTitleError(String msg) {
        toast(msg);
    }

    @Override
    public void showExpressDetailError(String msg) {
        toast(msg);
    }

    @Override
    public void showExpressOfferError(String msg) {
        toast(msg);
    }

    @Override
    public void setPresenter(AddExpressContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
