public class Little_Endian {
	
	public static void main(String[] args) {
		
	    int num = 0;
	    int plus = 16;   //16進制	        
	    int move_to = 2; //位置
	   
	    for(String arg : args) {
	    
	    int[] sum = new int[arg.length()/2];  // 動態配置長度 	    	    
      System.out.println(arg);                                     
     
      for(int i =0;i<arg.length();i++)
      {       	     
      	//轉換  	
      	switch(	arg.substring(i,i+1))
      	{
      		case "A":
      		num = 10;
      		break;
      		case "B":
      		num = 11;
      		break;
      		case "C":
      		num = 12;
      		break;
      		case "D":
      		num = 13;
      		break;
      		case "E":
      		num = 14;
      		break;
      		case "F":
      		num = 15;
      		break;
      		default:
      		num = Integer.parseInt(arg.substring(i,i+1));
      		break;
      	}      	     	      		
      
      		//16 進位
      	  num = num *(int) Math.pow(plus, (i+1)%2 ); 
      	  sum[i/2] = sum[i/2] + num;	      	      	
      }      	
      
     for(int i = 2;i<=arg.length()/2;i++)
     {     
     System.out.println("sum["+(i-1)+"] ="+ 	 sum[i-1]);
     sum[i-1] = sum[i-1]<< (int)Math.pow(move_to,i +1 ); //Little_Endian
     System.out.println("sum["+(i-1)+"] move to "+ (int)Math.pow(move_to,i +1 ) );
     System.out.println("sum["+(i-1)+"] ="+ 	 sum[i-1]);    
     }
      
     num = 0; 
     for(int i=0;i< arg.length()/2;i++)
     {
     System.out.println("sum["+i+"] = " + sum[i]); 
     num = num + sum[i];    	 	
     }       
	   
	   System.out.println("Little_Endian = " +num) ;	
	  }
	}
}