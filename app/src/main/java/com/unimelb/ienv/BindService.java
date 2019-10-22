package com.unimelb.ienv;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class BindService extends Service implements SensorEventListener {

    private int currentCount = 0;
    private SensorManager sensorManager;
    private StepCount mStepCount;
    //whether has record history
    private boolean hasRecord = false;
    private int hasStepCount = 0;
    private int previousStepCount = 0;
    //data callback interface
    private UpdateUiCallBack mCallback;
    //record sensor type
    private static int stepSensorType = -1;

    //bridge of binder and activity
    private LcBinder lcBinder = new LcBinder();

    public BindService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("BindService—onCreate", "start stepCount");
        new Thread(new Runnable() {
            @Override
            public void run() {
                startStepDetector();
                Log.i("BindService—childThread", "startStepDetector()");
            }
        }).start();
    }

    /**
     * choose sensor:
     * SDK >= 19，choose STEP_COUNTER sensor else choose ACCELEROMETER sensor
     */
    private void startStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        //get sensor manager
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        //get SDK sensor
        int versionCodes = Build.VERSION.SDK_INT;

        if (versionCodes >= 19) {
            addCountStepListener();
        } else {
            addAccelerometerListener();
        }
    }

    /**
     *  start STEP_COUNTER sensor to compute steps
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_COUNTER;
            sensorManager.registerListener(BindService.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i("Type of sensor", "Sensor.TYPE_STEP_COUNTER");
        } else if (detectorSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_DETECTOR;
            sensorManager.registerListener(BindService.this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            addAccelerometerListener();
        }
    }

    /**
     * start ACCELEROMETER sensor to compute steps
     */
    private void addAccelerometerListener() {
        Log.i("Type of sensor", "TYPE_ACCELEROMETER");
        mStepCount = new StepCount();
        mStepCount.setSteps(currentCount);
        //get ACCELEROMETER sensor
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //registerListener
        boolean isAvailable = sensorManager.registerListener(mStepCount.getStepDetector(), sensor, SensorManager.SENSOR_DELAY_UI);
        mStepCount.initListener(new StepValueListener() {
            @Override
            public void stepChanged(int steps) {
                currentCount = steps;//callback to get step
                updateNotification();//notify
            }
        });
    }

    /**
     * notify server
     */
    private void updateNotification() {
        if (mCallback != null) {
            Log.i("BindService", "data update");
            mCallback.updateUi(currentCount);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return lcBinder;
    }

    /**
     *  sensor data change detect function
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stepSensorType == Sensor.TYPE_STEP_COUNTER) {

            int tempStep = (int) event.values[0];
            if (!hasRecord) {
                hasRecord = true;
                hasStepCount = tempStep;
            } else {
                int thisStepCount = tempStep - hasStepCount;
                int thisStep = thisStepCount - previousStepCount;
                currentCount += (thisStep);
                previousStepCount = thisStepCount;
            }
        }
        else if (stepSensorType == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0) {
                currentCount++;
            }
        }
        updateNotification();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class LcBinder extends Binder {
        BindService getService() {
            return BindService.this;
        }
    }

    public void registerCallback(UpdateUiCallBack paramICallback) {
        this.mCallback = paramICallback;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
