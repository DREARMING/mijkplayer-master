package com.mvcoder.common.log;

public interface ILogReport {

    void trace(int level, String tag, String message);

    void destroy();

}
