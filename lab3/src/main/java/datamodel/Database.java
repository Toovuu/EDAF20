package datamodel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database is a class that specifies the interface to the 
 * movie database. Uses JDBC and the MySQL Connector/J driver.
 */
public class Database {
    /** 
     * The database connection.
     */
    private Connection conn;
        
    /**
     * Create the database interface object. Connection to the database
     * is performed later.
     */
    public Database() {
        conn = null;
    }
       
    /* --- TODO: Change this method to fit your choice of DBMS --- */
    /** 
     * Open a connection to the database, using the specified user name
     * and password.
     *
     * @param userName The user name.
     * @param password The user's password.
     * @return true if the connection succeeded, false if the supplied
     * user name and password were not recognized. Returns false also
     * if the JDBC driver isn't found.
     */
    public boolean openConnection(String userName, String password) {
        try {
        	// Connection strings for included DBMS clients:
        	// [MySQL]       jdbc:mysql://[host]/[database]
        	// [PostgreSQL]  jdbc:postgresql://[host]/[database]
        	// [SQLite]      jdbc:sqlite://[filepath]
        	
        	// Use "jdbc:mysql://puccini.cs.lth.se/" + userName if you using our shared server
        	// If outside, this statement will hang until timeout.
            conn = DriverManager.getConnection 
                ("jdbc:mysql://localhost/", userName, password);
        }
        catch (SQLException e) {
            System.err.println(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
        
    /**
     * Close the connection to the database.
     */
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
        
    /**
     * Check if the connection to the database has been established
     *
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }
	
  	public Show getShowData(String mTitle, String mDate) {
		Integer mFreeSeats = 0;
		String mVenue = "";
		ResultSet rs;
		String query = "select nbrseats, count(ticket.movieshowid), movieshow.theatername from movieshow" +
                " left join ticket on movieshow.movieshowid = ticket.movieshowid" +
                " join theater on movieshow.theatername = theater.theatername" +
                " join movie on movieshow.movieid = movie.movieid" +
                " where moviename = ? and showdate = ?" +
                " group by movieshow.movieshowid";
 		try(PreparedStatement stmt = conn.prepareStatement(query)){
		    stmt.setString(1,mTitle);
		    stmt.setString(2, mDate);
		    rs = stmt.executeQuery();
		    while(rs.next()){
		        mVenue = rs.getString("movieshow.theatername");
		        mFreeSeats = rs.getInt("nbrseats") - rs.getInt("count(ticket.movieshowid)");
		        System.out.println(mTitle + " " + mDate + " " + mVenue + " " + mFreeSeats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /* --- TODO: add code for database query --- */
		
		return new Show(mTitle, mDate, mVenue, mFreeSeats);
	}

    public boolean login(String uname){
        String query = "select userName from Users where userName = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, uname);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getMovies(List<String> movies){
        String query = "select movieName from movieShow" +
                " join movie on movieShow.movieID = movie.movieID" +
                " group by movieShow.movieID";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                movies.add(rs.getString("movieName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getDates(List<String> dates, String mTitle){
        String query = "select showDate from movieShow" +
                " join movie on movieShow.movieID = movie.movieID" +
                " where movieName = ?" +
                " group by movieshow.movieshowid";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,mTitle);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                dates.add(rs.getString("showDate"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getmovieshowid(String mTitle, String mDate){
        String movieshowid ="";
        String query ="Select movieshow.movieshowid from movieshow" +
                " join movie on movieshow.movieid = movie.movieid" +
                " where moviename = ? and showdate = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,mTitle);
            stmt.setString(2,mDate);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                movieshowid = rs.getString("movieshow.movieshowid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieshowid;
    }

    public int resTicket(String user, String movieshowid){
        String query = "insert into ticket(username, movieshowid) values (?,?)";
        try {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user);
                stmt.setString(2, movieshowid);
                stmt.executeUpdate();
            }

            try(PreparedStatement stmt = conn.prepareStatement("select last_insert_id()")){
                var rs = stmt.executeQuery();
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Reservation> res(String username){
        List<Reservation> reslist = new ArrayList<>();
        int resnbr = 0;
        String venue = "";
        String mTitle = "";
        String mDate = "";
        String query = "select resnbr, theatername, moviename, showdate from ticket" +
                " join movieshow on movieshow.movieshowid = ticket.movieshowid" +
                " join movie on movie.movieid = movieshow.movieid" +
                " where username = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,username);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                resnbr = rs.getInt("resnbr");
                venue = rs.getString("movieshow.theatername");
                mTitle = rs.getString("moviename");
                mDate = rs.getString("showdate");
                reslist.add(new Reservation(resnbr, mTitle,mDate,venue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reslist;
    }




    /* --- TODO: insert more own code here --- */
}
