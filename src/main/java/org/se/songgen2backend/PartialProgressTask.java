package org.se.songgen2backend;

import javafx.concurrent.Task;

/**
 * A Wrapper around the {@link Task} from JavaFX to more easily update the progress, when several procedures have to be
 * executed. Each procedure commits the same amount to the total progress
 *
 * @author Val Richter
 */
public abstract class PartialProgressTask<V> extends Task<V> {
	private final int proceduresAmount;
	private int currentProcedureNum = 0;
	private double currentProgress = 0;

	protected void updateProgress(double progress) {
		double alreadyDone = (double) currentProcedureNum / proceduresAmount;
		setCurrentProgress(alreadyDone + progress / proceduresAmount);
	}

	protected void procedureDone() {
		currentProcedureNum++;
		setCurrentProgress((double) currentProcedureNum / proceduresAmount);
	}

	protected void setCurrentProgress(double currentProgress) {
		this.currentProgress = currentProgress;
		super.updateProgress(currentProgress, 1);
	}

	// Boilerplate:

	protected PartialProgressTask(int proceduresAmount) {
		this.proceduresAmount = proceduresAmount;
	}

	public int getProceduresAmount() {
		return this.proceduresAmount;
	}

	public int getCurrentProcedureNum() {
		return this.currentProcedureNum;
	}

	public double getCurrentProgress() {
		return this.currentProgress;
	}

	public void setCurrentProcedureNum(int currentProcedureNum) {
		this.currentProcedureNum = currentProcedureNum;
	}
}
