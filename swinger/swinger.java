import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class swinger {

    private final static String version = "v0.0.1";
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception exception) {
            System.out.printf("UIManager.setLookAndFeel failed, err: %s\n)", exception.getMessage());
            System.exit(1);
        }
        SwingUtilities.invokeLater(swinger::createAndShowGUI);
    }
    
    private static void createAndShowGUI() {

        // Create the outermost frame (aka main window) and set some characteristics.
        JFrame frame = new JFrame("Swinger" + version);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Create the top panel with 2 JLabels: current Java FQN, input file path.
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20)); // top, left, bottom, right margins
        JLabel fqnLabel = new JLabel("Method: java/lang/Double.toHexString(D)Ljava/lang/String;");
        JLabel fileLabel = new JLabel("File: Agedashi.Tofu", SwingConstants.RIGHT);
        topPanel.add(fqnLabel, BorderLayout.WEST);
        topPanel.add(fileLabel, BorderLayout.EAST);

        // Create the vertically-centered JPanel (centerPanel) with a defined border.
        // centerPanel will hold 4 JList objects: codeList, opStackList, localsList, and frameStackList.
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // top, left, bottom, right margins
        centerContainer.add(centerPanel, BorderLayout.CENTER);

        /*
        The grid bag object act as constraints for the scroll panels in centerPanel.
        GridBagConstraints object is used to specify how components are positioned and sized within a GridBagLayout
        when added to a container, such as a JPanel. The GridBagConstraints object holds a set of constraints that
        control the component's placement and resizing behavior in the layout grid.
        The fields:
        * gridx and gridy: Specify the grid position (row and column).
        * gridwidth and gridheight: Define how many columns or rows the component should span.
        * weightx and weighty: Define how the component grows when the container is resized.
        * anchor: Defines how the component is aligned within the grid cell (e.g., center, north, east, west).
        * fill: Determines whether the component should expand to fill the available space in the grid cell.
        */
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        
        // Create the codeList with DefaultListModel to allow dynamic changes.
        DefaultListModel<String> codeListModel = new DefaultListModel<>();
        codeListModel.addElement("0: dload_0");
        codeListModel.addElement("1: invokestatic #11");
        codeListModel.addElement("4: ifne 12");
        codeListModel.addElement("7: dload_0");
        codeListModel.addElement("8: invokestatic #15");
        codeListModel.addElement("11: areturn");
        codeListModel.addElement("12: new #16");
        codeListModel.addElement("15: dup");
        codeListModel.addElement("16: bipush 24");
        codeListModel.addElement("18: invokespecial #18");
        codeListModel.addElement("21: astore_2");
        codeListModel.addElement("22: dconst_1");
        codeListModel.addElement("23: dload_0");
        codeListModel.addElement("24: invokestatic #22");
        codeListModel.addElement("27: ldc2_w");
        codeListModel.addElement("30: dcmpl");
        codeListModel.addElement("31: ifne 41");
        codeListModel.addElement("34: aload_2");
        codeListModel.addElement("35: invokevirtual #28");
        codeListModel.addElement("38: areturn");
        JList<String> codeList = new JList<>(codeListModel);
        codeList.setVisibleRowCount(5);
        codeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create the opStackList with DefaultListModel to allow dynamic changes.
        DefaultListModel<String> opStackListModel = new DefaultListModel<>();
        opStackListModel.addElement("0");
        opStackListModel.addElement("-226.4");
        JList<String> opStackList = new JList<>(opStackListModel);
        opStackList.setVisibleRowCount(5);

        // Create the Locals JList object.
        JList<String> localsList = createList(new String[]{"420", "42", "0", "-226.4"}, 5);

        // Create the Frame Stack JList object.
        JList<String> frameStackList = createList(new String[]{
            "toHexString(D)Ljava/lang/String;",
                "format(D)Ljava/lang/String;",
                "<init>(D)", "<clinit>()",
                "main()"
        }, 5);
        
        // Set the first item as selected for all lists.
        codeList.setSelectedIndex(0);
        opStackList.setSelectedIndex(0);
        localsList.setSelectedIndex(0);
        frameStackList.setSelectedIndex(0);

        // Scroll to make the first selected item visible in the Code scroll pane..
        JScrollPane codeScrollPane = new JScrollPane(codeList);
        codeScrollPane.getVerticalScrollBar().setValue(0);

        // Add a listener for Code JList changes.
        // If this happens, the listener will add selected Code item to the OpStack JList and give it focus.
        // This is not what we want in Jacobin; it is simply an exercise.
        codeList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String selectedCode = codeList.getSelectedValue();
                opStackListModel.addElement(selectedCode);  // Add selected Code entry to Op Stack
                opStackList.setSelectedIndex(opStackListModel.getSize() - 1);  // Select newly added item
            }
        });

        // Add the scroll panes to the center panel as governed by the GridBagConstraint field definitions.
        // Documentation of GridBagConstraint can be found earlier in this source file.
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        centerPanel.add(wrapInPanel(codeScrollPane, "Code"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        centerPanel.add(wrapInPanel(new JScrollPane(opStackList), "Op Stack"), gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.25;
        centerPanel.add(wrapInPanel(new JScrollPane(localsList), "Locals"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 2;
        centerPanel.add(wrapInPanel(new JScrollPane(frameStackList), "Frame Stack"), gbc);

        // Create an Exit button and insert it into the bottom JPanel.
        JPanel bottomPanel = new JPanel();
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        bottomPanel.add(exitButton);

        // Finally, to the frame, add the top, center, and bottom panels.
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerContainer, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Start the show!
        frame.setVisible(true);
    }

    // Create a JList with the specified data, governed by the specified visible row count.
    private static JList<String> createList(String[] data, int visibleRowCount) {
        JList<String> list = new JList<>(data);
        list.setVisibleRowCount(visibleRowCount);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }

    // Add a scroll pane to a JPanel.
    private static JPanel wrapInPanel(JScrollPane scrollPane, String label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(new JLabel(label, SwingConstants.CENTER), BorderLayout.SOUTH);
        return panel;
    }
}

