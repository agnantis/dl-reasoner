package uom.dl.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class DLEditor extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6701509284138703800L;
	private JTextPane textPane;
	private JButton existsBtn;
	private JButton forallBtn;
	private JButton lteBtn;
	private JButton mteBtn;
	private JButton unionBtn;
	private JButton intersectionBtn;
	private JButton notBtn;
	private JButton copyBtn;
	private JButton saveBtn;
	private JButton openBtn;
	private JButton subsumesBtn;
	private JButton subsumedBtn;
	private JButton topConceptBtn;
	private JButton bottomConceptBtn;
	private JButton equivalentBtn;
	private StyledDocument doc;

	public DLEditor() {
		super("A Simple DL Editor");
		initComponents();
	}

	// ⌐≥≤∀∃⊓⊔⊑⊒≡⊤⊥
	private void initComponents() {
		setLayout(new BorderLayout(10, 10));
		JLabel lbl1 = new JLabel("Add your DL statements:");

		textPane = new JTextPane();//15, 50);
		textPane.setMargin(new Insets(5, 5, 5, 5));
		
		JScrollPane textScroll = new JScrollPane(textPane);
		textScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//textArea.setMargin(new Insets(14, 14, 14, 14));

		JPanel charsPnl = new JPanel(new GridLayout(6, 2));
		charsPnl.setBorder(BorderFactory.createTitledBorder("Special Characters"));
		topConceptBtn 	= new CharJButton("⊤");
		bottomConceptBtn= new CharJButton("⊥");
		subsumesBtn 	= new CharJButton("⊒");
		subsumedBtn 	= new CharJButton("⊑");
		existsBtn 		= new CharJButton("∃");
		forallBtn 		= new CharJButton("∀");
		lteBtn 			= new CharJButton("≤");
		mteBtn 			= new CharJButton("≥");
		unionBtn 		= new CharJButton("⊔");
		intersectionBtn = new CharJButton("⊓");
		equivalentBtn	= new CharJButton("≡");
		notBtn 			= new CharJButton("⌐");
		
		//add listeners
		topConceptBtn	.addActionListener(this);
		bottomConceptBtn.addActionListener(this);
		subsumesBtn		.addActionListener(this);
		subsumedBtn		.addActionListener(this);
		existsBtn 		.addActionListener(this);
		forallBtn 		.addActionListener(this);
		lteBtn 			.addActionListener(this);
		mteBtn 			.addActionListener(this);
		unionBtn 		.addActionListener(this);
		intersectionBtn .addActionListener(this);
		equivalentBtn	.addActionListener(this);
        notBtn 			.addActionListener(this);
        
        //character buttons
		charsPnl.add(topConceptBtn);
		charsPnl.add(bottomConceptBtn);
		charsPnl.add(subsumesBtn);
		charsPnl.add(subsumedBtn);
		charsPnl.add(existsBtn);
		charsPnl.add(forallBtn);
		charsPnl.add(lteBtn);
		charsPnl.add(mteBtn);
		charsPnl.add(unionBtn);
		charsPnl.add(intersectionBtn);
		charsPnl.add(equivalentBtn);
		charsPnl.add(notBtn);
		
		//text format buttons
		JButton increaseSizeBtn = new JButton("A+");
		JButton decreaseSizeBtn = new JButton("A-");
		increaseSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font f = textPane.getFont();
				System.out.println("Font: " + f);
				f = f.deriveFont((float) Math.min(f.getSize()+2, 30));
				System.out.println("Font: " + f);
				textPane.setFont(f);				
			}
		});
		decreaseSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font f = textPane.getFont();
				System.out.println("Font: " + f);
				f = f.deriveFont((float) Math.max(f.getSize()-2, 10));
				System.out.println("Font: " + f);
				textPane.setFont(f);				
			}
		});
		
		JPanel textFormatPnl = new JPanel(new GridLayout(1, 2));
		textFormatPnl.setBorder(BorderFactory.createTitledBorder("Text Format"));
		textFormatPnl.add(increaseSizeBtn);
		textFormatPnl.add(decreaseSizeBtn);
		JPanel charsPPnl = new JPanel(new BorderLayout());
		charsPPnl.add(charsPnl, BorderLayout.NORTH);
		charsPPnl.add(textFormatPnl, BorderLayout.SOUTH);

		//operation buttons
		JPanel actionPnl = new JPanel(new GridLayout(1, 3));
		copyBtn = new JButton("Copy to clipboard");
		saveBtn = new JButton("Save to file");
		openBtn = new JButton("Open file");

		actionPnl.add(copyBtn);
		actionPnl.add(saveBtn);
		actionPnl.add(openBtn);

		add(lbl1, BorderLayout.NORTH);
		add(textScroll, BorderLayout.CENTER);
		add(charsPPnl, BorderLayout.EAST);
		add(actionPnl, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(640, 480));
		pack();
		//styles
		doc = textPane.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
	}
	
	@Override
	public Insets getInsets() {
		Insets newInsets = super.getInsets();
		newInsets.top = newInsets.top + 10;
		newInsets.bottom = newInsets.bottom + 10;
		newInsets.left = newInsets.left + 10;
		newInsets.right = newInsets.right + 10;
		return newInsets;
	}

	public static void main(String[] args) {
		try {
			System.out.println(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		final JFrame editor = new DLEditor();
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				editor.setVisible(true);
			}
		});
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() instanceof JButton) {
			String text = ((JButton)event.getSource()).getText();
			
			try {
				doc.insertString(doc.getLength(), " " + text, doc.getStyle("bold"));
				doc.insertString(doc.getLength(), " ", doc.getStyle("regular"));
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textPane.requestFocus();
		}
	}
	
	private static class CharJButton extends JButton {
		private static final long serialVersionUID = 39511486243933705L;

		public CharJButton(String text) {
			super(text);
			format();
		}
		
		private void format() {
			Dimension d = new Dimension(50, 40);
			this.setMinimumSize(d);
			this.setPreferredSize(d);
		}
		
	}

}
