package com.landleaf.normal.plc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.landleaf.normal.interfaces.PlcHandler;
import com.landleaf.normal.utils.ThreadPoolManager;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.landleaf.normal.plc.PlcOperType.OPER_READ;
import static com.landleaf.normal.plc.PlcOperType.OPER_WRITE;

/**
 * PLC读写任务执行器
 *
 * @author chenyifei
 */
public class PlcExecutor {

    private static final String TAG = PlcExecutor.class.getSimpleName();

    private static PlcExecutor ourInstance = new PlcExecutor();

    public static PlcExecutor getInstance() {
        return ourInstance;
    }

    private PlcExecutor() {
    }

    /**
     * 耗时方法，采用同步阻塞，读取or写入plc
     *
     * @param offset      plcid
     * @param val         写入
     * @param ip          ip
     * @param plcUnit     单位
     * @param operateType 操作类型
     * @param plcHandler  回调
     * @return 操作结果
     * @throws InterruptedException InterruptedException
     * @throws ExecutionException   ExecutionException
     * @throws TimeoutException     TimeoutException
     */
    public synchronized PlcReturnBean plcOperateMethod(int offset, float val, String ip, @UnitType String plcUnit, String operateType, @Nullable PlcHandler plcHandler) throws InterruptedException, ExecutionException, TimeoutException {
        PlcConnection plcWrite = PlcConnection.newBuilder().ip(ip).offset(offset).val(val).operType(operateType).unit(plcUnit).build();
        Future<?> returnFuture = ThreadPoolManager.getInstance().submitSingleJob(plcWrite, TAG, THREAD_PRIORITY_BACKGROUND, null);
        PlcReturnBean plcReturnBean = (PlcReturnBean) returnFuture.get(3, TimeUnit.SECONDS);
        plcReturnBean.setRes(plcReturnBean.getRes());
        if (plcHandler != null) {
            if (plcReturnBean.getErrorCode() == 0) {
                switch (operateType) {
                    case OPER_WRITE:
                        plcHandler.writeRes(plcReturnBean.getOffset(), plcReturnBean.getRes());
                        break;
                    case OPER_READ:
                        plcHandler.readRes(plcReturnBean.getOffset(), plcReturnBean.getRes());
                        break;
                }
            } else {
                Log.e(TAG, "Plc error:" + S7Client.ErrorText(plcReturnBean.getErrorCode()));
                plcHandler.operRes(false, S7Client.ErrorText(plcReturnBean.getErrorCode()), plcReturnBean.getOffset());
            }
        }
        return plcReturnBean;
    }
}