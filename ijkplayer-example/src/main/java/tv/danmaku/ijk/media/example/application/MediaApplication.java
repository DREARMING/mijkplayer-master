package tv.danmaku.ijk.media.example.application;

import android.app.Application;
import android.graphics.Color;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import com.mvcoder.common.log.Log;

public class MediaApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        ToastUtils.setMsgColor(Color.BLACK);
        Log.initXLog(this);
    }
}
