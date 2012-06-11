package uom.dl.editor;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DLEditor extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6701509284138703800L;
	private JTextArea textArea;
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

	public DLEditor() {
		super("A Simple DL Editor");
		initComponents();
	}

	// ⌐≥≤∀∃⊓⊔⊑⊒≡⊤⊥
	private void initComponents() {
		setLayout(new BorderLayout(10, 10));
		JLabel lbl1 = new JLabel("Add your DL statements:");

		textArea = new JTextArea(10, 30);
		//textArea.setMargin(new Insets(14, 14, 14, 14));
		textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textArea.setForeground(Color.BLUE);
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 14);
		textArea.setFont(f);

		JPanel charsPnl = new JPanel(new GridLayout(4, 2));
		existsBtn 		= new JButton(" ∃ ");
		forallBtn 		= new JButton(" ∀ ");
		lteBtn 			= new JButton(" ≤ ");
		mteBtn 			= new JButton(" ≥ ");
		unionBtn 		= new JButton(" ⊔ ");
		intersectionBtn = new JButton(" ⊓ ");
		notBtn 			= new JButton(" ⌐ ");
		
		//add listeners
		existsBtn 		.addActionListener(this);
		forallBtn 		.addActionListener(this);
		lteBtn 			.addActionListener(this);
		mteBtn 			.addActionListener(this);
		unionBtn 		.addActionListener(this);
		intersectionBtn .addActionListener(this);
        notBtn 			.addActionListener(this);
        
		charsPnl.add(existsBtn);
		charsPnl.add(forallBtn);
		charsPnl.add(lteBtn);
		charsPnl.add(mteBtn);
		charsPnl.add(unionBtn);
		charsPnl.add(intersectionBtn);
		charsPnl.add(notBtn);

		JPanel actionPnl = new JPanel(new GridLayout(1, 3));
		copyBtn = new JButton("Copy to clipboard");
		saveBtn = new JButton("Save to file");
		openBtn = new JButton("Open file");

		actionPnl.add(copyBtn);
		actionPnl.add(saveBtn);
		actionPnl.add(openBtn);

		add(lbl1, BorderLayout.NORTH);
		add(textArea, BorderLayout.CENTER);
		add(charsPnl, BorderLayout.EAST);
		add(actionPnl, BorderLayout.SOUTH);
		

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
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
			String text = ((JButton)event.getSource()).getText().trim();
			textArea.append(text);	
			textArea.requestFocus();
		}
	}

}
