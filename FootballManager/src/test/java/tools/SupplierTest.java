package tools;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fm.tools.Supplier;

public class SupplierTest {


	@Test
	public void test() {
		Supplier supplier = new Supplier();
		System.out.println(supplier.getTicketPrize(1, 1, 1));
	}

}
