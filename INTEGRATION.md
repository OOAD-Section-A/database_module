# Database Module Integration Guide

## What This Module Does

This module is the shared database layer for all subsystems.

Subsystems should:

- use the database module JAR
- configure database connection details
- call the provided adapter classes

Subsystems should **not**:

- connect to MySQL directly
- write their own SQL for shared database operations
- run `schema.sql` manually in normal usage

## Main Rule

Use the adapter classes in:

```java
com.jackfruit.scm.database.adapter
```

Each subsystem should talk to the database through its adapter, not through direct JDBC code.

## JAR Files To Use

After building this project, the main output JAR is:

```text
dist/database-module-1.0.0-SNAPSHOT-standalone.jar
```

This JAR contains:

- the database module classes
- required runtime dependencies
- the embedded `schema.sql`

This project also expects the exception handler JAR to be available:

```text
dist/scm-exception-handler-v3.jar
```

## How Subsystems Should Connect

### 1. Add the JAR to the subsystem

If the subsystem is a Maven project, install or publish this module and add it as a dependency.

If the subsystem is not Maven-based, add these JARs to its classpath:

- `database-module-1.0.0-SNAPSHOT-standalone.jar`
- `scm-exception-handler-v3.jar`

### 2. Provide database configuration

The subsystem must provide valid MySQL connection settings.

Supported configuration sources:

1. JVM system properties
2. environment variables
3. `database.properties`

Supported keys:

| Setting | Environment Variable | Required |
|---|---|---|
| `db.url` | `DB_URL` | Yes |
| `db.username` | `DB_USERNAME` | Yes |
| `db.password` | `DB_PASSWORD` | Yes |
| `db.pool.size` | `DB_POOL_SIZE` | No |

Example values:

```properties
db.url=jdbc:mysql://localhost:3306/OOAD
db.username=root
db.password=your_password
db.pool.size=5
```

### 3. Instantiate the facade and adapter

Example:

```java
import com.jackfruit.scm.database.adapter.InventoryAdapter;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;

public class InventorySubsystemApp {
    public static void main(String[] args) {
        try (SupplyChainDatabaseFacade facade = new SupplyChainDatabaseFacade()) {
            InventoryAdapter inventoryAdapter = new InventoryAdapter(facade);

            inventoryAdapter.listProducts()
                    .forEach(product -> System.out.println(product.productName()));
        }
    }
}
```

## Schema Setup Behavior

Subsystem teams do **not** need to manually run `schema.sql` in normal integration.

When `SupplyChainDatabaseFacade` starts, the module automatically:

1. reads the DB configuration
2. connects to MySQL
3. creates the target database if it does not already exist
4. checks whether the schema already exists
5. applies the embedded `schema.sql` only if required

Important:

- the module does **not** drop and recreate the database on every run
- the module bootstraps the schema automatically when needed
- MySQL must still be running and reachable
- the DB user must have enough permissions to create the database and tables when bootstrap is needed

## What Subsystem Teams Need To Do

Each subsystem team should follow this exact process:

1. Add the required JAR files to their subsystem.
2. Set valid database credentials.
3. Make sure MySQL is running.
4. Use the correct adapter for their subsystem.
5. Call adapter methods for create, read, update, and delete operations.

They do **not** need to:

- open MySQL and run `schema.sql` manually
- create separate DAOs
- build their own database connection layer

## Available Adapters

- `PricingAdapter`
- `OrderAdapter`
- `InventoryAdapter`
- `WarehouseManagementAdapter`
- `LogisticsAdapter`
- `ReportingAdapter`
- `BarcodeTrackingAdapter`
- `BarcodeReaderAdapter`
- `DeliveryOrdersAdapter`
- `DeliveryMonitoringAdapter`
- `OrderFulfillmentAdapter`
- `DemandForecastingAdapter`
- `CommissionAdapter`
- `PackagingAdapter`
- `ReturnsAdapter`
- `StockLedgerAdapter`
- `UiAdapter`
- `ExceptionHandlingAdapter`

## Build The JAR

From the `database_module` folder:

```bash
mvn clean package
```

Output:

```text
dist/database-module-1.0.0-SNAPSHOT-standalone.jar
```

## Quick Start For Other Teams

If another subsystem wants to use this module, they only need to:

1. get the JAR files
2. configure `db.url`, `db.username`, and `db.password`
3. instantiate `SupplyChainDatabaseFacade`
4. use the correct adapter

That is all.

## Exception Handling Integration

The database module integrates with the SCM Exception Handler subsystem for comprehensive error tracking and reporting.

### Files Required

The following JAR files from the Exception Handler subsystem should be available in the `dist/` folder:

- `scm-exception-handler-v3.jar` - Main exception handler with all subsystem classes
- `scm-exception-viewer-gui.jar` - GUI for viewing exceptions
- `jna-5.18.1.jar` - Windows Event Viewer integration
- `jna-platform-5.18.1.jar` - Windows Event Viewer integration

### One-time Setup

1. **Register Event Log Source** (Administrator Command Prompt required):

```cmd
reg add "HKLM\SYSTEM\CurrentControlSet\Services\EventLog\Application\SCM-DatabaseDesign" /v EventMessageFile /t REG_SZ /d "%SystemRoot%\System32\EventCreate.exe" /f
```

2. **Add JARs to Project Classpath**:
   - Add all four JAR files to your project's build path
   - In Maven: add them as external dependencies
   - In IDE: right-click project → Build Path/Project Structure → Add external libraries

### Exception Handling in Database Adapters

Each adapter class should use `DatabaseDesignSubsystem` for exception handling:

```java
import com.scm.exception.DatabaseDesignSubsystem;

public class InventoryAdapter {
    private final DatabaseDesignSubsystem exceptions = DatabaseDesignSubsystem.INSTANCE;

    public void listProducts() {
        try {
            // adapter logic
        } catch (SQLException e) {
            exceptions.onDbConnectionFailed("localhost:3306");
            return;
        } catch (Exception e) {
            exceptions.raise(0, "UNREGISTERED_EXCEPTION", 
                           "Error: " + e.getMessage(), 
                           Severity.MINOR);
            return;
        }
    }
}
```

### Common Database Exceptions

| Exception | Method | Severity |
|-----------|--------|----------|
| Connection Failed | `exceptions.onDbConnectionFailed(host)` | MAJOR |
| Query Timeout | `exceptions.onQueryTimeout(query, elapsedMs)` | WARNING |
| Deadlock | `exceptions.onTransactionDeadlock(entityId, operation)` | MAJOR |
| Record Not Found | `exceptions.onRecordNotFound(entityType, entityId)` | MINOR |
| Duplicate Primary Key | `exceptions.onDuplicatePrimaryKey(entityType, key)` | MAJOR |
| Foreign Key Violation | `exceptions.onForeignKeyViolation(childEntity, parentEntity, key)` | MAJOR |
| Schema Mismatch | `exceptions.onSchemaVersionMismatch(expected, actual)` | MAJOR |

### Running the Exception Viewer GUI

Place all four JAR files in the same folder and run:

```bash
java -cp .;"scm-exception-handler-v3.jar";"scm-exception-viewer-gui.jar";"jna-5.18.1.jar";"jna-platform-5.18.1.jar" com.scm.gui.ExceptionViewerGUI
```

Select your subsystem name on first launch (automatically saved).

### Viewing Exceptions in Windows Event Viewer

1. Open Event Viewer: `Win + R` → `eventvwr` → Enter
2. Navigate to: Event Viewer (Local) > Windows Logs > Application
3. Right-click Application → Filter Current Log
4. In Event sources, type: `SCM-DatabaseDesign`
5. Click OK

### Testing Exception Integration

Trigger one exception and verify all three occur:

1. A blocking popup appears with Exception ID, Name, Message, and Subsystem
2. Exception appears in the GUI after refresh
3. Exception appears in Windows Event Viewer under Application > SCM-DatabaseDesign

### Troubleshooting

- **Popup not appearing**: Ensure all four JARs are on the classpath
- **GUI shows nothing**: Run the `reg add` command as Administrator first
- **Event Viewer shows nothing**: Same as above, `reg add` must be run first

### Reference

For detailed exception method signatures, see `Exception_Method_Reference.docx` in the repository root.

## Notes

- If startup fails, first check DB URL, username, password, and whether MySQL is running.
- If schema bootstrap fails, check whether the DB user has create privileges.
- If an adapter does not expose an operation a subsystem needs, extend the database module instead of bypassing it.
- Exception data is stored locally in Windows Event Viewer; nothing is stored in the database.
- All exception handling is automatic once methods are called; no additional logging or UI code is needed.
