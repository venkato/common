package net.sf.jremoterun.utilities.nonjdk.swing

import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.*
import java.awt.*
import java.util.logging.Logger

public class NameAndTextField2 extends JPanel {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public JLabel label;

	public JTextField textField;
//	private int minWidth;

	public NameAndTextField2(String labelName, String textField, int fixedWidthInSymbols) {
		super(new BorderLayout(3, 3));
		this.label = new JLabel(labelName);
		this.textField = new JTextField(textField,fixedWidthInSymbols);
		// this.textField.setc
		add(label, BorderLayout.WEST);
		add(this.textField, BorderLayout.CENTER);
		//textField.wi = fixedWidthInSymbols;

	}

//	public NameAndTextField2(String labelName, String textField) {
//		this(labelName, textField, 30);
//		// this.textField.setMinimumSize(new Dimension(300,10));
//	}

//	public JLabel getLabel() {
//		return label;
//	}
//
//	public JTextField getTextField() {
//		return textField;
//	}

	public void setText(String text) {
		textField.setText(text);
	}

	public String getText() {
		return textField.getText();
	}

//	@Override
//	public Dimension getPreferredSize() {
//		Dimension dimension = new Dimension(super.getPreferredSize());
//		if (dimension.width <  minWidth) {
//			dimension.width = minWidth;
//		}
//		return dimension;
//	}


	@Override
	public boolean requestFocusInWindow() {
		return textField.requestFocusInWindow();
	}

}
