public class HelloWorld {

	public static void main(String[] args) {
		for(int i =1 ;i<5;i++)
		{
		System.out.println("h11ello wo1rld");
		
		String cardno = "["+String.format("%-19s", "0473")+"]";
		
		String redpoint = "001234".replaceFirst("^0*", "");
		
		String redpoint2 = "1234    ".replaceFirst("^ *", "");
		
		System.out.println(cardno);
		System.out.println(redpoint);

		}
	}
  public static int ttt( int a)
   {	 
     return a+5;
   }
}

