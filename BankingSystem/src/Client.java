
public class Client {

	public static void main(String[] args) {
		GUI gui = new GUI();
		new Thread(gui).run();
		
	}

}
