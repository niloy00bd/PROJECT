// Import Swing classes for GUI components (JFrame, JPanel, JButton, JComboBox, etc.)
import javax.swing.*; // GUI toolkit for desktop apps
// Import AWT classes for drawing and layout (Graphics, Color, Layout managers)
import java.awt.*; // Low-level drawing and component layout
// Import event packages for handling actions and mouse events
import java.awt.event.*; // ActionListener, MouseAdapter, MouseEvent
// Import IO classes for binary file read/write
import java.io.*; // FileInputStream, FileOutputStream, DataInputStream, DataOutputStream, IOException, EOFException
// Import utility classes (Arrays used for convenience)
import java.util.*; // General utilities

// Main application window class; extends JFrame to create the app window
public class RestaurantOrderSystem extends JFrame { // Application class

    // ===== Fixed menu constants (final to ensure they don't change at runtime) =====
    public static final String[] MENU_NAMES = { "Coffee", "Tea", "Burger", "Pizza", "Pasta", "Salad", "Juice" }; // Menu item names
    public static final double[] MENU_PRICES = { 120.0, 100.0, 350.0, 550.0, 400.0, 250.0, 150.0 }; // Menu item prices

    // ===== Status color constants for drawing tables =====
    public static final Color COLOR_AVAILABLE = Color.GREEN; // Available = green
    public static final Color COLOR_PENDING = Color.YELLOW;  // Pending = yellow
    public static final Color COLOR_SERVED = Color.RED;      // Served = red

    // ===== Binary data file name =====
    private static final String DATA_FILE = "restaurant_orders.bin"; // File for persistence

    // ===== Floor layout and tables =====
    private Table[] tables; // Array holding all tables on the floor. 
    private int rows = 2;   // Number of rows on the floor (editable)
    private int cols = 5;   // Number of columns on the floor (editable)

    // ===== GUI components =====
    private FloorPanel floorPanel;        // Custom panel to draw the floor and tables
    
    private JComboBox<String> menuCombo;  // Dropdown to select a menu item
    
    private JSpinner qtySpinner;          // Spinner to set quantity
    private JComboBox<Integer> tableCombo;// Dropdown to select a table ID
    private JButton addOrderBtn;          // Button to add an order
    private JButton serveBtn;             // Button to mark a table served
    private JButton clearBtn;             // Button to clear a table
    private JButton totalBtn;            // Button to show total bill
    private JTextArea orderArea;          // Text area to show summary of orders
    
    // Constructor: builds UI, initializes tables, loads data, wires events
    public RestaurantOrderSystem() { // Window constructor
        super("Restaurant Order System"); // Set window title

        tables = new Table[rows * cols]; // Create array for all tables
        
        for (int i = 0; i < tables.length; i++) { // Initialize each table
            tables[i] = new Table(i + 1); // Assign human-friendly IDs starting at 1
        }

        floorPanel = new FloorPanel(); // Create drawing panel
        
        menuCombo = new JComboBox<>(MENU_NAMES); // Menu dropdown with names
        
        qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1)); // Quantity spinner: start 0, min 0, max 20, step 1
        
        tableCombo = new JComboBox<>(); // Table selection dropdown
        for (Table t : tables) { // Populate table combo with table IDs
            tableCombo.addItem(t.tableId); // Add each table's ID
        }
        
        addOrderBtn = new JButton("Add Order"); // Button to add order
        serveBtn = new JButton("Mark Served");  // Button to mark served
        clearBtn = new JButton("Clear Table");  // Button to clear a table
        totalBtn = new JButton("Show Total Bill"); //button to show bill
        orderArea = new JTextArea(10, 30);      // Text area for summary
        
        orderArea.setEditable(false);           // Make summary read-only
        
        setLayout(new BorderLayout()); // Use BorderLayout for main layout
        
        JPanel controlPanel = new JPanel(); // Panel to hold controls

        controlPanel.setLayout(new GridBagLayout()); // Flexible grid layout
        
        GridBagConstraints gbc = new GridBagConstraints(); // Constraints for GridBag
        
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around controls
        gbc.anchor = GridBagConstraints.WEST; // Left-align labels/controls

        gbc.gridx = 0; gbc.gridy = 0; controlPanel.add(new JLabel("Menu:"), gbc); // Menu label
        gbc.gridx = 1; controlPanel.add(menuCombo, gbc); // Menu dropdown
        
        gbc.gridx = 2; controlPanel.add(new JLabel("Qty:"), gbc); // Quantity label

        gbc.gridx = 3; controlPanel.add(qtySpinner, gbc); // Quantity spinner
        
        gbc.gridx = 0; gbc.gridy = 1; controlPanel.add(new JLabel("Table:"), gbc); // Table label (Row=1, Column=0)
        gbc.gridx = 1; controlPanel.add(tableCombo, gbc); // Table dropdown

        gbc.gridx = 0; gbc.gridy = 2; controlPanel.add(addOrderBtn, gbc); // Add order button
        gbc.gridx = 1; controlPanel.add(serveBtn, gbc);                   // Mark served button
        gbc.gridx = 2; controlPanel.add(clearBtn, gbc);                   // Clear table button
        gbc.gridx = 3; controlPanel.add(totalBtn, gbc);                   // Place next to other buttons

        JPanel rightPanel = new JPanel(new BorderLayout()); // Right side panel. etake amra pore right side e place korbo

        rightPanel.add(new JLabel("Orders Summary:"), BorderLayout.NORTH); // Summary label
        rightPanel.add(new JScrollPane(orderArea), BorderLayout.CENTER);   // Scrollable text area

        add(controlPanel, BorderLayout.NORTH);  // Place controls at the top
        add(floorPanel, BorderLayout.CENTER);   // Center drawing panel
        add(rightPanel, BorderLayout.EAST);     // Summary on the right

        addOrderBtn.addActionListener(e -> { // Add order button handler
            int tableId = (Integer) tableCombo.getSelectedItem(); // Get selected table ID
            
            int qty = (Integer) qtySpinner.getValue();            // Get quantity
            int menuIndex = menuCombo.getSelectedIndex();         // Get selected menu index
            String name = MENU_NAMES[menuIndex];                  // Resolve item name
            double price = MENU_PRICES[menuIndex];                // Resolve item price
            
            Table t = getTableById(tableId);                      // Find table by ID
            if (t == null) return;                                // Safety: if not found, exit
            
            MenuItem item = new MenuItem(name, price);            // Create menu item
            Order order = new Order(item, qty);                   // Create order
            
            if (qty != 0) {                                         // If table has orders
                t.orders.append(order);                            // Append to table's order list
                t.status = TableStatus.PENDING;                   // Set table status to pending

                saveAll();                                        // Save state to binary file
                refreshUI();                                      // Refresh UI and summary
            } else {
                JOptionPane.showMessageDialog(this, "No orders for this table."); // Info message
            }
        });

        serveBtn.addActionListener(e -> { // Mark served handler
            int tableId = (Integer) tableCombo.getSelectedItem(); // Get selected table ID
            Table t = getTableById(tableId);                      // Find table
            if (t == null) return;                                // Safety

            if (!t.orders.isEmpty()) {                            // If table has orders
                t.status = TableStatus.SERVED;                    // Mark as served
                saveAll();                                        // Persist
                refreshUI();                                      // Refresh UI
            } else {
                JOptionPane.showMessageDialog(this, "No orders for this table."); // Info message
            }
        });

        clearBtn.addActionListener(e -> { // Clear table handler
            int tableId = (Integer) tableCombo.getSelectedItem(); // Get selected table ID
            Table t = getTableById(tableId);                      // Find table
            if (t == null) return;                                // Safety

            t.orders.clear();                                     // Remove all orders
            t.status = TableStatus.AVAILABLE;                     // Reset status to available
            saveAll();                                            // Persist
            refreshUI();                                          // Refresh UI
        });

        totalBtn.addActionListener(e -> { // Total bill handler
            int tableId = (Integer) tableCombo.getSelectedItem(); // Get selected table
            Table t = getTableById(tableId); // Find table object
            if (t == null || t.orders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No orders for this table.");
                return;
            }

            double total = 0.0;
            DoublyLinkedList.Node<Order> cur = t.orders.head; // Traverse order list

            while (cur != null) {
                Order o = cur.data;
                total += o.item.price * o.quantity; // Add item total
                cur = cur.next;
            }

            JOptionPane.showMessageDialog(this,
                "Total bill for Table " + tableId + " is ৳" + total,
                "Bill Summary", JOptionPane.INFORMATION_MESSAGE);

        });

        loadAll(); // Load persisted data on startup

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app when window closes
        setSize(1000, 600);                             // Window size
        setLocationRelativeTo(null);                    // Center window on screen
        setVisible(true);                               // Show window
    } // End of class constructor

    // Helper to find a table object by its ID
    private Table getTableById(int id) { // Linear search by ID
        for (Table t : tables) { // Iterate tables
            if (t.tableId == id) return t; // Return matched table object from array list of tables(table object gula tables array te ache)
        }
        return null; // Not found
    }

    // Refresh drawing and text summary
    private void refreshUI() { // Central refresh method
        floorPanel.repaint();                 // Redraw floor and tables
        orderArea.setText(buildSummaryText()); // Update order summary text
    }

    // Build a human-readable summary of all tables and their orders
    private String buildSummaryText() { // Compose summary
        StringBuilder sb = new StringBuilder(); // Efficient string builder
        for (Table t : tables) { // For each table
            sb.append("Table ").append(t.tableId) // Table header
              .append(" — Status: ").append(t.status) // Status
              .append("\n"); // Newline
            if (t.orders.isEmpty()) { // If no orders
                sb.append("  (No orders)\n"); // Show empty info
            } else {
                // Traverse linked list using the correct static nested type reference
                DoublyLinkedList.Node<Order> cur = t.orders.head; // Start at head
                while (cur != null) { // Traverse nodes
                    Order o = cur.data; // Extract order object
                    sb.append("  - ")   // Bullet
                      .append(o.item.name) // Item name
                      .append(" x").append(o.quantity) // Quantity
                      .append(" @ ").append(o.item.price) // Unit price
                      .append("\n"); // Newline
                    cur = cur.next; // Move to next
                }
            }
            sb.append("\n"); // Blank line between tables
        }
        return sb.toString(); // Return summary
    }

    // Save all tables and orders to a binary file
    private void saveAll() { // Persistence writer
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(DATA_FILE))) { // Open output stream
            dos.writeInt(rows);            // Write row count
            dos.writeInt(cols);            // Write column count
            dos.writeInt(tables.length);   // Write total tables
            for (Table t : tables) {       // For each table
                dos.writeInt(t.tableId);                 // Write table ID
                dos.writeInt(t.status.ordinal());        // Write status ordinal (int)

                dos.writeInt(t.orders.size());           // Write number of orders
                DoublyLinkedList.Node<Order> cur = t.orders.head; // Traverse orders
                while (cur != null) {                    // Loop through each order
                    Order o = cur.data;                  // Access order
                    dos.writeUTF(o.item.name);           // Write item name
                    dos.writeDouble(o.item.price);       // Write item price
                    dos.writeInt(o.quantity);            // Write quantity
                    cur = cur.next;                      // Next order
                }
            }
        } catch (IOException e) { // Handle IO errors
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage()); // Show error
        }
    }

    // Load tables and orders from the binary file
    private void loadAll() { // Persistence reader
        File f = new File(DATA_FILE); // Create file object to check existence
        if (!f.exists()) {            // If file doesn't exist
            refreshUI();              // Show empty layout
            return;                   // Exit load
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(DATA_FILE))) { // Open input stream
            rows = dis.readInt();              // Read row count
            cols = dis.readInt();              // Read column count
            int total = dis.readInt();         // Read total tables
            if (total != tables.length) {      // If saved layout size differs
                tables = new Table[total];     // Resize tables array
                for (int i = 0; i < total; i++) { // Create tables
                    tables[i] = new Table(i + 1);  // Assign IDs
                }
                tableCombo.removeAllItems();   // Refresh table combo list
                for (Table t : tables) {       // Repopulate table IDs
                    tableCombo.addItem(t.tableId); // Add ID
                }
            }

            for (int i = 0; i < tables.length; i++) { // Read table data
                int id = dis.readInt();                        // Read table ID

                int statusOrd = dis.readInt();                 // Read status ordinal
                int orderCount = dis.readInt();                // Read order count
                Table t = getTableById(id);                    // Get table object by ID
                if (t == null) {                               // If not found
                    t = new Table(id);                         // Create new table
                    tables[i] = t;                             // Place in array
                }
                t.status = TableStatus.values()[statusOrd];    // Restore status
                t.orders.clear();                              // Clear previous orders
                for (int k = 0; k < orderCount; k++) {         // Read each order
                    String name = dis.readUTF();               // Read item name
                    double price = dis.readDouble();           // Read item price
                    int qty = dis.readInt();                   // Read quantity
                    MenuItem item = new MenuItem(name, price); // Recreate menu item
                    Order order = new Order(item, qty);        // Recreate order
                    t.orders.append(order);                    // Append to list
                }
            }
            refreshUI(); // Update UI after load
        } catch (IOException e) { // Handle IO errors
            JOptionPane.showMessageDialog(this, "Error loading: " + e.getMessage()); // Show error
        }
    }

    // Custom panel that draws the restaurant tables on the floor
    private class FloorPanel extends JPanel { // Drawing panel
        public FloorPanel() { // Panel constructor
            setPreferredSize(new Dimension(600, 400)); // Preferred size for drawing area
            setBackground(Color.WHITE);                // White background

            addMouseListener(new MouseAdapter() { // Add mouse click handler
                @Override
                public void mouseClicked(MouseEvent e) { // On mouse click
                    int x = e.getX(); // Click X coordinate
                    int y = e.getY(); // Click Y coordinate
                    int index = hitTestTable(x, y); // Determine clicked table index
                    if (index != -1) { // If a table was clicked
                        tableCombo.setSelectedItem(tables[index].tableId); // Select it in dropdown
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) { // Paint callback for drawing
            super.paintComponent(g); // Clear background
            int padding = 20; // Space around edges
            int cellW = (getWidth() - 2 * padding) / cols;  // Cell width per column
            int cellH = (getHeight() - 2 * padding) / rows; // Cell height per row
            int tableSize = Math.min(cellW, cellH) - 30;    // Table circle size
            
            for (int r = 0; r < rows; r++) { // For each row
                for (int c = 0; c < cols; c++) { // For each column
                    int index = r * cols + c;          // Compute table index
                    
                    if (index >= tables.length) break; // Guard mismatches
                    Table t = tables[index];           // Get table

                    Color fill = COLOR_AVAILABLE; // Default green set hobe
                    
                    if (t.status == TableStatus.PENDING) fill = COLOR_PENDING; // Pending = yellow
                    else if (t.status == TableStatus.SERVED) fill = COLOR_SERVED; // Served = red

                    int cx = padding + c * cellW + cellW / 2; // Center X of cell
                    int cy = padding + r * cellH + cellH / 2; // Center Y of cell
                    
                    int x = cx - tableSize / 2;               // Top-left X of circle
                    int y = cy - tableSize / 2;               // Top-left Y of circle
                    
                    g.setColor(fill); // Set fill color
                    g.fillOval(x, y, tableSize, tableSize); // Draw filled circle
                    // circle আঁকা হচ্ছে। ebong circle-এর ভিতর রঙ করা হচ্ছে।

                    g.setColor(Color.DARK_GRAY); // Outline color
                    g.drawOval(x, y, tableSize, tableSize); // Draw outline
                    // circle-এর বাইরের রেখা আঁকা হচ্ছে।

                    g.setColor(Color.BLACK); // Text color
                    String label = "TABLE : " + t.tableId; // Table label text
                    FontMetrics fm = g.getFontMetrics(); // Measure text
                    int tx = cx - fm.stringWidth(label) / 2; // Center X for text
                    int ty = cy + fm.getAscent() / 2 - 2;    // Center Y for text
                    g.drawString(label, tx, ty); // Draw label
                }
            }
        }

        // Determine which table (if any) contains the clicked point
        private int hitTestTable(int x, int y) { // Returns table index or -1
            int padding = 20; // Same padding as paint
            int cellW = (getWidth() - 2 * padding) / cols; // Cell width
            int cellH = (getHeight() - 2 * padding) / rows; // Cell height
            int tableSize = Math.min(cellW, cellH) - 30; // Circle size
            int radius = tableSize / 2; // Circle radius

            for (int r = 0; r < rows; r++) { // Iterate rows
                for (int c = 0; c < cols; c++) { // Iterate cols
                    int index = r * cols + c; // Index in array
                    if (index >= tables.length) break; // Guard mismatch
                    int cx = padding + c * cellW + cellW / 2; // Center X
                    int cy = padding + r * cellH + cellH / 2; // Center Y
                    int dx = x - cx; // X delta from center
                    int dy = y - cy; // Y delta from center
                    if (dx * dx + dy * dy <= radius * radius) { // Point inside circle?
                        return index; // Return table index
                    }
                }
            }
            return -1; // No hit
        }
    }

    // Model class for a table
    private static class Table { // Holds status and orders
        int tableId;                            // Unique table ID
        TableStatus status = TableStatus.AVAILABLE; // Default status
        DoublyLinkedList<Order> orders = new DoublyLinkedList<>(); // Order list per table

        Table(int id) { // Constructor
            this.tableId = id; // Set ID
        }
    }

    private enum TableStatus { // Used for state and persistence via ordinal
        AVAILABLE, // No active orders
        PENDING,   // Orders placed, not served
        SERVED     // Orders served
    }

    // Model class for menu item
    private static class MenuItem { // Immutable menu item
        final String name;  // Name of the item
        final double price; // Price of the item

        MenuItem(String name, double price) { // Constructor
            this.name = name;  // Set name
            this.price = price; // Set price
        }
    }

    // Model class for an order (item + quantity)
    private static class Order { // Immutable order
        final MenuItem item; // Ordered item
        final int quantity;  // Quantity ordered

        Order(MenuItem item, int quantity) { // Constructor
            this.item = item;     // Set item
            this.quantity = quantity; // Set quantity
        }
    }

    // Generic doubly linked list implementation for storing orders
    private static class DoublyLinkedList<T> { // Minimal generic list
        // Static nested node class; use a separate generic type parameter to avoid capture issues

        static class Node<E> { // Linked list node
            E data;        // Payload data
            Node<E> prev;  // Previous node
            Node<E> next;  // Next node
            Node(E d) { this.data = d; } // Node constructor
        }

        Node<T> head; // Head (first) node reference
        Node<T> tail; // Tail (last) node reference
        int size = 0; // Count of nodes

        void append(T data) { // Add new element to the end
            Node<T> n = new Node<>(data); // Create a node carrying data
            if (head == null) {           // If list is empty
                head = tail = n;          // Head and tail point to the new node
            } else {
                tail.next = n;            // Link old tail to new node
                n.prev = tail;            // Back-link new node to old tail
                tail = n;                 // Move tail to the new node
            }
            size++; // Increase size
        }


        boolean isEmpty() { // Check if list has no elements
            return size == 0; // True when size is zero
        }

        int size() { // Return number of elements
            return size; // Current size
        }

        void clear() { // Remove all elements
            head = tail = null; // Drop references so GC can collect
            size = 0;           // Reset size
        }
    }

    // Application entry point sets up UI on the Event Dispatch Thread
    public static void main(String[] args) { // Main method
        SwingUtilities.invokeLater(RestaurantOrderSystem::new); // Create and show app
    }
}
