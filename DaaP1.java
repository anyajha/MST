import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

class MSTAppAdvanced extends JFrame {
    private GraphPanel graphPanel;
    private JTextArea resultArea;
    private JTextField vertex1Field, vertex2Field, weightField;
    private JButton addEdgeButton, computeButton;
    private JComboBox<String> algorithmDropdown;
    private Map<String, Point> vertexCoordinates = new HashMap<>();

    public MSTAppAdvanced() {
        setTitle("Minimum Spanning Tree Solver");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        graphPanel = new GraphPanel();
        resultArea = new JTextArea(10, 30);
        vertex1Field = new JTextField(5);
        vertex2Field = new JTextField(5);
        weightField = new JTextField(3);
        addEdgeButton = new JButton("Add Edge");
        algorithmDropdown = new JComboBox<>(new String[]{"Kruskal's Algorithm", "Prim's Algorithm"});
        computeButton = new JButton("Compute MST");

        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Vertex 1:"));
        controlPanel.add(vertex1Field);
        controlPanel.add(new JLabel("Vertex 2:"));
        controlPanel.add(vertex2Field);
        controlPanel.add(new JLabel("Weight:"));
        controlPanel.add(weightField);
        controlPanel.add(addEdgeButton);
        controlPanel.add(new JLabel("Select Algorithm:"));
        controlPanel.add(algorithmDropdown);
        controlPanel.add(computeButton);
        add(controlPanel, BorderLayout.NORTH);

        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        MSTAlgorithms algorithms = new MSTAlgorithms();

        addEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vertex1 = vertex1Field.getText();
                String vertex2 = vertex2Field.getText();
                int weight = Integer.parseInt(weightField.getText());
                graphPanel.addEdge(vertex1, vertex2, weight);
                vertexCoordinates.put(vertex1, new Point((int)(Math.random() * graphPanel.getWidth()), (int)(Math.random() * graphPanel.getHeight())));
                vertexCoordinates.put(vertex2, new Point((int)(Math.random() * graphPanel.getWidth()), (int)(Math.random() * graphPanel.getHeight())));
                vertex1Field.setText("");
                vertex2Field.setText("");
                weightField.setText("");
                graphPanel.setVertexCoordinates(vertexCoordinates);
            }
        });

        computeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String algorithm = (String) algorithmDropdown.getSelectedItem();
                List<Edge> mst = new ArrayList<>();

                if (algorithm.equals("Kruskal's Algorithm")) {
                    mst = algorithms.kruskalsAlgorithm(graphPanel.getEdges());
                } else if (algorithm.equals("Prim's Algorithm")) {
                    mst = algorithms.primsAlgorithm(graphPanel.getEdges());
                }

                resultArea.setText("Minimum Spanning Tree:\n" + mst);

                // Display MST graphically by updating the GraphPanel
                graphPanel.setMST(mst);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MSTAppAdvanced app = new MSTAppAdvanced();
            app.setVisible(true);
        });
    }
}

class GraphPanel extends JPanel {
    private List<Edge> edges = new ArrayList<>();
    private List<Edge> mst = new ArrayList<>();
    private Map<String, Point> vertexCoordinates = new HashMap<>();

    public void addEdge(String vertex1, String vertex2, int weight) {
        if (!vertex1.isEmpty() && !vertex2.isEmpty()) {
            edges.add(new Edge(vertex1, vertex2, weight));
            repaint();
        }
    }

    public void setVertexCoordinates(Map<String, Point> vertexCoordinates) {
        this.vertexCoordinates = vertexCoordinates;
        repaint();
    }

    public void setMST(List<Edge> mst) {
        this.mst = mst;
        repaint();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        for (Edge edge : edges) {
            Point point1 = vertexCoordinates.get(edge.getVertex1());
            Point point2 = vertexCoordinates.get(edge.getVertex2());

            if (point1 != null && point2 != null) {
                g2d.setColor(Color.BLACK); // Set color for regular edges
                g2d.draw(new Line2D.Double(point1.getX(), point1.getY(), point2.getX(), point2.getY()));

                // Draw edge weight at the midpoint of the edge
                int textX = (int) (point1.getX() + point2.getX()) / 2;
                int textY = (int) (point1.getY() + point2.getY()) / 2;
                g2d.drawString(String.valueOf(edge.getWeight()), textX, textY - 10);

                // Draw vertex labels separately on the vertices
                g2d.drawString(edge.getVertex1(), (int) point1.getX() - 5, (int) point1.getY() - 10);
                g2d.drawString(edge.getVertex2(), (int) point2.getX() - 5, (int) point2.getY() - 10);
            }
        }


        // Draw MST edges in red
        g2d.setColor(Color.RED);
        for (Edge edge : mst) {
            Point point1 = vertexCoordinates.get(edge.getVertex1());
            Point point2 = vertexCoordinates.get(edge.getVertex2());

            if (point1 != null && point2 != null) {
                g2d.draw(new Line2D.Double(point1.getX(), point1.getY(), point2.getX(), point2.getY()));
            }
        }

        // Draw solid black circles at vertices
        g2d.setColor(Color.BLACK);
        for (Point point : vertexCoordinates.values()) {
            int circleDiameter = 20; // Diameter of the circle
            g2d.fillOval((int) point.getX() - circleDiameter / 2, (int) point.getY() - circleDiameter / 2, circleDiameter, circleDiameter);
        }
    }
}

class Edge {
    private String vertex1;
    private String vertex2;
    private int weight;

    public Edge(String vertex1, String vertex2, int weight) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.weight = weight;
    }

    public String getVertex1() {
        return vertex1;
    }

    public String getVertex2() {
        return vertex2;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return vertex1 + " - " + vertex2 + " (" + weight + ")";
    }
}

class MSTAlgorithms {
    public List<Edge> kruskalsAlgorithm(List<Edge> edges) {
        // Implement Kruskal's algorithm
        // Sort edges by weight, then add them to the MST while avoiding cycles
        List<Edge> mst = new ArrayList<>();
        edges.sort(Comparator.comparing(Edge::getWeight));

        Map<String, String> parent = new HashMap<>();
        for (Edge edge : edges) {
            String v1 = edge.getVertex1();
            String v2 = edge.getVertex2();

            if (find(parent, v1).equals(find(parent, v2)))
            {
                continue;
            }

            mst.add(edge);
            union(parent, v1, v2);
        }
        return mst;
    }

    private String find(Map<String, String> parent, String vertex) {
        if (!parent.containsKey(vertex)) {
            parent.put(vertex, vertex);
        }

        if (!vertex.equals(parent.get(vertex))) {
            parent.put(vertex, find(parent, parent.get(vertex)));
        }
        return parent.get(vertex);
    }

    private void union(Map<String, String> parent, String vertex1, String vertex2) {
        String root1 = find(parent, vertex1);
        String root2 = find(parent, vertex2);
        parent.put(root1, root2);
    }

    public List<Edge> primsAlgorithm(List<Edge> edges) {
        // Implement Prim's algorithm
        // Start with an arbitrary vertex, add the minimum-weight edge to the MST
        // and continue until all vertices are included
        List<Edge> mst = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        String startVertex = edges.get(0).getVertex1();
        visited.add(startVertex);








        while (visited.size() < edges.size()) {
            Edge minEdge = null;
            for (Edge edge : edges) {
                if (visited.contains(edge.getVertex1()) && !visited.contains(edge.getVertex2())) {
                    if (minEdge == null || edge.getWeight() < minEdge.getWeight()) {
                        minEdge = edge;
                    }
                } else if (visited.contains(edge.getVertex2()) && !visited.contains(edge.getVertex1())) {
                    if (minEdge == null || edge.getWeight() < minEdge.getWeight()) {
                        minEdge = edge;
                    }
                }
            }

            if (minEdge != null) {
                mst.add(minEdge);
                if (!visited.contains(minEdge.getVertex1())) {
                    visited.add(minEdge.getVertex1());
                }
                if (!visited.contains(minEdge.getVertex2())) {
                    visited.add(minEdge.getVertex2());
                }
            }
        }
        return mst;
    }
}