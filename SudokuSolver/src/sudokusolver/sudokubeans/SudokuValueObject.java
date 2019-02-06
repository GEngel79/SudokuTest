package sudokusolver.sudokubeans;

public class SudokuValueObject {
private int val=-1;
private boolean isEvaluating, isSetting;
private int row,col;

public SudokuValueObject(int row, int col) {
	this.row=row;
	this.col=col;
}
public int getVal() {
	return val;
}
public void setVal(int val) {
	this.val = val;
}
public boolean isEvaluating() {
	return isEvaluating;
}
public void setEvaluating(boolean isEvaluating) {
	this.isEvaluating = isEvaluating;
}
public boolean isSetting() {
	return isSetting;
}
public void setSetting(boolean isSetting) {
	this.isSetting = isSetting;
}
public String toString() {
	return val+"";
}
public int getRow() {
	return row;
}
public void setRow(int row) {
	this.row = row;
}
public int getCol() {
	return col;
}
public void setCol(int col) {
	this.col = col;
}
}
