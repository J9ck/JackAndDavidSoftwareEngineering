package licensemanagergui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * To-Do List Manager for tracking project tasks and features
 */
public class TodoManager extends JDialog {
    private DatabaseHelper db;
    private DefaultTableModel todoTableModel;
    private JTable todoTable;
    
    public TodoManager(JFrame parent, DatabaseHelper database) {
        super(parent, "To-Do List Manager", true);
        this.db = database;
        
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Modern color scheme
        Color primaryColor = new Color(41, 128, 185);
        Color accentColor = new Color(52, 152, 219);
        Color backgroundColor = new Color(245, 247, 250);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("Project To-Do List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Status", "Task", "Priority", "Notes"};
        todoTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only status column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
        };
        
        todoTable = new JTable(todoTableModel);
        todoTable.setFont(new Font("Arial", Font.PLAIN, 12));
        todoTable.setRowHeight(30);
        todoTable.setShowGrid(true);
        todoTable.setGridColor(new Color(230, 230, 230));
        todoTable.setSelectionBackground(accentColor);
        todoTable.setSelectionForeground(Color.WHITE);
        
        // Style table header
        todoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        todoTable.getTableHeader().setBackground(primaryColor);
        todoTable.getTableHeader().setForeground(Color.WHITE);
        
        // Alternating row colors
        todoTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        });
        
        // Set column widths
        todoTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        todoTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        todoTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        todoTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(todoTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        
        JButton addButton = new JButton("Add Task");
        styleButton(addButton, new Color(39, 174, 96), Color.WHITE);
        
        JButton deleteButton = new JButton("Delete Task");
        styleButton(deleteButton, new Color(231, 76, 60), Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, primaryColor, Color.WHITE);
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(149, 165, 166), Color.WHITE);
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        
        // Load initial data
        loadTodos();
        
        // Button actions
        addButton.addActionListener(e -> addTodoDialog());
        deleteButton.addActionListener(e -> deleteTodo());
        refreshButton.addActionListener(e -> loadTodos());
        closeButton.addActionListener(e -> dispose());
        
        // Update status when checkbox is clicked
        todoTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                int row = e.getFirstRow();
                boolean completed = (Boolean) todoTableModel.getValueAt(row, 0);
                String task = (String) todoTableModel.getValueAt(row, 1);
                updateTodoStatus(task, completed);
            }
        });
    }
    
    private void loadTodos() {
        todoTableModel.setRowCount(0);
        List<TodoItem> todos = db.getAllTodos();
        for (TodoItem todo : todos) {
            todoTableModel.addRow(new Object[]{
                todo.completed,
                todo.task,
                todo.priority,
                todo.notes
            });
        }
    }
    
    private void addTodoDialog() {
        JDialog dialog = new JDialog(this, "Add New Task", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel taskLabel = new JLabel("Task:");
        taskLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JTextField taskField = new JTextField();
        
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        String[] priorities = {"High", "Medium", "Low"};
        JComboBox<String> priorityCombo = new JComboBox<>(priorities);
        
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JTextField notesField = new JTextField();
        
        formPanel.add(taskLabel); formPanel.add(taskField);
        formPanel.add(priorityLabel); formPanel.add(priorityCombo);
        formPanel.add(notesLabel); formPanel.add(notesField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        styleButton(saveButton, new Color(39, 174, 96), Color.WHITE);
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(149, 165, 166), Color.WHITE);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        saveButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                String priority = (String) priorityCombo.getSelectedItem();
                String notes = notesField.getText().trim();
                
                if (db.addTodo(task, priority, notes)) {
                    loadTodos();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Task added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add task", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    private void deleteTodo() {
        int selectedRow = todoTable.getSelectedRow();
        if (selectedRow >= 0) {
            String task = (String) todoTableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete task: " + task + "?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (db.deleteTodo(task)) {
                    loadTodos();
                    JOptionPane.showMessageDialog(this, "Task deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updateTodoStatus(String task, boolean completed) {
        db.updateTodoStatus(task, completed);
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    public static class TodoItem {
        public boolean completed;
        public String task;
        public String priority;
        public String notes;
        
        public TodoItem(boolean completed, String task, String priority, String notes) {
            this.completed = completed;
            this.task = task;
            this.priority = priority;
            this.notes = notes;
        }
    }
}
