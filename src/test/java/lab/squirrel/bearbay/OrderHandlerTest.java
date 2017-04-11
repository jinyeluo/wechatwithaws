package lab.squirrel.bearbay;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderHandlerTest {

    private OrderHandler hander;
    private Products products;

    @Before
    public void setup() {
        hander = new OrderHandler();
        products = new Products();
    }

    @Test
    public void testRegex() {
        assertFalse(OrderHandler.ORDER_PATTERN.matcher("AB#1.#1").find());
        assertFalse(OrderHandler.ORDER_PATTERN.matcher("A#1.#1").find());
        assertFalse(OrderHandler.ORDER_PATTERN.matcher("A#1#1.0").find());

        assertFalse(OrderHandler.WAIT_TIME_PATTERN.matcher("3m").find());

        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#1#1").find());
        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#1.5#1").find());
        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#1.5#10").find());
        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#10.5#10").find());

        assertTrue(OrderHandler.WAIT_TIME_PATTERN.matcher("0H30").find());
        assertTrue(OrderHandler.WAIT_TIME_PATTERN.matcher("0H").find());
        assertTrue(OrderHandler.WAIT_TIME_PATTERN.matcher("3H").find());

    }

    @Test
    public void goodOrder() throws Exception {
        StringBuilder answer = new StringBuilder();

        String order1 = hander.order("id1", " a#2.5 # 3", products);
        assertEquals("\uD83D\uDE1Epls use menu to select 堂吃 or 外卖", order1);

        hander.newOrder("id1", Order.DINE_IN, answer);
        String order2 = hander.order("id1", " a#2.0 # 3", products);
        assertEquals("缅因大龙虾 2.0磅 辣度 3\n堂吃\n", order2);

        String order3 = hander.order("id1", " b # 2 # 10", products);
        assertEquals("缅因大龙虾 2.0磅 辣度 3\n路易斯安那小龙虾 2磅 辣度 10\n堂吃\n",
            order3);
    }

    @Test
    public void badOrder() throws Exception {
        StringBuilder answer = new StringBuilder();
        String order1 = hander.order("id1", " a#2.5 # 3", products);
        assertEquals("\uD83D\uDE1Epls use menu to select 堂吃 or 外卖", order1);

        hander.newOrder("id1", Order.TO_GO, answer);
        hander.order("id1", " a#2.5 # 3", products);
        String order2 = hander.order("id1", " a#2.5 # 30", products);
        assertEquals("\uD83D\uDE1Espicy level can only be 1 - 10", order2);

        String order3 = hander.order("id1", " a#2.4 # 30", products);
        assertEquals("\uD83D\uDE1Eorder can be at least one pound, increased by half pound", order3);
    }
    @Test
    public void newOrder() throws Exception {
        StringBuilder answer = new StringBuilder();

        assertTrue(hander.newOrder("id1", Order.DINE_IN, answer));
        assertTrue(answer.length() == 0);
    }

    @Test
    public void confirm() throws Exception {
        StringBuilder answer = new StringBuilder();

        assertFalse(hander.confirm("id1", products, answer));
        assertEquals("Hah???", answer.toString());

        answer.setLength(0);
        hander.newOrder("id1", Order.TO_GO, answer);

        answer.setLength(0);
        assertFalse(hander.confirm("id1", products, answer));
        assertEquals("nothing to confirm", answer.toString());

        hander.order("id1", " a#2.5 # 3", products);
        hander.order("id1", " c#2 # 1", products);
        answer.setLength(0);
        assertTrue(hander.confirm("id1", products, answer));
        assertEquals("缅因大龙虾 2.5磅 辣度 3\n大虾 2磅 辣度 1\n外卖\n✔Order# 1",
            answer.toString());
    }

    @Test
    public void confirmWithTime() throws Exception {
        StringBuilder answer = new StringBuilder();
        hander.newOrder("id1", Order.TO_GO, answer);
        hander.order("id1", " 2h15 ", products);
        hander.order("id1", " 0h ", products);
        hander.order("id1", " 3h ", products);
        hander.order("id1", " c#2 # 1", products);
        answer.setLength(0);
        hander.confirm("id1", products, answer);
        assertTrue(answer.toString().contains("取货时间"));
    }

    @Test
    public void cancel() throws Exception {
        String failed = hander.cancel("id1");
        assertEquals("Hah???", failed);

        StringBuilder answer = new StringBuilder();
        hander.newOrder("id1", Order.TO_GO, answer);
        String cancelled = hander.cancel("id1");
        assertEquals("cancelled", cancelled);

        String failed2 = hander.order("id1", " 2:15 ", products);
        assertEquals("\uD83D\uDE1Epls use menu to select 堂吃 or 外卖", failed2);
    }

}
