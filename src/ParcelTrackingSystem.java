import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ParcelTrackingSystem extends JFrame {
    private final List<Parcel> parcels = new ArrayList<>();
    private final List<String> history = new ArrayList<>();
    private final DefaultTableModel parcelModel = new DefaultTableModel(
            new String[]{"Tracking ID", "Sender", "Receiver", "Destination", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel userModel = new DefaultTableModel(
            new String[]{"Username", "Role"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel historyModel = new DefaultTableModel(
            new String[]{"Date & Time", "Activity"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private int nextId = 1001;
    private final JLabel totalLabel = new JLabel("Total parcels: 0");
    private final JLabel bookedLabel = new JLabel("Booked: 0");
    private final JLabel deliveredLabel = new JLabel("Delivered: 0");

    public ParcelTrackingSystem() {
        setTitle("Parcel Tracking System");
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(buildHeader(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", dashboardPanel());
        tabs.addTab("User Management", userPanel());
        tabs.addTab("Parcel Registration", registrationPanel());
        tabs.addTab("Delivery Management", deliveryPanel());
        tabs.addTab("Parcel Management", managementPanel());
        tabs.addTab("Report & History", reportPanel());
        add(tabs, BorderLayout.CENTER);
        userModel.addRow(new Object[]{"admin", "Administrator"});
        refreshAll();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(25, 55, 90));
        p.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JLabel title = new JLabel("PARCEL TRACKING SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        JLabel subtitle = new JLabel("Java Swing Desktop Application");
        subtitle.setForeground(new Color(220, 230, 240));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JPanel labels = new JPanel(new GridLayout(2, 1));
        labels.setOpaque(false);
        labels.add(title);
        labels.add(subtitle);
        p.add(labels, BorderLayout.WEST);
        return p;
    }

    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel welcome = new JLabel("Welcome to Parcel Tracking System");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.add(statCard(totalLabel, new Color(225, 239, 252)));
        cards.add(statCard(bookedLabel, new Color(255, 244, 218)));
        cards.add(statCard(deliveredLabel, new Color(222, 245, 229)));
        JTextArea note = new JTextArea(
                "Use the tabs to manage users, register parcels, update delivery status, " +
                "search parcel details, and view activity history.\\n\\n" +
                "Note: This starter version stores data in memory. Data will reset when the application closes.");
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setFont(new Font("SansSerif", Font.PLAIN, 15));
        note.setBackground(new Color(247, 249, 252));
        note.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        p.add(welcome, BorderLayout.NORTH);
        p.add(cards, BorderLayout.CENTER);
        p.add(note, BorderLayout.SOUTH);
        return p;
    }

    private JPanel statCard(JLabel label, Color color) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 230)));
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        card.add(label);
        return card;
    }

    private JPanel userPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JTable table = new JTable(userModel);
        JTextField username = new JTextField(15);
        JComboBox<String> role = new JComboBox<>(new String[]{"Administrator", "Staff", "Delivery Agent"});
        JButton add = new JButton("Add User");
        JButton remove = new JButton("Remove Selected");
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Username:")); form.add(username);
        form.add(new JLabel("Role:")); form.add(role); form.add(add); form.add(remove);
        add.addActionListener(e -> {
            String name = username.getText().trim();
            if (name.isEmpty()) { message("Enter a username."); return; }
            for (int i = 0; i < userModel.getRowCount(); i++) {
                if (userModel.getValueAt(i, 0).toString().equalsIgnoreCase(name)) {
                    message("Username already exists."); return;
                }
            }
            userModel.addRow(new Object[]{name, role.getSelectedItem()});
            username.setText("");
            log("User added: " + name);
        });
        remove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { message("Select a user to remove."); return; }
            if (userModel.getValueAt(row, 0).equals("admin")) { message("Default admin cannot be removed."); return; }
            String name = userModel.getValueAt(row, 0).toString();
            userModel.removeRow(row); log("User removed: " + name);
        });
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel registrationPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(9, 9, 9, 9);
        c.fill = GridBagConstraints.HORIZONTAL;
        JTextField sender = new JTextField(22);
        JTextField receiver = new JTextField(22);
        JTextField destination = new JTextField(22);
        JComboBox<String> status = new JComboBox<>(new String[]{"Booked", "Packed", "Shipped"});
        int y = 0;
        addFormRow(p, c, y++, "Sender Name:", sender);
        addFormRow(p, c, y++, "Receiver Name:", receiver);
        addFormRow(p, c, y++, "Destination:", destination);
        addFormRow(p, c, y++, "Initial Status:", status);
        JButton register = new JButton("Register Parcel");
        c.gridx = 1; c.gridy = y; c.weightx = 1;
        p.add(register, c);
        register.addActionListener(e -> {
            String s = sender.getText().trim(), r = receiver.getText().trim(), d = destination.getText().trim();
            if (s.isEmpty() || r.isEmpty() || d.isEmpty()) { message("Fill in all parcel details."); return; }
            String id = "PT" + nextId++;
            Parcel parcel = new Parcel(id, s, r, d, status.getSelectedItem().toString());
            parcels.add(parcel);
            log("Parcel registered: " + id);
            refreshAll();
            sender.setText(""); receiver.setText(""); destination.setText("");
            message("Parcel registered successfully. Tracking ID: " + id);
        });
        return p;
    }

    private void addFormRow(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        p.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        p.add(field, c);
    }

    private JPanel deliveryPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JComboBox<String> idBox = new JComboBox<>();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Booked", "Packed", "Shipped", "Out for Delivery", "Delivered", "Returned"});
        JButton update = new JButton("Update Delivery Status");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tracking ID:")); top.add(idBox);
        top.add(new JLabel("New Status:")); top.add(statusBox); top.add(update);
        update.addActionListener(e -> {
            String id = (String) idBox.getSelectedItem();
            if (id == null) { message("Register a parcel first."); return; }
            Parcel parcel = findParcel(id);
            if (parcel != null) {
                parcel.status = statusBox.getSelectedItem().toString();
                log("Status updated for " + id + ": " + parcel.status);
                refreshAll();
                message("Delivery status updated.");
            }
        });
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(parcelModel)), BorderLayout.CENTER);
        p.putClientProperty("idBox", idBox);
        return p;
    }

    private JPanel managementPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JTextField search = new JTextField(18);
        JButton find = new JButton("Search");
        JButton delete = new JButton("Delete Selected");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tracking ID:")); top.add(search); top.add(find); top.add(delete);
        JTable table = new JTable(parcelModel);
        find.addActionListener(e -> {
            String key = search.getText().trim();
            if (key.isEmpty()) { refreshAll(); return; }
            Parcel parcel = findParcel(key);
            if (parcel == null) { message("No parcel found for " + key); return; }
            message("Tracking ID: " + parcel.id + "\\nSender: " + parcel.sender +
                    "\\nReceiver: " + parcel.receiver + "\\nDestination: " + parcel.destination +
                    "\\nStatus: " + parcel.status);
        });
        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { message("Select a parcel to delete."); return; }
            String id = parcelModel.getValueAt(row, 0).toString();
            parcels.removeIf(parcel -> parcel.id.equals(id));
            log("Parcel deleted: " + id);
            refreshAll();
        });
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel reportPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton summary = new JButton("Show Summary");
        JButton clear = new JButton("Clear History");
        top.add(summary); top.add(clear);
        JTextArea summaryArea = new JTextArea(4, 40);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        summary.addActionListener(e -> {
            int delivered = 0;
            for (Parcel parcel : parcels) if (parcel.status.equals("Delivered")) delivered++;
            summaryArea.setText("PARCEL REPORT\\n------------------------------\\nTotal parcels: " + parcels.size() +
                    "\\nDelivered: " + delivered + "\\nPending / Other: " + (parcels.size() - delivered));
        });
        clear.addActionListener(e -> {
            history.clear(); refreshHistory(); log("History cleared");
        });
        JPanel north = new JPanel(new BorderLayout());
        north.add(top, BorderLayout.NORTH);
        north.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        p.add(north, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(historyModel)), BorderLayout.CENTER);
        return p;
    }

    private Parcel findParcel(String id) {
        for (Parcel p : parcels) if (p.id.equalsIgnoreCase(id.trim())) return p;
        return null;
    }

    private void log(String activity) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        history.add(0, time + " | " + activity);
        refreshHistory();
    }

    private void refreshHistory() {
        historyModel.setRowCount(0);
        for (String entry : history) {
            String[] parts = entry.split(" \\\\| ", 2);
            historyModel.addRow(new Object[]{parts[0], parts.length > 1 ? parts[1] : ""});
        }
    }

    private void refreshAll() {
        parcelModel.setRowCount(0);
        for (Parcel p : parcels) parcelModel.addRow(new Object[]{p.id, p.sender, p.receiver, p.destination, p.status});
        int delivered = 0, booked = 0;
        for (Parcel p : parcels) {
            if (p.status.equals("Delivered")) delivered++;
            if (p.status.equals("Booked")) booked++;
        }
        totalLabel.setText("Total parcels: " + parcels.size());
        bookedLabel.setText("Booked: " + booked);
        deliveredLabel.setText("Delivered: " + delivered);
        refreshHistory();
        // Refresh tracking-ID selectors in the delivery tab.
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JTabbedPane tabs) {
                for (int i = 0; i < tabs.getTabCount(); i++) {
                    if (tabs.getTitleAt(i).equals("Delivery Management")) {
                        JPanel panel = (JPanel) tabs.getComponentAt(i);
                        Object obj = panel.getClientProperty("idBox");
                        if (obj instanceof JComboBox<?> raw) {
                            @SuppressWarnings("unchecked") JComboBox<String> box = (JComboBox<String>) raw;
                            String selected = (String) box.getSelectedItem();
                            box.removeAllItems();
                            for (Parcel parcel : parcels) box.addItem(parcel.id);
                            if (selected != null) box.setSelectedItem(selected);
                        }
                    }
                }
            }
        }
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text, "Parcel Tracking System", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParcelTrackingSystem().setVisible(true));
    }

    private static class Parcel {
        String id, sender, receiver, destination, status;
        Parcel(String id, String sender, String receiver, String destination, String status) {
            this.id = id; this.sender = sender; this.receiver = receiver;
            this.destination = destination; this.status = status;
        }
    }
}
