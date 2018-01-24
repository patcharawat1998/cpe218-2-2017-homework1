import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.net.URL;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Stack;

public class TreeDemo extends JPanel implements TreeSelectionListener {
    private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
    private static boolean DEBUG = false;

    //Optionally play with line styles.  Possible values are
    //"Angled" (the default), "Horizontal", and "None".
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";

    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;

    //DIY-----------------------------------------------------------------------
    public static Node current;
    public static Medhod medhod;

    DefaultMutableTreeNode  rootOperand = null;
    static DefaultMutableTreeNode selNode;


    boolean leaf = false;
    //-----------------------------------------------------------------------------------------------------




    public TreeDemo() {
        super(new GridLayout(1,0));

        //Create the nodes.
//        DefaultMutableTreeNode rootOperand = new DefaultMutableTreeNode(current.key);///////////////////////////////////////////////////////
//        createNodes(rootOperand,current);

        //rootOperand = new DefaultMutableTreeNode(current.key);///////////////////////////////////////////////////////
        createNodes(rootOperand,current);

        //Create a tree that allows one selection at a time.
        tree = new JTree(rootOperand);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        if (playWithLineStyle) {
            System.out.println("line style = " + lineStyle);
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);

        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        initHelp();
        JScrollPane htmlView = new JScrollPane(htmlPane);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);

        Dimension minimumSize = new Dimension(100, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100);
        splitPane.setPreferredSize(new Dimension(500, 300));

        //Add the split pane to this panel.
        add(splitPane);
    }



    public String printOutput(DefaultMutableTreeNode selNode,String a){

        if(selNode.isLeaf()){// node is num
            leaf = true;
            return selNode.toString();
        }else {
            leaf = false;
            if(selNode.getFirstChild().isLeaf() && selNode.getLastChild().isLeaf()){ //left is num but right is num
                a = selNode.getFirstChild().toString() + selNode.toString() + selNode.getLastChild().toString();
            }else if(!selNode.getFirstChild().isLeaf() && selNode.getLastChild().isLeaf()){ //left is operand but right is num
                a +=  "("+printOutput((DefaultMutableTreeNode) selNode.getFirstChild(),a) +")" + selNode.toString() + selNode.getLastChild().toString();
            }else if(selNode.getFirstChild().isLeaf() && !selNode.getLastChild().isLeaf()){ //left is num but right is opearand
                a += selNode.getFirstChild().toString()+ selNode.toString() +"("+ printOutput((DefaultMutableTreeNode) selNode.getLastChild(),a)+")";
            }else if(!selNode.getFirstChild().isLeaf() && !selNode.getLastChild().isLeaf()){ //left and right is opearand both
                a += "("+printOutput((DefaultMutableTreeNode) selNode.getFirstChild(),a)+")"+selNode.toString()+"("+printOutput((DefaultMutableTreeNode) selNode.getLastChild(),a)+")";
            }


            return a;
        }

    }

    private static String inf2postf(String infix) {

        String postfix = "";
        Stack<Character> operator = new Stack<Character>();
        char popped;

        for (int i = 0; i < infix.length(); i++) {

            char get = infix.charAt(i);

            if (!isOperand(get))
                postfix += get;

            else if (get == ')')
                while ((popped = operator.pop()) != '(')
                    postfix += popped;

            else {
                while (!operator.isEmpty() && get != '(' && precedence(operator.peek()) >= precedence(get))
                    postfix += operator.pop();

                operator.push(get);
            }
        }
        // pop any remaining operator
        while (!operator.isEmpty())
            postfix += operator.pop();

        return postfix;
    }

    private static boolean isOperand(char i) {
        return precedence(i) > 0;
    }

    private static int precedence(char i) {

        if (i == '(' || i == ')') return 1;
        else if (i == '-' || i == '+') return 2;
        else if (i == '*' || i == '/') return 3;
        else return 0;
    }

    public String Calculate(String input) {
        Stack<String> s = new Stack<>();
        for (int i = 0; i < input.length(); i++) {
            char x = input.charAt(i);
            if (!isOperator(x)) {
                s.push("" + x);
            } else {
                int b = Integer.parseInt(s.pop());
                int a = Integer.parseInt(s.pop());
                if (x == '+') {
                    s.push(Integer.toString(a + b));
                } else if (x == '-') {
                    s.push(Integer.toString(a - b));
                } else if (x == '*') {
                    s.push(Integer.toString(a * b));
                } else if (x == '/') {
                    s.push(Integer.toString(a / b));
                }
            }
        }
        return s.pop();
    }

    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {

        selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        String infix = printOutput(selNode,"");

        String p = inf2postf(infix);
        String result = Calculate(p);


        if(leaf){
            htmlPane.setText(infix);
        }else {
            htmlPane.setText(infix+"="+result);
        }

//        if(selNode.isLeaf()){// node is num
//            htmlPane.setText(selNode.toString());
//        }else {
//            if(selNode.getFirstChild().isLeaf() && selNode.getLastChild().isLeaf()){ //left is num but right is num
//                htmlPane.setText(selNode.getFirstChild().toString()+selNode.toString()+selNode.getLastChild().toString());
//            }else if(!selNode.getFirstChild().isLeaf() && selNode.getLastChild().isLeaf()){ //left is operand but right is num
//
//            }
//        }


//        if (selNode == null) return;
//
//        Object nodeInfo = selNode.getUserObject();
//        if (selNode.isLeaf()) {
//            BookInfo book = (BookInfo) nodeInfo;
//            //displayURL(book.bookURL);
//            if (DEBUG) {
//                System.out.print(book.bookURL + ":  \n    ");
//            }
//        } else {
//            displayURL(helpURL);
//        }
//        if (DEBUG) {
//            System.out.println(nodeInfo.toString());
//        }
//            }
//        }
    }

    private class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = getClass().getResource(filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                        + filename);
            }
        }

        public String toString() {
            return bookName;
       }
    }


    private void initHelp() {
        String s = "TreeDemoHelp.html";
        helpURL = getClass().getResource(s);
        if (helpURL == null) {
            System.err.println("Couldn't open help file: " + s);
        } else if (DEBUG) {
            System.out.println("Help URL is " + helpURL);
        }

        displayURL(helpURL);
    }

    private void displayURL(URL url) { //-------------------------show output
//        try {
//            if (url != null) {
//                htmlPane.setPage(url);
//            } else { //null url
//                htmlPane.setText(medhod.output+""); //----------------------------------show output-------------
//                if (DEBUG) {
//                    System.out.println("Attempted to display a null URL.");
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Attempted to read a bad URL: " + url);
//        }
    }

    private void createNodes(DefaultMutableTreeNode top,Node current) {//////////////////////////
        if(rootOperand == null){
            rootOperand = new DefaultMutableTreeNode(current.key);
            top = rootOperand;
            createNodes(top,current);
        }
        else{
            DefaultMutableTreeNode num = null;
            if(isOperator(current.left.key)&&isOperator(current.right.key)){ //left right are operand both
                DefaultMutableTreeNode temp = top;
                top = new DefaultMutableTreeNode(current.left.key);
                temp.add(top);
                createNodes(top,current.left);

                top = new DefaultMutableTreeNode(current.right.key);
                temp.add(top);
                createNodes(top,current.right);
            }
            else if(!isOperator(current.left.key)&&!isOperator(current.right.key))  { //left and right is num
                //Base case
                num = new DefaultMutableTreeNode(new BookInfo(current.left.key+"", "tutorial.html"));
                top.add(num);
                num = new DefaultMutableTreeNode(new BookInfo(current.right.key+"", "tutorial.html"));
                top.add(num);
            }else if(!isOperator(current.left.key) && isOperator(current.right.key)){ //left is num but right is opearand
                DefaultMutableTreeNode temp = top;
                num = new DefaultMutableTreeNode(new BookInfo(current.left.key+"", "tutorial.html"));
                temp.add(num);
                top = new DefaultMutableTreeNode(current.right.key);
                temp.add(top);
                createNodes(top,current.right);
            }else if(isOperator(current.left.key) && !isOperator(current.right.key)){
                DefaultMutableTreeNode temp = top;
                top = new DefaultMutableTreeNode(current.left.key);
                num = new DefaultMutableTreeNode(new BookInfo(current.right.key+"", "tutorial.html"));
                temp.add(top);
                temp.add(num);
                createNodes(top,current.left);
            }
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }

        //Create and set up the window.
        JFrame frame = new JFrame("Binary Tree Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new TreeDemo());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    public boolean isOperator(char a) {
        if (a == '+' || a == '-' || a == '*' || a == '/') {
            return true;
        } else {
            return false;
        }
    }


    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.

        String input = "251-*32*+";
        input = "1251-*32*+*";
        Medhod medhod = new Medhod();

        if (args.length > 0) {
            input = args[0];
        }

        medhod.makeTree(input);
        //medhod.printTree();
        //medhod.inorder();
        //medhod.infix();
        //medhod.printResult();
        current = medhod.root;
        //System.out.println();



        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }
}