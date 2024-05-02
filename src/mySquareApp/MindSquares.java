package mySquareApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.io.*;

// connections, squares

public class MindSquares extends JFrame {
    public JPanel drawingPanel;
    public JPanel buttonPanel;
    public JPanel plusMinusButtons;
    private List<Square> squares;
    private List<List<Point>> connections;
    private boolean isDragging = false;
    private boolean isResizing = false;
    private Square selectedSquare;           // a square which is currently being moved
    private Square editSquare;               // s square which is currently being resized
    private Point selectedNode;              // highlighted node, first node in a connection
    static JButton addButton, saveButton, loadButton, resizeButton;
    static JButton colorButton1, colorButton2, colorButton3, plusButton, minusButton;
    private int offsetX, offsetY;  // the difference between where square starts and where mouse clicked
    private int objectCounter;
    private boolean coloursOpen;

    public MindSquares() {
        squares = new ArrayList<>();
        connections = new ArrayList<>();

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        setLayout(new BorderLayout());

        JPanel colorButtonsPanel = new JPanel(new FlowLayout());
        colorButtonsPanel.setLayout(new GridLayout(1, 3));

        coloursOpen = false;
        colorButton1 = new JButton();
        colorButton2 = new JButton();
        colorButton3 = new JButton();

        Dimension squareButtonSize = new Dimension(15, 15);
        colorButton1.setPreferredSize(squareButtonSize);
        colorButton2.setPreferredSize(squareButtonSize);
        colorButton3.setPreferredSize(squareButtonSize);

        colorButton1.setBackground(new Color(255, 183, 0));
        colorButton2.setBackground(new Color(39, 225, 68));
        colorButton3.setBackground(new Color(17, 0, 255));
        colorButton1.setOpaque(true);
        colorButton2.setOpaque(true);
        colorButton3.setOpaque(true);

        colorButtonsPanel.add(colorButton1);
        colorButtonsPanel.add(colorButton2);
        colorButtonsPanel.add(colorButton3);

        plusMinusButtons = new JPanel();
        plusMinusButtons.setLayout(new GridLayout(2, 1));

        plusButton = new JButton("+");
        minusButton = new JButton("-");

        Dimension size = new Dimension(30, 30);
        plusButton.setPreferredSize(size);
        minusButton.setPreferredSize(size);
        plusButton.setFont(buttonFont);
        minusButton.setFont(buttonFont);

        plusMinusButtons.add(plusButton);
        plusMinusButtons.add(minusButton);

        drawingPanel = new DrawingPanel();
        drawingPanel.addMouseListener(new SquareMouseAdapter());
        drawingPanel.addMouseMotionListener(new SquareMouseAdapter());

        addButton = new JButton("Add □");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        resizeButton = new JButton("Resize");

        addButton.setFont(buttonFont);
        saveButton.setFont(buttonFont);
        loadButton.setFont(buttonFont);
        resizeButton.setFont(buttonFont);

        addButton.setBackground(Color.white);
        saveButton.setBackground(Color.white);
        loadButton.setBackground(Color.white);
        resizeButton.setBackground(Color.white);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(10, 1));
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        buttonPanel.setBackground(new Color(42, 175, 232));

        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(resizeButton);

        addButton.addActionListener(e -> {
            buttonPanel.removeAll();
            buttonPanel.add(addButton);
            if (coloursOpen){coloursOpen = false;}
            else{
                buttonPanel.add(colorButtonsPanel);
                coloursOpen = true;
            }
            buttonPanel.add(saveButton);
            buttonPanel.add(loadButton);
            buttonPanel.add(resizeButton);

            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
        colorButton1.addActionListener(e -> {
            squares.add(new Square(100, 10, 100, "double click to edit text",
                    "#FFB700"));
            objectCounter += 1;
            repaint();
        });
        colorButton2.addActionListener(e -> {
            squares.add(new Square(100, 10, 100, "double click to edit text",
                    "#00FF00"));
            objectCounter += 1;
            repaint();
        });
        colorButton3.addActionListener(e -> {
            squares.add(new Square(100, 10, 100, "double click to edit text",
                    "#0000FF"));
            objectCounter += 1;
            repaint();
        });
        saveButton.addActionListener(e -> {
//            MindSquares.this.saveToFile("src/saved_state.ser");
            boolean success = MindSquares.this.saveToDb();
            if (success){
                showMessage("Successfully saved", true);
            }
            else{
                showMessage("Something went wrong", false);
            }

        });
//        loadButton.addActionListener(e -> MindSquares.this.loadFromFile("src/saved_state.ser"));
        loadButton.addActionListener(e -> MindSquares.this.loadFromDb());
        resizeButton.addActionListener(e -> {
            isResizing = !isResizing;  // turn on the opposite mode
            if (isResizing) {
                resizeButton.setBackground(Color.RED); // Change the color to indicate resize mode
            } else {
                if(editSquare != null){editSquare.editing = false;}
                editSquare = null;
                isResizing = false;
                resizeButton.setBackground(Color.white); // Reset the color
                hidePlusMinusButtons();
            }
            repaint();
        });
        plusButton.addActionListener(e -> {
            editSquare.resizeSquare(1);
            repaint();
        });
        minusButton.addActionListener(e -> {
            editSquare.resizeSquare(-1);
            repaint();
        });

        drawingPanel.setLayout(new BorderLayout());
        drawingPanel.add(buttonPanel, BorderLayout.WEST);

        add(drawingPanel);

        String iconPath = "src/mySquareApp/icon.jpg";
        ImageIcon icon = new ImageIcon(iconPath);
        setIconImage(icon.getImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Mind Squares App");

        // add the fist square
        objectCounter = 0;
        squares.add(new Square(100, 10, 100, "double click to edit text",
                "#0000FF"));
        objectCounter += 1;

        setVisible(true);
    }
// ----------------------------------------------------------------------------
    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawLines(g);
            for (Square square : squares) {
                square.drawSquare(g);
                square.drawBorder(g);
                square.drawNodes(g, selectedNode);
            }
        }
    }
    private class SquareMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (isResizing) {
                for (Square square : squares) {
                    if (square.contains(e.getX(), e.getY())) {
                        if (editSquare == null){  // selecting a square
                            editSquare = square;
                            square.editing = true;
                            displayPlusMinusButtons();
                            repaint();
                            return;
                        }
                        if (square != editSquare){  // switching from a square
                            editSquare.editing = false;
                            editSquare = square;
                            square.editing = true;
                            repaint();
                            return;
                        }
                    }
                }
                // selected edit square for resizing cancels if I click away
                if (editSquare != null){
                    editSquare.editing = false;
                    editSquare = null;
                }
                return;
            }
            // right mouse click
            if (SwingUtilities.isRightMouseButton(e)) {
                // if there line remove line
                if(!removeConnectionAt(e.getX(), e.getY()))  // if it finds a line it deletes the line
                {
                //if there square remove square
                    removeSquareAt(e.getX(), e.getY());
                }
                repaint();
                return;
            }
            // left mouse click
            Point clickedNode = null;
            for (Square square : squares) {
                // check nodes
                clickedNode = square.getNodeAt(e.getX(), e.getY());
                if (clickedNode != null) {
                    if (selectedNode == null){
                        selectedNode = clickedNode;
                        repaint();
                        return;
                    }
                    createConnection(selectedNode, clickedNode);
                    selectedNode = null;
                    repaint();
                    return;
                }
                // check square
                else if (square.contains(e.getX(), e.getY())) {
                    if (e.getClickCount() == 2) {
                        editSquareText(square);
                    }
                    else {
                        startMoving(square, e);
                    }
                    break;
                }
            }
            // clicked node is null
            if (selectedNode != null){
                selectedNode = null;
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectedSquare != null){selectedSquare.selected = false;}
            selectedSquare = null;
            isDragging = false;
            repaint();
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDragging) {
                if (e.getX() < drawingPanel.getWidth() && e.getX() > 85 &&
                    e.getY() < drawingPanel.getHeight() && e.getY() > 0){
                    moveSquare(e.getX(), e.getY());
                }

            }
        }
    }
    private void displayPlusMinusButtons() {
        buttonPanel.add(plusMinusButtons);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    private void hidePlusMinusButtons() {
        if (plusMinusButtons != null) {
            buttonPanel.remove(plusMinusButtons);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }
    }

//    -------------------------------------------------------------------------
    private void startMoving(Square square, MouseEvent e) {
        selectedSquare = square;
        square.selected = true;
        isDragging = true;
        offsetX = e.getX() - square.getX();
        offsetY = e.getY() - square.getY();
    }
    private void moveSquare(int x, int y){
        // recalculate the relative possition of nodes
        for (Point node : selectedSquare.getNodes()) {
            int ofx, ofy;
            ofx = node.x - selectedSquare.getX();
            ofy = node.y - selectedSquare.getY();
            node.x = ofx + x - offsetX;
            node.y = ofy + y - offsetY;
        }
        // recalculate the square
        selectedSquare.setX(x - offsetX);
        selectedSquare.setY(y - offsetY);
        repaint();
    }
    private void editSquareText(Square square) {
        String currentText = square.getText();
        String inputText = JOptionPane.showInputDialog("Edit text:", currentText);
        if (inputText != null) {
            square.setText(inputText);
            repaint();
        }
    }
    private void drawLines(Graphics g){
        // draw background grid
        g.setColor(new Color(166, 163, 163));
        int maxx = Math.max(drawingPanel.getWidth(), drawingPanel.getHeight());
        for (int i = 0; i < (maxx)/10 + 1; i++){
            g.drawLine(0, i*10, drawingPanel.getWidth(), i*10);
            g.drawLine(i*10, 0, i*10, drawingPanel.getHeight());
        }
        // draw connecting lines
        g.setColor(new Color(0, 0, 0));
        for (List<Point> connection : connections) {
            Point node1 = connection.get(0);
            Point node2 = connection.get(1);
            g.drawLine(node1.x, node1.y, node2.x, node2.y);
        }
    }
    private void createConnection(Point a, Point b){
        if(a.equals(b)){
            return;
        }
        List<Point> conect = new ArrayList<>();
        conect.add(a);
        conect.add(b);
        connections.add(conect);
    }

// ---------------removing---------------------------------
    private boolean isPointNearLine(int px, int py, int x1, int y1, int x2, int y2) {
    double lengthSquared = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);

    if (lengthSquared == 0) {
        // If the line segment is just a point, check the distance to that point
        return Math.sqrt(Math.pow(x1 - px, 2) + Math.pow(y1 - py, 2)) <= 5.0; // 5.0 threshold
    }

    double t = Math.max(0, Math.min(1, ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / lengthSquared));
    double closestX = x1 + t * (x2 - x1);
    double closestY = y1 + t * (y2 - y1);

    double distanceSquared = Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2);

    double threshold = 5.0;
    return distanceSquared <= Math.pow(threshold, 2);
}
    private boolean removeConnectionAt(int px, int py) {  // removes one connection based on mouse click
        for (List<Point> connection : connections) {
            Point node1 = connection.get(0);
            Point node2 = connection.get(1);
            if (isPointNearLine(px, py, node1.x, node1.y, node2.x, node2.y)) {
                connections.remove(connection);
                return true;
            }
        }
        return false;
    }
    private void removeConnections(Square square) {  // removes all connection based on given square
        List<List<Point>> connectionsToRemove = new ArrayList<>();
        for (List<Point> connection : connections) {
            for (Point point : square.getNodes()){
                if (connection.contains(point)) {
                    connectionsToRemove.add(connection);
                }
            }
        }
        connections.removeAll(connectionsToRemove);
    }
    private void removeSquareAt(int px, int py){  // removes one square based on mouse click
        for (Square square : squares) {
            if (square.contains(px, py)) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to remove this square and its connections?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    squares.remove(square);
                    removeConnections(square);
                    repaint();
                }
                return;
            }
        }
    }

    // --------------save and load from file---------------------------------
    private void showMessage(String message, boolean success) {
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setForeground(Color.WHITE);

        JPanel messagePanel = new JPanel();
        if(success){
            messagePanel.setBackground(new Color(17, 255, 0));
        }
        else{
            messagePanel.setBackground(new Color(225, 17, 20));
        }

        messagePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        messagePanel.add(label, gbc);

        JDialog dialog = new JDialog(this, "Message", true);
        dialog.getContentPane().add(messagePanel);
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(this);

        // Set a timer to automatically close the message after 2 seconds
        Timer timer = new Timer(1000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }
    private void saveToFile(String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(squares);
            outputStream.writeObject(connections);
            outputStream.writeInt(objectCounter);
            System.out.println("Save successful: " + fileName);
        } catch (IOException e) {
            handleIOException("Error saving file: " + e.getMessage(), e);
        }
    }
    private void loadFromFile(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            squares.clear();
            squares.addAll((List<Square>) inputStream.readObject());
            connections.clear();
            connections.addAll((List<List<Point>>) inputStream.readObject());
            objectCounter = inputStream.readInt();
            repaint();
            System.out.println("Load successful: " + fileName);
        } catch (FileNotFoundException e) {
            handleIOException("File not found: " + fileName, e);
        } catch (IOException | ClassNotFoundException e) {
            handleIOException("Error loading file: " + e.getMessage(), e);
        }
    }
    // nacitani s ukladanim ruznych souboru
//    private void loadFromFile(String filename) {
//
//        JFileChooser fileChooser = new JFileChooser();
//        int userChoice = fileChooser.showOpenDialog(this);
//
//        if (userChoice == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//
//            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(selectedFile))) {
//                squares.clear();
//                squares.addAll((List<Square>) inputStream.readObject());
//                connections.clear();
//                connections.addAll((List<List<Point>>) inputStream.readObject());
//                objectCounter = inputStream.readInt();
//                repaint();
//                System.out.println("Load successful: " + selectedFile.getAbsolutePath());
//            } catch (FileNotFoundException e) {
//                handleIOException("File not found: " + selectedFile.getAbsolutePath(), e);
//            } catch (IOException | ClassNotFoundException e) {
//                handleIOException("Error loading file: " + e.getMessage(), e);
//            }
//        }
//    }
//    private void saveToFile(String fileName) {
//        JFileChooser fileChooser = new JFileChooser();
//        int userChoice = fileChooser.showSaveDialog(this);
//
//        if (userChoice == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//
//            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
//                outputStream.writeObject(squares);
//                outputStream.writeObject(connections);
//                outputStream.writeInt(objectCounter);
//                System.out.println("Save successful: " + selectedFile.getAbsolutePath());
//            } catch (IOException e) {
//                handleIOException("Error saving file: " + e.getMessage(), e);
//            }
//        }
//    }
    private void handleIOException(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // --------------save and load from db---------------------------------
    private void linkConnections2Squares(){
        for (List<Point> connection : connections){
            Point node1 = connection.get(0);
            Point node2 = connection.get(1);
            for (Square square : squares){
                for(Point node : square.getNodes()){
                    if (node.getX() == node1.getX() && node.getY() == node1.getY()){
                        connection.set(0, node);
                    }
                    else if (node.getX() == node2.getX() && node.getY() == node2.getY()){
                        connection.set(1, node);
                    }
                }
            }
        }
    }

    private void loadFromDb(){
        SQL data = new SQL();
        squares = data.getSquares();
        connections = data.getConnections();
        linkConnections2Squares();
        repaint();
    }
    private boolean saveToDb(){
        SQL data = new SQL();
        boolean stateSquares = data.saveSquares(squares);
        boolean stateConns = data.saveConnections(connections);
        return stateConns || stateSquares;

    }

    public static void main(String[] args) {
            SwingUtilities.invokeLater(MindSquares::new);
    }
}