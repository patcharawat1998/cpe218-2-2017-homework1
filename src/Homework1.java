
public class Homework1 {

	public static void main(String[] args) {
//		// Begin of arguments input sample
//		if (args.length > 0) {
//			String input = args[0];
//			if (input.equalsIgnoreCase("251-*32*+")) {
//				System.out.println("(2*(5-1))+(3*2)=14");
//			}
//		}
//		// End of arguments input sample
//		
//		// TODO: Implement your project here
		
		String input = "251-*32*+";
		input = "1251-*32*+*";
        Medhod medhod = new Medhod();

        if (args.length > 0) {
            input = args[0];
        }

        medhod.makeTree(input);
        medhod.inorder();
        medhod.infix();
        medhod.printResult();

		
	}
}
