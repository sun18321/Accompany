package com.play.accompany.net;

import android.widget.Toast;
import com.play.accompany.R;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.BaseResponse;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.utils.CipherUtil;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.NetUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.view.AccompanyApplication;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AccompanyRequest{
    private Disposable mDisposable;
    private NetListener mListener;
    private StringListener mStringListener;

    public void beginRequest(Observable<BaseResponse> observable, final Type type, NetListener listener) {
        mListener = listener;
        if (!NetUtils.isNetworkConnected(AccompanyApplication.getContext())) {
            Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
            if (mListener != null) {
                mListener.onComplete();
            }
            return;
        }

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BaseResponse>() {

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                LogUtils.d("request", "subscribe");
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                String recEncode = baseResponse.getRecEncode();
                try {
                    String desDecrypt = CipherUtil.desDecrypt(recEncode);
                    LogUtils.d("request", "all:" + desDecrypt);
                    BaseDecodeBean bean = GsonUtils.fromJson(desDecrypt, type);
                    if (mListener != null) {
                        if (bean != null && bean.getCode() == AppConstant.RESPONSE_SUCCESS) {
                            mListener.onSuccess(bean.getMsg());
                        } else {
                            if (bean != null) {
                                Toast.makeText(AccompanyApplication.getContext(), StringUtils.getErrorInfo(bean.getCode()),Toast.LENGTH_SHORT).show();
                                mListener.onFailed(bean.getCode());
                            } else {
                                mListener.onError();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mListener != null) {
                    mListener.onError();
                }
                LogUtils.d("request", "error");
                Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.service_failed), Toast.LENGTH_SHORT).show();
                onComplete();
            }

            @Override
            public void onComplete() {
                if (mListener != null) {
                    mListener.onComplete();
                }
                LogUtils.d("request", "complete");
            }
        });
    }

    public void onlyRequest(Observable<BaseResponse> observable) {
        observable.subscribeOn(Schedulers.io()).subscribe();
    }


    public void requestBackString(Observable<BaseResponse> observable, StringListener listener) {
        mStringListener = listener;
        if (!NetUtils.isNetworkConnected(AccompanyApplication.getContext())) {
            Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
            if (mStringListener != null) {
                mStringListener.onComplete();
            }
            return;
        }
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                String recEncode = baseResponse.getRecEncode();
                try {
                    String desDecrypt = CipherUtil.desDecrypt(recEncode);
                    LogUtils.d("request", "all:" + desDecrypt);
                    OnlyCodeBean bean = GsonUtils.fromJson(desDecrypt, OnlyCodeBean.class);
                    if (bean != null) {
                        int code = bean.getCode();
                        if (code == AppConstant.RESPONSE_SUCCESS) {
                            if (mStringListener != null) {
                                mStringListener.onSuccess(desDecrypt);
                            }
                        } else {
                            Toast.makeText(AccompanyApplication.getContext(), StringUtils.getErrorInfo(bean.getCode()),Toast.LENGTH_SHORT).show();
                            if (mStringListener != null) {
                                mStringListener.onFailed(code);
                            }
                        }
                    } else {
                        if (mStringListener != null) {
                            mStringListener.onError();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mStringListener != null) {
                    mStringListener.onError();
                }
                Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.service_failed), Toast.LENGTH_SHORT).show();
                onComplete();
            }

            @Override
            public void onComplete() {
                if (mStringListener != null) {
                    mStringListener.onComplete();
                }
            }
        });
    }

    public void destroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
