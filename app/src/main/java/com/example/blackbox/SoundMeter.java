package com.example.blackbox;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import java.io.IOException;

public class SoundMeter {
    private MediaRecorder mRecorder = null;
    private final Context context;

    // Thêm Constructor để nhận Context
    public SoundMeter(Context context) {
        this.context = context;
    }

    public void start() {
        if (mRecorder == null) {
            // Sửa cách khởi tạo cho Android đời mới
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mRecorder = new MediaRecorder(context);
            } else {
                mRecorder = new MediaRecorder();
            }

            try {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");

                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
                // Nếu lỗi thì giải phóng luôn để tránh rác bộ nhớ
                stop();
            }
        }
    }

    public void stop() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (RuntimeException e) {
                // Chặn lỗi nếu stop() bị gọi sai trạng thái
                e.printStackTrace();
            }
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            try {
                return mRecorder.getMaxAmplitude();
            } catch (IllegalStateException e) {
                return 0;
            }
        }
        return 0;
    }
}