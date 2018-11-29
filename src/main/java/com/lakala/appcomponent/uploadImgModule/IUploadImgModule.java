package com.lakala.appcomponent.uploadImgModule;

import com.taobao.weex.bridge.JSCallback;

/**
 * 拍照选择图片
 * Created by dingqq on 2018/4/17.
 */

public interface IUploadImgModule {

    //上传照片
    boolean uploadImg(String params, final JSCallback callback);

}
