package observable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.service.OrderStateChangeService;

public class ObserverIntegrationTest {
	@Test
    public void whenChangingPCLNewsAgencyState_thenONewsChannelNotified() {

        Order observable = new Order();
        OrderStateChangeService observer = new OrderStateChangeService();

        observable.addPropertyChangeListener(observer);

        observable.setCompleted(3);
        assertEquals(observer.getCompleted(), 3);
    }
}
