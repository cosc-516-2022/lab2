import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Performs tranactions on an Azure SQL database.
 */
public class AzureSQL {
	/**
	 * Connection to database
	 */
	private Connection con;
	
	/**
	 * Main method
	 * 
	 * @param args
	 *             no arguments required
	 */
	public static void main(String[] args) throws Exception {
		AzureSQL db = new AzureSQL();
		Connection c = db.connect();
		// TODO: Once you have data loaded successfully once. Comment out drop() and load() for testing to save time.
		db.drop();
		db.load();

		System.out.println(AzureSQL.resultSetToString(db.query1(), 1000));
		System.out.println(AzureSQL.resultSetToString(db.query2(), 1000));
		
		// Sample transaction: Retrieve nation name by key
		try {
			System.out.println("Nation name for key 5: "+db.getNationName(5));
		}
		catch (SQLException e)
		{ 	System.out.println(e); }

		try {
			System.out.println("Nation name for key 35: "+db.getNationName(35));
		}
		catch (SQLException e)
		{ 	System.out.println(e); }

		// Get orders transaction: Retrieve order count by customer key
		try {
			System.out.println("Customer orders for key 1: "+db.orderCount(1));
		}
		catch (SQLException e)
		{ 	System.out.println(e); }
		try {
			System.out.println("Customer orders for key 20000: "+db.orderCount(20000));
		}
		catch (SQLException e)
		{ 	System.out.println(e); }

		// Add order transaction: Add an order with lineitems given sufficient quantities
		try {
			if (c != null)
			{ 
				Statement stmt = c.createStatement();
				stmt.executeUpdate("DELETE FROM lineitem WHERE l_orderkey = 50000 or l_orderkey=60000; DELETE FROM orders where o_orderkey = 50000 or o_orderkey=60000");
			}
			db.addOrder(50000, 1, java.sql.Date.valueOf("2022-09-25"), new int[]{1}, new int[]{2}, new int[]{1000}, new String[]{"771.64"});			
		}
		catch (SQLException e)
		{ 	System.out.println(e); }		

		try {
			db.addOrder(60000, 1, java.sql.Date.valueOf("2022-09-25"), new int[]{1,4}, new int[]{2,5}, new int[]{1000,2000}, new String[]{"771.64","113.97"});			
		}
		catch (SQLException e)
		{ 	System.out.println(e); }		
	}

	/**
	 * Connects to Azure SQL database and returns connection.
	 * 
	 * @return
	 *         connection
	 */
	public Connection connect() throws SQLException {
		// TODO: Connect to Azure SQL database
		
		System.out.println("Connecting to database.");
		
		// Important: Setting autoCommit() to false requires you to commit or rollback
		// explicitly rather than driver committing after every statement.
		// con.setAutoCommit(false);
		return null;
	}

	
	/**
	 * Closes connection to database.
	 */
	public void close()
	{
		// TODO: Close database connection
		System.out.println("Closing database connection.");		
	}
	
	/**
	 * Drops the tables from the database.  If a table does not exist, error is ignored. 
	 */
	public void drop()
	{
		// TODO: Drop all tables		
	}

	/**
	 * Loads data into database.
	 */
	public void load() throws SQLException {
		String path = "bin/ddl/";

		// TODO: Load data files from script DDL files
		// Hint: Can read file as string and execute multiple commands at once.
		// For larger files, break up into multiple strings to avoid command size limits.
		// Suggestion: Create a help function to read a file into a string and execute.
		
	}
	
	/**
	 * Query returns the top 5 products by lineitem quantity for customers in the country `CANADA`.
	 * 
	 * @return
	 * 		ResultSet
	 * @throws SQLException
	 * 		if an error occurs
	 */
	public ResultSet query1() throws SQLException
	{
		// TODO: Write query #1	
		return null;	
	}

	/**
	 * Query returns the customers that have at least 70% more orders than the average number of orders per customer. Order by number of orders descending.
	 * 
	 * @return
	 * 		ResultSet
	 * @throws SQLException
	 * 		if an error occurs
	 */
	public ResultSet query2() throws SQLException
	{
		// TODO: Write query #2
		return null;
	}

	/**
	 * Sample transaction that returns the nation name given its key.
	 * 
	 * @param nationkey
	 *               nationkey
	 * @return
	 *         nation name
	 * @throws SQLException
	 *                      if an error occurs or key is not found
	 */
	public String getNationName(int nationkey) throws SQLException {		
		try {
			if (con == null)
				return "No connection";
			PreparedStatement stmt = con.prepareStatement("SELECT n_name FROM nation WHERE n_nationkey = ?");
			stmt.setInt(1, nationkey);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				con.rollback();
				throw new SQLException("Nation " + nationkey + " not found.");
			}
			String value = rs.getString(1);
			con.commit();
			return value;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		}
	}

	/**
	 * Transaction that returns the count of the number of orders given a customer id.
	 * 
	 * @param custkey
	 *               customer key
	 * @return
	 *         count of number of orders
	 * @throws SQLException
	 *                      if an error occurs or key is not found
	 */
	public int orderCount(int custkey) throws SQLException {
		// TODO: Get order count given a customer id id using a PreparedStatement
		// Throw the following exception if the account is not found:
		// throw new SQLException("Customer "+custkey+" not found.");
		// Use the con instance variable for the JDBC connection.
		// Make sure to commit() if success or rollback if exception or account is not found.

		return 0;
	}

	/**
	 * Transaction that adds an order and all lineitems to database assuming that the `ps_availqty` is >= `l_quantity` for all items that were requested to be ordered. 
	 * Otherwise, transaction is denied.
	 * 
	 * @param orderkey
	 *               order key
	 * @param custkey
	 *               customer key
	 * @param orderdate
	 *               order date
	 * @param parts
	 *             Array of part keys
	 * @param suppliers
	 *             Array of supplier keys
	 * @param quantities
	 *             Array of quantities
	 * @param prices
	 *             Array of prices	 
	 * @throws SQLException
	 *             if an error occurs or insufficient quantities
	 */
	public void addOrder(int orderkey, int custkey, Date orderdate, int[] parts, int[] suppliers, int[] quantities, String[] prices) throws SQLException {
		// TODO: Add order with all lineitems assuming sufficient quantity.
		// Throw an exception if insufficient quantity.
		// Compute o_totalprice based on ordered items.
		// Make sure to commit() if success or rollback if exception or insufficient quantity.

		// Throw exception if invalid part/supplier
		// 	throw new SQLException("Invalid part/supplier. Part: "+parts[i]+" Supplier: "+suppliers[i]);
		
		// Throw exception if insufficient quantity
		// throw new SQLException("Insufficient quantity for part/supplier. Part: "+parts[i]+" Supplier: "+suppliers[i]+" Quantity ordered: "+quantities[i]+" Quantity available: "+qty);
			
		// Add lineitems to order
			
		// Update order total price		
	}

	
	/*
	 * Do not change anything below here.
	 */
	/**
	 * Converts a ResultSet to a string with a given number of rows displayed.
	 * Total rows are determined but only the first few are put into a string.
	 * 
	 * @param rst
	 *                ResultSet
	 * @param maxrows
	 *                maximum number of rows to display
	 * @return
	 *         String form of results
	 * @throws SQLException
	 *                      if a database error occurs
	 */
	public static String resultSetToString(ResultSet rst, int maxrows) throws SQLException {
		StringBuffer buf = new StringBuffer(5000);
		int rowCount = 0;
		if (rst == null)
			return "No resultset.";
		ResultSetMetaData meta = rst.getMetaData();
		buf.append("Total columns: " + meta.getColumnCount());
		buf.append('\n');
		if (meta.getColumnCount() > 0)
			buf.append(meta.getColumnName(1));
		for (int j = 2; j <= meta.getColumnCount(); j++)
			buf.append(", " + meta.getColumnName(j));
		buf.append('\n');

		while (rst.next()) {
			if (rowCount < maxrows) {
				for (int j = 0; j < meta.getColumnCount(); j++) {
					Object obj = rst.getObject(j + 1);
					buf.append(obj);
					if (j != meta.getColumnCount() - 1)
						buf.append(", ");
				}
				buf.append('\n');
			}
			rowCount++;
		}
		buf.append("Total results: " + rowCount);
		return buf.toString();
	}	
}
