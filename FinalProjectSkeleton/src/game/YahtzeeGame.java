package game;

import java.io.Serializable;

public class YahtzeeGame implements Serializable {

    private String name;
    private String upperFields;
    private String upperBtns;
    private String upperSubTotal;
    private String upperBonus;
    private String upperTotal;
    private String lowerFields;
    private String lowerBtns;
    private String lowerSubTotal;
    private String lowerBonus;
    private String lowerTotal;
    private String dice;
    private String keeps;
    private String turn;
    private String rollLeft;
    private String time;
    private Integer id;

    public YahtzeeGame() {
    }

    public YahtzeeGame(String name, String upperFields, String upperBtns, String upperSubTotal,
                       String upperBonus, String upperTotal, String lowerFields, String lowerBtns,
                       String lowerSubTotal, String lowerBonus, String lowerTotal, String dice,
                       String keeps, String turn, String rollLeft) {
        this.name = name;
        this.upperFields = upperFields;
        this.upperBtns = upperBtns;
        this.upperSubTotal = upperSubTotal;
        this.upperBonus = upperBonus;
        this.upperTotal = upperTotal;
        this.lowerFields = lowerFields;
        this.lowerBtns = lowerBtns;
        this.lowerSubTotal = lowerSubTotal;
        this.lowerBonus = lowerBonus;
        this.lowerTotal = lowerTotal;
        this.dice = dice;
        this.keeps = keeps;
        this.turn = turn;
        this.rollLeft = rollLeft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpperFields() {
        return upperFields;
    }

    public void setUpperFields(String upperFields) {
        this.upperFields = upperFields;
    }

    public String getUpperBtns() {
        return upperBtns;
    }

    public void setUpperBtns(String upperBtns) {
        this.upperBtns = upperBtns;
    }

    public String getUpperSubTotal() {
        return upperSubTotal;
    }

    public void setUpperSubTotal(String upperSubTotal) {
        this.upperSubTotal = upperSubTotal;
    }

    public String getUpperBonus() {
        return upperBonus;
    }

    public void setUpperBonus(String upperBonus) {
        this.upperBonus = upperBonus;
    }

    public String getUpperTotal() {
        return upperTotal;
    }

    public void setUpperTotal(String upperTotal) {
        this.upperTotal = upperTotal;
    }

    public String getLowerFields() {
        return lowerFields;
    }

    public void setLowerFields(String lowerFields) {
        this.lowerFields = lowerFields;
    }

    public String getLowerBtns() {
        return lowerBtns;
    }

    public void setLowerBtns(String lowerBtns) {
        this.lowerBtns = lowerBtns;
    }

    public String getLowerSubTotal() {
        return lowerSubTotal;
    }

    public void setLowerSubTotal(String lowerSubTotal) {
        this.lowerSubTotal = lowerSubTotal;
    }

    public String getLowerBonus() {
        return lowerBonus;
    }

    public void setLowerBonus(String lowerBonus) {
        this.lowerBonus = lowerBonus;
    }

    public String getLowerTotal() {
        return lowerTotal;
    }

    public void setLowerTotal(String lowerTotal) {
        this.lowerTotal = lowerTotal;
    }

    public String getDice() {
        return dice;
    }

    public void setDice(String dice) {
        this.dice = dice;
    }

    public String getKeeps() {
        return keeps;
    }

    public void setKeeps(String keeps) {
        this.keeps = keeps;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getRollLeft() {
        return rollLeft;
    }

    public void setRollLeft(String rollLeft) {
        this.rollLeft = rollLeft;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "YahtzeeGame{" +
                "name='" + name + '\'' +
                ", upperFields='" + upperFields + '\'' +
                ", upperBtns='" + upperBtns + '\'' +
                ", upperSubTotal='" + upperSubTotal + '\'' +
                ", upperBonus='" + upperBonus + '\'' +
                ", upperTotal='" + upperTotal + '\'' +
                ", lowerFields='" + lowerFields + '\'' +
                ", lowerBtns='" + lowerBtns + '\'' +
                ", lowerSubTotal='" + lowerSubTotal + '\'' +
                ", lowerBonus='" + lowerBonus + '\'' +
                ", lowerTotal='" + lowerTotal + '\'' +
                ", dice='" + dice + '\'' +
                ", keeps='" + keeps + '\'' +
                ", turn='" + turn + '\'' +
                ", rollLeft='" + rollLeft + '\'' +
                '}';
    }
}
