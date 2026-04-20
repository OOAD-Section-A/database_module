# Database Module JAR Integration

## Purpose

The database module is packaged as a JAR so other Java subsystems can reuse the
same database access code instead of writing SQL connection logic again.

The main entry point is:

```java
com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade
```

Subsystems can either call the facade directly or use the subsystem-specific
adapter classes in:

```java
com.jackfruit.scm.database.adapter
```

## JAR Location

After building the project, the integration JAR is created at:

```text
dist/database-module-1.0.0-SNAPSHOT-standalone.jar
```

This standalone JAR includes the database module and its required runtime
dependencies, so it is the correct file to share with non-Maven subsystems.
It also packages the canonical `schema.sql` and supporting SQL resources.

Build it with:

```bash
mvn clean package
```

To install it into the local Maven repository:

```bash
mvn install
```

## Automatic Schema Initialization

The database schema is automatically initialized every time the module runs. When a subsystem
instantiates `SupplyChainDatabaseFacade` or calls an adapter:

1. The module drops the existing `OOAD` database (if it exists).
2. The module automatically creates a fresh database with all tables, views, and
   relationships from the embedded `schema.sql`.
3. This ensures you always have the latest schema version.
4. No manual schema setup or SQL script execution is required.

**Important**: The schema is **always recreated**, so any local data in the `OOAD`
database will be lost on each run. This is by design to ensure schema updates
are always applied.

### Database Connection Configuration

Subsystems must provide MySQL database connection details via **environment
variables** or **JVM system properties** (in order of precedence):

| Property | Environment Variable | Required | Example |
|----------|----------------------|----------|----------|
| `db.url` | `DB_URL` | **Yes** | `jdbc:mysql://localhost:3306` |
| `db.username` | `DB_USERNAME` | **Yes** | `app_user` |
| `db.password` | `DB_PASSWORD` | **Yes** | `SecurePassword123` |
| `db.pool.size` | `DB_POOL_SIZE` | No (default: 5) | `10` |

### Credentials are Required

**Database URL, username, and password are mandatory.** The module will fail to start with a clear error
message if any of these are missing. There are no default values for credentials.

### Configuration Methods (in priority order)

1. **JVM System Properties** (highest priority):
   ```bash
   java -Ddb.url=jdbc:mysql://db:3306/OOAD \
        -Ddb.username=user \
        -Ddb.password=pass \
        -cp "lib/database-module.jar:." MySubsystem
   ```

2. **Environment Variables** (medium priority):
   ```bash
   export DB_URL=jdbc:mysql://db:3306/OOAD
   export DB_USERNAME=user
   export DB_PASSWORD=pass
   java -cp "lib/database-module.jar:." MySubsystem
   ```

3. **database.properties file** (lowest priority):
   ```properties
   db.url=jdbc:mysql://db:3306/OOAD
   db.username=user
   db.password=pass
   db.pool.size=5
   ```

The module will use whichever method is configured. System properties override
environment variables, which override the properties file.

## Maven Integration

If another subsystem is a Maven project, add this dependency to its `pom.xml`:

```xml
<dependency>
    <groupId>com.jackfruit.scm</groupId>
    <artifactId>database-module</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

This is the recommended method because Maven also brings required dependencies
like MySQL Connector/J.



## Manual JAR Integration

If the subsystem is not using Maven:

1. Add `dist/database-module-1.0.0-SNAPSHOT-standalone.jar` to the subsystem classpath.
2. Set database connection values using one of the methods above (environment variables, system properties, or database.properties file).
3. Import and call the required adapter class for your subsystem's operations.

When the subsystem instantiates the database facade or adapter:
1. The schema will automatically drop any existing `OOAD` database
2. A fresh schema will be created from the embedded SQL
3. All tables and views will be ready to use

**Credentials are required.** If you don't provide `db.url`, `db.username`, and `db.password`,
the module will throw an exception with a clear error message.

**Example - Using Environment Variables:**

```bash
# Set environment variables
export DB_URL=jdbc:mysql://localhost:3306/OOAD
export DB_USERNAME=root
export DB_PASSWORD=password

# Compile and run
javac -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar;lib/scm-exception-handler-v3.jar" MySubsystem.java
java -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar;" MySubsystem.java
java -cp "lib/database-module-1.0.0-SNAPSHOT-standalone

**Example - Using System Properties:**

```bash
javac -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar" MySubsystem.java
java -Ddb.url=jdbc:mysql://localhost:3306/OOAD \
     -Ddb.username=root \
     -Ddb.password=password \
     -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar;." MySubsystem
```

On Windows, use `;` in the classpath. On Linux/macOS, use `:`.

## Using Adapters (Required)

Subsystems **must** access database operations through adapter classes. Do **not** access
the database directly. Each adapter provides a subsystem-specific API with only the
operations needed for that subsystem.

**Example:**

```java
import com.jackfruit.scm.database.adapter.InventoryAdapter;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;

public class InventorySubsystem {
    public static void main(String[] args) {
        try (SupplyChainDatabaseFacade facade = new SupplyChainDatabaseFacade()) {
            InventoryAdapter inventoryAdapter = new InventoryAdapter(facade);

            inventoryAdapter.listProducts()
                    .forEach(product -> System.out.println(product.productName()));
        }
    }
}
```
### Available Adapters

Each subsystem has a dedicated adapter class:

- `PricingAdapter` - Pricing and discount operations
- `OrderAdapter` - Order creation and queries
- `InventoryAdapter` - Inventory management
- `WarehouseManagementAdapter` - Warehouse operations
- `LogisticsAdapter` - Logistics and shipping
- `ReportingAdapter` - Reporting and analytics
- `BarcodeTrackingAdapter` - Barcode scanning and tracking
- `DeliveryOrdersAdapter` - Delivery order management
- `OrderFulfillmentAdapter` - Order fulfillment
- `DeliveryMonitoringAdapter` - Delivery monitoring
- `DemandForecastingAdapter` - Demand forecasting
- `CommissionAdapter` - Commission tracking
- `PackagingAdapter` - Packaging operations
- `ReturnsAdapter` - Returns management
- `StockLedgerAdapter` - Stock ledger operations

## Important: Adapter-Only Access

**Subsystems must use adapters exclusively.** Do not attempt to:
- Access the database directly
- Bypass adapters to write custom SQL
- Create your own DAOs or database connections
- Access internal service classes

The adapters encapsulate all database logic and ensure consistent data access patterns
across all subsystems. Using adapters guarantees proper exception handling, data validation,
and integration with the rest of the system.

If an adapter doesn't provide the functionality you need, contact the database module
maintainers to extend it—do not create workaroundsion
types, see [EXCEPTION_HANDLING_IMPLEMENTATION.md](EXCEPTION_HANDLING_IMPLEMENTATION.md).
