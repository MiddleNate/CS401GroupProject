import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({
	CheckingAccountTest.class, SavingsAccountTest.class, LOCAccountTest.class, UserTest.class
})
public class AllTests {
}