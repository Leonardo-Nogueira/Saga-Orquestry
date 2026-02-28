package org.leonardonogueira.application.orchestrator;

import org.leonardonogueira.application.dto.SagaKey;
import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.EventStatusEnum;
import org.leonardonogueira.application.enums.EventTopicsEnum;

import java.util.Map;

import static org.leonardonogueira.application.enums.EventSourceEnum.*;
import static org.leonardonogueira.application.enums.EventSourceEnum.ORCHESTRATOR;
import static org.leonardonogueira.application.enums.EventStatusEnum.*;
import static org.leonardonogueira.application.enums.EventTopicsEnum.*;

public final class OrchestratorSagaHandler {

    private static final Map<SagaKey, EventTopicsEnum> HANDLER = Map.ofEntries(
            Map.entry(new SagaKey(ORCHESTRATOR, SUCCESS), PRODUCT_SUCCESS),
            Map.entry(new SagaKey(ORCHESTRATOR, FAILED), FINISH_FAIL),

            Map.entry(new SagaKey(PRODUCT_SERVICE, ROLLBACK), PRODUCT_FAIL),
            Map.entry(new SagaKey(PRODUCT_SERVICE, FAILED), FINISH_FAIL),
            Map.entry(new SagaKey(PRODUCT_SERVICE, SUCCESS), PAYMENT_SUCCESS),

            Map.entry(new SagaKey(PAYMENT_SERVICE, ROLLBACK), PAYMENT_FAIL),
            Map.entry(new SagaKey(PAYMENT_SERVICE, FAILED), PRODUCT_FAIL),
            Map.entry(new SagaKey(PAYMENT_SERVICE, SUCCESS), INVENTORY_SUCCESS),

            Map.entry(new SagaKey(INVENTORY_SERVICE, ROLLBACK), INVENTORY_FAIL),
            Map.entry(new SagaKey(INVENTORY_SERVICE, FAILED), PAYMENT_FAIL),
            Map.entry(new SagaKey(INVENTORY_SERVICE, SUCCESS), FINISH_SUCCESS)

    );

    public static EventTopicsEnum getNextTopic(EventSourceEnum source, EventStatusEnum status) {
        return HANDLER.get(new SagaKey(source, status));
    }

}
