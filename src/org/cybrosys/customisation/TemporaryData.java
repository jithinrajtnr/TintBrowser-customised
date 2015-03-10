package org.cybrosys.customisation;

import android.view.View;

public class TemporaryData {

	public final View VIEW;
	public int POSITION;

	public TemporaryData(View v, int pos) {
		this.VIEW = v;
		this.POSITION = pos;
	}
}
