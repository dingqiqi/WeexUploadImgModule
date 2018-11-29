package com.lakala.appcomponent.uploadImgModule;

import android.text.TextUtils;
import android.util.Log;

import com.lakala.appcomponent.retrofitManager.RetrofitManager;
import com.lakala.appcomponent.retrofitManager.builder.PostFormFileBuilder;
import com.lakala.appcomponent.retrofitManager.callback.ResponseCallBack;
import com.lakala.appcomponent.retrofitManager.mode.HttpResponse;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;

/**
 * 获取联系人打电话
 * Created by dingqq on 2018/4/24.
 */

public class UploadImgModule extends WXModule implements IUploadImgModule {

    @JSMethod
    @Override
    public boolean uploadImg(String params, final JSCallback callback) {

        if (TextUtils.isEmpty(params)) {
            if (callback != null) {
                callback.invoke("数据为空!");
            }
            return false;
        }

        Map<String, String> mapParams = new HashMap<>();
        Map<String, String> headParams = new HashMap<>();

        JSONObject jsonObject;
        String path = null;
        String uploadUrl = null;
        String type = null;
        try {
            jsonObject = new JSONObject(params);

            //参数
            if (jsonObject.has("params")) {
                JSONObject paramsJson = jsonObject.getJSONObject("params");

                for (Iterator<String> it = paramsJson.keys(); it.hasNext(); ) {
                    String key = it.next();
                    Object value = paramsJson.opt(key);
                    mapParams.put(key, value.toString());
                }
            }

            if (jsonObject.has("heads")) {
                JSONObject headsJson = jsonObject.getJSONObject("heads");

                for (Iterator<String> it = headsJson.keys(); it.hasNext(); ) {
                    String key = it.next();

                    //获取传输方式
                    if ("Content-Type".equals(key)) {
                        type = headsJson.optString(key);
                    } else {
                        Object value = headsJson.opt(key);
                        headParams.put(key, value.toString());
                    }
                }
            }

            path = jsonObject.getString("imagePath");
            uploadUrl = jsonObject.getString("uploadUrl");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(uploadUrl)) {
            if (callback != null) {
                callback.invoke("参数错误!");
            }
            return false;
        }

        //去除文件路径reslocal://头
        if (path.contains("://")) {
            String[] str = path.split("://");
            if (str.length < 2) {
                if (callback != null) {
                    callback.invoke("文件路径错误");
                }
                return false;
            }
            path = str[1];
        }

        PostFormFileBuilder builder = RetrofitManager.postFile()
                .url(uploadUrl)
                .addFile(new File(path))
                .params(mapParams)
                .heads(headParams);

        if (!TextUtils.isEmpty(type)) {
            builder.mediaType(MediaType.parse(type));
        }

        builder.build().execute(new ResponseCallBack() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d("aaa", "uploadImg:" + response);
                if (callback != null) {

                    Map<String, Object> map = new HashMap<>();
                    if (response != null) {
                        map.put("code", response.getCode());
                        map.put("body", response.getBody());
                        map.put("message", response.getMessage());
                    }

                    callback.invoke(map);
                }
            }

            @Override
            public void onFail(int code, String msg, Throwable e) {
                e.printStackTrace();
                Log.d("aaa", "uploadImg: code:" + code + "Throwable:" + e + " msg:" + msg);
                if (callback != null) {
                    callback.invoke(null);
                }
            }

            @Override
            public void progress(float progress, long total) {
                Log.d("aaa", "uploadImg progress:" + progress + " total:" + total);
            }
        });

        return true;
    }

}
