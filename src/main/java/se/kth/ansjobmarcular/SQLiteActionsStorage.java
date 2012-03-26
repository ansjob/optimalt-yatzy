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

	private static Connection getDb() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + FILE_NAME);
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
			loadedDriver = true;
		} catch (ClassNotFoundException e) {
			/* Nothing to do about it */
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
				"expected real);");
	}

	public RollingAction suggestRoll(Hand currentHand, ScoreCard currentScore,
			int roll) {
		Connection con = null;
		try {
			con = getDb();
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
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}

	public MarkingAction suggestMarking(Hand currentHand, ScoreCard currentScore) {
		Connection con = null;
		try {
			con = getDb();
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
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}

	public void addMarkingAction(MarkingAction action, ScoreCard currentScore,
			Hand hand) {
		try {
			Connection con = getDb();
			PreparedStatement stmt = con.prepareStatement("insert into markingActions VALUES(?,?,?);");
			
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, action.getIndex());
			stmt.execute();
			con.close();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void addRollingAction(RollingAction action, ScoreCard currentScore,
			Hand hand, int roll) {
		try {
			Connection con = getDb();
			PreparedStatement stmt = con.prepareStatement("insert into rollingActions values (?, ?, ?, ?);");
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, roll);
			stmt.setInt(4, action.getIndex());
			stmt.execute();
			con.close();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void putExpectedScore(double expected, ScoreCard currentScore,
			Hand hand, int roll) {
		try {
			Connection con = getDb();
			PreparedStatement stmt = con.prepareStatement("insert into expectedScores values (?, ?, ?, ?);");
			stmt.setInt(1, hand.getIndex());
			stmt.setInt(2, currentScore.getIndex());
			stmt.setInt(3, roll);
			stmt.setDouble(4, expected);
			stmt.execute();
			con.close();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public double getExpectedScore(ScoreCard currentScore, Hand hand, int roll,
			double expected) {
		Connection con = null;
		try {
			con = getDb();
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
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}

}
