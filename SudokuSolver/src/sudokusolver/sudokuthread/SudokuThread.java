package sudokusolver.sudokuthread;


import sudokusolver.sudokubeans.SudokuValueObject;
import sudokusolver.sudokuinterface.SudokuListener;

public class SudokuThread extends Thread {
private int defaultVal=-1;
private Object[][] data;
private SudokuListener sudokuListener;
private boolean pause;


public SudokuThread(Object[][] d,SudokuListener l,boolean pause) {
	this.data=d;
	this.sudokuListener=l;
	this.pause=pause;
	start();
}

public void run() {
	try {
		sudokuListener.sudokuStarted();
		doIt();
		sudokuListener.sudokuFinished();
	}catch(Exception ex) {
		ex.printStackTrace();
		sudokuListener.isError();
	}
}
private boolean doIt() {
	SudokuValueObject current=null;
	for(int rw=0;rw<data.length;rw++) {
		for(int cl=0;cl<data.length;cl++) {
			if(current!=null&&pause) {
				current.setSetting(false);
				current.setEvaluating(false);
				sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
			}
			current=((SudokuValueObject)data[rw][cl]);

			if(current.getVal()==defaultVal) {
				if(current!=null&&pause) {
				current.setSetting(true);
				sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
				snooze();
				}
				
				for(int val=1;val<=data.length;val++) {
					current.setVal(val);
					sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
					if(isValid(rw, cl, current)) {
						if(current!=null&&pause) {
						current.setSetting(false);
						
						sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
						snooze();
						}
						if(doIt()) {
							return true;
						}else {
							current.setVal(defaultVal);
							if(current!=null&&pause) {
							current.setSetting(false);
							
							sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
							snooze();
							}
						}
					}
				}
				current.setVal(defaultVal);
				sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
				if(current!=null&&pause) {
				current.setSetting(false);
				
				sudokuListener.sudokuValueChanged(current.getRow(), current.getCol());
				snooze();
				}
				return false;
			}
		}
	}
	return true;
}
private void snooze() {
	if(pause) {
	try {
		yield();
		sleep(25);
	}catch(InterruptedException ex) {}
	}
}
private boolean isValid(int row, int col,SudokuValueObject o) {
	if(!isValidForCol(col, o)) {
		return false;
	}
	if(!isValidForRow(row, o)) {
		return false;
	}
	if(!isValidForSection(row, col, o)) {
		return false;
	}
	return true;
}
private boolean isValidForRow(int row, SudokuValueObject o) {
	SudokuValueObject so=null;
	for(int i=0;i<data[0].length;i++) {
		so=((SudokuValueObject)data[row][i]);
		if(so==o) {
			
			so.setEvaluating(false);
			sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
			
			//return true;
			continue;
		}
		if(pause) {
		so.setEvaluating(true);
		sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
		}
		if(so.getVal()==o.getVal()) {
			so.setEvaluating(false);
			sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
			return false;
		}
		so.setEvaluating(false);
		sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
	}

	
	return true;
}
private boolean isValidForCol(int col, SudokuValueObject o) {
	SudokuValueObject so=null;
	for(int i=0;i<data.length;i++) {
		so=((SudokuValueObject)data[i][col]);
		if(so==o) {
			
			so.setEvaluating(false);
			sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
			
			
			continue;
		}
		if(pause) {
		so.setEvaluating(true);
		sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
		}
		if(so.getVal()==o.getVal()) {
			so.setEvaluating(false);
			sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
			return false;
		}
		so.setEvaluating(false);
		sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
	}
	so.setEvaluating(false);
	sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
	return true;
}
private boolean isValidForSection(int row, int col,SudokuValueObject o) {
	int rw=row-row%3;
	int cl=col-col%3;
	SudokuValueObject so=null;
	for(int i=rw;i<rw+3;i++) {
		for(int x=cl;x<cl+3;x++) {
			so=((SudokuValueObject)data[i][x]);
			if(so==o) {
				if(pause) {
				so.setEvaluating(false);
				sudokuListener.sudokuStateChanged(so.getRow(), so.getCol());
				}
				continue;
			}
			if(pause) {
			so.setEvaluating(true);
			sudokuListener.sudokuValueChanged(so.getRow(), so.getCol());
			}
			if(so.getVal()==o.getVal()) {
				if(pause) {
				so.setEvaluating(false);
				sudokuListener.sudokuValueChanged(so.getRow(), so.getCol());
				}
				return false;
			}
		}
	}
	if(pause) {
	so.setEvaluating(false);
	sudokuListener.sudokuValueChanged(so.getRow(), so.getCol());
	}
	return true;
}

public int getDefaultVal() {
	return defaultVal;
}

public Object[][] getData() {
	return data;
}
public void setData(Object[][] data) {
	this.data = data;
}

}
