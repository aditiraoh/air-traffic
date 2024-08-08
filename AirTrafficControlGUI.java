import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

class Airport {
    String name;
    java.util.List<Flight> flights;

    public Airport(String name) {
        this.name = name;
        this.flights = new java.util.ArrayList<>();
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }
}

class Flight {
    Airport source;
    Airport destination;
    int distance;

    public Flight(Airport source, Airport destination, int distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }
}

public class AirTrafficControlGUI extends JFrame {
    private JTextField airportCountField;
    private java.util.List<JTextField> airportNameFields;
    private java.util.List<JTextField> distanceFields;
    private JButton submitButton;
    private JTextArea resultArea;
    private JPanel inputPanel;
    private JPanel distancePanel;
    private int airportCount;
    private java.util.List<Airport> airports;

    public AirTrafficControlGUI() {
        setTitle("Air Traffic Control");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2));

        airportCountField = new JTextField();
        inputPanel.add(new JLabel("Enter the number of airports: "));
        inputPanel.add(airportCountField);

        submitButton = new JButton("Submit");
        inputPanel.add(submitButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                airportCount = Integer.parseInt(airportCountField.getText());
                airportNameFields = new java.util.ArrayList<>();

                inputPanel.removeAll();

                for (int i = 1; i <= airportCount; i++) {
                    JTextField airportNameField = new JTextField();
                    airportNameFields.add(airportNameField);
                    inputPanel.add(new JLabel("Enter the name of Airport " + i + ": "));
                    inputPanel.add(airportNameField);
                }

                JButton nextButton = new JButton("Next");
                inputPanel.add(nextButton);

                nextButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        airports = new java.util.ArrayList<>();
                        for (JTextField field : airportNameFields) {
                            airports.add(new Airport(field.getText()));
                        }

                        showDistanceEntryPanel();
                    }
                });

                inputPanel.revalidate();
                inputPanel.repaint();
            }
        });
    }

    private void showDistanceEntryPanel() {
        distancePanel = new JPanel();
        distancePanel.setLayout(new GridLayout(0, 2));
        distanceFields = new java.util.ArrayList<>();

        for (int i = 0; i < airportCount; i++) {
            for (int j = i + 1; j < airportCount; j++) {
                JTextField distanceField = new JTextField();
                distanceFields.add(distanceField);
                distancePanel.add(new JLabel("Enter the distance from " + airports.get(i).name + " to " + airports.get(j).name + ": "));
                distancePanel.add(distanceField);
            }
        }

        JButton calculateButton = new JButton("Calculate MST");
        distancePanel.add(calculateButton);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int k = 0;
                for (int i = 0; i < airportCount; i++) {
                    for (int j = i + 1; j < airportCount; j++) {
                        int distance = Integer.parseInt(distanceFields.get(k++).getText());
                        airports.get(i).addFlight(new Flight(airports.get(i), airports.get(j), distance));
                        airports.get(j).addFlight(new Flight(airports.get(j), airports.get(i), distance));
                    }
                }

                java.util.List<Flight> mst = findMinimumSpanningTree(airports);

                resultArea.setText("Minimum Spanning Tree Flights:\n");
                for (Flight flight : mst) {
                    resultArea.append("From: " + flight.source.name + " To: " + flight.destination.name + " Distance: " + flight.distance + "\n");
                }
            }
        });

        remove(inputPanel);
        add(distancePanel, BorderLayout.NORTH);
        distancePanel.revalidate();
        distancePanel.repaint();
    }

    public static java.util.List<Flight> findMinimumSpanningTree(java.util.List<Airport> airports) {
        PriorityQueue<Flight> minHeap = new PriorityQueue<>(java.util.Comparator.comparingInt(flight -> flight.distance));
        Set<Airport> visitedAirports = new HashSet<>();
        Airport startAirport = airports.get(0);

        minHeap.addAll(startAirport.flights);
        visitedAirports.add(startAirport);
        java.util.List<Flight> mst = new java.util.ArrayList<>();

        while (!minHeap.isEmpty()) {
            Flight shortestFlight = minHeap.poll();
            Airport nextAirport = shortestFlight.destination;

            if (!visitedAirports.contains(nextAirport)) {
                visitedAirports.add(nextAirport);
                mst.add(shortestFlight);
                minHeap.addAll(nextAirport.flights);
            }
        }

        return mst;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AirTrafficControlGUI().setVisible(true);
            }
        });
    }
}
