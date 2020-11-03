package krusty;

import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static krusty.Jsonizer.anythingToJson;
import static krusty.Jsonizer.toJson;

public class Database {
	/**
	 * Modify it to fit your environment and then use this string when connecting to your database!
	 */
	private static final String jdbcString = "jdbc:mysql://localhost/";

	// For use with MySQL or PostgreSQL
	private static final String jdbcUsername = "";
	private static final String jdbcPassword = "";
	private Connection conn;

	public Database(){
		conn = null;
	}

	public void connect() {
		// Connect to database here
		try{
			conn = DriverManager.getConnection(jdbcString, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			if (conn != null)
				conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		conn = null;

		System.err.println("Database connection closed.");
	}

	// TODO: Implement and change output in all methods below!

	public String getCustomers(Request req, Response res) {
		String query = "select Customer_Name as name, address from Customers";
		try(Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			String json = Jsonizer.toJson(rs, "customers");
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
			return Jsonizer.anythingToJson(e.getMessage(), "status");
		}
	}

	public String getRawMaterials(Request req, Response res) {
		String query = "select ingredient_name as name, amount, unit from ingredients";
		try(Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			String json = Jsonizer.toJson(rs, "raw-materials");
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
            return Jsonizer.anythingToJson(e.getMessage(), "status");
        }
	}

	public String getCookies(Request req, Response res) {
		String query = "select product_name as name from products";
		try(Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			String json = Jsonizer.toJson(rs, "cookies");
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
            return Jsonizer.anythingToJson(e.getMessage(), "status");
        }
	}

	public String getRecipes(Request req, Response res) {
		String query = "select product_name as cookie," +
				" recipes.ingredient_name as raw_material," +
				" recipes.amount as amount, unit from recipes join" +
                " ingredients on recipes.ingredient_name = ingredients.ingredient_name";
		try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            String json = Jsonizer.toJson(rs, "recipes");
            return json;
        } catch (SQLException e) {
            e.printStackTrace();
            return Jsonizer.anythingToJson(e.getMessage(), "status");
        }
	}

	public String getPallets(Request req, Response res) {
	    String query = "select pallet_id as id," +
				" product_name as cookie," +
				" production_date," +
				" customer_name as customer," +
				" if(pallets.blocked, 'yes', 'no') as blocked" +
				" from pallets left join orders" +
				" on pallets.order_id = orders. order_id";
	    ArrayList<String> values = new ArrayList<String>();
	    if(nullFrom(req, res) || nullTo(req, res) || nullCookie(req, res) || nullBlocked(req, res)){
	        query += " where";
        }
	    if(nullFrom(req,res)){
	        query += " production_date >= ?";
	        values.add(req.queryParams("from"));
        }
	    if(nullTo(req, res)){
	        if(nullFrom(req,res)){
	            query += " and";
            }
	        query += " production_date <= ?";
	        values.add(req.queryParams("to"));
        }
	    if(nullCookie(req,res)){
	        if(nullFrom(req,res) || nullTo(req,res)){
	            query += " and";
            }
	        query += " product_name = ?";
	        values.add(req.queryParams("cookie"));
        }
	    if(nullBlocked(req,res)){
	        if(nullFrom(req,res) || nullTo(req,res) || nullCookie(req,res)){
	            query += " and";
            }
	        query += " if(pallets.blocked, 'yes', 'no') = ?";
	        values.add(req.queryParams("blocked"));
        }
	    try(PreparedStatement stmt = conn.prepareStatement(query)){
	        if(!values.isEmpty()){
	            for(int i = 0; i < values.size(); i++){
	                stmt.setString(i+1, values.get(i));
                }
            }
	        ResultSet rs = stmt.executeQuery();
	        String json = Jsonizer.toJson(rs, "pallets");
	        return json;
        } catch (SQLException e) {
            e.printStackTrace();
            return Jsonizer.anythingToJson(e.getMessage(), "status");
        }
	}

	//funkar
	public String getPalletstest(String from, String to, String cookie, String blocked) {
		String query = "select pallet_id as id," +
				" product_name as cookie," +
				" production_date," +
				" customer_name as customer," +
				" if(pallets.blocked, 'yes', 'no') as blocked" +
				" from pallets left join orders" +
				" on pallets.order_id = orders. order_id";
		ArrayList<String> values = new ArrayList<String>();
		if(from != null || to != null || cookie != null || blocked != null){
			query += " where";
		}
		if(from!=null){
			query += " production_date >= ?";
			values.add(from);
		}
		if(to!=null){
			if(from!=null){
				query += " and";
			}
			query += " production_date <= ?";
			values.add(to);
		}
		if(cookie!=null){
			if(from!=null || to!=null){
				query += " and";
			}
			query += " product_name = ?";
			values.add(cookie);
		}
		if(blocked!=null){
			if(from!=null || to!=null || cookie!=null){
				query += " and";
			}
			query += " if(pallets.blocked, 'yes', 'no') = ?";
			values.add(blocked);
		}
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			if(!values.isEmpty()){
				for(int i = 0; i < values.size(); i++){
					stmt.setString(i+1, values.get(i));
				}
			}
			ResultSet rs = stmt.executeQuery();
			String json = Jsonizer.toJson(rs, "pallets");
			rs.close();
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
			return Jsonizer.anythingToJson(e.getMessage(), "status");
		}
	}

	public String reset(Request req, Response res) {
		String query1 = "set foreign_key_checks = 0";
		String query2 = "truncate table pallets";
		String query3 = "truncate table orders";
		String query4 = "truncate table customers";
		String query5 = "truncate table recipes";
		String query6 = "truncate table products";
		String query7 = "truncate table ingredients";
		String query8 = "set foreign_key_checks = 1";
		String query9 = "INSERT INTO Customers(Customer_Name, Address) VALUES" +
				"('Bjudkakor AB', 'Ystad')," +
				"('Finkakor AB', 'Helsingborg')," +
				"('Gästkakor AB', 'Hässleholm')," +
				"('Kaffebröd AB', 'Landskrona')," +
				"('Kalaskakor AB', 'Trelleborg')," +
				"('Partykakor AB', 'Kristianstad')," +
				"('Skånekakor AB', 'Perstorp')," +
				"('Småbröd AB', 'Malmö')";
		String query10 = "INSERT INTO Products(Product_Name) VALUES" +
				"('Almond delight')," +
				"('Amneris')," +
				"('Berliner')," +
				"('Nut cookie')," +
				"('Nut ring')," +
				"('Tango');";
		String query11 = "INSERT INTO Ingredients(Ingredient_Name, Amount, Unit) VALUES" +
				"('Bread crumbs', 500000, 'g')," +
				"('Butter', 500000, 'g')," +
				"('Chocolate', 500000, 'g')," +
				"('Chopped almonds', 500000, 'g')," +
				"('Cinnamon', 500000, 'g')," +
				"('Egg whites', 500000, 'ml')," +
				"('Eggs', 500000, 'g')," +
				"('Fine-ground nuts', 500000, 'g')," +
				"('Flour', 500000, 'g')," +
				"('Ground, roasted nuts', 500000, 'g')," +
				"('Icing sugar', 500000, 'g')," +
				"('Marzipan', 500000, 'g')," +
				"('Potato starch', 500000, 'g')," +
				"('Roasted, chopped nuts', 500000, 'g')," +
				"('Sodium bicarbonate', 500000, 'g')," +
				"('Sugar', 500000, 'g')," +
				"('Vanilla sugar', 500000, 'g')," +
				"('Vanilla', 500000, 'g')," +
				"('Wheat flour', 500000, 'g');";
		String query12 = "INSERT INTO Recipes(Product_Name, Ingredient_Name, Amount) VALUES" +
				"('Almond delight', 'Butter', 400)," +
				"('Almond delight', 'Chopped almonds', 279)," +
				"('Almond delight', 'Cinnamon', 10)," +
				"('Almond delight', 'Flour', 400)," +
				"('Almond delight', 'Sugar', 270)," +
				"('Amneris', 'Butter', 250)," +
				"('Amneris', 'Eggs', 250)," +
				"('Amneris', 'Marzipan', 750)," +
				"('Amneris', 'Potato starch', 25)," +
				"('Amneris', 'Wheat flour', 25)," +
				"('Berliner', 'Butter', 250)," +
				"('Berliner', 'Chocolate', 50)," +
				"('Berliner', 'Eggs', 50)," +
				"('Berliner', 'Flour', 350)," +
				"('Berliner', 'Icing sugar', 100)," +
				"('Berliner', 'Vanilla sugar', 5)," +
				"('Nut cookie', 'Bread crumbs', 125)," +
				"('Nut cookie', 'Chocolate', 50)," +
				"('Nut cookie', 'Egg whites', 350)," +
				"('Nut cookie', 'Fine-ground nuts', 750)," +
				"('Nut cookie', 'Ground, roasted nuts', 625)," +
				"('Nut cookie', 'Sugar', 375)," +
				"('Nut ring', 'Butter', 450)," +
				"('Nut ring', 'Flour', 450)," +
				"('Nut ring', 'Icing sugar', 190)," +
				"('Nut ring', 'Roasted, chopped nuts', 225)," +
				"('Tango', 'Butter', 200)," +
				"('Tango', 'Flour', 300)," +
				"('Tango', 'Sodium bicarbonate', 4)," +
				"('Tango', 'Sugar', 250)," +
				"('Tango', 'Vanilla', 2);";
		try(Statement stmt = conn.createStatement()){
			conn.setAutoCommit(false);
			stmt.addBatch(query1);
			stmt.addBatch(query2);
			stmt.addBatch(query3);
			stmt.addBatch(query4);
			stmt.addBatch(query5);
			stmt.addBatch(query6);
			stmt.addBatch(query7);
			stmt.addBatch(query8);
			stmt.addBatch(query9);
			stmt.addBatch(query10);
			stmt.addBatch(query11);
			stmt.addBatch(query12);
			stmt.executeBatch();
			conn.commit();
			return Jsonizer.anythingToJson("ok", "status");
		} catch (SQLException e) {
			e.printStackTrace();
			return Jsonizer.anythingToJson(e.getMessage(), "status");
		}
	}

	public String createPallet(Request req, Response res) {
		String query = "insert into pallets(product_name) values(?)";
		String status = "";
		if(cookieCheck(req)){
			try{
				try(PreparedStatement stmt = conn.prepareStatement(query)){
					stmt.setString(1,req.queryParams("cookie"));
					stmt.executeUpdate();
					status += Jsonizer.anythingToJson("ok", "status");
				}
				try(PreparedStatement stmt = conn.prepareStatement("select last_insert_id()")){
					var rs = stmt.executeQuery();
					rs.next();
					status += Jsonizer.anythingToJson(rs.getInt(1), "id");
				}
				return status;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Jsonizer.anythingToJson("unknown cookie", "status");
	}

	public String createPallettest(String cookie){
		String query = "insert into pallets(product_name) values(?)";
		String status = "";
		if(cookie!=null){
			try{
				try(PreparedStatement stmt = conn.prepareStatement(query)){
					stmt.setString(1,cookie);
					stmt.executeUpdate();
					status += Jsonizer.anythingToJson("ok", "status");
				}
				try(PreparedStatement stmt = conn.prepareStatement("select last_insert_id()")){
					var rs = stmt.executeQuery();
					rs.next();
					status += Jsonizer.anythingToJson(rs.getInt(1), "id");
				}
				return status;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Jsonizer.anythingToJson("unknown cookie", "status");
	}

	public boolean cookieCheck(Request req){
		String query = "Select product_name from products" +
				" where product_name = ?";
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setString(1,req.queryParams("cookie"));
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

    public boolean nullFrom(Request req, Response res){
        return req.queryParams("from")!=null;
    }

	public boolean nullTo(Request req, Response res){
	    return req.queryParams("to")!=null;
    }

    public boolean nullCookie(Request req, Response res){
        return req.queryParams("cookie")!=null;
    }

    public boolean nullBlocked(Request req, Response res){
        return req.queryParams("blocked")!=null;
    }

}

