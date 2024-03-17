package com.shao.beautiful.config;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import bms.helper.tools.TimeDelayer;

public class MainThread {
	public ArrayList<Runnable> runs=new ArrayList<Runnable>();
	private int onceRun=20;
	private ExecutorService singlExecutor=Executors.newSingleThreadExecutor();
	private ArrayBlockingQueue<Runnable> arrayBlockingQueue;
	private ThreadFactory namedThreadFactory;
	private ThreadPoolExecutor tdpool;
	
	public  MainThread() {
		arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(
				256);
		namedThreadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable p1) {
				return new Thread(p1);
			}
		};

		tdpool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, arrayBlockingQueue, namedThreadFactory,
				new ThreadPoolExecutor.CallerRunsPolicy());
		
	}
	public void start(Runnable r) {
		singlExecutor.execute(r);
	}
	public boolean isLoadOver() {
		return runs.size()<onceRun;
	}
	public void upDate() {
		int i=onceRun;
		TimeDelayer delayer=new TimeDelayer(200);
		delayer.UpdateLastTime();
		while (i>0) {
			if (runs.size()==0) {
				break;
			}
			runs.get(0).run();
			
			
			i-=delayer.GetDelay();
		}
	}
	public void postRunnable(Runnable r) {
		runs.add(r);
	}
	
	
	public int getOnceRun() {
		return onceRun;
	}
	public void setOnceRun(int onceRun) {
		this.onceRun = onceRun;
	}
	
	
}
