package org.cybrosys.customisation;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

/**
 * @author Kevin
 * 
 */
public abstract class ClockGenerator {
	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	private int nCounter = 0;
	private final float TOTAL_TICKS;
	private final float DISPLACEMENT_ON_SINGLE_TICK;
	private final long PERIOD;
	public boolean RUNNING_FLAG = false;
	public boolean IS_RUNNING = false;
	private float TOTAL_DISPLACEMENT = 0;

	public ClockGenerator(long TotalTime, long Period, long Displacement) {
		TOTAL_TICKS = TotalTime / Period;
		DISPLACEMENT_ON_SINGLE_TICK = Displacement / TOTAL_TICKS;
		this.PERIOD = Period;
	}

	/**
	 * Callback fired on regular interval.
	 */
	public abstract void onTick(float DISPLACEMENT_ON_SINGLE_TICK);

	/**
	 * Callback fired on finish
	 */
	public abstract void onFinish(float DISPLACEMENT_ON_SINGLE_TICK);

	/**
	 * Starts the timer
	 * 
	 * @param internalRequestFlag
	 */
	public void startTranslationCallBack(boolean internalRequestFlag) {
		this.IS_RUNNING = true;
		if (internalRequestFlag == true) {

		} else {
			if (RUNNING_FLAG != false)
				return;
		}
		RUNNING_FLAG = true;
		nCounter = 0;
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						nCounter++;

						TOTAL_DISPLACEMENT = TOTAL_DISPLACEMENT
								+ DISPLACEMENT_ON_SINGLE_TICK;

						if (nCounter == TOTAL_TICKS) {
							mTimerTask.cancel();
							ClockGenerator.this.RUNNING_FLAG = false;
							endTranslationCallBack();
							t.cancel();
							RUNNING_FLAG = false;

						} else
							onTick(DISPLACEMENT_ON_SINGLE_TICK);
					}
				});
			}
		};
		t.scheduleAtFixedRate(mTimerTask, 0, PERIOD);
	}

	public void endTranslationCallBack() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
			onFinish(DISPLACEMENT_ON_SINGLE_TICK);
		}
	}
}