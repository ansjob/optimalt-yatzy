package se.kth.ansjobmarcular;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteActionsStorage implements ActionsStorage {

	private static boolean loadedDriver = false;

	private static final String FILE_NAME = "optimal-yatzy.db";
	
	private Connection con;

	private static Connection getDb() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + FILE_NAME);
	}
	
	public SQLiteActionsStorage() {
		try {
			this.con = getDb();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
			loadedDriver = true;
			DataDefinition();
		} catch (ClassNotFoundException e) {
			/* Nothing to do about it */
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void DataDefinition() throws SQLException {
		Connection con = getDb();
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
		con.close();
	}

	public RollingAction suggestRoll(Hand currentHand, ScoreCard currentScore,
			int roll) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"select action from rollingActions where" +
					"hand = ? AND scorecard = ? AND roll = ?");
			stmt.setInt(1, currentHand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, roll);
			ResultSet res = stmt.executeQuery();
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
			PreparedStatement stmt = con.prepareStatement(
					"select action from markingActions where" +
					"hand = ? AND scorecard = ?");
			stmt.setInt(1, currentHand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			ResultSet res = stmt.executeQuery();
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
			PreparedStatement stmt = con.prepareStatement("insert into markingActions VALUES(?,?,?);");
			
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, action.getIndex());
			stmt.execute();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void addRollingAction(RollingAction action, ScoreCard currentScore,
			Hand hand, int roll) {
		try {
			PreparedStatement stmt = con.prepareStatement("insert into rollingActions values (?, ?, ?, ?);");
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, roll);
			stmt.setInt(4, action.getIndex());
			stmt.execute();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void putExpectedScore(double expected, ScoreCard currentScore,
			Hand hand, int roll) {
		try {
			PreparedStatement stmt = con.prepareStatement("insert into expectedScores values (?, ?, ?, ?);");
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, roll);
			stmt.setDouble(4, expected);
			stmt.execute();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public double getExpectedScore(ScoreCard currentScore, Hand hand, int roll) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"select expected from expectedScores where" +
					"hand = ? AND scorecard = ? and roll = ?");
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, roll);
			ResultSet res = stmt.executeQuery();
			if (res != null && res.next()) {
				return res.getDouble(1);
			}
			return -1;
		} catch (SQLException e) {
			return -1;
		}
	}

	@Override
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
