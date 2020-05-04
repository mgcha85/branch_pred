package sim;
import java.util.Hashtable;
import java.util.logging.Logger;


public class Util {
	int mispred = 0;
	int[] bc, ct;
	Hashtable<String, String> ht;
	int bhr, k, m, n, lim;	// initialize value
	int cnt = 0;
	String name;
	Logger logger;
	
	public Util(Logger logger) {
		this.logger = logger;
		// hexadecimal to binary
    	ht = new Hashtable<String, String>() {
    	    {
    	        put("0","0000");
    	        put("1","0001");
    	        put("2","0010");
    	        put("3","0011");
    	        put("4","0100");
    	        put("5","0101");
    	        put("6","0110");
    	        put("7","0111");
    	        put("8","1000");
    	        put("9","1001");
    	        put("a","1010");
    	        put("b","1011");
    	        put("c","1100");
    	        put("d","1101");
    	        put("e","1110");
    	        put("f","1111");
    	    }
    	};
	}
	
	public String hex_to_bin(String address) {
    	String bin_addr = "";
		for (int i=0; i<address.length(); i++) {
			char ch = address.charAt(i);
			String str = Character.toString(ch);
			bin_addr += ht.get(str);
		}
		return bin_addr;
	}
	
	public String binaryStringWithZeroPad(String val, int len) {
		if(val.length() == len)
			return val;
		
		int valLen = val.length();
		for(int i=0; i<len - valLen; i++)
			val = "0" + val;
		return val;
	}

	public int get_bhr() {
		return bhr;
	}

	public int get_bc(int index) {
		return bc[index];
	}
	
	public void set_bc(boolean taken, int index) {
		if(taken && bc[index] < 7) {
			bc[index]++;
		}
		else if(!taken && bc[index] > 0){
			bc[index]--;
		}
	}

	public String get_name() {
		return name;
	}

	public String print_bhr(String bin) {
		String msg = "	BHR now set to:	";
		for(int i=0; i<n; i++) {
			msg += String.format("[%c]", bin.charAt(i));
		}
		return msg;
	}

	public double get_misrate() {
		return (double) mispred / cnt * 100;
	}

	public void set_bhr(boolean taken) {
		String bin = binaryStringWithZeroPad(Integer.toBinaryString(bhr), n);
		if(bhr == 0 && !taken) {
			if(logger != null)
				logger.severe(print_bhr(bin));
			return;
		}
		if(bhr == lim && taken) {
			if(logger != null)
				logger.severe(print_bhr(bin));
			return;
		}

		bin = Integer.toBinaryString( (1 << n) | bhr).substring(1);
		bin = bin.substring(0, n-1);
		if(taken) 	bin = "1" + bin;
		else 		bin = "0" + bin;
		
		bhr = Integer.parseInt(bin, 2);
		if(logger != null)
			logger.severe(print_bhr(bin));
	}
	
	public int get_ct(int index) {
		return ct[index];
	}
}


interface PredFunction {
	public int get_index(String address, int bhr);
	public String toString();
	public boolean predict(int index);
}
