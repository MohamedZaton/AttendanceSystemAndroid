package com.pclink.attendance.system.ThreadTasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

public class StillThereThread
{
    Context mContext;
    int JOB_ID;

    public StillThereThread(Context context)
    {
        this.mContext = context;
    }

    public StillThereThread(Context context, int JOB_ID)
    {
        this.mContext = context;
        this.JOB_ID = JOB_ID;
    }

    private int getJobType(int jobTypeSelectedPos) //0
    {
        switch (jobTypeSelectedPos)
        {
            default:
            case 0:
                return Job.Type.JOB_TYPE_HANDLER;
            case 1:
                return Job.Type.JOB_TYPE_ALARM;
            case 2:
                return Job.Type.JOB_TYPE_PERIODIC_TASK;
        }
    }

    private int getNetworkTypeForJob(int networkTypeSelectedPos) //0
    {

        switch (networkTypeSelectedPos)
        {
            default:
            case 0:
                return Job.NetworkType.NETWORK_TYPE_ANY;
            case 1:
                return Job.NetworkType.NETWORK_TYPE_CONNECTED;
            case 2:
                return Job.NetworkType.NETWORK_TYPE_UNMETERED;
        }
    }

    private void removeSchedulerJob()   // remove
    {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(mContext);
        smartScheduler.removeJob(JOB_ID);

    }



    private void removePeriodicJob()
    {
        //smartJobButton.setText(getString(R.string.schedule_job_btn));

        SmartScheduler jobScheduler = SmartScheduler.getInstance(mContext);
        if (!jobScheduler.contains(JOB_ID))
        {
            Log.e("SmartScheduler : ","No job exists with JobID: " + JOB_ID);
            return;
        }

        if (jobScheduler.removeJob(JOB_ID))
        {
            Log.e("SmartScheduler : ", "Job successfully removed! with id :" + JOB_ID);

        }
    }

    private Job createJobXshc( Long  intervalInMillis) {
        String JOB_PERIODIC_TASK_TAG = "io.hypertrack.android_scheduler_demo.JobPeriodicTask";

        int jobType = getJobType(1);
        int networkType = getNetworkTypeForJob(0);
        boolean requiresCharging = false ;

        if (intervalInMillis == null)
        {
            return null;
        }

        Job.Builder builder = new Job.Builder(JOB_ID, (SmartScheduler.JobScheduledCallback) mContext, jobType, JOB_PERIODIC_TASK_TAG)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(requiresCharging)
                .setIntervalMillis(intervalInMillis);


        builder.setPeriodic(intervalInMillis);


        return builder.build();
    }

    public void startStillThereMission(long intervalMinuteseconds )
    {
        SmartScheduler jobScheduler = SmartScheduler.getInstance(mContext);
        // Check if any periodic job is currently scheduled

        if (jobScheduler.contains(JOB_ID))
        {
            removePeriodicJob();
            return;
        }
        // Create a new job with specified params
        Job job = createJobXshc(intervalMinuteseconds* 1000);//Long  intervalInMillis
        if (job == null)
        {
            Log.e("startStillThere: ","Error create job ");
            return;
        }

        // Schedule current created job
        if (jobScheduler.addJob(job))
        {
            // Toast.makeText(mContext, "Job successfully added!", Toast.LENGTH_SHORT).show();


        }
        else
        {
            //  Toast.makeText(mContext, "XXX Job failed  added!", Toast.LENGTH_SHORT).show();

        }



    }





}
