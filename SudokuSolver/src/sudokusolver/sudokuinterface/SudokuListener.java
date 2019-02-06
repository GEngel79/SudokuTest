package sudokusolver.sudokuinterface;

public interface SudokuListener {
	public void sudokuStarted();
	public void sudokuFinished();
	public void sudokuValueChanged(int row, int col);
	public void sudokuStateChanged(int row,int col);
	public void isError();
}
