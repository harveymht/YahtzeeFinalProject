package server;

import game.YahtzeeGame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaveServer extends JFrame {

	private final String ddl = "CREATE TABLE IF NOT EXISTS Yahtzee (" +
			"name text," +
			"upper_fields text," +
			"upper_btns text," +
			"upper_sub_total text," +
			"upper_bonus text," +
			"upper_total text," +
			"lower_fields text," +
			"lower_btns text," +
			"lower_sub_total text," +
			"lower_bonus text," +
			"lower_total text," +
			"dice text," +
			"keeps text," +
			"turn text," +
			"roll_left text," +
			"time text," +
			"id INTEGER primary key autoincrement" +
			");";
	private final String saveSql = "INSERT into Yahtzee (name, upper_fields, upper_btns, upper_sub_total, " +
			"upper_bonus, upper_total, lower_fields, lower_btns, lower_sub_total, lower_bonus, lower_total, " +
			"dice, keeps, turn, roll_left, time) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,datetime('now'))";
	private final String listSql = "SELECT id, name, turn, roll_left, time from Yahtzee order by id desc";
	private final String selectSql = "SELECT * from Yahtzee WHERE id=?";

	private Connection conn;
	private PreparedStatement savePs;
	private PreparedStatement listPs;
	private PreparedStatement selectPs;

	private JTextArea wordsBox;
	
	public SaveServer() {
		Statement st = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:yahtzee.db");

			// create table
			st = conn.createStatement();
			st.executeUpdate(ddl);

			savePs = conn.prepareStatement(saveSql);
			listPs = conn.prepareStatement(listSql);
			selectPs = conn.prepareStatement(selectSql);

		} catch (SQLException | ClassNotFoundException e) {
			System.err.println("Server Connection error: " + e);
			System.exit(1);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
		}

		createMainPanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,400);
		setVisible(true);

		wordsBox.append("Ready to Accept Connections...\n");
		startServer();
	}

	public void createMainPanel() {
		wordsBox = new JTextArea(35,10);
		wordsBox.setEditable(false);
		JScrollPane listScroller = new JScrollPane(wordsBox);
		this.add(listScroller, BorderLayout.CENTER);
		listScroller.setPreferredSize(new Dimension(250, 80));
	}

	private void startServer() {
		new Thread(() -> {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(8888);
				wordsBox.append("Server started at " + new Date() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			while (true) {
				Socket socket;
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Connection accepted failed: " + e);
					wordsBox.append("One connection accepted failed at " + new Date() + "\n");
					continue;
				}
				new Thread(new HandleAClient(socket)).start();
			}
		}).start();
	}


	class HandleAClient implements Runnable {
		private Socket socket;

		public HandleAClient(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				// Create data input and output streams
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				wordsBox.append("Accept one new client at " + new Date() + " !\n");

				while (true) {
					try {
						Object obj = input.readObject();
						if (obj instanceof YahtzeeGame) {
							// do save operation
							YahtzeeGame game = (YahtzeeGame) obj;
							PreparedStatement stat = savePs;
							stat.setString(1, game.getName());
							stat.setString(2, game.getUpperFields());
							stat.setString(3, game.getUpperBtns());
							stat.setString(4, game.getUpperSubTotal());
							stat.setString(5, game.getUpperBonus());
							stat.setString(6, game.getUpperTotal());
							stat.setString(7, game.getLowerFields());
							stat.setString(8, game.getLowerBtns());
							stat.setString(9, game.getLowerSubTotal());
							stat.setString(10, game.getLowerBonus());
							stat.setString(11, game.getLowerTotal());
							stat.setString(12, game.getDice());
							stat.setString(13, game.getKeeps());
							stat.setString(14, game.getTurn());
							stat.setString(15, game.getRollLeft());
							wordsBox.append("Try to save game: " + game + " ... ");
							if (stat.executeUpdate() == 1) {
								wordsBox.append("Successfully!\n");
								output.writeObject("Save successfully!");
							} else {
								wordsBox.append("Failed!\n");
								output.writeObject("Save failed!");
							}
						} else if ("list".equals(obj)) {
							// request a list of games
							List<YahtzeeGame> ls = new ArrayList<>();
							PreparedStatement stat = listPs;
							wordsBox.append("Try to list games ... ");
							ResultSet rs = stat.executeQuery();
							while (rs.next()) {
								YahtzeeGame game = new YahtzeeGame();
								game.setId(rs.getInt(1));
								game.setName(rs.getString(2));
								game.setTurn(rs.getString(3));
								game.setRollLeft(rs.getString(4));
								game.setTime(rs.getString(5));
								ls.add(game);
							}
							wordsBox.append("A list of " + ls.size() + " games has been retrieved!\n");
							output.writeObject(ls);
						} else if (obj instanceof Integer) {
							// select a game
							int id = (int) obj;
							PreparedStatement stat = selectPs;
							stat.setInt(1, id);
							wordsBox.append("Try to retrieve the game with id="+id+" ... ");
							ResultSet rs = stat.executeQuery();
							if (rs.next()) {
								YahtzeeGame game = new YahtzeeGame(rs.getString(1), rs.getString(2), rs.getString(3),
										rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
										rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11),
										rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15));
								wordsBox.append("Successfully!\n");
								output.writeObject(game);
							} else {
								wordsBox.append("Failed!\n");
								output.writeObject("Retrieve failed!");
							}
						} else if ("Connect".equals(obj)) {
							wordsBox.append("Accept client successfully!\n");
							output.writeObject("yes");
						} else {
							output.writeObject("Unknown");
						}
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						try {
							output.writeObject("Unknown");
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] main) {
		SaveServer saveServer = new SaveServer();
	}
}
