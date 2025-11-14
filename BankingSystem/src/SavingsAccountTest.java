import static org.junit.Assert.*;
import java.util.ArrayList;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

public class SavingsAccountTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	public void testInterest() {
		
		Clock fixedClock = Clock.fixed(Instant.parse("2020-01-01T14:00:00.00Z"), ZoneId.of("UTC"));
		SavingsAccount.setClock(fixedClock);
		
	}

}
