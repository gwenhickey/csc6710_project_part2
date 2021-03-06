package jokeproject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/***************************************************
 * JokeDAO.java
 * This DAO class provides CRUD operation for Joke table
 * @author Gwen Hickey
 *
 ***************************************************/

public class JokeDAO
{
	/* values to connect to the database */
	protected String databaseURL;
	protected String databaseUserName;
	protected String databasePassword;
	private Connection connection;
	
	/* constructors */
	public JokeDAO(String databaseURL, String databaseUserName, String databasePassword)
	{
		this.databaseURL = databaseURL;
		this.databaseUserName = databaseUserName;
		this.databasePassword = databasePassword;
	}
	
	/* establish connection to database */
	public void connect() throws SQLException
	{
		if (connection == null || connection.isClosed())
		{
            try 
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } 
            catch (ClassNotFoundException e)
            {
                throw new SQLException(e);
            }
            connection = DriverManager.getConnection(databaseURL +  "?" + "user=" + databaseUserName + 
					 "&password=" + databasePassword + "&useSSL=false");
        }
	}
	

	/* disconnect from the database */
	public void disconnect() throws SQLException
	{
		if (connection != null && !connection.isClosed())
		{
	        connection.close();
	    }
	}
	
	/* drop Joke table */
	public void dropJokeTable() throws SQLException
	{
		connect();
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS Joke");
		statement.close();
		disconnect();
	}
	
	/* create Joke table */
	public void createJokeTable() throws SQLException
	{
		connect();
		Statement statement = connection.createStatement();
		String sqlStatement = "CREATE TABLE IF NOT EXISTS Joke" +
				              "(jokeId int(20) NOT NULL AUTO_INCREMENT," +
				              " jokeTitle varchar (50) DEFAULT NULL," + 
				              " jokeText varchar(500) DEFAULT NULL," +
				              " jokePostDate date DEFAULT NULL," +
				              " postUserId int(20) NOT NULL," +
				              " PRIMARY KEY (jokeId)," +
				              " FOREIGN KEY (postUserId) REFERENCES User(userId))";
		statement.executeUpdate(sqlStatement);
		statement.close();
		disconnect();
	}
		
	/* initialize Joke table */
	public void initJokeTable() throws SQLException
	{
		/* insert the default jokes */
		List<Joke> jokeList = new ArrayList<Joke>();
		Date date = Date.valueOf(LocalDate.now());
		jokeList.add(new Joke("joke1", "Text1", date, 1));
		jokeList.add(new Joke("joke2", "Text2", date, 1));
		jokeList.add(new Joke("joke3", "Text3", date, 1));
		jokeList.add(new Joke("joke4", "Text4", date, 1));
		jokeList.add(new Joke("joke5", "Text5", date, 1));
		jokeList.add(new Joke("joke6", "Text6", date, 1));
		jokeList.add(new Joke("joke7", "Text7", date, 1));
		jokeList.add(new Joke("joke8", "Text8", date, 1));
		jokeList.add(new Joke("joke9", "Text9", date, 1));
		jokeList.add(new Joke("joke10", "Text10", date, 1));
		insertJoke(jokeList);
	}
	
	/* insert a joke to Joke table */
	public boolean insertJoke(List<Joke> jokeList) throws SQLException
	{
		Joke joke = new Joke();
		boolean status = false;
		
		String sqlInsert = "INSERT INTO Joke (jokeId, jokeTitle, jokeText, jokePostDate, postUserId) " +
							"VALUES (?, ?, ?, ?, ?)";
		connect();
		for (int i = 0; i < jokeList.size(); i++)
		{
			joke = jokeList.get(i);
			
			PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
			preparedStatement.setInt(1, joke.getjokeId());
			preparedStatement.setString(2, joke.getjokeTitle());
			preparedStatement.setString(3, joke.getjokeText());
			preparedStatement.setDate(4, joke.getjokePostDate());
			preparedStatement.setInt(5, joke.getpostUserId());
			
			status &= preparedStatement.executeUpdate() > 0;
			preparedStatement.close();
		}
		disconnect();
		
		return status;
	}
	
	/* insert a joke to Joke table */
	public boolean insertJoke(Joke joke) throws SQLException
	{
		String sqlInsert = "INSERT INTO Joke (jokeId, jokeTitle, jokeText, jokePostDate, postUserId) " +
							"VALUES (?, ?, ?, ?, ?)";
		connect();
		PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
		preparedStatement.setInt(1, joke.getjokeId());
		preparedStatement.setString(2, joke.getjokeTitle());
		preparedStatement.setString(3, joke.getjokeText());
		preparedStatement.setDate(4, joke.getjokePostDate());
		preparedStatement.setInt(5, joke.getpostUserId());
		
		boolean status = preparedStatement.executeUpdate() > 0;
		preparedStatement.close();
		disconnect();
		
		return status;
	}
	

	
	/* update a joke information in Joke table */
	public boolean updateJoke(Joke joke) throws SQLException
	{
		String sqlUpdate = "UPDATE Joke SET jokeTitle = ?, jokeText = ?, jokePostDate = ?, postUserId = ?" +
							" WHERE jokeId = ?";
		connect();
		PreparedStatement prepareStatement = connection.prepareStatement(sqlUpdate);
		prepareStatement.setString(1, joke.getjokeTitle());
		prepareStatement.setString(2, joke.getjokeText());
		prepareStatement.setDate(3, joke.getjokePostDate());
		prepareStatement.setInt(4, joke.getpostUserId());
		prepareStatement.setInt(5, joke.getjokeId());
		
		boolean status = prepareStatement.executeUpdate() > 0;
		prepareStatement.close();
		disconnect();
		
		return status;
	}
	
	/* delete a joke from Joke table */
	public boolean deleteJoke(int jokeId) throws SQLException
	{
		String sqlDelete = "DELETE FROM Joke WHERE jokeId = ?";
		connect();
		
		PreparedStatement prepareStatement = connection.prepareStatement(sqlDelete);
		prepareStatement.setInt(1, jokeId);
		
		boolean status = prepareStatement.executeUpdate() > 0;
		prepareStatement.close();
		disconnect();
		
		return status;
	}
	
	/* get a joke from Joke table based on jokerId */
	public Joke getJoke(int jokeId) throws SQLException
	{
		Joke joke = null;
		String sqlGet = "SELECT * FROM Joke WHERE jokeId = ?";
		connect();
		
		PreparedStatement preparedStatement = connection.prepareStatement(sqlGet);
		preparedStatement.setInt(1, jokeId);
		ResultSet result = preparedStatement.executeQuery();
		
		while (result.next())
		{
			String title = result.getString("jokeTitle");
			String text = result.getString("jokeText");
			Date date = result.getDate("jokePostDate");
			int userId = result.getInt("postUserId");
			
			joke = new Joke(jokeId, title, text, date, userId);
		}
		
		result.close();
		preparedStatement.close();
		disconnect();
		
		return joke;
	}
	
	/* get a user's joked from Joke table */
	public Joke getUserJokes(int userId) throws SQLException
	{
		Joke joke = null;
		String sqlGet = "SELECT * FROM Joke WHERE postUserId = ?";
		connect();
		
		PreparedStatement preparedStatement = connection.prepareStatement(sqlGet);
		preparedStatement.setInt(1, userId);
		ResultSet result = preparedStatement.executeQuery();
		
		while (result.next())
		{
			int jokeId = result.getInt("jokeId");
			String title = result.getString("jokeTitle");
			String text = result.getString("jokeText");
			Date date = result.getDate("jokeDate");
			
			joke = new Joke(jokeId, title, text, date, userId);
		}
		
		result.close();
		preparedStatement.close();
		disconnect();
		
		return joke;
	}
	
	/* get list of all jokes from joke table */
	public List<Joke> getJokeList() throws SQLException
	{
		List<Joke> jokeList = new ArrayList<Joke>();
		String sqlQuery = "SELECT * FROM Joke";
		
		connect();
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(sqlQuery);
		
		while(result.next())
		{
			int jokeId = result.getInt("jokeId");
			String jokeTitle = result.getString("jokeTitle");
			String jokeText = result.getString("jokeText");
			Date jokePostDate = result.getDate("jokePostDate");
			int postUserId = result.getInt("postUserId");
			
			Joke joke = new Joke(jokeId, jokeTitle, jokeText, jokePostDate, postUserId);			
			jokeList.add(joke);
		}
		result.close();
		statement.close();
		disconnect();
		
		return jokeList;

	}
}

