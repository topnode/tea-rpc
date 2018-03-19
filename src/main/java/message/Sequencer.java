package message;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Sequencer {
	
	private static int base=0;
	private static AtomicInteger offset=new AtomicInteger(1) ;
	
	static{
		base=(int) (0xFFFF&(System.currentTimeMillis()));
	}
	
    public static int next(){
    	       if(offset.get()>0xFF){
    	    	   base=(int) (0xFFFF&(System.currentTimeMillis()));
    	       }
    	       return base+(offset.incrementAndGet()<<10);
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Set<Integer> set=new HashSet<Integer>();
	    int count=10000;
		while(count-->0){
			int i=Sequencer.next();
			if(set.contains(i)){
				System.out.println(count);
				break;
			}
			System.out.println(":"+i);
			 set.add(i);
		}
	}

}
