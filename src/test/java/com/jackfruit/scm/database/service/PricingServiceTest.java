package com.jackfruit.scm.database.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jackfruit.scm.database.adapter.PricingAdapter;
import com.jackfruit.scm.database.dao.PriceListDao;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.facade.subsystem.PricingSubsystemFacade;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.PricingModels.Promotion;
import com.jackfruit.scm.database.model.PricingModels.TierDefinition;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pricing Subsystem Flow Tests")
class PricingServiceTest {

    private static final LocalDateTime EFFECTIVE_FROM = LocalDateTime.of(2026, 4, 20, 9, 0);
    private static final LocalDateTime EFFECTIVE_TO = LocalDateTime.of(2026, 4, 30, 18, 0);

    @Test
    @DisplayName("publishPrice flows from adapter to facade to subsystem to service and persists the price")
    void publishPriceFlowsAcrossSubsystemLayers() {
        InMemoryPriceListDao priceListDao = new InMemoryPriceListDao();
        RecordingJdbcOperations jdbcOperations = new RecordingJdbcOperations();
        PricingService pricingService = new PricingService(priceListDao);
        PricingSubsystemFacade pricingSubsystemFacade = new PricingSubsystemFacade(pricingService, jdbcOperations);
        TestSupplyChainDatabaseFacade facade = new TestSupplyChainDatabaseFacade(pricingSubsystemFacade);
        PricingAdapter adapter = new PricingAdapter(facade);

        PriceList price = validPrice("PRICE-101");

        adapter.publishPrice(price);

        assertEquals(1, facade.pricingCalls(), "adapter should ask the database facade for the pricing subsystem");
        assertEquals(1, priceListDao.savedPrices.size(), "service should persist the published price");
        assertSame(price, priceListDao.savedPrices.getFirst(), "the same price object should move through the whole chain");
        assertEquals(List.of(price), facade.pricing().listPrices(), "listing prices should expose the persisted price");
    }

    @Test
    @DisplayName("invalid price data is rejected even when called through adapter and facade")
    void publishPriceRejectsInvalidDataAcrossSubsystemLayers() {
        InMemoryPriceListDao priceListDao = new InMemoryPriceListDao();
        PricingService pricingService = new PricingService(priceListDao);
        PricingSubsystemFacade pricingSubsystemFacade =
                new PricingSubsystemFacade(pricingService, new RecordingJdbcOperations());
        PricingAdapter adapter = new PricingAdapter(new TestSupplyChainDatabaseFacade(pricingSubsystemFacade));

        PriceList invalidPrice = new PriceList(
                "PRICE-INVALID",
                "SKU-404",
                "SOUTH",
                "RETAIL",
                "RETAIL",
                new BigDecimal("100.00"),
                new BigDecimal("120.00"),
                "INR",
                EFFECTIVE_FROM,
                EFFECTIVE_TO,
                "ACTIVE");

        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class, () -> adapter.publishPrice(invalidPrice));

        assertEquals("priceFloor cannot be greater than basePrice", error.getMessage());
        assertTrue(priceListDao.savedPrices.isEmpty(), "invalid data should never reach the DAO save operation");
    }

    @Test
    @DisplayName("pricing side-record creation routes from adapter to subsystem JDBC operations")
    void createPromotionAndTierDefinitionUseJdbcOperationsThroughFacade() {
        RecordingJdbcOperations jdbcOperations = new RecordingJdbcOperations();
        PricingSubsystemFacade pricingSubsystemFacade =
                new PricingSubsystemFacade(new PricingService(new InMemoryPriceListDao()), jdbcOperations);
        PricingAdapter adapter = new PricingAdapter(new TestSupplyChainDatabaseFacade(pricingSubsystemFacade));

        Promotion promotion = new Promotion(
                "PROMO-15",
                "Summer Offer",
                "SUMMER15",
                "PERCENTAGE",
                new BigDecimal("15.00"),
                LocalDateTime.of(2026, 5, 1, 0, 0),
                LocalDateTime.of(2026, 5, 15, 23, 59),
                "SKU-APPLE,SKU-JACKFRUIT",
                new BigDecimal("500.00"),
                250,
                0);
        TierDefinition tierDefinition =
                new TierDefinition(3, "Gold", new BigDecimal("10000.00"), new BigDecimal("8.50"));

        adapter.createPromotion(promotion);
        adapter.createTierDefinition(tierDefinition);

        assertEquals(2, jdbcOperations.executedUpdates.size(), "both subsystem operations should reach JdbcOperations");

        RecordedUpdate promotionInsert = jdbcOperations.executedUpdates.get(0);
        assertTrue(promotionInsert.sql.contains("INSERT INTO promotions"));
        assertEquals("PROMO-15", promotionInsert.parameters.get(1));
        assertEquals("SUMMER15", promotionInsert.parameters.get(3));
        assertEquals(new BigDecimal("15.00"), promotionInsert.parameters.get(5));
        assertEquals(250, promotionInsert.parameters.get(10));

        RecordedUpdate tierInsert = jdbcOperations.executedUpdates.get(1);
        assertTrue(tierInsert.sql.contains("INSERT INTO tier_definitions"));
        assertEquals(3, tierInsert.parameters.get(1));
        assertEquals("Gold", tierInsert.parameters.get(2));
        assertEquals(new BigDecimal("10000.00"), tierInsert.parameters.get(3));
        assertEquals(new BigDecimal("8.50"), tierInsert.parameters.get(4));
    }

    @Test
    @DisplayName("deletePrice flows from adapter to facade to service and removes the price")
    void deletePriceFlowsAcrossSubsystemLayers() {
        InMemoryPriceListDao priceListDao = new InMemoryPriceListDao();
        PricingService pricingService = new PricingService(priceListDao);
        PricingSubsystemFacade pricingSubsystemFacade =
                new PricingSubsystemFacade(pricingService, new RecordingJdbcOperations());
        PricingAdapter adapter = new PricingAdapter(new TestSupplyChainDatabaseFacade(pricingSubsystemFacade));

        PriceList price = validPrice("PRICE-DELETE");
        priceListDao.save(price);

        adapter.deletePrice("PRICE-DELETE");

        assertTrue(priceListDao.findById("PRICE-DELETE").isEmpty(), "delete should remove the stored price");
    }

    private static PriceList validPrice(String priceId) {
        return new PriceList(
                priceId,
                "SKU-101",
                "SOUTH",
                "RETAIL",
                "STANDARD",
                new BigDecimal("120.00"),
                new BigDecimal("100.00"),
                "INR",
                EFFECTIVE_FROM,
                EFFECTIVE_TO,
                "ACTIVE");
    }

    private static final class TestSupplyChainDatabaseFacade extends SupplyChainDatabaseFacade {

        private final PricingSubsystemFacade pricingSubsystemFacade;
        private int pricingCalls;

        private TestSupplyChainDatabaseFacade(PricingSubsystemFacade pricingSubsystemFacade) {
            super(pricingSubsystemFacade);
            this.pricingSubsystemFacade = pricingSubsystemFacade;
        }

        @Override
        public PricingSubsystemFacade pricing() {
            pricingCalls++;
            return pricingSubsystemFacade;
        }

        int pricingCalls() {
            return pricingCalls;
        }

        @Override
        public void close() {
            // The test facade never opens real connections.
        }
    }

    private static final class InMemoryPriceListDao implements PriceListDao {

        private final List<PriceList> savedPrices = new ArrayList<>();

        @Override
        public void save(PriceList entity) {
            savedPrices.add(entity);
        }

        @Override
        public void update(PriceList entity) {
            OptionalInt index = findIndex(entity.getPriceId());
            if (index.isPresent()) {
                savedPrices.set(index.getAsInt(), entity);
                return;
            }
            savedPrices.add(entity);
        }

        @Override
        public void deleteById(String priceId) {
            findIndex(priceId).ifPresent(savedPrices::remove);
        }

        @Override
        public Optional<PriceList> findById(String priceId) {
            return savedPrices.stream()
                    .filter(price -> price.getPriceId().equals(priceId))
                    .findFirst();
        }

        @Override
        public List<PriceList> findAll() {
            return List.copyOf(savedPrices);
        }

        private OptionalInt findIndex(String priceId) {
            for (int index = 0; index < savedPrices.size(); index++) {
                if (savedPrices.get(index).getPriceId().equals(priceId)) {
                    return OptionalInt.of(index);
                }
            }
            return OptionalInt.empty();
        }
    }

    private static final class RecordingJdbcOperations extends JdbcOperations {

        private final List<RecordedUpdate> executedUpdates = new ArrayList<>();

        @Override
        public void update(String sql, SqlConsumer<PreparedStatement> binder) {
            Map<Integer, Object> parameters = new LinkedHashMap<>();
            PreparedStatement preparedStatement = (PreparedStatement) Proxy.newProxyInstance(
                    PreparedStatement.class.getClassLoader(),
                    new Class<?>[]{PreparedStatement.class},
                    (proxy, method, args) -> {
                        String methodName = method.getName();
                        if (methodName.startsWith("set") && args != null && args.length >= 2 && args[0] instanceof Integer parameterIndex) {
                            Object value = args[1];
                            if ("setNull".equals(methodName)) {
                                value = "SQL_NULL(" + sqlTypeName((Integer) args[1]) + ")";
                            }
                            parameters.put(parameterIndex, value);
                            return null;
                        }
                        if ("close".equals(methodName)) {
                            return null;
                        }
                        if ("isClosed".equals(methodName)) {
                            return false;
                        }
                        if ("unwrap".equals(methodName)) {
                            return null;
                        }
                        if ("isWrapperFor".equals(methodName)) {
                            return false;
                        }
                        throw new UnsupportedOperationException("Unsupported PreparedStatement method in test: " + methodName);
                    });

            try {
                binder.accept(preparedStatement);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            executedUpdates.add(new RecordedUpdate(sql, parameters));
        }

        private String sqlTypeName(int sqlType) {
            return switch (sqlType) {
                case Types.INTEGER -> "INTEGER";
                case Types.TIMESTAMP -> "TIMESTAMP";
                default -> String.valueOf(sqlType);
            };
        }
    }

    private record RecordedUpdate(String sql, Map<Integer, Object> parameters) {
    }
}
