package com.xsfdev;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public interface Constants {
    int MILLISECOND_SPEC = 1000;
    int SECOND_SPEC = 60;
    int MINUTE_SPEC = 60;
    int HOUR_SPEC = 24;

    long SECOND = MILLISECOND_SPEC;
    long MINUTE = SECOND * SECOND_SPEC;
    long HOUR = MINUTE * MINUTE_SPEC;
    long DAY = HOUR * HOUR_SPEC;

    int BITS_OF_BYTE = 8;
    int BITS_OF_2_BYTES = BITS_OF_BYTE * 2;
    int BITS_OF_3_BYTES = BITS_OF_BYTE * 3;
    int BITS_OF_4_BYTES = BITS_OF_BYTE * 4;

    int KB = 1024;
    int MB = KB * KB;
    long GB = MB * KB;
    long TB = GB * KB;
    /**
     * 是否返回
     */
    String PARAMS_GO_BACK = "params_go_back";
}
