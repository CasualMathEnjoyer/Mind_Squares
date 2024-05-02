package mySquareApp;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Square implements Serializable {
    private int x;
    private int y;
    private int size;
    private String text;
    private List<Point> nodes;
    private int nodeSize = 8;
    public Color color;
    public boolean selected = false;
    public boolean editing = false;
//    public boolean text_in = false;

    public Square(int x, int y, int size, String text, String color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.text = text; // Default empty text
        this.color = Color.decode(color);

        nodes = new ArrayList<>();
        nodes.add(new Point(x + size / 2, y));           // Top node
        nodes.add(new Point(x + size, y + size / 2)); // Right node
        nodes.add(new Point(x + size / 2, y + size)); // Bottom node
        nodes.add(new Point(x, y + size / 2));           // Left node

        nodes.add(new Point(x, y));                         // Top left node
        nodes.add(new Point(x + size, y));               // Top right node
        nodes.add(new Point(x + size, y + size));     // Bottom right node
        nodes.add(new Point(x, y + size));               // Bottom Left node
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getSize(){
        return size;
    }
    public String getColor(){
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        String hex = String.format("#%02X%02X%02X", red, green, blue);
        return hex;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
//        text_in = true;
    }
    private List<String> splitTextIntoLines(String text, int maxChars) {
        List<String> lines = new ArrayList<>();

        // Split the text into lines that fit within the specified character limit
        int start = 0;
        while (start < text.length()) {
            int end = start + maxChars;
            if (end >= text.length()) {
                // Reached the end of the text
                lines.add(text.substring(start));
                break;
            } else {
                // Find the nearest space character before the end
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    lines.add(text.substring(start, lastSpace));
                    start = lastSpace + 1; // Skip the space
                } else {
                    // No space found, split at the specified character limit
                    lines.add(text.substring(start, end));
                    start = end;
                }
            }
        }

        return lines;
    }


    public void drawSquare(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, size, size);

        g.setColor(Color.BLACK);

        // the maximum number of characters that can fit inside the square
        int maxChars = size / g.getFontMetrics().charWidth('W');
        List<String> lines = splitTextIntoLines(text, maxChars);

        // Draw each line centered within the square
        int lineHeight = g.getFontMetrics().getHeight();
        int startY = y + (size - lines.size() * lineHeight) / 2 + g.getFontMetrics().getAscent();

        for (String line : lines) {
            int textX = x + (size - g.getFontMetrics().stringWidth(line)) / 2;
            g.drawString(line, textX, startY);
            startY += lineHeight;
        }
    }
    public void drawNodes(Graphics g, Point selectedNode) {
        if (this.editing){return;}
        g.setColor(Color.RED);
        for (Point node : nodes) {
            if (node.equals(selectedNode)) {
                // Highlight the selected node with a different color
                nodeSize = 10;
                g.setColor(new Color(8, 228, 236));
            }
            g.fillOval(node.x - nodeSize / 2, node.y - nodeSize / 2, nodeSize, nodeSize);
            g.setColor(Color.RED);
            nodeSize = 8;
        }
    }

    public Point getNodeAt(int px, int py) {
        int nodeLarger = 6;
        nodeSize += nodeLarger; // making the node bigger for easier clicking
        for (Point node : nodes) {
            if (px >= node.x - nodeSize / 2 && px <= node.x + nodeSize / 2 &&
                    py >= node.y - nodeSize / 2 && py <= node.y + nodeSize / 2) {
                nodeSize -= nodeLarger;
                return node;
            }
        }
        nodeSize -= nodeLarger;
        return null;
    }
    public List<Point> getNodes() {
        return nodes;
    }

    public boolean contains(int px, int py) {
        return px >= x && px <= x + size && py >= y && py <= y + size;
    }
    // ---------------------- RESIZING ----------------------------------------------
    public void resizeSquare(int bo) {
        if (editing) {
            if (size + bo*5 < 20){return;} // ensuring square doesnt get too small
            size += bo*5; // bo is either 1 or -1

            nodes.get(0).x = x + size/ 2;
            nodes.get(0).y = y;
            nodes.get(1).x = x + size;
            nodes.get(1).y = y + size / 2;
            nodes.get(2).x = x + size/ 2;
            nodes.get(2).y = y + size;
            nodes.get(3).x = x;
            nodes.get(3).y = y + size / 2;

            nodes.get(4).x = x;
            nodes.get(4).y = y;
            nodes.get(5).x = x + size;
            nodes.get(5).y = y;
            nodes.get(6).x = x + size;
            nodes.get(6).y = y + size;
            nodes.get(7).x = x;
            nodes.get(7).y = y + size;

        }
    }
    public void drawBorder(Graphics g) {
        if (selected) {
            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size);
        }
        if (editing) {
            g.setColor(Color.RED);
            g.drawRect(x, y, size, size);
        }
    }
}
