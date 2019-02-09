import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

class no_name{
	private JFrame frame;
	private JWindow window;
	private JPanel panel;
	private JTree tree;

	no_name() throws IOException{
		frame = new JFrame();
		window = new JWindow();
		panel = new JPanel();
		
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize().getSize());
		frame.setLayout(null);

		//window
		window.setLayout(new FlowLayout());
		JLabel wait = new JLabel("WAIT");
		wait.setFont(new Font("Arial Black",Font.BOLD,30));
		window.add(wait);
		window.setVisible(true);
		window.setSize(200,60);
		window.setLocationRelativeTo(null);
		
		DefaultMutableTreeNode item = new DefaultMutableTreeNode("First");
		add_items(item);
		tree= new JTree(item);
		tree.setRootVisible(false);
		
		tree.addMouseListener((MouseListener) new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==3) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent(); 	
					JPopupMenu jpp = getPopUpMenu(selectedNode);
					jpp.show(panel, e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});
		
		JScrollPane sc = new JScrollPane(tree);
		sc.setBounds(0,0,frame.getWidth(),frame.getHeight()-30);
		
		panel.add(sc);
		panel.setLayout(null);
		
		//frame
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Explorer");
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
	
	private void add_items(DefaultMutableTreeNode item) throws IOException {
		Iterable<Path> root_directories = FileSystems.getDefault().getRootDirectories();
		for (Path name_of_root_directories : root_directories) {
			String path= name_of_root_directories.toString() + "/";
			DefaultMutableTreeNode items = new DefaultMutableTreeNode(path);
			addContenu(path,items);
			item.add(items);
			window.dispose();
		}
	}
	
	private void addContenu(String path,DefaultMutableTreeNode items){
		String old_path=path;
		File item = new File(path);
		boolean isSymbolicLink = Files.isSymbolicLink(item.toPath());
		if(item.isDirectory() && item.canRead() && isSymbolicLink == false) {
			String [] tab = item.list();
			for(int i=0;i<tab.length;i++) {
				path += tab[i] + "/";
				File sous_items = new File(path);
				String sous_item_array[] = sous_items.toString().split("/");
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(sous_item_array[sous_item_array.length - 1]);
				items.add(node);
				if(sous_items.isDirectory()){
					System.out.println(path);
					addContenu(path,node);
				}else {
					path += "/../";
				}
				path = old_path;
			}
		}
	}
	
	private JPopupMenu getPopUpMenu(DefaultMutableTreeNode selectedNode) {
	    JPopupMenu menu = new JPopupMenu();
	    String path="",sec_path="";
	    TreeNode[] node=selectedNode.getPath();
	    for(int i=1;i<selectedNode.getPath().length;i++) path += node[i] + "/";
	    for(int i=1;i<selectedNode.getPath().length-1;i++) sec_path += node[i] + "/";
	    File file = new File(path);
	    final String path__=path;
	    final String path_=sec_path;
	    if(file.isFile()) {
		    JMenuItem edit = new JMenuItem("Edit");
		    edit.addActionListener(new ActionListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void actionPerformed(ActionEvent e) {
		    		BufferedReader bfr = null;
		    		try{
		    			bfr = new BufferedReader(new FileReader(file));
		    			String str,content="";
		    			while((str = bfr.readLine()) != null)	content += str;
		    			
		    			JFrame jf = new JFrame();
		    			jf.setSize(500,500);
		    			
		    			JTextArea text = new JTextArea();
		    			text.setText(content);
		    			text.setRows(66);
		    			text.setColumns(10);
		    			text.setLineWrap(true);
		    			text.setWrapStyleWord(true);
		    			
		    			JScrollPane sc = new JScrollPane(text);
		    			sc.setBounds(0,0,jf.getWidth(),jf.getHeight()-80);
		    			
		    			JButton save = new JButton("Save");
		    			save.setBounds(150, jf.getHeight()-70, 90, 35);
		    			save.setCursor(Cursor.getDefaultCursor());
		    			save.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								PrintWriter ecrire;
								try {
									ecrire = new PrintWriter(new FileOutputStream(path__));
									ecrire.println(text.getText());
									ecrire.close();
								} catch (FileNotFoundException e1) {}
							}
		    				
		    			});
		    			
		    			JButton cancel = new JButton("Cancel");
		    			cancel.setBounds(270, jf.getHeight()-70, 90, 35);
		    			cancel.setCursor(Cursor.getDefaultCursor());
		    			cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								jf.dispose();
							}
		    			});
		    			
		    			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    			jf.setVisible(true);
		    			jf.setLayout(null);
		    			jf.setLocationRelativeTo(null);
		    			jf.setResizable(false);
		    			jf.add(sc);
		    			jf.add(save);
		    			jf.add(cancel);
		    			jf.setCursor(Cursor.TEXT_CURSOR);
		    			text.setFont( new Font("Arial Black",Font.BOLD,16));
		    		}catch(Exception e1){}
				}	
		    });
		    menu.add(edit);
	    }
	    JMenuItem rename = new JMenuItem("Rename");
	    rename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newname =JOptionPane.showInputDialog(frame, "Enter The new Name", "Rename", JOptionPane.QUESTION_MESSAGE);
				file.renameTo(new File(path_+newname));
				System.out.println(path__+newname);
				selectedNode.setUserObject(newname);
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
				model.reload(root);
			}	
	    });
	    menu.add(rename);
	    JMenuItem remove = new JMenuItem("Remove");
	    remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int delete =JOptionPane.showConfirmDialog(frame, "Ae you sur you want to delete this file ?");//showInputDialog(frame, , "Delete", JOptionPane.YES_NO_OPTION);
				if(delete==0) {
					file.delete();
					selectedNode.removeFromParent();
					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
					DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
					model.reload(root);
				}
			}	
	    });
	    menu.add(remove);
	    return menu;
	}
}

public class Main {

	public static void main(String[] args) throws IOException{
		new no_name();
	}
}