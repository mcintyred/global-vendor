package gv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;
import gv.test.UnitTest;
import gv.warehouse.api.WarehouseService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.MessageDeliveryException;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
@Category(UnitTest.class)
public class RoutingTests {
	
	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	@Qualifier("inboundRequests")
	private MessageChannel inputChannel;
	
	@Autowired
	@Qualifier("outbound")
	private SubscribableChannel outputChannel;
	
	
	@Test(expected = MessageDeliveryException.class)
	public void shouldRejectInvalidWarehouseId() {
		// given
		Message<String> message = MessageBuilder.withPayload("").setHeader("GV_WarehouseId", 99).build();
		
		// when
		inputChannel.send(message);
		
		// then
		// should have caught an exception
				
	}

	@Test(expected = MessagingException.class)
	public void shouldRejectInvalidMethod() {
		// given
		Message<String> message = MessageBuilder.withPayload("")
				.setHeader("GV_WarehouseId", 1)
				.setHeader("GV_Method", "undefined")
				.build();
		
		// when
		inputChannel.send(message);
		
		// then
		// should have caught an exception
				
	}
	
	@Test
	public void shouldSetStock() throws InterruptedException {
		// given
		StockChangeRequest request = new StockChangeRequest(1L,3L,5);
		Message<StockChangeRequest> message = MessageBuilder.withPayload(request)
				.setHeader("GV_WarehouseId", 1)
				.setHeader("GV_Method", "set-stock")
				.setReplyChannel(outputChannel)
				.build();
		
		ArgumentCaptor<StockChangeRequest> captor = ArgumentCaptor.forClass(StockChangeRequest.class);
		given(warehouseService.setStock(captor.capture())).willReturn(55);
		
		
		CountDownHandler handler = new CountDownHandler() {
			
			@Override
			protected void verifyMessage(Message<?> message) {
				Integer newStock = (Integer)message.getPayload();
				assertEquals(new Integer(55), newStock);
			}
		};

		
		// when
		boolean sent = sendAndExpectResponse(message, handler);
		
		
		// then		
		assertTrue("message not sent to expected output channel", sent);
		assertEquals(request.getWarehouseId(), captor.getValue().getWarehouseId());
		assertEquals(request.getProductId(), captor.getValue().getProductId());
		assertEquals(request.getQty(), captor.getValue().getQty());
	}

	@Test
	public void shouldUpdateStock() throws InterruptedException {
		// given
		StockChangeRequest request = new StockChangeRequest(1L,3L,5);
		Message<StockChangeRequest> message = MessageBuilder.withPayload(request)
				.setHeader("GV_WarehouseId", 1)
				.setHeader("GV_Method", "update-stock")
				.setReplyChannel(outputChannel)
				.build();
		
		ArgumentCaptor<StockChangeRequest> captor = ArgumentCaptor.forClass(StockChangeRequest.class);
		given(warehouseService.updateStock(captor.capture())).willReturn(55);
		
		CountDownHandler handler = new CountDownHandler() {
			
			@Override
			protected void verifyMessage(Message<?> message) {
				Integer newStock = (Integer)message.getPayload();
				assertEquals(new Integer(55), newStock);
			}
		};
		
		// when
		boolean sent = sendAndExpectResponse(message, handler);
		
		// then
		assertTrue("message not sent to expected output channel", sent);
		assertEquals(request.getWarehouseId(), captor.getValue().getWarehouseId());
		assertEquals(request.getProductId(), captor.getValue().getProductId());
		assertEquals(request.getQty(), captor.getValue().getQty());

	}

	@Test
	public void shouldGetStock() throws InterruptedException {
		// given
		StockQueryRequest request = new StockQueryRequest(1L, 3L);
		
		Message<StockQueryRequest> message = MessageBuilder.withPayload(request)
				.setHeader("GV_WarehouseId", 1)
				.setHeader("GV_Method", "get-stock")
				.setReplyChannel(outputChannel)
				.build();
		
		ArgumentCaptor<StockQueryRequest> captor = ArgumentCaptor.forClass(StockQueryRequest.class);
		given(warehouseService.getStock(captor.capture())).willReturn(5);
		
		CountDownHandler handler = new CountDownHandler() {
			
			@Override
			protected void verifyMessage(Message<?> message) {
				Integer newStock = (Integer)message.getPayload();
				assertEquals(new Integer(5), newStock);
			}
		};
		
		// when
		boolean sent = sendAndExpectResponse(message, handler);
		
		// then
		assertTrue("message not sent to expected output channel", sent);
		assertEquals(request.getWarehouseId(), captor.getValue().getWarehouseId());
		assertEquals(request.getProductId(), captor.getValue().getProductId());
	}
	
	@Test
	public void shouldRequestShipment() throws InterruptedException {
		// given
		Long warehouseId = 1L;
		Long productId = 3L;
		int qty = 5;
		
		ShipmentRequest request = new ShipmentRequest(warehouseId, productId, qty);
		
		Message<ShipmentRequest> message = MessageBuilder.withPayload(request)
				.setHeader("GV_WarehouseId", warehouseId)
				.setHeader("GV_Method", "request-shipment")
				.setReplyChannel(outputChannel)
				.build();
		
		given(warehouseService.requestShipment(any(ShipmentRequest.class))).willReturn(new ShipmentConfirmation(productId, new LocalDate(), qty));
		
		CountDownHandler handler = new CountDownHandler() {
			
			@Override
			protected void verifyMessage(Message<?> message) {
				String response =(String) message.getPayload();
				ObjectMapper mapper = new ObjectMapper();
				ShipmentConfirmation confirmation;
				try {
					confirmation = mapper.readValue(response, ShipmentConfirmation.class);
					
					// Then
					assertEquals(new Long(3), confirmation.getProductId());
					assertEquals(5, confirmation.getQty());
					
				} catch (Exception e) {
					assertNull(e);

				}

			}
		};
		
		// when
		boolean sent = sendAndExpectResponse(message, handler);
		
		// then
		assertTrue("message not sent to expected output channel", sent);
	}
	
	protected boolean sendAndExpectResponse(Message message, CountDownHandler handler) throws InterruptedException {
		

		CountDownLatch latch = new CountDownLatch(1);
		handler.setLatch(latch);
		
		outputChannel.subscribe(handler);

		inputChannel.send(message);
		
		boolean latchCountedToZero = latch.await(2000L, TimeUnit.MILLISECONDS);
		return latchCountedToZero;
		
	}
	
	/*
	 * A MessageHandler that uses a CountDownLatch to synchronize with the calling thread
	 */
	private abstract class CountDownHandler implements MessageHandler {

		CountDownLatch latch;

		public final void setLatch(CountDownLatch latch){
			this.latch = latch;
		}

		protected abstract void verifyMessage(Message<?> message);

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.springframework.integration.core.MessageHandler#handleMessage
		 * (org.springframework.integration.Message)
		 */
		public void handleMessage(Message<?> message) throws MessagingException {
			verifyMessage(message);
			latch.countDown();
		}
	}

}
