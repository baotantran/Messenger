import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
	    Server bao = new Server();
	    bao.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    bao.startRunning();
    }
}
