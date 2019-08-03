package com.play.accompany.design;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.EditPriceBean;
import com.play.accompany.bean.GameProperty;
import com.play.accompany.bean.MasterBean;
import com.play.accompany.bean.MasterCheckBean;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.RequestBody;

public class MasterLayout extends FrameLayout {

    private TextView mTvSkill;
    private TextView mTvPrice;
    private LinearLayout mLinEdit;
    private TextView mTvCheck;
    private TextView mTvAdd;
    private LinearLayout mLinDetail;
    private MasterListener mListener;
    private Context mContext;

    public MasterLayout(@NonNull Context context) {
        this(context, null);
    }

    public MasterLayout( @NonNull Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MasterLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.layout_master, this);
        mTvSkill = view.findViewById(R.id.tv_skill);
        mTvPrice = view.findViewById(R.id.tv_price);
        mLinEdit = view.findViewById(R.id.lin_edit);
        mTvCheck = view.findViewById(R.id.tv_check);
        mTvAdd = view.findViewById(R.id.tv_add);
        mLinDetail = view.findViewById(R.id.lin_detail);
    }

    public void setCommon(final MasterCheckBean bean) {
        mTvSkill.setText(bean.getName());
        mTvPrice.setText(bean.getPrice() + AccompanyApplication.getContext().getResources().getString(R.string.money) + "/" + bean.getUnit());
        mLinEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceDialog(bean);
            }
        });
    }

    public void setLast(String name, MasterListener listener) {
        mListener = listener;
        mTvSkill.setText(name);
        mLinEdit.setVisibility(GONE);
        mTvCheck.setVisibility(VISIBLE);
        mTvCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChecking();
                }
            }
        });
    }

    public void setAddLayout(MasterListener listener) {
        mListener = listener;
        mLinDetail.setVisibility(INVISIBLE);
        mTvAdd.setVisibility(VISIBLE);
        mTvAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAddNew();
                }
            }
        });
    }

    public int getPrice() {
        int price = -1;
        if (mTvPrice != null) {
            CharSequence text = mTvPrice.getText();
            if (text != null) {
                String s = text.toString();
                price = Integer.parseInt(s);
            }
        }
        return price;
    }

    private void editPrice(final String game, final int oldPrice, final int price, int gamType, final String unit) {
        GameProperty property = new GameProperty();
        property.setType(gamType);
        property.setPrice(price);
        EditPriceBean bean = new EditPriceBean();
        bean.setGameTypeDao(property);
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        RequestBody body = EncodeUtils.encodeInBody(GsonUtils.toJson(bean));
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().editMasterPrice(body), new TypeToken<BaseDecodeBean<OnlyCodeBean>>() {
        }.getType(), new NetListener<String>() {
            @Override
            public void onSuccess(String s) {
                mTvPrice.setText(price + AccompanyApplication.getContext().getResources().getString(R.string.money) + "/" + unit);
                ToastUtils.showCommonToast(getResources().getString(R.string.edit_success));

                EventUtils.getInstance().upEditPrice(game, oldPrice, price);
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void showPriceDialog(final MasterCheckBean bean) {
        final int typeId = bean.getTypeId();
        List<Integer> priceList = bean.getInterval();
        if (priceList.isEmpty()) {
            ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
            return;
        }
        int price = bean.getPrice();
        Iterator<Integer> iterator = priceList.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next == price) {
                iterator.remove();
            }
        }

        final PriceDialog priceDialog = new PriceDialog(mContext, priceList);
        priceDialog.setPositiveButton(AccompanyApplication.getContext().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int price = priceDialog.getSelect();
                editPrice(bean.getName(),bean.getPrice(), price, typeId, bean.getUnit());
                dialog.dismiss();
            }
        });
        priceDialog.setNegativeButton(AccompanyApplication.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        priceDialog.create().show();
    }

    public interface MasterListener {
        void onChecking();

        void onAddNew();
    }
}
