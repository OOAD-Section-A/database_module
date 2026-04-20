# SCM Database Schema Reference

**Database:** `OOAD`  
**Last Updated:** April 20, 2026  
**Purpose:** Complete schema documentation for all subsystems

---

## Table of Contents

1. [Subsystem 1: Pricing & Discount Management](#subsystem-1-pricing--discount-management)
2. [Subsystem 2: Warehouse Management](#subsystem-2-warehouse-management)
3. [Subsystem 3: Inventory Management](#subsystem-3-inventory-management)
4. [Subsystem 4: Procurement & Vendor Management](#subsystem-4-procurement--vendor-management)
5. [Subsystem 5: Order Fulfillment](#subsystem-5-order-fulfillment)
6. [Subsystem 6: Orders](#subsystem-6-orders)
7. [Subsystem 7: Delivery & Logistics](#subsystem-7-delivery--logistics)
8. [Subsystem 8: Packaging, Repairs & Receipt](#subsystem-8-packaging-repairs--receipt-management)
9. [Subsystem 9: Product Returns](#subsystem-9-product-advancement--returns-management)
10. [Subsystem 10: Demand Forecasting](#subsystem-10-demand-forecasting)
11. [Subsystem 11: Commission Tracking](#subsystem-11-multi-tier-commission-tracking)
12. [Subsystem 12: UI Subsystem](#subsystem-12-ui-subsystem)
13. [Subsystem 13: Double-Entry Stock Keeping](#subsystem-13-double-entry-stock-keeping)
14. [Subsystem 14: Reporting & Analytics](#subsystem-14-reporting--analytics)

---

## Subsystem 1: Pricing & Discount Management

### `price_list`
**Purpose:** Stores active and historical prices for every SKU by region, channel, and buyer type.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| price_id | VARCHAR(50) | PK | Unique price record identifier |
| sku_id | VARCHAR(50) | | Reference to Inventory SKU (external FK) |
| region_code | VARCHAR(20) | | e.g., SOUTH, NORTH |
| channel | VARCHAR(30) | | e.g., RETAIL, DISTRIBUTOR |
| price_type | ENUM('RETAIL','DISTRIBUTOR') | | Buyer-level classification |
| base_price | DECIMAL(12,2) | | Base unit price |
| price_floor | DECIMAL(12,2) | | Minimum price; discount engine cannot go below this |
| currency_code | CHAR(3) | | ISO 4217 code (default: INR) |
| effective_from | DATETIME | | Start date for this price |
| effective_to | DATETIME | | End date for this price |
| status | ENUM | | ACTIVE, INACTIVE, SUPERSEDED |

**Constraints:**
- Unique: `(sku_id, region_code, channel, price_type, effective_from)`
- Check: `price_floor <= base_price`
- Check: `base_price >= 0`
- Check: `effective_to > effective_from`

---

### `tier_definitions`
**Purpose:** Master list of customer tiers (Bronze, Silver, Gold). Tiers can be reused across many customers.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| tier_id | INT | PK | Tier identifier |
| tier_name | VARCHAR(50) | UQ | e.g., Bronze, Silver, Gold |
| min_spend_threshold | DECIMAL(12,2) | | Minimum cumulative spend to qualify |
| default_discount_pct | DECIMAL(5,2) | | Default discount % for this tier |

**Constraints:**
- Check: `default_discount_pct BETWEEN 0 AND 100`
- Check: `min_spend_threshold >= 0`

---

### `customer_segmentation`
**Purpose:** Maps each customer to a computed or manually-overridden tier.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| segmentation_id | VARCHAR(50) | PK | Unique segmentation record |
| customer_id | VARCHAR(50) | UQ | Reference to CRM subsystem (not local FK) |
| cumulative_spend | DECIMAL(14,2) | | Total historical spend from CRM |
| historical_order_totals | DECIMAL(14,2) | | Read-only aggregated order value |
| assigned_tier_id | INT | FK | Reference to tier_definitions |
| manual_override | BOOLEAN | | Is tier manually overridden? |
| override_tier_id | INT | FK | Overridden tier (NULL if no override) |

---

### `price_configuration`
**Purpose:** Pricing Admin inputs COGS and desired margin. Derived base_price is written to price_list.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| price_config_id | VARCHAR(50) | PK | Configuration identifier |
| sku_id | VARCHAR(50) | | Product this config applies to |
| cogs_value | DECIMAL(12,2) | | Cost of Goods Sold (from Warehouse) |
| desired_margin_pct | DECIMAL(5,2) | | Target margin % |
| computed_base_price | DECIMAL(12,2) | | Derived: COGS / (1 - margin%) |
| product_attributes | TEXT | | Read-only metadata from Inventory |
| created_at | DATETIME | | Record creation timestamp |

---

### `discount_rule_results`
**Purpose:** Records the outcome of discount calculation for each order line.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| order_line_id | VARCHAR(50) | PK | From Order subsystem |
| order_id | VARCHAR(50) | | Parent order ID (from POS/Order subsystem) |
| quantity | INT | | Units on this line (for volume discount) |
| batch_expiry_date | DATETIME | | Written by Warehouse; read by rules engine |
| final_price | DECIMAL(12,2) | | Computed discounted unit price |
| applied_discount_pct | DECIMAL(5,2) | | Total combined discount % |
| discount_breakdown | TEXT | | Itemized log of every discount applied |
| computed_at | DATETIME | | Calculation timestamp |

---

### `promotions`
**Purpose:** Stores promotional codes and their redemption counters.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| promo_id | VARCHAR(50) | PK | Promotion identifier |
| promo_name | VARCHAR(100) | | Human-readable promotion name |
| coupon_code | VARCHAR(50) | UQ | Unique coupon code |
| discount_type | ENUM | | PERCENTAGE_OFF, FIXED_AMOUNT, BUY_X_GET_Y |
| discount_value | DECIMAL(10,2) | | Discount amount or % |
| start_date | DATETIME | | Promotion start time |
| end_date | DATETIME | | Promotion end time |
| eligible_sku_ids | JSON | | JSON array of SKU IDs |
| min_cart_value | DECIMAL(12,2) | | Minimum cart value for eligibility |
| max_uses | INT | | Maximum uses allowed |
| current_use_count | INT | | Current number of uses |
| expired | BOOLEAN | | Is promotion expired? |

---

### `promotion_eligible_skus`
**Purpose:** Normalized promotion-to-SKU mapping for subsystem read access.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| promo_id | VARCHAR(50) | FK, UQ | Reference to promotions |
| sku_id | VARCHAR(100) | UQ | SKU eligible for this promotion |

---

### `bundle_promotions`
**Purpose:** Bundle-specific promotions for combined SKU offers.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| promo_id | VARCHAR(50) | PK | Bundle promotion ID |
| promo_name | VARCHAR(200) | | Bundle promotion name |
| discount_pct | DECIMAL(5,4) | | Discount percentage (0-1) |
| start_date | DATE | | Start date |
| end_date | DATE | | End date |
| expired | BOOLEAN | | Is promotion expired? |

---

### `bundle_promotion_skus`
**Purpose:** Required SKUs for a bundle promotion.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| promo_id | VARCHAR(50) | FK, UQ | Reference to bundle_promotions |
| sku_id | VARCHAR(100) | UQ | Required SKU in bundle |

---

### `discount_policies`
**Purpose:** Global rules governing how discounts stack and cap.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| policy_id | VARCHAR(50) | PK | Policy identifier |
| policy_name | VARCHAR(100) | | Policy name |
| stacking_rule | ENUM | | EXCLUSIVE or ADDITIVE |
| priority_level | INT | UQ | Higher = applied first |
| max_discount_cap_pct | DECIMAL(5,2) | | Absolute ceiling for combined discounts |
| perishability_days | INT | | Days-to-expiry threshold for clearance |
| clearance_discount_pct | DECIMAL(5,2) | | Auto-applied markdown % near expiry |
| is_active | BOOLEAN | | Is policy active? |

---

### `rebate_programs`
**Purpose:** Tracks rebate program participation by customer and SKU.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| program_id | VARCHAR(100) | PK | Rebate program ID |
| customer_id | VARCHAR(100) | UQ | Reference to customer |
| sku_id | VARCHAR(100) | UQ | Reference to product |
| target_spend | DECIMAL(19,4) | | Target spend to achieve |
| accumulated_spend | DECIMAL(19,4) | | Accumulated spend so far |
| rebate_pct | DECIMAL(5,4) | | Rebate percentage (0-1) |

---

### `volume_discount_schedules`
**Purpose:** Volume discount schedule master per SKU.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| schedule_id | VARCHAR(50) | PK | Schedule identifier |
| sku_id | VARCHAR(100) | UQ | Reference to product |

---

### `volume_tier_rules`
**Purpose:** Tier rules within a volume discount schedule.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| schedule_id | VARCHAR(50) | FK | Reference to volume_discount_schedules |
| min_qty | INT | | Minimum quantity for this tier |
| max_qty | INT | | Maximum quantity for this tier |
| discount_pct | DECIMAL(5,4) | | Discount percentage (0-1) |

---

### `customer_tier_cache`
**Purpose:** Cached customer pricing tiers for downstream reads.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| customer_id | VARCHAR(100) | PK | Customer ID |
| tier | VARCHAR(20) | | Cached tier level |
| evaluated_at | TIMESTAMP | | Cache evaluation time |

---

### `customer_tier_overrides`
**Purpose:** Manual pricing tier overrides by customer.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| customer_id | VARCHAR(100) | PK | Customer ID |
| override_tier | VARCHAR(20) | | Override tier level |
| override_set_at | TIMESTAMP | | When override was set |

---

### `regional_pricing_multipliers`
**Purpose:** Region-specific pricing multipliers.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| region_code | VARCHAR(20) | PK | Region identifier |
| multiplier | DECIMAL(6,4) | | Pricing multiplier (must be > 0) |

---

### `contract_pricing`
**Purpose:** B2B negotiated prices per customer per SKU.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| contract_id | VARCHAR(50) | PK | Contract identifier |
| contract_customer_id | VARCHAR(50) | UQ | Reference to CRM customer (external FK) |
| contract_sku_id | VARCHAR(50) | UQ | Reference to Inventory SKU |
| negotiated_price | DECIMAL(12,2) | | Locked price agreed by Sales Rep |
| contract_start_date | DATETIME | | Contract start date |
| contract_expiry_date | DATETIME | | Contract end date |
| contract_status | ENUM | | ACTIVE, EXPIRED, PENDING |

---

### `contracts`
**Purpose:** Contract header for negotiated commercial terms.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| contract_id | VARCHAR(50) | PK | Contract identifier |
| customer_id | VARCHAR(100) | | Reference to customer |
| status | VARCHAR(20) | | Contract status |
| start_date | DATE | | Contract start date |
| end_date | DATE | | Contract end date |

---

### `contract_sku_prices`
**Purpose:** Per-SKU negotiated prices within a contract.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| contract_id | VARCHAR(50) | FK, UQ | Reference to contracts |
| sku_id | VARCHAR(100) | UQ | Reference to product |
| negotiated_price | DECIMAL(19,4) | | Negotiated price for this SKU |

---

### `price_approvals`
**Purpose:** Audit trail for manual price override requests.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| approval_id | VARCHAR(50) | PK | Approval request ID |
| request_type | ENUM | | MANUAL_DISCOUNT, CONTRACT_BYPASS, POLICY_EXCEPTION |
| requested_by | VARCHAR(50) | | Employee ID of cashier/sales rep |
| requested_discount_amt | DECIMAL(10,2) | | Discount amount or % |
| justification_text | TEXT | | Free-text reason for override |
| approving_manager_id | VARCHAR(50) | | Manager approver ID (set externally) |
| approval_status | ENUM | | PENDING, APPROVED, REJECTED, ESCALATED |
| approval_timestamp | DATETIME | | When manager acted on request |
| audit_log_flag | BOOLEAN | | Has audit entry been written? |
| created_at | DATETIME | | Request creation time |

---

### `approval_requests`
**Purpose:** Approval workflow requests for external pricing subsystem compatibility.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| approval_id | VARCHAR(36) | PK | Approval request UUID |
| request_type | VARCHAR(50) | | Request type |
| order_id | VARCHAR(100) | | Reference to order |
| requested_discount_amt | DECIMAL(19,4) | | Discount amount requested |
| status | VARCHAR(20) | | Request status |
| submission_time | TIMESTAMP | | When submitted |
| escalation_time | TIMESTAMP | | When escalated (if any) |
| approval_timestamp | TIMESTAMP | | When approved (if any) |
| routed_to_approver_id | VARCHAR(100) | | Initial approver ID |
| approving_manager_id | VARCHAR(100) | | Final approver ID |
| rejection_reason | TEXT | | Reason for rejection (if any) |
| audit_log_flag | BOOLEAN | | Has audit entry been written? |

---

### `audit_log`
**Purpose:** Audit trail entries for approval requests.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented audit ID |
| approval_id | VARCHAR(36) | FK, Indexed | Reference to approval_requests |
| timestamp | TIMESTAMP | | Event timestamp |
| event_type | VARCHAR(50) | | Type of event |
| actor | VARCHAR(100) | | User/system that performed action |
| detail | TEXT | | Event details |

---

### `profitability_analytics`
**Purpose:** Approval profitability analytics snapshots.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| approval_id | VARCHAR(36) | FK, Indexed | Reference to approval_requests |
| request_type | VARCHAR(50) | | Request type |
| discount_amount | DECIMAL(19,4) | | Discount amount |
| final_status | VARCHAR(20) | | Final status |
| recorded_at | TIMESTAMP | | Recording timestamp |

---

## Subsystem 2: Warehouse Management

### `warehouses`
**Purpose:** Master warehouse locations.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| warehouse_id | VARCHAR(50) | PK | Unique warehouse identifier |
| warehouse_name | VARCHAR(100) | UQ | Human-readable warehouse name |

---

### `warehouse_zones`
**Purpose:** Zones within a warehouse (STORAGE, PICKING, STAGING, RECEIVING, DISPATCH).

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| zone_id | VARCHAR(50) | PK | Unique zone identifier |
| warehouse_id | VARCHAR(50) | FK | Reference to warehouses |
| zone_type | ENUM | | STORAGE, PICKING, STAGING, RECEIVING, DISPATCH |
| temperature_class | ENUM | | AMBIENT, COLD, FROZEN, HAZMAT |

---

### `bins`
**Purpose:** Individual storage bins within a zone.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| bin_id | VARCHAR(50) | PK | Unique bin identifier |
| zone_id | VARCHAR(50) | FK | Reference to warehouse_zones |
| bin_capacity | INT | | Maximum unit capacity |
| bin_status | ENUM | | AVAILABLE, OCCUPIED, RESERVED, DAMAGED |
| max_weight_kg | DECIMAL(10,2) | | Maximum weight allowed |
| barcode | VARCHAR(100) | UQ | Barcode for WMS scanning |

---

### `goods_receipts`
**Purpose:** Records inbound PO deliveries.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| goods_receipt_id | VARCHAR(50) | PK | Receipt identifier |
| purchase_order_id | VARCHAR(50) | | Reference to Purchase Order subsystem |
| supplier_id | VARCHAR(50) | | Reference to Supplier subsystem |
| product_id | VARCHAR(50) | | Reference to Inventory subsystem |
| ordered_qty | INT | | Original order quantity |
| received_qty | INT | | Actual quantity received |
| received_at | DATETIME | | Receipt timestamp |
| condition_status | ENUM | | GOOD, DAMAGED, PARTIAL, REJECTED |
| asn_id | VARCHAR(50) | FK | Reference to proc_asn (optional) |
| qc_status | ENUM | | PENDING, PASSED, FAILED, BYPASSED |

---

### `stock_records`
**Purpose:** Current inventory per bin per product.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| stock_id | VARCHAR(50) | PK | Stock record identifier |
| product_id | VARCHAR(50) | UQ | Reference to Inventory subsystem |
| bin_id | VARCHAR(50) | FK, UQ | Reference to bins |
| quantity | INT | | Current quantity in this bin |
| batch_id | VARCHAR(50) | | Reference to product_batches (FEFO support) |
| lpn_id | VARCHAR(50) | FK | Reference to wms_storage_units_lpn (pallet/tote tracking) |
| last_updated | DATETIME | | Last update timestamp (auto-updated) |

---

### `stock_movements`
**Purpose:** Audit trail of every bin transfer.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| movement_id | VARCHAR(50) | PK | Movement identifier |
| movement_type | ENUM | | INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT, RETURN |
| from_bin | VARCHAR(50) | FK | Source bin (NULL for inbound) |
| to_bin | VARCHAR(50) | FK | Destination bin (NULL for outbound) |
| product_id | VARCHAR(50) | | Product being moved |
| moved_qty | INT | | Quantity moved |
| movement_ts | DATETIME | | Movement timestamp |

---

### `pick_tasks`
**Purpose:** Pick tasks assigned to employees for order picking.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| pick_task_id | VARCHAR(50) | PK | Task identifier |
| order_id | VARCHAR(50) | | Reference to Order Fulfillment subsystem |
| assigned_employee_id | VARCHAR(50) | | Reference to HR/Employee subsystem |
| product_id | VARCHAR(50) | | Product to pick |
| pick_qty | INT | | Quantity to pick |
| task_status | ENUM | | PENDING, IN_PROGRESS, COMPLETED, CANCELLED |
| wave_id | VARCHAR(50) | FK | Reference to wms_pick_waves |
| bin_id | VARCHAR(50) | FK | Exact location for picking |
| target_lpn_id | VARCHAR(50) | FK | Physical tote to pick into |
| updated_at | TIMESTAMP | | Last update timestamp |

---

### `wms_storage_units_lpn`
**Purpose:** License Plate Numbers (LPN) for tracking physical containers (pallets, totes, cases).

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| lpn_id | VARCHAR(50) | PK | LPN identifier |
| unit_type | ENUM | | PALLET, TOTE, CASE |
| current_location_type | ENUM | | BIN, DOCK, TRANSIT, PACKING_STATION |
| current_bin_id | VARCHAR(50) | FK | Current bin location (if any) |
| gross_weight_kg | DECIMAL(10,2) | | Container weight |
| status | ENUM | | ACTIVE, CLOSED, DAMAGED |
| created_at | TIMESTAMP | | Creation timestamp |

---

### `wms_pick_waves`
**Purpose:** Groups pick tasks for warehouse execution.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| wave_id | VARCHAR(50) | PK | Wave identifier |
| warehouse_id | VARCHAR(50) | FK | Reference to warehouses |
| wave_type | ENUM | | SINGLE_ORDER, BATCH, ZONE |
| status | ENUM | | PLANNED, RELEASED, COMPLETED, CANCELLED |
| created_at | TIMESTAMP | | Creation timestamp |
| released_at | TIMESTAMP | | When wave was released |
| version | INT | | Optimistic locking version |

---

### `wms_task_queue`
**Purpose:** Execution queue for internal warehouse operations (putaway, replenishment, etc.).

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| task_id | VARCHAR(50) | PK | Task identifier |
| task_type | ENUM | | PUTAWAY, REPLENISHMENT, CROSS_DOCK, BIN_CONSOLIDATION, CYCLE_COUNT |
| product_id | VARCHAR(50) | | Product involved |
| source_lpn_id | VARCHAR(50) | FK | Source LPN (if any) |
| target_bin_id | VARCHAR(50) | FK | Target bin (if any) |
| assigned_employee_id | VARCHAR(50) | | Assigned employee |
| priority | INT | | Priority (lower = higher priority) |
| status | ENUM | | PENDING, ACTIVE, COMPLETED, FAILED |
| created_at | TIMESTAMP | | Creation timestamp |
| version | INT | | Optimistic locking version |

---

### `staging_dispatch`
**Purpose:** Staging and dispatch records for outbound shipments.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| staging_id | VARCHAR(50) | PK | Staging record identifier |
| dock_door_id | VARCHAR(50) | | Dock door identifier |
| order_id | VARCHAR(50) | | Reference to Order subsystem |
| dispatched_at | DATETIME | | Dispatch timestamp (NULL until dispatched) |
| shipment_status | ENUM | | STAGED, LOADED, DISPATCHED, CANCELLED |

---

### `warehouse_returns`
**Purpose:** Records of returned goods received at warehouse.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| return_id | VARCHAR(50) | PK | Return record identifier |
| product_id | VARCHAR(50) | | Product being returned |
| return_qty | INT | | Quantity returned |
| condition_status | ENUM | | GOOD, DAMAGED, PARTIAL, REJECTED |
| return_ts | DATETIME | | Return timestamp |

---

### `cycle_counts`
**Purpose:** Periodic physical stock counts for accuracy verification.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| cycle_count_id | VARCHAR(50) | PK | Cycle count identifier |
| product_id | VARCHAR(50) | | Product being counted |
| product_name | VARCHAR(100) | | Product name (read-only from Inventory) |
| sku | VARCHAR(50) | | SKU (read-only from Inventory) |
| employee_id | VARCHAR(50) | | Counting employee |
| employee_name | VARCHAR(100) | | Employee name (read-only from HR) |
| expected_qty | INT | | Expected quantity |
| counted_qty | INT | | Actual counted quantity |
| count_ts | DATETIME | | Count timestamp |

---

## Subsystem 3: Inventory Management

### `stock_levels`
**Purpose:** Tracks stock levels, availability, and reorder signals.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| stock_level_id | VARCHAR(50) | PK | Stock level record ID |
| product_id | VARCHAR(50) | | Reference to product |
| current_stock_qty | INT | | Total physical stock |
| reserved_stock_qty | INT | | Allocated to orders |
| available_stock_qty | INT | | Available = current - reserved |
| reorder_threshold | INT | | Minimum stock before reorder |
| reorder_quantity | INT | | Suggested reorder quantity |
| safety_stock_level | INT | | Buffer stock to avoid stockouts |
| zone_assignment | VARCHAR(100) | | Zone assignment for stock placement |
| stock_health_status | VARCHAR(50) | | Healthy, low stock, critical, etc. |
| snapshot_timestamp | DATETIME | | Inventory snapshot time |
| last_updated | DATETIME | | Last update timestamp |

---

### `products`
**Purpose:** Central product catalog shared across subsystems.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| product_id | VARCHAR(50) | PK | Product identifier |
| product_name | VARCHAR(150) | | Human-readable product name |
| sku | VARCHAR(50) | UQ | Stock Keeping Unit |
| category | VARCHAR(100) | | Product category |
| sub_category | VARCHAR(100) | | Product sub-category |
| supplier_id | VARCHAR(50) | | Reference to Supplier subsystem |
| unit_of_measure | VARCHAR(20) | | e.g., PCS, KG, LITRE |
| zone | VARCHAR(100) | | Default storage/catalog zone |
| reorder_threshold | INT | | Product-level reorder threshold |
| product_image_reference | VARCHAR(255) | | External image reference |
| storage_conditions | VARCHAR(255) | | Temperature, humidity constraints |
| shelf_life_days | INT | | Expected usable duration |
| created_at | DATETIME | | Creation timestamp |

---

### `product_batches`
**Purpose:** Batch and lot-level traceability for compliance and recalls.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| batch_id | VARCHAR(50) | PK | Batch identifier |
| product_id | VARCHAR(50) | | Reference to product |
| lot_id | VARCHAR(50) | | Lot identifier |
| manufacturing_date | DATE | | Manufacturing date |
| supplier_id | VARCHAR(50) | | Supplier of this batch |
| batch_status | VARCHAR(50) | | ACTIVE, BLOCKED |
| linked_sku | VARCHAR(50) | | Associated SKU |
| quantity_received | INT | | Quantity received |
| receipt_date | DATETIME | | Receipt date |
| expiry_date | DATE | | Expiration date |
| lot_status | VARCHAR(50) | | Lot status |
| perishability_flag | BOOLEAN | | Is this perishable? |
| received_date | DATETIME | | Received date |

---

### `expiry_tracking`
**Purpose:** Tracks expiry status and alert conditions for perishable goods.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| expiry_id | VARCHAR(50) | PK | Expiry tracking ID |
| batch_id | VARCHAR(50) | | Reference to product_batches |
| expiry_date | DATE | | Expiration date |
| days_remaining | INT | | Days until expiry |
| expiry_status | VARCHAR(50) | | VALID, EXPIRING, EXPIRED |
| alert_flag | BOOLEAN | | Triggers alert if TRUE |
| expiry_trigger_flag | VARCHAR(50) | | Expiry trigger flag |
| lot_status | VARCHAR(50) | | Lot status |

---

### `stock_adjustments`
**Purpose:** Maintains audit trail for stock changes.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| adjustment_id | VARCHAR(50) | PK | Adjustment identifier |
| product_id | VARCHAR(50) | | Reference to product |
| batch_id | VARCHAR(50) | | Reference to batch (optional) |
| adjustment_type | ENUM | | INCREASE, DECREASE, CORRECTION |
| quantity_adjusted | INT | | Amount adjusted |
| reason | VARCHAR(255) | | Reason for adjustment |
| adjusted_by | VARCHAR(50) | | Employee reference |
| adjusted_at | DATETIME | | Adjustment timestamp |
| sku_reference | VARCHAR(50) | | SKU reference |
| performer | VARCHAR(50) | | Performer of adjustment |
| reason_note | VARCHAR(255) | | Additional notes |
| audit_lock_flag | BOOLEAN | | Is record locked for audit? |
| adjustment_date | DATETIME | | Adjustment date |

---

### `reorder_management`
**Purpose:** Determines replenishment requirements based on stock levels and thresholds.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| reorder_id | VARCHAR(50) | PK | Reorder identifier |
| product_id | VARCHAR(50) | | Reference to product |
| current_stock | INT | | Current stock level |
| reorder_threshold | INT | | Reorder threshold |
| reorder_quantity | INT | | Reorder quantity |
| supplier_id | VARCHAR(50) | | Reference to supplier |
| reorder_status | VARCHAR(50) | | PENDING, ORDERED |
| last_reorder_date | DATETIME | | Last reorder date |
| supplier_name | VARCHAR(150) | | Supplier name |
| suggested_reorder_qty | INT | | Suggested quantity |
| order_date | DATETIME | | Order date |
| order_reference | VARCHAR(100) | | PO reference |

---

### `stock_reservations`
**Purpose:** Tracks reservation lifecycle to prevent over-allocation.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| reservation_id | VARCHAR(50) | PK | Reservation identifier |
| product_id | VARCHAR(50) | | Reference to product |
| order_id | VARCHAR(50) | | Reference to order |
| reserved_qty | INT | | Quantity reserved |
| reservation_status | VARCHAR(50) | | ACTIVE, RELEASED |
| reserved_at | DATETIME | | Reservation timestamp |
| expiry_time | DATETIME | | Reservation expiry time |
| linked_sku | VARCHAR(50) | | Linked SKU |
| reserved_quantity | INT | | Reserved quantity |

---

### `stock_freeze`
**Purpose:** Blocks stock due to quality issues or regulatory constraints.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| freeze_id | VARCHAR(50) | PK | Freeze record ID |
| product_id | VARCHAR(50) | | Reference to product |
| batch_id | VARCHAR(50) | | Reference to batch (optional) |
| freeze_status | BOOLEAN | | Is product frozen? |
| freeze_reason | VARCHAR(255) | | Reason for freeze |
| frozen_by | VARCHAR(50) | | Employee who froze stock |
| frozen_at | DATETIME | | Freeze timestamp |
| freeze_status_flag | BOOLEAN | | Additional status flag |
| reason_for_freeze | VARCHAR(255) | | Detailed reason |
| freeze_applied_by | VARCHAR(50) | | Person applying freeze |
| freeze_timestamp | DATETIME | | Freeze timestamp |

---

### `dead_stock`
**Purpose:** Identifies slow-moving inventory for liquidation or clearance.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| dead_stock_id | VARCHAR(50) | PK | Dead stock record ID |
| product_id | VARCHAR(50) | | Reference to product |
| last_movement_date | DATETIME | | Date of last movement |
| stagnant_days | INT | | Days without movement |
| stagnant_quantity | INT | | Quantity not moving |
| action_flag | VARCHAR(50) | | CLEARANCE, HOLD |
| action_status | VARCHAR(50) | | Action status |

---

### `stock_valuation`
**Purpose:** Provides financial view of inventory.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| valuation_id | VARCHAR(50) | PK | Valuation identifier |
| product_id | VARCHAR(50) | | Reference to product |
| unit_cost | DECIMAL(12,2) | | Cost per unit |
| total_quantity | INT | | Total quantity |
| total_value | DECIMAL(14,2) | | Total inventory value |
| reserved_value | DECIMAL(14,2) | | Value of reserved stock |
| valuation_method | VARCHAR(50) | | FIFO, LIFO, AVG |
| total_inventory_value | DECIMAL(14,2) | | Total inventory value |
| reserved_stock_value | DECIMAL(14,2) | | Reserved stock value |
| dead_stock_value | DECIMAL(14,2) | | Dead stock value |
| monthly_writeoff_value | DECIMAL(14,2) | | Monthly write-off value |
| stock_value_by_category | TEXT | | Stock value breakdown |
| monthly_valuation_trend | TEXT | | Monthly trend data |

---

## Subsystem 4: Procurement & Vendor Management

### `proc_suppliers`
**Purpose:** Supplier master data.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| supplier_id | VARCHAR(50) | PK | Supplier identifier |
| name | VARCHAR(150) | | Supplier name |
| avg_lead_time | INT | | Average lead time in days |
| reliability_score | DECIMAL(5,2) | | Reliability score (0-100) |
| status | VARCHAR(20) | | ACTIVE, INACTIVE |

---

### `proc_product_supplier`
**Purpose:** Supplier/product purchasing terms.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| product_id | VARCHAR(50) | FK, UQ | Reference to products |
| supplier_id | VARCHAR(50) | FK, UQ | Reference to proc_suppliers |
| price | DECIMAL(12,2) | | Purchase price |
| min_order_qty | INT | | Minimum order quantity |
| last_updated | TIMESTAMP | | Last update timestamp |

---

### `proc_purchase_orders`
**Purpose:** Inbound procurement purchase orders.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| po_id | VARCHAR(50) | PK | Purchase order ID |
| supplier_id | VARCHAR(50) | FK | Reference to proc_suppliers |
| warehouse_id | VARCHAR(50) | FK | Reference to warehouses |
| order_date | DATE | | Order date |
| expected_delivery | DATE | | Expected delivery date |
| priority | VARCHAR(20) | | NORMAL, HIGH, URGENT |
| status | VARCHAR(20) | | CREATED, CONFIRMED, RECEIVED, etc. |
| version | INT | | Optimistic locking version |

---

### `proc_po_items`
**Purpose:** Line items for procurement purchase orders.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| po_id | VARCHAR(50) | FK | Reference to proc_purchase_orders |
| product_id | VARCHAR(50) | FK | Reference to products |
| ordered_qty | INT | | Quantity ordered |
| received_qty | INT | | Quantity received |
| pending_qty | INT | Generated | Calculated: ordered_qty - received_qty |
| agreed_price | DECIMAL(12,2) | | Price agreed upon |

---

### `proc_asn`
**Purpose:** Advance shipment notices for inbound receiving.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| asn_id | VARCHAR(50) | PK | ASN identifier |
| po_id | VARCHAR(50) | FK | Reference to proc_purchase_orders |
| supplier_id | VARCHAR(50) | FK | Reference to proc_suppliers |
| expected_arrival | DATE | | Expected arrival date |

---

### `proc_quality_inspections`
**Purpose:** Quality checks performed during procurement receipt.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| inspection_id | VARCHAR(50) | PK | Inspection identifier |
| grn_id | VARCHAR(50) | FK | Reference to goods_receipts |
| product_id | VARCHAR(50) | FK | Reference to products |
| passed_qty | INT | | Quantity that passed QC |
| failed_qty | INT | | Quantity that failed QC |
| remarks | VARCHAR(255) | | QC remarks |

---

### `proc_supplier_invoices`
**Purpose:** Supplier invoice headers for 3-way matching.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| invoice_id | VARCHAR(50) | PK | Invoice identifier |
| po_id | VARCHAR(50) | FK | Reference to proc_purchase_orders |
| total_amount | DECIMAL(14,2) | | Invoice total amount |
| invoice_date | DATE | | Invoice date |

---

### `proc_invoice_items`
**Purpose:** Supplier invoice line items.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| invoice_id | VARCHAR(50) | FK | Reference to proc_supplier_invoices |
| product_id | VARCHAR(50) | FK | Reference to products |
| billed_qty | INT | | Quantity billed |
| billed_price | DECIMAL(12,2) | | Price billed |

---

### `proc_supplier_payments`
**Purpose:** Payments made against supplier invoices.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| payment_id | VARCHAR(50) | PK | Payment identifier |
| invoice_id | VARCHAR(50) | FK | Reference to proc_supplier_invoices |
| amount_paid | DECIMAL(14,2) | | Amount paid |
| status | VARCHAR(20) | | PENDING, COMPLETED, FAILED |

---

### `proc_discrepancies`
**Purpose:** Procurement 3-way-match discrepancy records.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented ID |
| type | VARCHAR(20) | | QUANTITY, PRICE, DAMAGE |
| product_id | VARCHAR(50) | FK | Reference to products |
| supplier_id | VARCHAR(50) | FK | Reference to proc_suppliers |
| description | TEXT | | Discrepancy description |
| created_at | TIMESTAMP | | Creation timestamp |

---

## Subsystem 5: Order Fulfillment

### `fulfillment_orders`
**Purpose:** Tracks order processing stages (picking, packing, shipment readiness).

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| fulfillment_id | VARCHAR(50) | PK | Fulfillment record ID |
| order_id | VARCHAR(50) | | Reference to order |
| customer_id | VARCHAR(50) | | Reference to customer |
| product_id | VARCHAR(50) | | Reference to product |
| quantity | INT | | Order quantity |
| order_status | VARCHAR(50) | | Order status |
| order_date | DATETIME | | Order creation date |
| total_amount | DECIMAL(12,2) | | Order total |
| customer_name | VARCHAR(150) | | Customer name |
| shipping_address | TEXT | | Shipping address |
| contact_number | VARCHAR(50) | | Customer contact |
| payment_id | VARCHAR(50) | | Payment reference |
| payment_status | VARCHAR(50) | | Payment status |
| payment_method | VARCHAR(50) | | Payment method |
| product_stock_available | INT | | Available stock for product |
| reserved_quantity | INT | | Quantity reserved |
| warehouse_id | VARCHAR(50) | | Warehouse for fulfillment |
| storage_location_rack_id | VARCHAR(100) | | Storage location |
| picking_status | VARCHAR(50) | | Picking status |
| packing_status | VARCHAR(50) | | Packing status |
| shipment_id | VARCHAR(50) | | Shipment reference |
| courier_partner | VARCHAR(100) | | Courier partner |
| tracking_id | VARCHAR(100) | | Tracking number |
| shipping_status | VARCHAR(50) | | Shipping status |
| estimated_delivery_date | DATE | | Estimated delivery |
| fulfillment_status | VARCHAR(50) | | Overall fulfillment status |
| assigned_staff_id | VARCHAR(50) | | Assigned staff member |
| reservation_timestamp | DATETIME | | When stock was reserved |
| delivery_instructions | TEXT | | Special delivery notes |
| failed_delivery_attempts | INT | | Number of failed delivery attempts |
| cancellation_status | VARCHAR(50) | | Cancellation status (if any) |
| cancellation_reason | TEXT | | Reason for cancellation |
| cancellation_timestamp | DATETIME | | When cancelled |
| assigned_warehouse | VARCHAR(50) | | Assigned warehouse |
| priority_level | VARCHAR(50) | | Order priority |
| created_at | DATETIME | | Record creation time |

---

### `packing_details`
**Purpose:** Captures packing stage details.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| packing_id | VARCHAR(50) | PK | Packing record ID |
| fulfillment_id | VARCHAR(50) | FK | Reference to fulfillment_orders |
| package_type | VARCHAR(50) | | Type of packaging |
| packed_by | VARCHAR(50) | | Employee who packed |
| packed_at | DATETIME | | Packing timestamp |
| package_weight | DECIMAL(10,2) | | Total package weight |

---

## Subsystem 6: Orders

### `orders`
**Purpose:** Customer order header used across fulfillment and delivery.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| order_id | VARCHAR(50) | PK | Order identifier |
| customer_id | VARCHAR(50) | | Reference to customer |
| order_status | VARCHAR(50) | | PLACED, CONFIRMED, CANCELLED, FULFILLED |
| order_date | DATETIME | | Order creation date |
| total_amount | DECIMAL(12,2) | | Order total |
| payment_status | VARCHAR(50) | | PENDING, PAID, FAILED, REFUNDED |
| sales_channel | VARCHAR(50) | | ONLINE, POS, DISTRIBUTOR |

---

### `order_items`
**Purpose:** Line items that belong to customer orders.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| order_item_id | VARCHAR(50) | PK | Order item identifier |
| order_id | VARCHAR(50) | FK | Reference to orders |
| product_id | VARCHAR(50) | | Reference to product |
| ordered_quantity | INT | | Quantity ordered |
| unit_price | DECIMAL(12,2) | | Price per unit |
| line_total | DECIMAL(12,2) | | Total for this line |

---

## Subsystem 7: Delivery & Logistics

### `delivery_orders`
**Purpose:** Handles final delivery execution.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| delivery_id | VARCHAR(50) | PK | Delivery identifier |
| order_id | VARCHAR(50) | | Reference to order |
| customer_id | VARCHAR(50) | | Reference to customer |
| delivery_address | TEXT | | Delivery address |
| delivery_status | VARCHAR(50) | | Delivery status |
| delivery_date | DATETIME | | Actual/planned delivery date |
| delivery_type | VARCHAR(50) | | Type of delivery |
| delivery_cost | DECIMAL(10,2) | | Delivery cost |
| assigned_agent | VARCHAR(50) | | Assigned delivery agent |
| warehouse_id | VARCHAR(50) | | Source warehouse |
| created_at | DATETIME | | Creation timestamp |
| updated_at | DATETIME | | Last update timestamp |

---

### `delivery_tracking_routes`
**Purpose:** Route-plan level tracking for real-time delivery monitoring.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| route_plan_id | VARCHAR(50) | PK | Route plan identifier |
| delivery_id | VARCHAR(50) | FK | Reference to delivery_orders |
| order_id | VARCHAR(50) | | Reference to order |
| customer_id | VARCHAR(50) | | Reference to customer |
| pickup_address | TEXT | | Pickup location |
| dropoff_address | TEXT | | Dropoff location |
| item_description | TEXT | | Item description |
| item_weight_kg | DECIMAL(10,2) | | Item weight |
| committed_delivery_window_start | DATETIME | | Committed delivery window start |
| committed_delivery_window_end | DATETIME | | Committed delivery window end |
| order_created_at | DATETIME | | Order creation time |
| dispatched_at | DATETIME | | Dispatch time |
| warehouse_id | VARCHAR(50) | | Source warehouse |
| warehouse_latitude | DECIMAL(10,6) | | Warehouse GPS latitude |
| warehouse_longitude | DECIMAL(10,6) | | Warehouse GPS longitude |
| rider_id | VARCHAR(50) | | Assigned rider/driver |
| assigned_at | DATETIME | | When rider was assigned |
| customer_name | VARCHAR(100) | | Customer name |
| customer_email | VARCHAR(150) | | Customer email |
| customer_phone | VARCHAR(50) | | Customer phone |
| preferred_notification_channel | VARCHAR(50) | | Preferred contact method |
| vehicle_id | VARCHAR(50) | | Vehicle identifier |
| plate_number | VARCHAR(30) | | Vehicle plate number |
| vehicle_type | VARCHAR(50) | | Type of vehicle |
| max_payload_kg | DECIMAL(10,2) | | Maximum vehicle capacity |
| temperature_min_c | DECIMAL(10,2) | | Minimum temperature for cargo |
| temperature_max_c | DECIMAL(10,2) | | Maximum temperature for cargo |
| is_hazardous | BOOLEAN | | Is cargo hazardous? |
| carrier_id | VARCHAR(50) | | Carrier identifier |
| tracking_api_url | VARCHAR(255) | | External tracking API URL |
| waypoints | TEXT | | Waypoint list (JSON) |
| planned_departure | DATETIME | | Planned departure time |
| planned_arrival | DATETIME | | Planned arrival time |
| current_eta | DATETIME | | Current estimated arrival |
| route_status | VARCHAR(50) | | Route status |

---

### `delivery_tracking_waypoints`
**Purpose:** Waypoint list for monitored delivery routes.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| waypoint_id | VARCHAR(50) | PK | Waypoint identifier |
| route_plan_id | VARCHAR(50) | FK | Reference to delivery_tracking_routes |
| waypoint_sequence | INT | | Waypoint sequence number |
| waypoint_location | VARCHAR(255) | | Waypoint address/location |

---

### `delivery_tracking_events`
**Purpose:** Live delivery milestones and alert events.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| tracking_event_id | VARCHAR(50) | PK | Event identifier |
| delivery_id | VARCHAR(50) | FK | Reference to delivery_orders |
| rider_id | VARCHAR(50) | | Rider identifier |
| vehicle_id | VARCHAR(50) | | Vehicle identifier |
| timeline_stage | VARCHAR(50) | | Event stage (Picked Up, In Transit, etc.) |
| gps_coordinates | VARCHAR(100) | | GPS coordinates |
| event_timestamp | DATETIME | | Event timestamp |
| alert_message | VARCHAR(255) | | Alert message (if any) |
| requires_rerouting | BOOLEAN | | Does route need to be changed? |

---

### `shipments`
**Purpose:** Shipment planning for transport and logistics decisions.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| shipment_id | VARCHAR(50) | PK | Shipment identifier |
| order_id | VARCHAR(50) | FK | Reference to orders |
| origin_address | TEXT | | Origin address |
| destination_address | TEXT | | Destination address |
| package_weight | DECIMAL(10,2) | | Package weight |
| is_drop_ship | BOOLEAN | | Is this a drop-ship? |
| shipping_priority | VARCHAR(50) | | STANDARD, EXPRESS, etc. |
| shipment_status | VARCHAR(50) | | Shipment status |
| supplier_id | VARCHAR(50) | | Supplier (if drop-ship) |
| inventory_level | INT | | Inventory level |
| route_id | VARCHAR(50) | | Route identifier |
| carrier_id | VARCHAR(50) | | Carrier identifier |
| tracking_id | VARCHAR(50) | | Tracking number |
| min_cost_constraint | BOOLEAN | | Minimize cost? |
| min_time_constraint | BOOLEAN | | Minimize time? |
| avoid_tolls_constraint | BOOLEAN | | Avoid tolls? |
| calculated_cost | DECIMAL(12,2) | | Calculated shipping cost |
| created_at | DATETIME | | Creation timestamp |

---

### `logistics_routes`
**Purpose:** Calculated logistics routes for shipments.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| route_id | VARCHAR(50) | PK | Route identifier |
| shipment_id | VARCHAR(50) | FK | Reference to shipments |
| gps_coordinates | VARCHAR(100) | | GPS coordinates |
| current_eta | DATETIME | | Current estimated arrival |
| timeline_stage | VARCHAR(50) | | Current stage |
| route_status | VARCHAR(50) | | Route status |
| requires_rerouting | BOOLEAN | | Does route need adjustment? |

---

### `shipment_alerts`
**Purpose:** Alerts produced by transport and logistics flows.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| alert_id | VARCHAR(50) | PK | Alert identifier |
| shipment_id | VARCHAR(50) | FK | Reference to shipments |
| alert_message | VARCHAR(255) | | Alert message |
| alert_severity | VARCHAR(20) | | LOW, MEDIUM, HIGH, CRITICAL |
| created_at | DATETIME | | Alert creation time |

---

## Subsystem 8: Packaging, Repairs & Receipt Management

### `packaging_jobs`
**Purpose:** Packaging jobs linked to order handling.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| package_id | VARCHAR(50) | PK | Package identifier |
| order_id | VARCHAR(50) | FK | Reference to orders |
| quantity | INT | | Quantity packaged |
| total_amount | DECIMAL(12,2) | | Total amount |
| discounts | DECIMAL(12,2) | | Discounts applied |
| packaging_status | VARCHAR(50) | | Packaging status |
| packed_by | VARCHAR(50) | | Employee who packed |
| created_at | DATETIME | | Creation timestamp |

---

### `repair_requests`
**Purpose:** Repair requests raised for packaged or delivered items.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| request_id | VARCHAR(50) | PK | Repair request ID |
| order_id | VARCHAR(50) | FK | Reference to orders |
| product_id | VARCHAR(50) | | Reference to product |
| defect_details | TEXT | | Details of defect |
| request_status | VARCHAR(50) | | Request status |
| requested_at | DATETIME | | Request timestamp |

---

### `receipt_records`
**Purpose:** Receipt and acknowledgement records for packaged orders.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| receipt_record_id | VARCHAR(50) | PK | Receipt record ID |
| order_id | VARCHAR(50) | FK | Reference to orders |
| package_id | VARCHAR(50) | FK | Reference to packaging_jobs |
| received_amount | DECIMAL(12,2) | | Amount received/confirmed |
| receipt_status | VARCHAR(50) | | Receipt status |
| recorded_at | DATETIME | | Recording timestamp |

---

## Subsystem 9: Product Advancement & Returns Management

### `product_returns`
**Purpose:** Customer-facing product return and advancement records.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| return_request_id | VARCHAR(50) | PK | Return request ID |
| order_id | VARCHAR(50) | FK | Reference to orders |
| customer_id | VARCHAR(50) | | Reference to customer |
| product_details | TEXT | | Product details |
| defect_details | TEXT | | Details of defect |
| customer_feedback | TEXT | | Customer feedback |
| transport_details | TEXT | | Transport/shipping details |
| warranty_valid_until | DATETIME | | Warranty expiration |
| return_status | VARCHAR(50) | | Return status |
| created_at | DATETIME | | Creation timestamp |

---

### `return_growth_statistics`
**Purpose:** Trend and growth metrics for returns management.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| growth_stat_id | VARCHAR(50) | PK | Statistic record ID |
| return_request_id | VARCHAR(50) | FK | Reference to product_returns |
| metric_period | VARCHAR(30) | | Period of measurement |
| return_rate | DECIMAL(8,2) | | Return rate (%) |
| resolution_rate | DECIMAL(8,2) | | Resolution rate (%) |
| recorded_at | DATETIME | | Recording timestamp |

---

## Subsystem 10: Demand Forecasting

### `sales_records`
**Purpose:** Historical sales inputs for forecasting models.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| sale_id | VARCHAR(50) | PK | Sale record ID |
| product_id | VARCHAR(50) | | Reference to product |
| store_id | VARCHAR(50) | | Store identifier |
| sale_date | DATE | | Sale date |
| quantity_sold | INT | | Quantity sold |
| unit_price | DECIMAL(12,2) | | Unit price |
| revenue | DECIMAL(14,2) | | Total revenue |
| region | VARCHAR(50) | | Region |

---

### `holiday_calendar`
**Purpose:** Holiday and calendar features for demand forecasting.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| holiday_id | VARCHAR(50) | PK | Holiday identifier |
| holiday_date | DATE | | Holiday date |
| holiday_name | VARCHAR(100) | | Holiday name |
| holiday_type | VARCHAR(50) | | Type of holiday |
| region_applicable | VARCHAR(50) | | Applicable region |

---

### `promotional_calendar`
**Purpose:** Promotional events used as demand-forecasting features.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| promo_calendar_id | VARCHAR(50) | PK | Promotional calendar ID |
| promo_id | VARCHAR(50) | | Reference to promotions |
| promo_name | VARCHAR(100) | | Promotion name |
| promo_start_date | DATE | | Start date |
| promo_end_date | DATE | | End date |
| discount_percentage | DECIMAL(5,2) | | Discount percentage |
| promo_type | VARCHAR(50) | | Type of promotion |
| applicable_products | TEXT | | Applicable products |

---

### `product_metadata`
**Purpose:** Product metadata used by demand forecasting.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| product_id | VARCHAR(50) | PK | Product identifier |
| product_name | VARCHAR(150) | | Product name |
| category | VARCHAR(100) | | Category |
| sub_category | VARCHAR(100) | | Sub-category |
| seasonality_type | VARCHAR(100) | | Seasonality classification |

---

### `product_lifecycle_stages`
**Purpose:** Lifecycle-stage metadata for products in forecasting.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| lifecycle_id | VARCHAR(50) | PK | Lifecycle record ID |
| product_id | VARCHAR(50) | | Reference to product |
| current_stage | VARCHAR(50) | | Current lifecycle stage |
| stage_start_date | DATE | | When stage began |
| previous_stage | VARCHAR(50) | | Previous stage |
| transition_date | DATE | | Date of transition |

---

### `inventory_supply`
**Purpose:** Inventory and supply features used by forecasting.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| product_id | VARCHAR(50) | PK | Product identifier |
| current_stock | INT | | Current stock level |
| reorder_point | INT | | Reorder point |
| lead_time_days | INT | | Lead time in days |
| supplier_id | VARCHAR(50) | | Supplier reference |

---

### `demand_forecasts`
**Purpose:** Core demand forecasting output table used across subsystems.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| forecast_id | VARCHAR(50) | PK | Forecast identifier |
| product_id | VARCHAR(50) | | Reference to product |
| forecast_period | VARCHAR(30) | | WEEKLY, MONTHLY, etc. |
| forecast_date | DATE | | Date for which prediction applies |
| predicted_demand | INT | | Model output for expected demand |
| confidence_score | DECIMAL(5,2) | | Prediction confidence (0–100) |
| reorder_signal | BOOLEAN | | Triggers reorder if TRUE |
| suggested_order_qty | INT | | Recommended replenishment quantity |
| lifecycle_stage | VARCHAR(50) | | Derived from lifecycle metadata |
| algorithm_used | VARCHAR(100) | | Model used (ARIMA, LSTM, etc.) |
| generated_at | DATETIME | | Generation timestamp |
| source_event_reference | VARCHAR(100) | | Reference event |

---

### `forecast_performance_metrics`
**Purpose:** Evaluation metrics for generated demand forecasts.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| eval_id | VARCHAR(50) | PK | Evaluation record ID |
| forecast_id | VARCHAR(50) | FK | Reference to demand_forecasts |
| forecast_date | DATE | | Forecast date |
| predicted_qty | INT | | Predicted quantity |
| actual_qty | INT | | Actual quantity |
| mape | DECIMAL(8,2) | | Mean Absolute Percentage Error |
| rmse | DECIMAL(12,4) | | Root Mean Square Error |
| model_used | VARCHAR(100) | | Model used for forecast |

---

### `forecast_timeseries`
**Purpose:** Forecast series values shared between forecasting and UI subsystems.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | VARCHAR(50) | PK | Series record ID |
| forecast_id | VARCHAR(50) | FK | Reference to demand_forecasts |
| time_index | INT | | Time index in series |
| forecast_value | DECIMAL(10,2) | | Forecast value for this time point |
| lower_bound | DECIMAL(10,2) | | Confidence interval lower bound |
| upper_bound | DECIMAL(10,2) | | Confidence interval upper bound |

---

### `barcode_rfid_events`
**Purpose:** Barcode and RFID events for inventory tracking.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| event_id | VARCHAR(50) | PK | Event identifier |
| product_id | VARCHAR(50) | | Reference to product |
| rfid_tag | VARCHAR(100) | | RFID tag value |
| product_name | VARCHAR(150) | | Product name |
| category | VARCHAR(100) | | Category |
| description | TEXT | | Description |
| transaction_id | VARCHAR(50) | | Transaction reference |
| warehouse_id | VARCHAR(50) | | Warehouse reference |
| event_timestamp | DATETIME | | Event timestamp |
| status | VARCHAR(50) | | Event status |
| source | VARCHAR(100) | | Event source |

---

## Subsystem 11: Multi-Tier Commission Tracking

### `agents`
**Purpose:** Defines hierarchical agent structure.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| agent_id | VARCHAR(50) | PK | Agent identifier |
| agent_name | VARCHAR(150) | | Agent name |
| level | INT | | Hierarchical level |
| parent_agent_id | VARCHAR(50) | | Parent agent (for hierarchy) |
| downstream_agents | TEXT | | List of downstream agents |
| status | VARCHAR(50) | | Agent status |

---

### `commission_sales`
**Purpose:** Stores sales transactions for commission.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| sale_id | VARCHAR(50) | PK | Sale identifier |
| agent_id | VARCHAR(50) | | Reference to agents |
| sale_amount | DECIMAL(12,2) | | Sale amount |
| sale_date | DATETIME | | Sale date |
| status | VARCHAR(50) | | Sale status |

---

### `commission_tiers`
**Purpose:** Defines commission tiers.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| tier_id | VARCHAR(50) | PK | Tier identifier |
| tier_level | INT | | Tier level number |
| min_sales | DECIMAL(12,2) | | Minimum sales for tier |
| max_sales | DECIMAL(12,2) | | Maximum sales for tier |
| commission_pct | DECIMAL(5,2) | | Commission percentage |

---

### `commission_history`
**Purpose:** Stores computed commissions.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| commission_id | VARCHAR(50) | PK | Commission record ID |
| agent_id | VARCHAR(50) | | Reference to agents |
| period_start | DATE | | Period start date |
| period_end | DATE | | Period end date |
| total_sales | DECIMAL(14,2) | | Total sales in period |
| tier_breakdown | TEXT | | Tier-wise breakdown |
| total_commission | DECIMAL(14,2) | | Calculated commission |
| calculated_at | DATETIME | | Calculation timestamp |

---

## Subsystem 12: UI Subsystem

### `ui_users`
**Purpose:** User accounts, roles, and profile settings.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| user_id | INT | PK, AI | Auto-incremented user ID |
| username | VARCHAR(100) | UQ | Unique username |
| password_hash | VARCHAR(255) | | bcrypt hash (never plaintext) |
| two_factor_token | INT | | 2FA token |
| authorized_menu_items | TEXT | | JSON of authorized menu items |
| user_role | ENUM | | ADMIN, MANAGER, SALES_REP, WAREHOUSE_STAFF, CASHIER, ANALYST, DRIVER |
| is_account_locked | BOOLEAN | | Is account locked? |
| login_attempt_count | INT | | Failed login attempts |
| last_login_timestamp | DATETIME | | Last login time |
| user_email | VARCHAR(150) | UQ | User email address |
| user_display_name | VARCHAR(100) | | Display name |
| theme_preference | VARCHAR(20) | | LIGHT, DARK, etc. |
| language_preference | VARCHAR(10) | | Language code |
| created_at | DATETIME | | Account creation time |

---

### `ui_sessions`
**Purpose:** Active JWT sessions per user.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| session_id | INT | PK, AI | Auto-incremented session ID |
| user_id | INT | FK, UQ | Reference to ui_users |
| jwt_session_token | VARCHAR(512) | UQ | JWT token |
| redirect_panel_url | VARCHAR(255) | | Panel to redirect to after login |
| session_expiry_time | BIGINT | | Unix epoch millis when session expires |
| session_status | VARCHAR(20) | | ACTIVE, EXPIRED, REVOKED |
| created_at | DATETIME | | Session creation time |

---

### `ui_panel_state`
**Purpose:** Persisted panel navigation state per user.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| panel_state_id | INT | PK, AI | Auto-incremented state ID |
| user_id | INT | FK | Reference to ui_users |
| panel_id | VARCHAR(50) | UQ | Panel identifier |
| notification_count | INT | | Notification count |
| current_panel_state | VARCHAR(100) | | Current state |
| breadcrumb_trail | TEXT | | Breadcrumb navigation |
| sidebar_menu_items | TEXT | | Sidebar items |
| active_user_role | VARCHAR(50) | | Active role |
| updated_at | DATETIME | | Last update time |

---

### `ui_notifications`
**Purpose:** Notification inbox per user.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| notification_id | INT | PK, AI | Auto-incremented notification ID |
| user_id | INT | FK | Reference to ui_users |
| notification_type | VARCHAR(50) | | Type of notification |
| notification_message | VARCHAR(500) | | Notification message |
| is_read | BOOLEAN | | Has notification been read? |
| created_at | DATETIME | | Creation timestamp |

---

### `ui_audit_log`
**Purpose:** Immutable audit log; INSERT only, no UPDATE/DELETE.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| audit_id | INT | PK, AI | Auto-incremented audit ID |
| audit_timestamp | DATETIME | | Audit timestamp |
| audit_action_user | VARCHAR(100) | | User who performed action |
| audit_action_description | VARCHAR(500) | | Description of action |
| audit_module_name | VARCHAR(100) | | Module where action occurred |

---

### `ui_notification_preferences`
**Purpose:** Per-user notification preferences.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| pref_id | INT | PK, AI | Auto-incremented preference ID |
| user_id | INT | FK | Reference to ui_users |
| pref_key | VARCHAR(100) | UQ | Preference key (e.g., EMAIL_ON_LOW_STOCK) |
| pref_value | BOOLEAN | | Preference value |

---

### `ui_system_config`
**Purpose:** System-wide configuration key-value store.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| config_key | VARCHAR(100) | PK | Configuration key |
| config_value | VARCHAR(500) | | Configuration value |
| updated_at | DATETIME | | Last update time |

---

## Subsystem 13: Double-Entry Stock Keeping

### `stock_ledger_entries`
**Purpose:** Double-entry ledger; INSERT only, no updates. Every stock movement has a debit and credit account.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| ledger_id | INT | PK, AI | Auto-incremented surrogate PK |
| transaction_id | VARCHAR(50) | UQ | Business-level transaction identifier |
| transaction_type | VARCHAR(50) | | e.g., INBOUND, SALE, RETURN, ADJUSTMENT |
| item_name | VARCHAR(100) | | Item name |
| quantity | INT | | Quantity |
| unit | VARCHAR(20) | | e.g., KG, PCS, LITRE |
| debit_account | VARCHAR(100) | | System-determined debit account |
| credit_account | VARCHAR(100) | | System-determined credit account |
| entry_date | DATE | | System-set entry date (not updatable) |
| reference_number | VARCHAR(50) | | Links to source document |
| total_debit | DECIMAL(14,2) | | System-computed total debit |
| total_credit | DECIMAL(14,2) | | System-computed total credit |
| balance_status | VARCHAR(20) | | BALANCED / UNBALANCED |

**Constraints:**
- Double-entry invariant: `total_debit = total_credit`
- Check: `quantity > 0`
- Check: `total_debit > 0`

---

## Subsystem 14: Reporting & Analytics

### `SCM_EXCEPTION_LOG`
**Purpose:** Canonical exception log used across subsystems.

| Column | Type | Key | Notes |
|--------|------|-----|-------|
| id | BIGINT | PK, AI | Auto-incremented exception ID |
| exception_id | INT | | Exception identifier |
| exception_name | VARCHAR(100) | | Exception class name |
| severity | VARCHAR(10) | | Severity level |
| subsystem | VARCHAR(100) | | Subsystem where exception occurred |
| error_message | TEXT | | Exception error message |
| logged_at | DATETIME(3) | | Logging timestamp (millisecond precision) |

---

### Views

#### `vw_inventory_stock_report`
**Purpose:** Stock levels per product per bin with warehouse context.

| Column | Type | Notes |
|--------|------|-------|
| productID | VARCHAR(50) | Product identifier |
| bin_id | VARCHAR(50) | Bin identifier |
| zone_id | VARCHAR(50) | Zone identifier |
| warehouseID | VARCHAR(50) | Warehouse identifier |
| current_stock_level | INT | Current stock quantity |
| last_updated | DATETIME | Last update timestamp |

---

#### `vw_price_discount_report`
**Purpose:** Active prices with promotion and discount context.

| Column | Type | Notes |
|--------|------|-------|
| productID | VARCHAR(50) | Product identifier |
| product_price | DECIMAL(12,2) | Base price |
| region_code | VARCHAR(20) | Region |
| channel | VARCHAR(30) | Sales channel |
| currency_code | CHAR(3) | Currency |
| status | ENUM | Price status |

---

#### `vw_exception_report`
**Purpose:** Approval requests flagged for audit (exception tracking).

| Column | Type | Notes |
|--------|------|-------|
| exceptionID | VARCHAR(50) | Exception identifier |
| exceptionType | VARCHAR(50) | Type of exception |
| severity_level | ENUM | Severity level |
| timestamp | DATETIME | Event timestamp |
| requested_by | VARCHAR(50) | User who requested |
| justification_text | TEXT | Justification provided |

---

#### `vw_reporting_dashboard`
**Purpose:** Comprehensive dashboard view aggregating order, inventory, delivery, pricing, and exception data.

**Key Columns:**
- Order Info: order_id, order_date, order_status, order_quantity
- Delivery Info: delivery_date, delivery_status, delivery_location
- Inventory Info: current_stock_level, reorder_level, stock_out_flag
- Supplier Info: supplier_id, supplier_name, lead_time
- Financial Info: product_price, discount_applied, revenue
- Forecasting Info: demand_forecast, forecast_period, predicted_inventory_needs
- Exception Info: exception_id, exception_type, severity_level

---

## Key Relationships & Foreign Keys

### Cross-Subsystem References (External Foreign Keys)
These are references to entities owned by external subsystems. They are stored as VARCHAR and not enforced as database foreign keys to allow external teams to evolve their PKs without breaking this schema.

- **To Inventory Subsystem:** `sku_id`, `product_id` references
- **To CRM Subsystem:** `customer_id` references
- **To HR/Employee Subsystem:** `employee_id`, `assigned_employee_id` references
- **To Supplier Subsystem:** `supplier_id` references

### Internal Foreign Key Relationships

**Pricing Subsystem:**
- `customer_segmentation.assigned_tier_id` → `tier_definitions.tier_id`
- `customer_segmentation.override_tier_id` → `tier_definitions.tier_id`
- `promotion_eligible_skus.promo_id` → `promotions.promo_id`
- `bundle_promotion_skus.promo_id` → `bundle_promotions.promo_id`
- `contract_sku_prices.contract_id` → `contracts.contract_id`
- `approval_requests` → `audit_log`, `profitability_analytics`

**Warehouse Subsystem:**
- `warehouse_zones.warehouse_id` → `warehouses.warehouse_id`
- `bins.zone_id` → `warehouse_zones.zone_id`
- `stock_records.bin_id` → `bins.bin_id`
- `stock_records.lpn_id` → `wms_storage_units_lpn.lpn_id`
- `stock_movements.from_bin` → `bins.bin_id`
- `stock_movements.to_bin` → `bins.bin_id`
- `pick_tasks.wave_id` → `wms_pick_waves.wave_id`
- `pick_tasks.bin_id` → `bins.bin_id`
- `pick_tasks.target_lpn_id` → `wms_storage_units_lpn.lpn_id`
- `wms_storage_units_lpn.current_bin_id` → `bins.bin_id`
- `wms_pick_waves.warehouse_id` → `warehouses.warehouse_id`
- `wms_task_queue.source_lpn_id` → `wms_storage_units_lpn.lpn_id`
- `wms_task_queue.target_bin_id` → `bins.bin_id`

**Inventory Subsystem:**
- `product_batches.product_id` → `products.product_id`
- `expiry_tracking.batch_id` → `product_batches.batch_id`
- `stock_adjustments.product_id` → `products.product_id`
- `stock_freeze.product_id` → `products.product_id`

**Procurement Subsystem:**
- `proc_product_supplier.product_id` → `products.product_id`
- `proc_product_supplier.supplier_id` → `proc_suppliers.supplier_id`
- `proc_purchase_orders.supplier_id` → `proc_suppliers.supplier_id`
- `proc_purchase_orders.warehouse_id` → `warehouses.warehouse_id`
- `proc_po_items.po_id` → `proc_purchase_orders.po_id`
- `proc_po_items.product_id` → `products.product_id`
- `proc_asn.po_id` → `proc_purchase_orders.po_id`
- `proc_asn.supplier_id` → `proc_suppliers.supplier_id`
- `goods_receipts.asn_id` → `proc_asn.asn_id`
- `proc_quality_inspections.grn_id` → `goods_receipts.goods_receipt_id`
- `proc_supplier_invoices.po_id` → `proc_purchase_orders.po_id`
- `proc_invoice_items.invoice_id` → `proc_supplier_invoices.invoice_id`
- `proc_supplier_payments.invoice_id` → `proc_supplier_invoices.invoice_id`

**Order Fulfillment:**
- `packing_details.fulfillment_id` → `fulfillment_orders.fulfillment_id`

**Orders:**
- `order_items.order_id` → `orders.order_id`

**Delivery & Logistics:**
- `delivery_tracking_routes.delivery_id` → `delivery_orders.delivery_id`
- `delivery_tracking_waypoints.route_plan_id` → `delivery_tracking_routes.route_plan_id`
- `delivery_tracking_events.delivery_id` → `delivery_orders.delivery_id`
- `shipments.order_id` → `orders.order_id`
- `logistics_routes.shipment_id` → `shipments.shipment_id`
- `shipment_alerts.shipment_id` → `shipments.shipment_id`

**Packaging & Returns:**
- `packaging_jobs.order_id` → `orders.order_id`
- `repair_requests.order_id` → `orders.order_id`
- `receipt_records.order_id` → `orders.order_id`
- `receipt_records.package_id` → `packaging_jobs.package_id`
- `product_returns.order_id` → `orders.order_id`
- `return_growth_statistics.return_request_id` → `product_returns.return_request_id`

**Demand Forecasting:**
- `forecast_performance_metrics.forecast_id` → `demand_forecasts.forecast_id`
- `forecast_timeseries.forecast_id` → `demand_forecasts.forecast_id`

**UI Subsystem:**
- `ui_sessions.user_id` → `ui_users.user_id`
- `ui_panel_state.user_id` → `ui_users.user_id`
- `ui_notifications.user_id` → `ui_users.user_id`
- `ui_notification_preferences.user_id` → `ui_users.user_id`
- `audit_log.approval_id` → `approval_requests.approval_id`
- `profitability_analytics.approval_id` → `approval_requests.approval_id`

---

## Design Principles Applied

### SOLID Principles
- **Single Responsibility:** Each table owns exactly one business concept
- **Open/Closed:** New price types, tier levels, zones, etc., can be added as new rows without altering table structure
- **Dependency Inversion:** Cross-subsystem references use VARCHAR FKs for flexibility

### GRASP Principles
- **Information Expert:** Each table stores data it is the natural owner of
- **Low Coupling:** External references are not enforced as database FKs, allowing external teams to evolve their schemas independently

### Design Patterns Referenced
- **Singleton:** DatabaseConnectionPool (one pool for all DAOs)
- **Factory:** DAOFactory (produces correct DAO instance)
- **Facade:** SupplyChainFacade (single entry point)
- **Observer:** EventBus (cross-subsystem event wiring)
- **Adapter:** BarcodeReaderAdapter, ExternalCRMAdapter

---

## Query Performance Considerations

- **Unique Keys:** Applied on frequently searched columns for optimal index utilization
- **Optimistic Locking:** Implemented via `version` columns in concurrent-access tables
- **Read-Only References:** External subsystem references stored as VARCHAR for flexibility
- **Derived Columns:** Some tables use GENERATED columns (e.g., `proc_po_items.pending_qty`)
- **Cascading Deletes:** Applied where appropriate to maintain referential integrity

---

**End of Schema Reference**
