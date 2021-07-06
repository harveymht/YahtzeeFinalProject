package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class YahtzeeClient {
    private String host;
    private int port;

    private Socket socket;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    public YahtzeeClient(String host, int port) throws IOException, ClassNotFoundException {
        this.host = host;
        this.port = port;
        socket = new Socket(this.host, this.port);
        toServer = new ObjectOutputStream(socket.getOutputStream());
        toServer.writeObject("Connect");
        fromServer = new ObjectInputStream(socket.getInputStream());
        Object obj = fromServer.readObject();
        if ("yes".equals(obj)) {
            System.out.println("Connect to server successfully!");
        } else {
            throw new IOException();
        }
    }

    public String save(YahtzeeGame game) {
        try {
            toServer.writeObject(game);
            Object obj = fromServer.readObject();
            return String.valueOf(obj);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "Save failed!";
    }

    public List<YahtzeeGame> list() {
        try {
            toServer.writeObject("list");
            Object obj = fromServer.readObject();
            if (obj instanceof List) {
                return (List<YahtzeeGame>) obj;
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public YahtzeeGame select(Object id) {
        try {
            toServer.writeObject(id);
            Object obj = fromServer.readObject();
            if (obj instanceof YahtzeeGame) {
                return (YahtzeeGame) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
