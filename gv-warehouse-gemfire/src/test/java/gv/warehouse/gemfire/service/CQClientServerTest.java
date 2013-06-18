package gv.warehouse.gemfire.service;

import gv.test.IntegrationTest;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration()
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CQClientServerTest extends ClientServerTest {

}
