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

        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#1#1").find());
        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#1.5#1").find());
        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#1.5#10").find());
        assertTrue(OrderHandler.ORDER_PATTERN.matcher("A#10.5#10").find());
    }

    @Test
    public void goodOrder() throws Exception {
        String order1 = hander.order("id1", " a#2.5 # 3", products);
        assertEquals("\uD83D\uDE1Epls use menu to select 堂吃 or 外卖", order1);

        hander.newOrder("id1", Order.DINE_IN);
        String order2 = hander.order("id1", " a#2.0 # 3", products);
        assertEquals("缅因大龙虾 2.0磅 辣度 3\n堂吃\n", order2);

        String order3 = hander.order("id1", " b # 2 # 10", products);
        assertEquals("缅因大龙虾 2.0磅 辣度 3\n路易斯安那小龙虾 2磅 辣度 10\n堂吃\n",
            order3);
    }

    @Test
    public void badOrder() throws Exception {
        String order1 = hander.order("id1", " a#2.5 # 3", products);
        assertEquals("\uD83D\uDE1Epls use menu to select 堂吃 or 外卖", order1);

        hander.newOrder("id1", Order.TO_GO);
        hander.order("id1", " a#2.5 # 3", products);
        String order2 = hander.order("id1", " a#2.5 # 30", products);
        assertEquals("\uD83D\uDE1Espicy level can only be 1 - 10", order2);

        String order3 = hander.order("id1", " a#2.4 # 30", products);
        assertEquals("\uD83D\uDE1Eorder can be at least one pound, increased by half pound", order3);
    }
    @Test
    public void newOrder() throws Exception {
        String newOrder = hander.newOrder("id1", Order.DINE_IN);
        assertEquals("龙虾热卖中", newOrder);
    }

    @Test
    public void confirm() throws Exception {
        String failed1 = hander.confirm("id1", products);
        assertEquals("Hah???", failed1);

        hander.newOrder("id1", Order.TO_GO);
        String failed2 = hander.confirm("id1", products);
        assertEquals("nothing to confirm", failed2);

        hander.order("id1", " a#2.5 # 3", products);
        hander.order("id1", " c#2 # 1", products);
        String confirmed = hander.confirm("id1", products);
        assertEquals("缅因大龙虾 2.5磅 辣度 3\n大虾 2磅 辣度 1\n外卖\n✔Order# 1",
            confirmed);
    }

    @Test
    public void confirmWithTime() throws Exception {
        hander.newOrder("id1", Order.TO_GO);
        hander.order("id1", " 2:15 ", products);
        hander.order("id1", " c#2 # 1", products);
        String confirmed = hander.confirm("id1", products);
        assertTrue(confirmed.contains("取货时间"));
    }

    @Test
    public void cancel() throws Exception {
        String failed = hander.cancel("id1");
        assertEquals("Hah???", failed);

        hander.newOrder("id1", Order.TO_GO);
        String cancelled = hander.cancel("id1");
        assertEquals("cancelled", cancelled);

        String failed2 = hander.order("id1", " 2:15 ", products);
        assertEquals("\uD83D\uDE1Epls use menu to select 堂吃 or 外卖", failed2);
    }

}
