package com.mvcoder.common.log;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.mvcoder.common.BuildConfig;
import com.mvcoder.common.utils.ProcessUtil;
import com.tencent.mars.xlog.Xlog;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class Log {

    private static final String TAG = "mars.xlog.log";

    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR = 4;
    public static final int LEVEL_FATAL = 5;
    public static final int LEVEL_NONE = 6;
    public static final int LEVEL_TRACE = 7;

    // defaults to LEVEL_NONE
    private static int level = LEVEL_NONE;

    //最多保持多少个日志文件
    private static final int MAX_NUMBER_LOG = 5;

    public static Context toastSupportContext = null;

    private static String logFileDir = null;

    public static void initXLog(@NonNull Application application, String logPath){
        String label = application.getPackageManager().getApplicationLabel(application.getApplicationInfo()).toString();
        // this is necessary, or may cash for SIGBUS
        final String cachePath = application.getFilesDir() + "/xlog";
        //获取进程名, 因为 xlog 只支持1个进程1个日志文件，这里用进程区分，是为了支持多进程
        String processName = ProcessUtil.getProcessName(Process.myPid());
        if(processName != null){
            int index =  processName.lastIndexOf(".");
            if(index != -1 && processName.length() - 1 > index){
                processName =  processName.substring(index + 1);
            }
            //私有进程模式，可以直接拿后缀名
            if(processName.contains(":")){
                int index2 = processName.indexOf(":");
                processName = processName.substring(index2 + 1);
            }
        }
        //init xlog
        if (BuildConfig.DEBUG) {
            //第二个参数是日志库最低输入level，低于该level的日志全部不输出
            Xlog.open(true, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath + processName, label,  "");
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.open(true, Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath + processName, label, "");
            Xlog.setConsoleLogOpen(false);
        }
        logFileDir = logPath + processName;
        Log.setLogImp(new Xlog());
        Log.i(TAG, "process name : %s", processName);
        cleanLog();
    }

    public static void initXLog(@NonNull Application application) {
        String packageName = application.getPackageName();
        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/" + packageName + "/log/";
        File logDir = new File(logPath);
        if(!logDir.exists()){
            logDir.mkdirs();
        }
        initXLog(application, logPath);
    }

    private static void cleanLog(){
        File file = new File(logFileDir);
        if(file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            if(files != null && files.length > MAX_NUMBER_LOG){
                Log.i(TAG, "cache too many logs, now clean them until the numbers of log file lower than %d", MAX_NUMBER_LOG);
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                for(int i = 0; i < files.length - MAX_NUMBER_LOG; i++){
                    FileUtils.deleteFile(files[i]);
                }
            }
        }
    }

    private volatile static ILogReport logReport;

    public static void setLogUploader(ILogReport report){
        logReport = report;
    }

    private static com.tencent.mars.xlog.Log.LogImp debugLog = new com.tencent.mars.xlog.Log.LogImp() {
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void logV(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_VERBOSE) {
                android.util.Log.v(tag, log);
            }
        }

        @Override
        public void logI(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_INFO) {
                android.util.Log.i(tag, log);
            }
        }

        @Override
        public void logD(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_DEBUG) {
                android.util.Log.d(tag, log);
            }

        }

        @Override
        public void logW(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_WARNING) {
                android.util.Log.w(tag, log);
            }

        }

        @Override
        public void logE(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_ERROR) {
                android.util.Log.e(tag, log);
            }
        }

        @Override
        public void logF(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, final String log) {
            if (level > LEVEL_FATAL) {
                return;
            }
            android.util.Log.e(tag, log);

            if (toastSupportContext != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(toastSupportContext, log, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public int getLogLevel() {
            return level;
        }

        @Override
        public void appenderClose() {

        }

        @Override
        public void appenderFlush(boolean isSync) {
        }

    };

    private static volatile com.tencent.mars.xlog.Log.LogImp logImp = debugLog;

    public static void setLogImp(com.tencent.mars.xlog.Log.LogImp imp) {
        logImp = imp;
    }

    public static com.tencent.mars.xlog.Log.LogImp getImpl() {
        return logImp;
    }

    public static void appenderClose() {
        if(logReport != null){
            logReport.destroy();
        }
        if (logImp != null) {
            logImp.appenderClose();
        }
    }

    public static void appenderFlush(boolean isSync) {
        if (logImp != null) {
            logImp.appenderFlush(isSync);
        }
    }

    public static int getLogLevel() {
        if (logImp != null) {
            return logImp.getLogLevel();
        }
        return LEVEL_NONE;
    }

    public static void setLevel(final int level, final boolean jni) {
        Log.level = level;
        android.util.Log.w(TAG, "new log level: " + level);

        if (jni) {
            Xlog.setLogLevel(level);
            //android.util.Log.e(TAG, "no jni log level support");
        }
    }

    /**
     * use trace(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void trace(final String tag, final String msg) {
        trace(tag, msg, (Object[]) null);
    }

    /**
     * use f(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void f(final String tag, final String msg) {
        f(tag, msg, (Object[]) null);
    }

    /**
     * use e(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void e(final String tag, final String msg) {
        e(tag, msg, (Object[]) null);
    }

    /**
     * use w(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void w(final String tag, final String msg) {
        w(tag, msg, (Object[]) null);
    }

    /**
     * use i(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void i(final String tag, final String msg) {
        i(tag, msg, (Object[]) null);
    }

    /**
     * use d(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void d(final String tag, final String msg) {
        d(tag, msg, (Object[]) null);
    }

    /**
     * use v(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void v(final String tag, final String msg) {
        v(tag, msg, (Object[]) null);
    }

    public static void f(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            final String log = obj == null ? format : String.format(format, obj);
            logImp.logF(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void e(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logE(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void w(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logW(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void i(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logI(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void d(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logD(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void v(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logV(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void trace(String tag, final String format, final Object... obj){
        if(logImp != null){
            String log = obj == null?format:String.format(format, obj);
            if(log == null){
                log = "";
            }
            logImp.logI(tag, "","", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
        if(logReport != null){
            String log = obj == null?format:String.format(format, obj);
            if(log == null){
                log = "";
            }
            logReport.trace(LEVEL_TRACE, tag, log);
        }
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            log += "  " + android.util.Log.getStackTraceString(tr);
            logImp.logE(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static String getLogFileDir(){
        return logFileDir;
    }

    private static final String SYS_INFO;

    static {
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append("VERSION.RELEASE:[" + android.os.Build.VERSION.RELEASE);
            sb.append("] VERSION.CODENAME:[" + android.os.Build.VERSION.CODENAME);
            sb.append("] VERSION.INCREMENTAL:[" + android.os.Build.VERSION.INCREMENTAL);
            sb.append("] BOARD:[" + android.os.Build.BOARD);
            sb.append("] DEVICE:[" + android.os.Build.DEVICE);
            sb.append("] DISPLAY:[" + android.os.Build.DISPLAY);
            sb.append("] FINGERPRINT:[" + android.os.Build.FINGERPRINT);
            sb.append("] HOST:[" + android.os.Build.HOST);
            sb.append("] MANUFACTURER:[" + android.os.Build.MANUFACTURER);
            sb.append("] MODEL:[" + android.os.Build.MODEL);
            sb.append("] PRODUCT:[" + android.os.Build.PRODUCT);
            sb.append("] TAGS:[" + android.os.Build.TAGS);
            sb.append("] TYPE:[" + android.os.Build.TYPE);
            sb.append("] USER:[" + android.os.Build.USER + "]");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        SYS_INFO = sb.toString();
    }

    public static String getSysInfo() {
        return SYS_INFO;
    }

}
