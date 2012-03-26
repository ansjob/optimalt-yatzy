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
	}

	public RollingAction suggestRoll(Hand currentHand, ScoreCard currentScore,
			int roll) {
		try {
			Connection con = getDb();
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
			Connection con = getDb();
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
			Connection con = getDb();
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
			Connection con = getDb();
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

}
