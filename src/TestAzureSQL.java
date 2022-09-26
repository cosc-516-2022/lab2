import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests database transactions on Azure SQL database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAzureSQL {
	/**
	 * Class being tested
	 */
	private static AzureSQL db;

	/**
	 * Connection to the database
	 */
	private static Connection con;

	/**
	 * Requests a connection to the server.
	 * 
	 * @throws Exception
	 *                   if an error occurs
	 */
	@BeforeAll
	public static void init() throws Exception {
		db = new AzureSQL();
		con = db.connect();
	}

	/**
     * Tests connect
     */
    @Test
	@Order(1)
    public void testConnect() 
    {   
		try
    	{ 	
			Connection c = db.connect();
			if (c == null)
				fail("Failed to connect");
		}
		catch (SQLException e)
		{
			System.out.println(e);
			fail("Connection failed");
		}		
    }

	/**
     * Tests drop command.
     */
    @Test
	@Order(2)
    public void testDrop() 
    {    		
    	db.drop();
    	
		if (con == null)
			fail("FAIL: No connection");			

    	// See if tables exist
		String []tables = new String[]{"region", "nation", "part", "supplier", "partsupp", "customer", "orders", "lineitem"};
		for (int i=0; i < tables.length; i++)
		{
			try
			{	    	
				Statement stmt = con.createStatement();			
	    		stmt.executeQuery("SELECT * FROM "+tables[i]);
	    		fail("Table "+tables[i]+" exists and should be dropped!");			
			}
			catch (SQLException e)
			{
				System.out.println(e);			
			}
		}
    }

	
	/**
     * Tests load command.
     */
    @Test
	@Order(3)
    public void testLoad() 
    {    		        	
    	// See if tables exists
    	try
    	{
			// System.out.println(System.getProperty("user.dir"));
			db.load();
			if (con == null)
				fail("FAIL: No connection");			
	    	Statement stmt = con.createStatement();						
	    	assertEquals(5, getFirstRowValue(stmt, "SELECT COUNT(*) FROM region"));
			assertEquals(25, getFirstRowValue(stmt, "SELECT COUNT(*) FROM nation"));
			assertEquals(2000, getFirstRowValue(stmt, "SELECT COUNT(*) FROM part"));
			assertEquals(100, getFirstRowValue(stmt, "SELECT COUNT(*) FROM supplier"));
			assertEquals(1500, getFirstRowValue(stmt, "SELECT COUNT(*) FROM customer"));
			assertEquals(8000, getFirstRowValue(stmt, "SELECT COUNT(*) FROM partsupp"));
			assertEquals(15000, getFirstRowValue(stmt, "SELECT COUNT(*) FROM orders"));
			assertEquals(60005, getFirstRowValue(stmt, "SELECT COUNT(*) FROM lineitem"));						
			System.out.println("Load successful");
    	}
    	catch (SQLException e)
    	{
    		System.out.println(e);
			fail(e.toString());
    	}
    }
	
	/**
     * Tests first query.
     */
    @Test
	@Order(4)
    public void testQuery1() throws SQLException
    {        	    	
    	ResultSet rst = db.query1();
    	
    	// Verify result
		String answer = "Total columns: 3"		
						+"\nn_name, l_partkey, totalQuantity"
						+"\nCANADA                   , 411, 204.0000"
						+"\nCANADA                   , 380, 190.0000"
						+"\nCANADA                   , 80, 184.0000"
						+"\nCANADA                   , 279, 175.0000"
						+"\nCANADA                   , 741, 174.0000"
						+"\nTotal results: 5";						
    	String queryResult = AzureSQL.resultSetToString(rst, 100);
    	System.out.println(queryResult);
    	assertEquals(answer, queryResult);    	    
    }

	/**
     * Tests second query.
     */
    @Test
	@Order(5)
    public void testQuery2() throws SQLException
    {        	    	
    	ResultSet rst = db.query2();
    	
    	// Verify result
    	String answer = "Total columns: 3"		
						+"\nc_custkey, c_name, totalOrders"
						+"\n686, Customer#000000686, 31"
						+"\n281, Customer#000000281, 27"
						+"\n352, Customer#000000352, 26"
						+"\n1202, Customer#000001202, 26"
						+"\nTotal results: 4";											
	
    	String queryResult = AzureSQL.resultSetToString(rst, 100);
    	System.out.println(queryResult);
    	assertEquals(answer, queryResult);   
    }

	/**
	 * Tests orderCount transaction.
	 */
	@Test
	@Order(6)
	public void testOrderCount() throws Exception {		
		try {
			int response = db.orderCount(686);
			assertEquals(31, response);
		}
		catch (SQLException e)
		{ 	System.out.println(e); 
			fail(e.toString());
		}

		try {
			int response = db.orderCount(70000);
			fail("Customer is invalid");
		}
		catch (SQLException e)
		{ 	System.out.println(e); 
			assertEquals("java.sql.SQLException: Customer 70000 not found.", e.toString());
		}		
	}

	/**
	 * Tests addOrder transaction.
	 */
	@Test
	@Order(7)
	public void testAddOrder() throws Exception {
		// TODO: Commented these out to be more efficient on tests. 
		// Note: Unless you manually restore database to be initial state before any previous updates, tests will fail.
		// db.drop();
		// db.load();

		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE FROM lineitem WHERE l_orderkey = 55000 or l_orderkey=65000; DELETE FROM orders where o_orderkey = 55000 or o_orderkey=65000");
			
			db.addOrder(55000, 1, java.sql.Date.valueOf("2022-09-25"), new int[]{1,8,11}, new int[]{2,34,62}, new int[]{1000,250,500}, new String[]{"771.64","957.34","818.84"});	
			// Verify order and lineitems exists
			if (con == null)
				fail("FAIL: No connection");	
			
			ResultSet rst = stmt.executeQuery("SELECT * FROM orders where o_orderkey = 55000");
			String queryResult = AzureSQL.resultSetToString(rst, 100);
    		System.out.println(queryResult);	
			String answer = "Total columns: 9"		
						+"\no_orderkey, o_custkey, o_orderstatus, o_totalprice, o_orderdate, o_orderpriority, o_clerk, o_shippriority, o_comment"
						+"\n55000, 1, null, 1420395.0000, 2022-09-25 00:00:00.0, null, null, null, null"						
						+"\nTotal results: 1";	
			assertEquals(answer, queryResult);  
			rst = stmt.executeQuery("SELECT * FROM lineitem where l_orderkey = 55000");
			queryResult = AzureSQL.resultSetToString(rst, 100);
    		System.out.println(queryResult);
			answer = "Total columns: 16"
						+"\nl_orderkey, l_partkey, l_suppkey, l_linenumber, l_quantity, l_extendedprice, l_discount, l_tax, l_returnflag, l_linestatus, l_shipdate, l_commitdate, l_receiptdate, l_shipinstruct, l_shipmode, l_comment"
						+"\n55000, 1, 2, 1, 1000.0000, 771.6400, null, null, null, null, null, null, null, null, null, null"
						+"\n55000, 8, 34, 2, 250.0000, 957.3400, null, null, null, null, null, null, null, null, null, null"
						+"\n55000, 11, 62, 3, 500.0000, 818.8400, null, null, null, null, null, null, null, null, null, null"
						+"\nTotal results: 3";			
		}
		catch (SQLException e)
		{ 	System.out.println(e);  
			fail(e.toString());	
		}		

		try {
			db.addOrder(65000, 1, java.sql.Date.valueOf("2022-09-25"), new int[]{1,4}, new int[]{2,5}, new int[]{1000,2000}, new String[]{"771.64","113.97"});			
		}
		catch (SQLException e)
		{ 	System.out.println(e);  
			assertEquals("java.sql.SQLException: Insufficient quantity for part/supplier. Part: 4 Supplier: 5 Quantity ordered: 2000 Quantity available: 1339", e.toString());	
		}		
	}

	public int getFirstRowValue(Statement stmt, String query) throws SQLException
	{
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getInt(1);
	}
}
