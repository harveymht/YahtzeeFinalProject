package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class YahtzeeFrame extends JFrame {

	private static final double SCALE_FACTOR = 0.45;
	private final Random random = new Random();

	private YahtzeeClient client;		// communicate with server

	private JTextField nameField;		// player name

	// btns and scores in upper section
	private JButton[] upperBtns;		// buttons(6)
	private JTextField[] upper;		// categories(6)
	private JTextField upperBonus;		// bonus
	private JTextField upperSubtotal;	// subtotal
	private JTextField upperTotal;		// total
	private final String[] btnNameUpper = {"Aces", "Twos", "Threes", "Fours", "Fives", "Sixes"};

	// btns and scores in lower section
	private JButton[] lowerBtns;		// buttons(7)
	private JTextField[] lower;		// categories(7)
	private JTextField lowerBonus;		// bonus
	private JTextField lowerSubtotal;	// subtotal
	private JTextField lowerTotal;		// total
	private final String[] btnNameLower = {"3 of a kind", "4 of a kind", "Full House", "Small Straight",
			"Large Straight", "Yahtzee", "Chance"};

	private ImagePanel[] dice;		// 5 dice images
	private JCheckBox[] keep;		// if keep for each die
	private JLabel turnLabel;		// label show the num of turn
	private JLabel rollLeftLabel;		// label show the num of rolls left
	private JButton rollBtn;		// roll button
	private JButton newGameBtn;		// new game button

	public YahtzeeFrame() {
		try {
			client = new YahtzeeClient("localhost", 8888);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Client Connection error: " + e);
			System.exit(1);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(460, 750);

		createMenu();

		JPanel player = new JPanel();
		player.add(new JLabel("Player Name:"));
		nameField = new JTextField(20);
		player.add(nameField);
		add(player, BorderLayout.NORTH);

		createBody();
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = menuBar.add(new JMenu("Game"));
		JMenuItem load = menu.add(new JMenuItem("Load Game"));
		JMenuItem save = menu.add(new JMenuItem("Save Game"));
		JMenuItem exit = menu.add(new JMenuItem("Exit"));
		setJMenuBar(menuBar);

		// add actions
		load.addActionListener(e -> {
			List<YahtzeeGame> list = client.list();
			if (list.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No games have been saved now.");
				return;
			}
			List<String> options = new ArrayList<>();
			list.forEach(game -> options.add(game.getId() + "," + game.getName() + "," + game.getTime()
					+ ",turn=" + game.getTurn() + ",rollLeft=" + game.getRollLeft()));
			Object rst = JOptionPane.showInputDialog(null, "Choose one game to load", "Input",
					JOptionPane.INFORMATION_MESSAGE, null, options.toArray(), options.get(0));
			if (rst != null) {
				YahtzeeGame game = client.select(Integer.parseInt(String.valueOf(rst).split(",")[0]));
				if (game == null) {
					JOptionPane.showMessageDialog(null, "Retrieve failed!");
					return;
				}
				newGame(game);
			}
		});

		save.addActionListener(e -> {
			if (nameField.isEditable()) {
				JOptionPane.showMessageDialog(null, "You haven't started your game!");
				return;
			}
			JOptionPane.showMessageDialog(null, client.save(getGameData()));
		});

		exit.addActionListener(e -> System.exit(0));
	}

	private void createBody() {
		JPanel body = new JPanel();
		JPanel leftBody = new JPanel(new GridLayout(2, 1));
		JPanel rightBody = new JPanel();
		BoxLayout boxLayout = new BoxLayout(rightBody, BoxLayout.Y_AXIS);
		rightBody.setLayout(boxLayout);

		// upper section
		upper = new JTextField[6];
		upperBtns = new JButton[6];
		JPanel upperPanel = new JPanel(new GridLayout(9, 2));
		upperPanel.setBorder(BorderFactory.createTitledBorder("Upper Section"));
		for (int i = 0; i < upper.length; i++) {
			upperBtns[i] = new JButton(btnNameUpper[i]);
			upperPanel.add(upperBtns[i]);
			upper[i] = newNoEditTF();
			upperPanel.add(upper[i]);

			int finalI = i;
			upperBtns[i].addActionListener(e -> clickUpper(finalI));
		}
		upperPanel.add(new JLabel("Score Subtotal:"));
		upperSubtotal = newNoEditTF();
		upperPanel.add(upperSubtotal);
		upperPanel.add(new JLabel("Bonus:"));
		upperBonus = newNoEditTF();
		upperPanel.add(upperBonus);
		upperPanel.add(new JLabel("Grand Total:"));
		upperTotal = newNoEditTF();
		upperPanel.add(upperTotal);
		leftBody.add(upperPanel);

		// lower section
		lower = new JTextField[7];
		lowerBtns = new JButton[7];
		JPanel lowerPanel = new JPanel(new GridLayout(10, 2));
		lowerPanel.setBorder(BorderFactory.createTitledBorder("Lower Section"));
		for (int i = 0; i < lower.length; i++) {
			lowerBtns[i] = new JButton(btnNameLower[i]);
			lowerPanel.add(lowerBtns[i]);
			lower[i] = newNoEditTF();
			lowerPanel.add(lower[i]);

			int finalI = i;
			lowerBtns[i].addActionListener(e -> clickLower(finalI));
		}
		lowerPanel.add(new JLabel("Yahtzee Bonus:"));
		lowerBonus = newNoEditTF();
		lowerPanel.add(lowerBonus);
		lowerPanel.add(new JLabel("Total of lower section:"));
		lowerSubtotal = newNoEditTF();
		lowerPanel.add(lowerSubtotal);
		lowerPanel.add(new JLabel("Grand Total:"));
		lowerTotal = newNoEditTF();
		lowerPanel.add(lowerTotal);
		leftBody.add(lowerPanel);

		// dice and keep checkbox
		dice = new ImagePanel[5];
		keep = new JCheckBox[5];
		for (int i = 0; i < dice.length; i++) {
			dice[i] = new ImagePanel(i+1);
			dice[i].scaleImage(SCALE_FACTOR);
			rightBody.add(dice[i]);

			keep[i] = new JCheckBox();
			keep[i].setEnabled(false);
			JPanel checkPanel = new JPanel();
			checkPanel.add(keep[i]);
			checkPanel.add(new JLabel("Keep"));
			rightBody.add(checkPanel);

			int finalI = i;
			dice[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					keep[finalI].doClick();
				}
			});
		}

		// roll record and button
		JPanel turnPanel = new JPanel();
		turnLabel = new JLabel("1");
		turnPanel.add(new JLabel("Turn: "));
		turnPanel.add(turnLabel);
		JPanel rollPanel = new JPanel();
		rollLeftLabel = new JLabel("3");
		rollPanel.add(new JLabel("Rolls left: "));
		rollPanel.add(rollLeftLabel);
		JPanel btnPanel = new JPanel();
		rollBtn = new JButton("Roll");
		rollBtn.addActionListener(e -> rollClick());
		newGameBtn = new JButton("Restart");
		newGameBtn.addActionListener(e -> newGame(null));
		newGameBtn.setVisible(false);
		btnPanel.add(rollBtn);
		btnPanel.add(newGameBtn);
		rightBody.add(turnPanel);
		rightBody.add(rollPanel);
		rightBody.add(btnPanel);

		body.add(leftBody);
		body.add(rightBody);
		add(body, BorderLayout.CENTER);
	}

	private JTextField newNoEditTF() {
		JTextField textField = new JTextField();
		textField.setEditable(false);
		return textField;
	}

	private boolean isBlank(String text) {
		return text == null || text.trim().isEmpty();
	}

	private void newGame(YahtzeeGame game) {
		// start a new game if parameter is null
		// otherwise set game
		boolean isNew = game == null;
		if (isNew) {
			nameField.setEditable(true);
		} else {
			nameField.setText(game.getName());
			nameField.setEditable(false);
		}
		String[] uBtns = isNew ? null : game.getUpperBtns().split(",", -1);
		String[] uFields = isNew ? null : game.getUpperFields().split(",", -1);
		for (int i = 0; i < upperBtns.length; i++) {
			if (isNew) {
				upperBtns[i].setEnabled(true);
				upper[i].setText("");
			} else {
				upperBtns[i].setEnabled("1".equals(uBtns[i]));
				upper[i].setText(uFields[i]);
			}
		}
		String[] lBtns = isNew ? null : game.getLowerBtns().split(",", -1);
		String[] lFields = isNew ? null : game.getLowerFields().split(",", -1);
		for (int i = 0; i < lowerBtns.length; i++) {
			if (isNew) {
				lowerBtns[i].setEnabled(true);
				lower[i].setText("");
			} else {
				lowerBtns[i].setEnabled("1".equals(lBtns[i]));
				lower[i].setText(lFields[i]);
			}
		}
		upperSubtotal.setText(isNew ? "":game.getUpperSubTotal());
		upperBonus.setText(isNew ? "":game.getUpperBonus());
		upperTotal.setText(isNew ? "":game.getUpperTotal());
		lowerSubtotal.setText(isNew ? "":game.getLowerSubTotal());
		lowerBonus.setText(isNew ? "":game.getLowerBonus());
		lowerTotal.setText(isNew ? "":game.getLowerTotal());
		String[] k = isNew ? null : game.getKeeps().split(",");
		String[] d = isNew ? null : game.getDice().split(",");
		for (int i = 0; i < keep.length; i++) {
			if (isNew) {
				keep[i].setSelected(false);
				keep[i].setEnabled(false);
			} else {
				keep[i].setEnabled(true);
				dice[i].setNum(Integer.parseInt(d[i]));
				if ("0".equals(k[i])) {
					keep[i].setSelected(false);
				} else if ("1".equals(k[i])) {
					keep[i].setSelected(true);
				} else {
					keep[i].setSelected(false);
					keep[i].setEnabled(false);
				}
			}
		}
		turnLabel.setText(isNew ? "1":game.getTurn());
		rollLeftLabel.setText(isNew ? "3":game.getRollLeft());
		rollBtn.setVisible(true);
		newGameBtn.setVisible(false);
		if (!isNew && !game.getUpperBtns().contains("1") && !game.getLowerBtns().contains("1")) {
			rollBtn.setVisible(false);
			newGameBtn.setVisible(true);
		}
	}

	private void clickUpper(int i) {
		String text = upper[i].getText();
		if (isBlank(text)) {
			JOptionPane.showMessageDialog(null, "Please roll first.");
			return;
		}
		if (isSpecialYahtzee()) {
			int yahtzee = isYahtzee();
			if (yahtzee-1 != i) {
				if (upperBtns[yahtzee-1].isEnabled()) {
					JOptionPane.showMessageDialog(null, "You must choose the category in the Upper Section " +
							"that corresponds to the numbers in the Yahtzee first!");
					return;
				}
				if (Arrays.stream(lowerBtns).anyMatch(Component::isEnabled)) {
					JOptionPane.showMessageDialog(null, "You must choose the unused category " +
							"in the Lower Section first!");
					return;
				}
			}
			// give yahtzee bonus
			if ("50".equals(lower[5].getText())) {
				if (isBlank(lowerBonus.getText())) {
					lowerBonus.setText("100");
				} else {
					lowerBonus.setText(String.valueOf(100 + Integer.parseInt(lowerBonus.getText())));
				}
			}
		}

		int score = Integer.parseInt(text);
		int subScore = 0;
		upperBtns[i].setEnabled(false);
		String subtotalText = upperSubtotal.getText();
		if (!isBlank(subtotalText)) {
			subScore = Integer.parseInt(subtotalText);
		}
		subScore += score;
		upperSubtotal.setText(String.valueOf(subScore));
		if (isBlank(upperBonus.getText()) && subScore >= 63) {
			upperBonus.setText("35");
		}

		// clear unused text fields and check if update total score and into next turn
		clearAndUpdate();
	}

	private void clickLower(int i) {
		String text = lower[i].getText();
		if (isBlank(text)) {
			JOptionPane.showMessageDialog(null, "Please roll first.");
			return;
		}
		if (isSpecialYahtzee()) {
			int yahtzee = isYahtzee();
			if (upperBtns[yahtzee-1].isEnabled()) {
				JOptionPane.showMessageDialog(null, "You must choose the category in the Upper Section " +
						"that corresponds to the numbers in the Yahtzee first!");
				return;
			}
			// give yahtzee bonus
			if ("50".equals(lower[5].getText())) {
				if (isBlank(lowerBonus.getText())) {
					lowerBonus.setText("100");
				} else {
					lowerBonus.setText(String.valueOf(100 + Integer.parseInt(lowerBonus.getText())));
				}
			}
		}

		int score = Integer.parseInt(text);
		int subScore = 0;
		lowerBtns[i].setEnabled(false);
		String subtotalText = lowerSubtotal.getText();
		if (!isBlank(subtotalText)) {
			subScore = Integer.parseInt(subtotalText);
		}
		subScore += score;
		lowerSubtotal.setText(String.valueOf(subScore));

		// clear unused text fields and check if update total score and into next turn
		clearAndUpdate();
	}

	private void clearAndUpdate() {
		// for upper section
		for (int i = 0; i < upperBtns.length; i++) {
			if (upperBtns[i].isEnabled()) {
				upper[i].setText("");
			}
		}
		// for lower section
		for (int i = 0; i < lowerBtns.length; i++) {
			if (lowerBtns[i].isEnabled()) {
				lower[i].setText("");
			}
		}

		// into next turn
		int turn = Integer.parseInt(turnLabel.getText());
		if (turn == 13) {
			// update upper total score
			int upperScore = Integer.parseInt(upperSubtotal.getText());
			if (!isBlank(upperBonus.getText())) {
				upperScore += Integer.parseInt(upperBonus.getText());
			}
			upperTotal.setText(String.valueOf(upperScore));
			// update lower total score
			int lowerScore = Integer.parseInt(lowerSubtotal.getText());
			if (!isBlank(lowerBonus.getText())) {
				lowerScore += Integer.parseInt(lowerBonus.getText());
			}
			lowerTotal.setText(String.valueOf(lowerScore));

			int totalScore = upperScore + lowerScore;
			JOptionPane.showMessageDialog(null, "Congratulations! Your final total score is " + totalScore + ".");
			rollBtn.setVisible(false);
			newGameBtn.setVisible(true);
			return;
		}
		turnLabel.setText(String.valueOf(++turn));
		rollLeftLabel.setText("3");
		for (JCheckBox checkBox : keep) {
			checkBox.setSelected(false);
			checkBox.setEnabled(false);
		}
	}

	private void rollClick() {
		if (nameField.isEditable()) {
			if (isBlank(nameField.getText())) {
				JOptionPane.showMessageDialog(null, "Please enter your name.");
				return;
			} else {
				nameField.setText(nameField.getText().trim());
				nameField.setEditable(false);
			}
		}

		int rollLeft = Integer.parseInt(rollLeftLabel.getText());
		if (rollLeft == 0) {
			JOptionPane.showMessageDialog(null, "You do not have rolls left on this turn. Please choose one category.");
			return;
		}
		List<Integer> diceList = new ArrayList<>();
		for (int i = 0; i < keep.length; i++) {
			keep[i].setEnabled(true);
			if (!keep[i].isSelected()) {
				dice[i].setNum(random.nextInt(6) + 1);
			}
			diceList.add(dice[i].getNum());
		}
		rollLeft -= 1;
		rollLeftLabel.setText(String.valueOf(rollLeft));

		calculateCategories(diceList);
	}

	private void calculateCategories(List<Integer> diceList) {
		// upper section
		for (int i = 0; i < upperBtns.length; i++) {
			if (!upperBtns[i].isEnabled()) {
				continue;
			}
			int num = i + 1;
			long count = diceList.stream().filter(n -> n == num).count();
			upper[i].setText(String.valueOf(num * count));
		}

		// the amount of each existing number(from 1 to 6) of dice
		Set<Map.Entry<Integer, Long>> entrySet = diceList.stream()
				.collect(Collectors.groupingBy(Integer::intValue, Collectors.counting())).entrySet();
		// check if a joker
		boolean joker = isSpecialYahtzee();
		// lower section
		for (int i = 0; i < lowerBtns.length; i++) {
			if (!lowerBtns[i].isEnabled()) {
				continue;
			}
			int score = 0;
			switch (i) {
				case 0:	// Three of a Kind
					Optional<Map.Entry<Integer, Long>> first = entrySet.stream()
							.filter(entry -> entry.getValue() >= 3).findFirst();
					if (first.isPresent() || joker) {
						score = diceList.stream().mapToInt(Integer::intValue).sum();
					}
					break;
				case 1: // Four of a Kind
					Optional<Map.Entry<Integer, Long>> first1 = entrySet.stream()
							.filter(entry -> entry.getValue() >= 4).findFirst();
					if (first1.isPresent() || joker) {
						score = diceList.stream().mapToInt(Integer::intValue).sum();
					}
					break;
				case 2: // Full House
					if ((entrySet.size() == 2 && entrySet.stream().anyMatch(entry -> entry.getValue() == 3))
							|| entrySet.size() == 1 || joker) {
						score = 25;
					}
					break;
				case 3:	// Small Straight
					if (diceList.containsAll(Arrays.asList(1, 2, 3, 4))
							|| diceList.containsAll(Arrays.asList(2, 3, 4, 5))
							|| diceList.containsAll(Arrays.asList(3, 4, 5, 6))
							|| joker) {
						score = 30;
					}
					break;
				case 4:	// Large Straight
					if (diceList.containsAll(Arrays.asList(1, 2, 3, 4, 5))
							|| diceList.containsAll(Arrays.asList(2, 3, 4, 5, 6))
							|| joker) {
						score = 40;
					}
					break;
				case 5:	// Yahtzee
					if (entrySet.size() == 1) {
						score = 50;
					}
					break;
				default:
					score = diceList.stream().mapToInt(Integer::intValue).sum();
			}
			lower[i].setText(String.valueOf(score));
		}
	}

	// check if it is yahtzee and return the yahtzee number (1-6)
	// otherwise return 0
	private int isYahtzee() {
		Set<Integer> set = new HashSet<>();
		Arrays.stream(dice).forEach(d -> set.add(d.getNum()));
		if (set.size() == 1) {
			return (int) set.toArray()[0];
		}
		return 0;
	}

	// If a player rolls a Yahtzee on their turn
	// but they have already filled in the Yahtzee category in a previous turn
	private boolean isSpecialYahtzee() {
		return isYahtzee() > 0 && !lowerBtns[5].isEnabled();
	}

	// return game data according to current state of this game
	private YahtzeeGame getGameData() {
		YahtzeeGame game = new YahtzeeGame();
		StringBuilder sb = new StringBuilder();
		game.setName(nameField.getText());
		// upper
		sb.delete(0, sb.length());
		for (JTextField field : upper) {
			if (!isBlank(field.getText())) {
				sb.append(field.getText());
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setUpperFields(sb.toString());
		sb.delete(0, sb.length());
		for (JButton btn : upperBtns) {
			if (btn.isEnabled()) {
				sb.append("1");
			} else {
				sb.append("0");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setUpperBtns(sb.toString());
		game.setUpperBonus(upperBonus.getText());
		game.setUpperSubTotal(upperSubtotal.getText());
		game.setUpperTotal(upperTotal.getText());
		// lower
		sb.delete(0, sb.length());
		for (JTextField field : lower) {
			if (!isBlank(field.getText())) {
				sb.append(field.getText());
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setLowerFields(sb.toString());
		sb.delete(0, sb.length());
		for (JButton btn : lowerBtns) {
			if (btn.isEnabled()) {
				sb.append("1");
			} else {
				sb.append("0");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setLowerBtns(sb.toString());
		game.setLowerBonus(lowerBonus.getText());
		game.setLowerSubTotal(lowerSubtotal.getText());
		game.setLowerTotal(lowerTotal.getText());
		// dice
		sb.delete(0, sb.length());
		for (ImagePanel die : dice) {
			sb.append(die.getNum()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setDice(sb.toString());
		sb.delete(0, sb.length());
		for (JCheckBox checkBox : keep) {
			if (checkBox.isEnabled()) {
				if (checkBox.isSelected()) {
					sb.append("1");
				} else {
					sb.append("0");
				}
			} else {
				sb.append("2");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setKeeps(sb.toString());
		game.setTurn(turnLabel.getText());
		game.setRollLeft(rollLeftLabel.getText());
		return game;
	}

	public static void main(String args[]) {
		YahtzeeFrame yahtzee = new YahtzeeFrame();
		yahtzee.setVisible(true);
	}
}
