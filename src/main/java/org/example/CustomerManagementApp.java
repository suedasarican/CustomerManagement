package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Customer {
    private int customerID;
    private String name;
    private LinkedList<Shipment> shipmentHistory;
    private Stack<Shipment> shipmentStack;

    public Customer(int customerID, String name) {
        this.customerID = customerID;
        this.name = name;
        this.shipmentHistory = new LinkedList<>();
        this.shipmentStack = new Stack<>();
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public LinkedList<Shipment> getShipmentHistory() {
        return shipmentHistory;
    }

    public Stack<Shipment> getShipmentStack() {
        return shipmentStack;
    }

    public void addShipment(Shipment shipment) {
        int index = 0;
        while (index < shipmentHistory.size() && shipmentHistory.get(index).getDate().compareTo(shipment.getDate()) < 0) {
            index++;
        }
        shipmentHistory.add(index, shipment);
        shipmentStack.push(shipment);
        if (shipmentStack.size() > 5) {
            shipmentStack.remove(0);
        }
    }
}

class Shipment implements Comparable<Shipment> {
    private int shipmentID;
    private String date;
    String deliveryStatus;
    private int deliveryTime;
    private String city;
    private Customer customer; // Gönderiyi hangi müşteri eklediğini tutacak alan

    public Shipment(int shipmentID, String date, String deliveryStatus, int deliveryTime, String city, Customer customer) {
        this.shipmentID = shipmentID;
        this.date = date;
        this.deliveryStatus = deliveryStatus;
        this.deliveryTime = deliveryTime;
        this.city = city;
        this.customer = customer; // Müşteri ekleniyor
    }

    public int getShipmentID() {
        return shipmentID;
    }

    public String getDate() {
        return date;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public String getCity() {
        return city;
    }

    public Customer getCustomer() {
        return customer; // Gönderinin bağlı olduğu müşteri
    }

    @Override
    public String toString() {
        return "Shipment ID: " + shipmentID + ", Date: " + date + ", Status: " + deliveryStatus + ", Delivery Time: " + deliveryTime + " days, City: " + city + ", Customer: " + customer.getName();
    }

    @Override
    public int compareTo(Shipment other) {
        return Integer.compare(this.deliveryTime, other.deliveryTime);
    }
}

public class CustomerManagementApp {
    private LinkedList<Customer> customers = new LinkedList<>();
    private Map<String, Integer> cityDistances = new HashMap<>();

    private int shipmentIDCounter = 1000;  // Benzersiz gönderi ID'leri için sayaç

    public CustomerManagementApp() {
        initializeCityDistances();
    }

    private void initializeCityDistances() {
        cityDistances.put("Istanbul", 0);
        cityDistances.put("Ankara", 450);
        cityDistances.put("Izmir", 330);
        cityDistances.put("Bursa", 155);
        cityDistances.put("Antalya", 500);
        cityDistances.put("Adana", 720);
        cityDistances.put("Gaziantep", 850);
        cityDistances.put("Trabzon", 1060);
        cityDistances.put("Diyarbakir", 970);
        cityDistances.put("Van", 1270);
    }

    public void showGUI() {
        JFrame frame = new JFrame("Müşteri Yönetimi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel(new GridLayout(12, 1));

        JButton addCustomerButton = new JButton("Müşteri Ekle");
        JButton addShipmentButton = new JButton("Gönderi Ekle");
        JButton viewAllCustomersButton = new JButton("Tüm Müşterileri Gör");
        JButton viewCustomerHistoryButton = new JButton("Müşteri Geçmişini Gör");
        JButton updateShipmentStatusButton = new JButton("Gönderi Durumunu Güncelle");
        JButton searchShipmentButton = new JButton("Gönderi Ara");
        JButton deleteShipmentButton = new JButton("Gönderi Sil");
        JButton viewShipmentStackButton = new JButton("Son 5 Gönderiyi Gör");
        JButton showDeliveryRouteButton = new JButton("Teslimat Rotasını Göster");

        panel.add(addCustomerButton);
        panel.add(addShipmentButton);
        panel.add(viewAllCustomersButton);
        panel.add(viewCustomerHistoryButton);
        panel.add(updateShipmentStatusButton);
        panel.add(searchShipmentButton);
        panel.add(deleteShipmentButton);
        panel.add(viewShipmentStackButton);
        panel.add(showDeliveryRouteButton);

        addCustomerButton.addActionListener(e -> addCustomerDialog());
        addShipmentButton.addActionListener(e -> addShipmentDialog());
        viewAllCustomersButton.addActionListener(e -> viewAllCustomers());
        viewCustomerHistoryButton.addActionListener(e -> viewCustomerHistory());
        updateShipmentStatusButton.addActionListener(e -> updateShipmentStatus());
        searchShipmentButton.addActionListener(e -> searchShipment());
        deleteShipmentButton.addActionListener(e -> deleteShipment());
        viewShipmentStackButton.addActionListener(e -> viewShipmentStack());
        showDeliveryRouteButton.addActionListener(e -> showDeliveryRoute());

        frame.add(panel);
        frame.setVisible(true);
    }

    private void addCustomerDialog() {
        JFrame dialog = new JFrame("Müşteri Ekle");
        dialog.setSize(300, 200);

        JLabel nameLabel = new JLabel("Adı:");
        JTextField nameField = new JTextField();
        JButton addButton = new JButton("Ekle");

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(new JLabel());
        panel.add(addButton);

        dialog.add(panel);

        addButton.addActionListener(e -> {
            try {
                int id = new Random().nextInt(10000);  // Rastgele ID üretme
                String name = nameField.getText();
                Customer newCustomer = new Customer(id, name);
                customers.add(newCustomer);

                JOptionPane.showMessageDialog(dialog, "Müşteri başarıyla eklendi.\nMüşteri ID: " + id);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Müşteri eklerken hata oluştu.");
            }
        });

        dialog.setVisible(true);
    }

    private void addShipmentDialog() {
        JFrame dialog = new JFrame("Gönderi Ekle");
        dialog.setSize(400, 300);

        JLabel customerLabel = new JLabel("Müşteri Seç:");

        // Müşteri isimlerini veya ID'lerini içeren bir liste hazırlıyoruz
        DefaultComboBoxModel<String> customerComboBoxModel = new DefaultComboBoxModel<>();
        for (Customer customer : customers) {
            customerComboBoxModel.addElement("ID: " + customer.getCustomerID() + " - " + customer.getName());
        }

        JComboBox<String> customerComboBox = new JComboBox<>(customerComboBoxModel);

        JLabel cityLabel = new JLabel("Teslimat Şehri:");
        JComboBox<String> cityComboBox = new JComboBox<>(cityDistances.keySet().toArray(new String[0])); // Şehirlerin listesi
        JButton addButton = new JButton("Ekle");

        JScrollPane scrollPane = new JScrollPane(cityComboBox);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(customerLabel);
        panel.add(customerComboBox);
        panel.add(cityLabel);
        panel.add(scrollPane);
        panel.add(new JLabel());
        panel.add(addButton);

        dialog.add(panel);

        addButton.addActionListener(e -> {
            try {
                // Seçilen müşteri ismi ile müşteri objesini buluyoruz
                String selectedCustomerText = (String) customerComboBox.getSelectedItem();
                int customerID = Integer.parseInt(selectedCustomerText.split(" - ")[0].replace("ID: ", "").trim());
                Customer selectedCustomer = null;
                for (Customer customer : customers) {
                    if (customer.getCustomerID() == customerID) {
                        selectedCustomer = customer;
                        break;
                    }
                }

                String city = (String) cityComboBox.getSelectedItem();
                Integer distance = cityDistances.get(city);
                if (distance != null) {
                    int deliveryTime = distance / 100; // Mesafeye göre teslimat süresi
                    Shipment shipment = new Shipment(shipmentIDCounter++, "2024-12-18", "Beklemede", deliveryTime, city, selectedCustomer); // Benzersiz ID
                    selectedCustomer.addShipment(shipment);

                    JOptionPane.showMessageDialog(dialog, "Gönderi başarıyla eklendi.\nTeslimat Süresi: " + deliveryTime + " gün.\nGönderi ID: " + shipment.getShipmentID());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Geçersiz şehir adı.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Gönderi eklerken hata oluştu.");
            }
        });

        dialog.setVisible(true);
    }


    private void viewAllCustomers() {
        StringBuilder customersList = new StringBuilder("Tüm Müşteriler:\n");
        for (Customer customer : customers) {
            customersList.append("ID: ").append(customer.getCustomerID()).append(", Ad: ").append(customer.getName()).append("\n");
        }
        JOptionPane.showMessageDialog(null, customersList.toString());
    }

    private void viewCustomerHistory() {
        String customerName = JOptionPane.showInputDialog("Geçmişi görmek istediğiniz müşteri adını girin:");
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(customerName)) {
                StringBuilder history = new StringBuilder("Müşteri Geçmişi:\n");
                for (Shipment shipment : customer.getShipmentHistory()) {
                    history.append(shipment).append("\n");
                }
                JOptionPane.showMessageDialog(null, history.toString());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Müşteri bulunamadı.");
    }

    private void updateShipmentStatus() {
        String shipmentID = JOptionPane.showInputDialog("Durumunu güncellemek istediğiniz gönderi ID'sini girin:");
        for (Customer customer : customers) {
            for (Shipment shipment : customer.getShipmentHistory()) {
                if (String.valueOf(shipment.getShipmentID()).equals(shipmentID)) {
                    String status = JOptionPane.showInputDialog("Yeni durum:");
                    shipment.deliveryStatus = status;
                    JOptionPane.showMessageDialog(null, "Durum başarıyla güncellendi.");
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Gönderi bulunamadı.");
    }

    private void searchShipment() {
        String shipmentID = JOptionPane.showInputDialog("Aradığınız gönderi ID'sini girin:");
        for (Customer customer : customers) {
            for (Shipment shipment : customer.getShipmentHistory()) {
                if (String.valueOf(shipment.getShipmentID()).equals(shipmentID)) {
                    JOptionPane.showMessageDialog(null, shipment);
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Gönderi bulunamadı.");
    }

    private void deleteShipment() {
        String shipmentID = JOptionPane.showInputDialog("Silmek istediğiniz gönderi ID'sini girin:");
        for (Customer customer : customers) {
            Iterator<Shipment> iterator = customer.getShipmentHistory().iterator();
            while (iterator.hasNext()) {
                Shipment shipment = iterator.next();
                if (String.valueOf(shipment.getShipmentID()).equals(shipmentID)) {
                    iterator.remove();
                    JOptionPane.showMessageDialog(null, "Gönderi başarıyla silindi.");
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Gönderi bulunamadı.");
    }

    private void viewShipmentStack() {
        StringBuilder shipmentStackList = new StringBuilder("Son 5 Gönderi:\n");
        for (Customer customer : customers) {
            for (Shipment shipment : customer.getShipmentStack()) {
                shipmentStackList.append(shipment).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, shipmentStackList.toString());
    }

    private void showDeliveryRoute() {
        // Teslimat rotası sıralama ve gösterme
        LinkedList<Shipment> allShipments = new LinkedList<>();
        for (Customer customer : customers) {
            allShipments.addAll(customer.getShipmentHistory());
        }

        // Teslimat sürelerine göre sıralama
        Collections.sort(allShipments);

        // Teslimat rotasını oluşturma
        StringBuilder route = new StringBuilder("Teslimat Rotası:\n");
        for (Shipment shipment : allShipments) {
            route.append(shipment.getCity())
                    .append(" - Teslimat Süresi: ").append(shipment.getDeliveryTime())
                    .append(" gün\n");
        }

        JOptionPane.showMessageDialog(null, route.toString());
    }

    public static void main(String[] args) {
        CustomerManagementApp app = new CustomerManagementApp();
        app.showGUI();
    }
}
