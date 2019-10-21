package com.unimelb.ienv;

import android.util.Log;


public class StepCount  implements StepCountListener {
    private int mCount; //current step
    private int count;  //save step
    private long LastTime = 0;
    private long ThisTime = 0;
    private StepValueListener stepValueListener;//interface for value pass
    private StepDetector stepDetector;//interface for step detect


    public StepCount() {
        stepDetector = new StepDetector();
        stepDetector.initListener(this);
    }

    @Override
    public void countStep() {
        this.LastTime = this.ThisTime;
        this.ThisTime = System.currentTimeMillis();
        Log.i("countStep","reset sensor data");
//        notifyListener();
        if (this.ThisTime - this.LastTime <= 3000L) {
            if (this.count < 9) {
                this.count++;
            } else if (this.count == 9) {
                this.count++;
                this.mCount += this.count;
                notifyListener();
            } else {
                this.mCount++;
                notifyListener();
            }
        } else {
            this.count = 1;
        }

    }
    public void setSteps(int CurrentCount){
        this.mCount = CurrentCount;
        this.count = 0;
        LastTime = 0;
        ThisTime = 0;
        notifyListener();
    }


    public StepDetector getStepDetector(){
        return stepDetector;
    }
    /**
     * update step
     */
    public void notifyListener(){
        if(this.stepValueListener != null){
            Log.i("countStep","data update");
            this.stepValueListener.stepChanged(this.mCount);
        }
    }
    public  void initListener(StepValueListener listener){
        this.stepValueListener = listener;
    }
}
