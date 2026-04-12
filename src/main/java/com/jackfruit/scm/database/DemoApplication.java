package com.jackfruit.scm.database;

import com.jackfruit.scm.database.adapter.BarcodeReaderAdapter;
import com.jackfruit.scm.database.adapter.DeliveryTrackingAdapter;
import com.jackfruit.scm.database.adapter.InventoryAdapter;
import com.jackfruit.scm.database.adapter.OrderAdapter;
import com.jackfruit.scm.database.adapter.SalesAdapter;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.OrderItem;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.Shipment;
import com.jackfruit.scm.database.model.Warehouse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DemoApplication {

    public static void main(String[] args) {
        SupplyChainDatabaseFacade facade = new SupplyChainDatabaseFacade();
        InventoryAdapter inventoryAdapter = new InventoryAdapter(facade);
        SalesAdapter salesAdapter = new SalesAdapter(facade);
        OrderAdapter orderAdapter = new OrderAdapter(facade);
        BarcodeReaderAdapter barcodeReaderAdapter = new BarcodeReaderAdapter(facade);
        DeliveryTrackingAdapter deliveryTrackingAdapter = new DeliveryTrackingAdapter(facade);

        try {
            inventoryAdapter.syncWarehouse(new Warehouse("WH-001", "Central Warehouse"));

            orderAdapter.createOrder(new Order(
                    "ORD-1001",
                    "CUST-77",
                    "CONFIRMED",
                    LocalDateTime.now(),
                    new BigDecimal("420.00"),
                    "PAID",
                    "ONLINE"));

            orderAdapter.addOrderItem(new OrderItem(
                    "ITEM-1001",
                    "ORD-1001",
                    "PROD-APPLE-001",
                    3,
                    new BigDecimal("140.00"),
                    new BigDecimal("420.00")));

            salesAdapter.publishPrice(new PriceList(
                    "PRICE-001",
                    "SKU-APPLE-001",
                    "SOUTH",
                    "RETAIL",
                    "RETAIL",
                    new BigDecimal("120.00"),
                    new BigDecimal("100.00"),
                    "INR",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(30),
                    "ACTIVE"));

            barcodeReaderAdapter.recordScan(new BarcodeRfidEvent(
                    "BAR-001",
                    "PROD-APPLE-001",
                    "SCAN_IN",
                    "RFID_GATE_A1",
                    "WH-001",
                    LocalDateTime.now(),
                    "{\"tag\":\"RFID-7788\"}"));

            facade.recordDemandForecast(new DemandForecast(
                    "DF-001",
                    "PROD-APPLE-001",
                    "2026-Q2",
                    850,
                    new BigDecimal("92.50"),
                    LocalDateTime.now(),
                    "BAR-001"));

            deliveryTrackingAdapter.upsertShipment(new Shipment(
                    "SHIP-001",
                    "ORD-1001",
                    "CUST-77",
                    "Bengaluru, Karnataka",
                    "PENDING",
                    "STANDARD",
                    new BigDecimal("180.00"),
                    "AGENT-22",
                    "WH-001",
                    LocalDateTime.now(),
                    LocalDateTime.now()));

            System.out.println("Database module demo flow prepared successfully.");
        } catch (Exception ex) {
            System.out.println("Demo flow requires the OOAD schema and MySQL connection to be configured: " + ex.getMessage());
        }
    }
}
