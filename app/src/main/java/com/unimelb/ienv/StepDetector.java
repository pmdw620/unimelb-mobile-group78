package com.unimelb.ienv;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class StepDetector implements SensorEventListener {

    //save 3-D data
    float[] dimensionvalues = new float[3];
    final int ValueNum = 4;
    float[] tempValue = new float[ValueNum];
    int tempCount = 0;
    boolean upFlag = false;
    int continueUpCount = 0;
    int continueUpFormerCount = 0;
    boolean lastStatus = false;
    float peakOfWave = 0;
    float valleyOfWave = 0;
    long timeOfThisPeak = 0;
    long timeOfLastPeak = 0;
    long timeOfNow = 0;
    float gravityNew = 0;
    float gravityOld = 0;
    final float InitialValue = (float) 1.3;
    float ThreadValue = (float) 2.0;
    int TimeInterval = 250;
    private StepCountListener mStepListeners;

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (int i = 0; i < 3; i++) {
            dimensionvalues[i] = event.values[i];
        }
        gravityNew = (float) Math.sqrt(dimensionvalues[0] * dimensionvalues[0]
                + dimensionvalues[1] * dimensionvalues[1] + dimensionvalues[2] * dimensionvalues[2]);
        detectorNewStep(gravityNew);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    public void initListener(StepCountListener listener) {
        this.mStepListeners = listener;
    }


    public void detectorNewStep(float values) {
        if (gravityOld == 0) {
            gravityOld = values;
        } else {
            if (detectorPeak(values, gravityOld)) {
                timeOfLastPeak = timeOfThisPeak;
                timeOfNow = System.currentTimeMillis();
                if (timeOfNow - timeOfLastPeak >= TimeInterval
                        && (peakOfWave - valleyOfWave >= ThreadValue)) {
                    timeOfThisPeak = timeOfNow;
                    mStepListeners.countStep();
                }
                if (timeOfNow - timeOfLastPeak >= TimeInterval
                        && (peakOfWave - valleyOfWave >= InitialValue)) {
                    timeOfThisPeak = timeOfNow;
                    ThreadValue = peakValleyThread(peakOfWave - valleyOfWave);
                }
            }
        }
        gravityOld = values;
    }


    public boolean detectorPeak(float newValue, float oldValue) {
        lastStatus = upFlag;
        if (newValue >= oldValue) {
            upFlag = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            upFlag = false;
        }

        if (!upFlag && lastStatus
                && (continueUpFormerCount >= 2 || oldValue >= 20)) {
            peakOfWave = oldValue;
            return true;
        } else if (!lastStatus && upFlag) {
            valleyOfWave = oldValue;
            return false;
        } else {
            return false;
        }
    }

    public float peakValleyThread(float value) {
        float tempThread = ThreadValue;
        if (tempCount < ValueNum) {
            tempValue[tempCount] = value;
            tempCount++;
        } else {
            tempThread = averageValue(tempValue, ValueNum);
            for (int i = 1; i < ValueNum; i++) {
                tempValue[i - 1] = tempValue[i];
            }
            tempValue[ValueNum - 1] = value;
        }
        return tempThread;

    }


    public float averageValue(float value[], int n) {
        float ave = 0;
        for (int i = 0; i < n; i++) {
            ave += value[i];
        }
        ave = ave / ValueNum;
        if (ave >= 8)
            ave = (float) 4.3;
        else if (ave >= 7 && ave < 8)
            ave = (float) 3.3;
        else if (ave >= 4 && ave < 7)
            ave = (float) 2.3;
        else if (ave >= 3 && ave < 4)
            ave = (float) 2.0;
        else {
            ave = (float) 1.3;
        }
        return ave;
    }

}

