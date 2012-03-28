package se.kth.ansjobmarcular;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.SynchronousMode;

public class SQLiteActionsStorage implements ActionsStorage {

	static {
		try {
			Class.forName("org.sqlite.JDBC");
			loadedDriver = true;
		} catch (ClassNotFoundException e) {
			/* Nothing to do about it */
		}
	}

	private static boolean loadedDriver = false;

	private static final String FILE_NAME = "optimal-yatzy.db";
	
	private PreparedStatement suggestRollStmt;
	
	private PreparedStatement putRollStmt;
	
	private PreparedStatement suggestMarkStmt;
	
	private PreparedStatement putMarkStmt;
	
	private PreparedStatement putExpectedStmt;
	
	private PreparedStatement getExpectedStmt;
	
	private Connection con;

	private static Connection getDb() throws SQLException {
		SQLiteConfig config = new SQLiteConfig();
		config.setSynchronous(SynchronousMode.OFF);
		return DriverManager.getConnection("jdbc:sqlite:");
	}
	
	public SQLiteActionsStorage() {
		try {
			this.con = getDb();
			con.setAutoCommit(false);
			DataDefinition();
			prepareStatements();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void prepareStatements() throws SQLException {
		
		this.getExpectedStmt = con.prepareStatement(
				"select expected from expectedScores where " +
				"hand = ? AND scorecard = ? and roll = ?");
		
		this.putExpectedStmt = con.prepareStatement("insert into expectedScores values (?, ?, ?, ?);");
		
		this.putMarkStmt = con.prepareStatement("insert into markingActions VALUES(?,?,?);");
		
		this.putRollStmt = con.prepareStatement("insert into rollingActions values (?, ?, ?, ?);");
		
		this.suggestMarkStmt = con.prepareStatement(
				"select action from markingActions where " +
				"hand = ? AND scorecard = ?");
		
		this.suggestRollStmt = con.prepareStatement(
				"select action from rollingActions where " +
				"hand = ? AND scorecard = ? AND roll = ?");
	}

	
	public void beginTransaction() {
	}
	
	public void endTransaction() {
		try {
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void DataDefinition() throws SQLException {
		Statement stmt = con.createStatement();
		stmt.execute("create table if not exists rollingActions(" +
				"hand integer, " +
				"scorecard integer," +
				"roll, integer" +
				"action integer," +
				"PRIMARY KEY (hand, scorecard, roll));");
		
		stmt.execute("create table if not exists markingActions(" +
				"hand integer ," +
				"scorecard integer ," +
				"action integer ," +
				"PRIMARY KEY (hand, scorecard));");
		
		stmt.execute("create table if not exists expectedScores(" +
				"hand integer," +
				"scorecard integer," +
				"roll integer," +
				"expected real, " +
				"PRIMARY KEY (hand, scorecard, roll));");
	}

	public RollingAction suggestRoll(Hand currentHand, ScoreCard currentScore,
			int roll) {
		try {
			
			this.suggestRollStmt.setInt(1, currentHand.getIndex());
			this.suggestRollStmt.setInt(2, currentScore.getIndex());
			this.suggestRollStmt.setInt(3, roll);
			ResultSet res = this.suggestRollStmt.executeQuery();
			if (res != null && res.next()) {
				return new RollingAction(res.getInt(1));
			}
			return null;
		} catch (SQLException e) {
			return null;
		}
	}

	public MarkingAction suggestMarking(Hand currentHand, ScoreCard currentScore) {
		try {
			
			this.suggestMarkStmt.setInt(1, currentHand.getIndex());
			this.suggestMarkStmt.setInt(2, currentScore.getIndex());
			ResultSet res = this.suggestMarkStmt.executeQuery();
			if (res != null && res.next()) {
				return new MarkingAction(res.getInt(1));
			}
			return null;
		} catch (SQLException e) {
			return null;
		}
	}

	public void addMarkingAction(MarkingAction action, ScoreCard currentScore,
			Hand hand) {
		try {
			
			this.putMarkStmt.setInt(1, hand.getIndex());
			this.putMarkStmt.setInt(2, currentScore.getIndex());
			this.putMarkStmt.setInt(3, action.getIndex());
			this.putMarkStmt.execute();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void addRollingAction(RollingAction action, ScoreCard currentScore,
			Hand hand, int roll) {
		try {
			this.putRollStmt.setInt(1, hand.getIndex());
			this.putRollStmt.setInt(2, currentScore.getIndex());
			this.putRollStmt.setInt(3, roll);
			this.putRollStmt.setInt(4, action.getIndex());
			this.putRollStmt.execute();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void putExpectedScore(double expected, ScoreCard currentScore,
			Hand hand, int roll) {
		try {
			this.putExpectedStmt.setInt(1, hand.getIndex());
			this.putExpectedStmt.setInt(2, currentScore.getIndex());
			this.putExpectedStmt.setInt(3, roll);
			this.putExpectedStmt.setDouble(4, expected);
			this.putExpectedStmt.execute();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public double getExpectedScore(ScoreCard currentScore, Hand hand, int roll) {
		try {
			this.getExpectedStmt.setInt(1, hand.getIndex());
			this.getExpectedStmt.setInt(2, currentScore.getIndex());
			this.getExpectedStmt.setInt(3, roll);
			ResultSet res = this.getExpectedStmt.executeQuery();
			if (res != null && res.next()) {
				return res.getDouble(1);
			}
			return -1;
		} catch (SQLException e) {
			return -1;
		}
	}

	public void clearDb() {
		try {
			Statement stmt = con.createStatement();
			
			stmt.execute("delete from rollingActions;");
			stmt.execute("delete from markingActions;");
			stmt.execute("delete from expectedScores;");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		con.close();
		super.finalize();
	}

}
