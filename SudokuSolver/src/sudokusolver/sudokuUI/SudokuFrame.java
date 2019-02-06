package sudokusolver.sudokuUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import sudokusolver.sudokubeans.SudokuValueObject;
import sudokusolver.sudokuinterface.SudokuListener;
import sudokusolver.sudokuthread.SudokuThread;


public class SudokuFrame extends JFrame implements SudokuListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private JButton solveButton,resetButton,cancelButton;
	private JTable grid;
	private JMenuBar menuBar;
	private JCheckBox animateBox;
	private JMenu fileMenu;
	private JMenuItem openItem,saveItem,exitItem;
	private SudokuThread sudokuThread;
	private Object data[][];
	private SudokuTableModel tableModel;
	private File selectedFile;
	
	public SudokuFrame() {
		super();
		setTitle("Sudoku Solver");
		setSize(new Dimension(550,550));
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		menuBar=new JMenuBar();
		this.setJMenuBar(menuBar);
		fileMenu=new JMenu("File");
		openItem=new JMenuItem("Open");
		openItem.addActionListener(this);
		fileMenu.add(openItem);
		saveItem=new JMenuItem("Save");
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);
		exitItem=new JMenuItem("Exit");
		exitItem.addActionListener(this);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		JPanel basePanel=new JPanel(new BorderLayout());
		makeDefaultData();
		tableModel=new SudokuTableModel(data);
		grid=new JTable(tableModel);
		setGridSize();
		grid.setDefaultRenderer(SudokuValueObject.class, new SudokuRenderer());
		JScrollPane gridScroller=new JScrollPane(grid,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		basePanel.add(gridScroller,BorderLayout.CENTER);
		JPanel solvePanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		solvePanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		solveButton=new JButton("Solve");
		solveButton.addActionListener(this);
		resetButton=new JButton("Reset");
		resetButton.addActionListener(this);
		cancelButton=new JButton("Stop");
		cancelButton.addActionListener(this);
		solvePanel.add(solveButton);
		solvePanel.add(cancelButton);
		solvePanel.add(resetButton);
		animateBox=new JCheckBox("Animate",false);
		solvePanel.add(animateBox);
		basePanel.add(solvePanel,BorderLayout.SOUTH);
		getContentPane().add(basePanel);
		setVisible(true);
	}
	private void reset() {
		makeDefaultData();
		tableModel=new SudokuTableModel(data);
	
	}
	private void saveResultsToUserFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		jfc.addChoosableFileFilter(filter);
		int returnValue = jfc.showOpenDialog(null);
		
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			saveResultsToFile(selectedFile.getAbsolutePath());	
		}
	}
	private void openSudokuFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		jfc.addChoosableFileFilter(filter);
		int returnValue = jfc.showOpenDialog(null);
		
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			readSudokuFile(selectedFile);	
		}
	}
	private void readSudokuFile(File f) {
		try {
			tableModel.clear();
			setGridSize();
			BufferedReader br=new BufferedReader(new FileReader(f));
			String line="";
			int counter=0;
			Vector<String> lines=new Vector<String>();
			while((line=br.readLine())!=null) {
				line=line.trim();
				line=line.replaceAll(" ", "");
				lines.add(line);
				
			}
			br.close();
			if(lines.size()!=data.length) {
				JOptionPane.showMessageDialog(this, "File contains invalid data.");
				return;
			}
			for(int i=0;i<lines.size();i++) {
				String l=lines.get(i);
				for(int x=0;x<l.length();x++) {
					try {
						SudokuValueObject o =(SudokuValueObject)data[i][x];
						String val=l.substring(x, x+1);
						if(val.equalsIgnoreCase("X")) {
							val="-1";
						}
						
						o.setVal(Integer.parseInt(val));
					}catch(NumberFormatException nfe) {
						JOptionPane.showMessageDialog(this, "File contains invalid data.");
						return;
					}
				}
				tableModel.fireTableDataChanged();
				setGridSize();
				selectedFile=f;
				setTitle("Sudoku Solver: "+f.getName());
			}
			
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, "An Error Occured Opening File. Ensure that it is a valid sudoku file.");
		}
	}
	private void solve() {
		
		sudokuThread=new SudokuThread(data, this,animateBox.isSelected());
		
	}
	private void setGridSize() {
		TableColumn column = null;
		for (int i = 0; i < data.length; i++) {
		    column = grid.getColumnModel().getColumn(i);
		    column.setPreferredWidth(50);
		    grid.setRowHeight(i, 50);
		}
	}
	private void makeDefaultData() {
		data=new Object[9][9];
		for(int i=0;i<data.length;i++) {
			for(int x=0;x<data.length;x++) {
				data[i][x]=new SudokuValueObject(i,x);
			}
		}
	}
	public void sudokuStarted() {
		
	}
	public void sudokuFinished() {
		saveResultsToDefault();
		
	}
	private void saveResultsToDefault() {
		if(selectedFile!=null) {
			try {
			String namePart=selectedFile.getName().replace(".txt","");
			String defName=selectedFile.getParent()+System.getProperty("file.separator")+namePart+".sln.txt ";
			saveResultsToFile(defName);
			JOptionPane.showMessageDialog(this, "Finished. Results Saved To "+defName);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void saveResultsToFile(String fileName) {
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(fileName));
			for(int i=0;i<data.length;i++) {
				String outString="";
				for(int x=0;x<data.length;x++) {
					SudokuValueObject o=(SudokuValueObject)data[i][x];
					if(o.getVal()==-1) {
						outString+="X";
					}else {
					outString+=o.getVal();
					}
				}
				bw.write(outString);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public void sudokuValueChanged(int row, int col) {	
		tableModel.fireTableCellUpdated(row, col);
	}
	public void isError() {
		JOptionPane.showMessageDialog(this, "An Error Has Occurred");
		sudokuThread.stop();
		
	}
	public void sudokuStateChanged(int row, int col) {
		tableModel.fireTableCellUpdated(row, col);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==openItem) {
			openSudokuFile();
		}
		if(e.getSource()==saveItem) {
			saveResultsToUserFile();
		}
		if(e.getSource()==solveButton) {
			solve();
		}
		if(e.getSource()==exitItem) {
			System.exit(0);
		}
		if(e.getSource()==resetButton) {
			tableModel.clear();
			setGridSize();
			if(selectedFile!=null) {
				readSudokuFile(selectedFile);
			}
		}
		if(e.getSource()==cancelButton) {
			if(sudokuThread!=null) {
				sudokuThread.stop();
				//tableModel.clear();
				//setGridSize();
			}
		}
		
	}
 public static void main(String[] args) {
	 new SudokuFrame();
 }
 
 class SudokuTableModel extends AbstractTableModel {
	    private String[] columnNames;
	    private Object[][] data;
	    
	    public SudokuTableModel(Object[][] data) {
	    	this.data=data;
	    }
	    
	    public int getColumnCount() {
	        return data.length;
	    }

	    public int getRowCount() {
	        return data.length;
	    }

	    public String getColumnName(int col) {
	        return "";
	    }

	    public Object getValueAt(int row, int col) {
	        return data[row][col];
	    }

	    public Class getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }

	   
	    public boolean isCellEditable(int row, int col) {
	            return false;
	    }
	    public void setValueAt(Object value, int row, int col) {
	        data[row][col] = value;
	        fireTableCellUpdated(row, col);
	    }
	    public void clear() {
	    	for(int i=0;i<data.length;i++) {
	    		for(int x=0;x<data.length;x++) {
	    			data[i][x]=new SudokuValueObject(i, x);
	    		}
	    	}
	    	fireTableDataChanged();
	    }
 }

 public class SudokuRenderer extends JLabel
 implements TableCellRenderer {

public SudokuRenderer() {
setOpaque(true); 
}

public Component getTableCellRendererComponent(
  JTable table, Object val,
  boolean isSelected, boolean hasFocus,
  int row, int column) {
	SudokuValueObject o=(SudokuValueObject)val;
	setHorizontalAlignment(JLabel.CENTER);
	if(o.getVal()==-1) {
		setText("");
	}else {
	setText(o.getVal()+"");
	}
	setBackground(Color.white);
	setForeground(Color.black);
	if(o.isEvaluating()) {
	 setForeground(Color.lightGray);
	}
	if(o.isSetting()) {
		setBackground(Color.green.brighter());
	}

return this;
}
}

}
