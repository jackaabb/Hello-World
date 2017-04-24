public class IDcheck {

	public static void main(String[] args) {
		
		// 11 位文數字,每一位數均有固定的權重(Weight)
		int [] NumWeight = {1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1};
		
		//ID 字母代表表
		String [][] IDWeight = {{"A","10"},{"B","11"},{"C","12"},{"D","13"},{"E","14"},{"F","15"},{"G","16"},{"H","17"},
				                    {"I","34"},{"J","18"},{"K","19"},{"L","20"},{"M","21"},{"N","22"},{"O","35"},{"P","23"},
				                    {"Q","24"},{"R","25"},{"S","26"},{"T","27"},{"U","28"},{"V","29"},{"W","32"},{"X","30"},
				                    {"Y","31"},{"Z","33"}};
				                    	
	 int[] idcheck = new int[11];	 
	 int n = args[0].length();
	 int m = 0;
	 int sum = 0;
	
	 //英文字母處理
	  for(String[] row : IDWeight)
   	 {   	   
   	 	 if( args[0].substring(0,1).equals(row[0]))
   	 	 {   	 	 	
   	 	 	idcheck[0] =  Integer.parseInt(row[1].substring(0,1))* NumWeight[0] ;
   	 	  idcheck[1] =  Integer.parseInt(row[1].substring(1,2))* NumWeight[1] ;
   	 	  break;
   	 	 }
     }
     
   //數字處理  
     for(int i = 2;i<=n;i++)
     {     	
    	idcheck[i] = Integer.parseInt(args[0].substring(i-1,i)) * NumWeight[i];
     }
   	 	
   //檢查碼加總	 	
   	 for(int i =0 ;i<11;i++)
   	 {  
   	 	sum += idcheck[i];  	 	 
   	}
   	   
   //確認	
   	if(sum%10 == 0)
   	{
     System.out.println("OK");
    }
    
	}  
}
