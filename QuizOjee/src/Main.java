import controller.Controller;

public class Main {

	public static void main (String[] args){
		System.out.println("hello");
		
		Controller c = new Controller();
		for(int i=0;i<64;++i) {
			System.out.println( c.getQuestion(1, 60));
		}
	}
}
