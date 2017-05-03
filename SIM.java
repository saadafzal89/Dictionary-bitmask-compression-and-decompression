/*On my honor, I have neither given nor received unauthorized aid on this assignment*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


public class SIM {
	
	public static class Node{
		int frequency;
		int priority;
		String value;
		
		Node(int freq, int pr, String val){
			frequency = freq;
			priority = pr;
			value = val;
		}
		
		int getFrequency(){
			return frequency;
		}
		
		int getPriority(){
			return priority;
		}
		
		String getValue(){
			return value;
		}
	};

	static HashMap<String, Node> input_map = new HashMap<String, Node>();
	static ArrayList<String> orig = new ArrayList<String>();
	static ArrayList<String> dict = new ArrayList<String>();
	
	static BufferedWriter wr = null;
	static BufferedReader input_br = null;
	static FileReader input_fr = null;
	static String inputFile = null;
	static String outputFile = null;
	static int p=0;
	static StringBuilder output = new StringBuilder();
	static StringBuilder output_dict = new StringBuilder();
/*******************************Compression code****************************/
	private static boolean bitOf(char in) {
	    return (in == '1');
	}

	private static char charOf(boolean in) {
	    return (in) ? '1' : '0';
	}
	
	public static void compress() {
	
		try{
			input_fr = new FileReader(inputFile);
			input_br = new BufferedReader(input_fr);
			String line_input;
			input_br = new BufferedReader(new FileReader(inputFile));
			
			while ((line_input = input_br.readLine()) != null) 
			{
				orig.add(line_input);
				
				if(input_map.containsKey(line_input)){
					Node n = input_map.get(line_input);
					n.frequency = n.frequency+1;
					input_map.put(line_input, n);
				}
				
				else{
					Node newNode = new Node(1,p,line_input);
					p++;
					input_map.put(line_input, newNode);
				}
			}
			
			
			wr = new BufferedWriter(new FileWriter(new File(outputFile)));	
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		
		//Adding frequency HashMap to new ArrayList
		ArrayList<Node> copyOfHash = new ArrayList<Node>();
		for (String line: input_map.keySet()){

            String key =line;
            Node temp = input_map.get(key);  
            copyOfHash.add(temp);
            //System.out.println(temp.value+", Freq--> "+temp.frequency+", Prior-->"+temp.priority);
		}
		
		/*System.out.println("Before sorting");
		for(int i=0;i<copyOfHash.size();i++){
			System.out.println(copyOfHash.get(i).value+", Fre--> "+copyOfHash.get(i).frequency+", Pri-->"+copyOfHash.get(i).priority);
		}*/
		
		//ordering ArrayList on frequency and priority
		order(copyOfHash);	
		
		/*System.out.println("\nAfter sorting");
		for(int i=0;i<16;i++){
			System.out.println(copyOfHash.get(i).value+", Fre--> "+copyOfHash.get(i).frequency+", Pri-->"+copyOfHash.get(i).priority);
			
		}*/
		
		//Adding 16 top frequent entries to Dictionary
		//System.out.println("\nAfter sorting");
		for(int i=0;i<16;i++){
			dict.add(copyOfHash.get(i).value);
		}
		/*
		System.out.println("\nDictionary entries");
		for(int i=0;i<dict.size();i++){
			System.out.println(dict.get(i));
		}
		System.out.println("------------");
		
		System.out.println("\nInput file data");
		for(int i=0;i<orig.size();i++){
			System.out.println(orig.get(i));
		}
		System.out.println("------------");*/
		
		
		String current, previous = null;
		
		for(int i=0;i<orig.size();i++){
			int rleVal=0;
			if(i==0){
				previous = "not valid";
				//rleVal =0;
			}
			//else
				//previous = orig.get(i-1);
			
			current = orig.get(i);
			
			if(current.equals(previous)){								//RLE check
				int rleCount=1;
				int j =i+1;
				previous = current;
				int flag =1;
				while(flag ==1 && rleCount <8 && j<orig.size()){
					String rle =orig.get(j);
					if(rle.equals(previous)){
						rleCount++;
						previous = rle;
						j++;
					}
					else{
						flag=0;
					}
				}
				//System.out.println("rleCount = "+rleCount);
				i = i+rleCount-1;
				//System.out.println("i= "+i);
				rleVal = rleCount -1;
				//System.out.println(rleVal);
				String tt = String.format("%3s", Integer.toBinaryString(rleCount-1)).replace(' ', '0');
				//System.out.print("RLE---");
				//System.out.println("001".concat(tt));
				output.append("001".concat(tt));
			}
			
			else{													
				if(dict.contains(current)){							//Direct match
					String res1 = String.format("%4s",Integer.toBinaryString(dict.indexOf(current))).replace(' ', '0');
			    	//System.out.print("Direct Match---");
					//System.out.println("111".concat(res1));
					output.append("111".concat(res1));
				}
				
				else{
					//System.out.println("input: "+current);
					exor(current);									//Not RLE or Direct
				}
			}
			if(rleVal !=7)
				previous = current;
			else
				previous = "Not valid";
		}
	}
	
	
	//exor of entry
	public static void exor(String c){
		ArrayList<String> exor_list = new ArrayList<String>();
		for(int j=0;j<dict.size();j++){
			StringBuilder sb = new StringBuilder();
		    for (int i = 0; i < c.length(); i++) {
		        sb.append(charOf(bitOf(c.charAt(i)) ^ bitOf((dict.get(j)).charAt(i))));
		    }
	
		    String result = sb.toString();
		    exor_list.add(result);
		}
		
		/*System.out.println("Exor List below:");
		for(int i=0;i<exor_list.size();i++){
			System.out.println(exor_list.get(i));
		}
		System.out.println("------------");*/
		
		int dict_index=32;
		int numofOne=0;
		int minOne =32;
		for(int i=0;i<exor_list.size();i++)
		{
			
			//System.out.println(exor_list.get(i));
			String curr = exor_list.get(i);
			//System.out.println("Exor list entry: "+curr);
			
			numofOne=0;
			for(int j=0;j<curr.length();j++)
			{
				if(curr.charAt(j)=='1')
					numofOne++;
			}
			//System.out.println("No of 1s: "+numofOne);
			/*ArrayList<Integer> find = new ArrayList<Integer>();
			if(numofOne<=4){
				int index = curr.indexOf('1');
				while (index >= 0) {
				    find.add(index);
				    index = curr.indexOf('1', index + 1);
				}
				
				int firs = find.get(0);
				int last = find.get(find.size()-1);
				
				if(last-firs<=3)
					dict_index =i;
			}
			System.out.println("Dict index: "+dict_index);*/
			
			if(numofOne<minOne)
			{
				minOne = numofOne;
				dict_index = i;
			}
		}
		String closest = dict.get(dict_index);
		String ex_list_entry = exor_list.get(dict_index); 
		//System.out.println("Input: "+c);
		//System.out.println("Exor list: "+ex_list_entry);
		//System.out.println("clostest Dictionary match: "+closest);
		
		//System.out.println("Min No of 1s: "+minOne);
		
		if(minOne ==1){										//1-bit mismatch
			int position = ex_list_entry.indexOf('1');
			//System.out.println("start index: "+position);
			String position_str = String.format("%5s",Integer.toBinaryString(position)).replace(' ', '0');
			String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
			//System.out.println("011"+position_str+d);
			output.append("011"+position_str+d);
		}
		
		else if(minOne ==2){								//2-bit consecutive
			int first_position = ex_list_entry.indexOf('1');
			
			if(ex_list_entry.charAt(first_position+1)=='1'){
				String position1_str = String.format("%5s",Integer.toBinaryString(first_position)).replace(' ', '0');
				String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
			    //System.out.println("100"+position1_str+d);
			    output.append("100"+position1_str+d);
			}
			
			else if(bitmask_range(ex_list_entry)==1){		//Bitmask for 2-bits
				
				int first_pos = ex_list_entry.indexOf('1');
				String bitmask;
				int start_location;
				
				if(first_pos<=28){
					bitmask = ex_list_entry.substring(first_pos,first_pos+4);
					start_location = first_pos;
				}
				else{
					bitmask = ex_list_entry.substring(28,32);
					start_location = 28;
				}
				
				String start_location_str = String.format("%5s",Integer.toBinaryString(start_location)).replace(' ', '0');
				String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
				/*System.out.println("In 2*****");
				System.out.println("Start location: "+start_location);
				System.out.println("Bitmask: "+bitmask);
				System.out.println("Dict addr: "+d);*/
				//System.out.println("010"+start_location_str+bitmask+d);
				output.append("010"+start_location_str+bitmask+d);
				
			}
			
			else{											//2-bit mismatches anywhere
				int second_position=32;
				for(int i = first_position+1;i<ex_list_entry.length();i++){
					if(ex_list_entry.charAt(i)=='1')
						second_position =i;
				}
				String position1_str = String.format("%5s",Integer.toBinaryString(first_position)).replace(' ', '0');
				String position2_str = String.format("%5s",Integer.toBinaryString(second_position)).replace(' ', '0');
				String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
				//System.out.println("110"+position1_str+position2_str+d);
				output.append("110"+position1_str+position2_str+d);
			}
		}
		
		else if(minOne ==3){
			int first_pos = ex_list_entry.indexOf('1');
			String bitmask;
			int start_location;
			
			if(first_pos <=28){
				if((ex_list_entry.charAt(first_pos+1)=='1' && ex_list_entry.charAt(first_pos+2)=='1')
					|| (ex_list_entry.charAt(first_pos+1)=='1' && ex_list_entry.charAt(first_pos+2)=='0'&& ex_list_entry.charAt(first_pos+3)=='1')
					|| (ex_list_entry.charAt(first_pos+1)=='0' && ex_list_entry.charAt(first_pos+2)=='1'&& ex_list_entry.charAt(first_pos+3)=='1')
				 )
				{
					bitmask = ex_list_entry.substring(first_pos,first_pos+4);
					start_location = first_pos;
					String start_location_str = String.format("%5s",Integer.toBinaryString(start_location)).replace(' ', '0');
					String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
					/*System.out.println("In 3*****");
					System.out.println("Start location: "+start_location);
					System.out.println("Bitmask: "+bitmask);
					System.out.println("Dict addr: "+d);*/
					//System.out.println("010"+start_location_str+bitmask+d);	
					output.append("010"+start_location_str+bitmask+d);	
					
				}
				
				else{
					//System.out.println("000"+c);			//original binary if not 3-bit consecutive
					output.append("000"+c);
				}
			}
			else{
				bitmask = ex_list_entry.substring(28,32);
				start_location = 28;
				String start_location_str = String.format("%5s",Integer.toBinaryString(start_location)).replace(' ', '0');
				String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
				/*System.out.println("In 3*****");
				System.out.println("Start location: "+start_location);
				System.out.println("Bitmask: "+bitmask);
				System.out.println("Dict addr: "+d);*/
				//System.out.println("010"+start_location_str+bitmask+d);
				output.append("010"+start_location_str+bitmask+d);
			}	
		}
		
		else if(minOne ==4){								// 4-bit consecutive mismatch
			int first_pos = ex_list_entry.indexOf('1');
			if(ex_list_entry.charAt(first_pos+1)=='1' && ex_list_entry.charAt(first_pos+2)=='1'
					&& ex_list_entry.charAt(first_pos+3)=='1'){
				
				String position1_str = String.format("%5s",Integer.toBinaryString(first_pos)).replace(' ', '0');
				String d = String.format("%4s",Integer.toBinaryString(dict_index)).replace(' ', '0');
				//System.out.println("101"+position1_str+position1_str+d);
				output.append("101"+position1_str+position1_str+d);
				
			}
			
			else{
				//System.out.println("000"+c);			//original binary if not 4-bit consecutive
				output.append("000"+c);
			}
		}
		
		else{												// original binary
			//System.out.println("000"+c);
			output.append("000"+c);
		}
	}
	
	
	public static int bitmask_range(String s){
		
		//System.out.println("String is: "+s);
		int first_pos = s.indexOf('1');
		int second_pos =0;
		
		for(int i=first_pos+1;i<s.length();i++){
			if(s.charAt(i)=='1'){
				second_pos =i;
				break;
			}
				
		}
		//System.out.println("First pos--> "+first_pos+", second pos--> "+second_pos);
		if(second_pos - first_pos <=3)
			return 1;
		else
			return 0;
	}
	
	
	private static void order(List<Node> nod) {

	    Collections.sort(nod, new Comparator() {

	        public int compare(Object o1, Object o2) {

	            Integer x1 = ((Node) o1).getFrequency();
	            Integer x2 = ((Node) o2).getFrequency();
	            Integer sComp = x2.compareTo(x1);

	            if (sComp != 0) {
	               return sComp;
	            } else {
	               Integer a1 = ((Node) o1).getPriority();
	               Integer a2 = ((Node) o2).getPriority();
	               return a1.compareTo(a2);
	            }
	    }});
	}
	
	public static void display(){
		try{
			for(int i=0; i< output.length(); i++)
		    {
		        if(i%32 == 0 && (i > 0))
		            wr.write("\n");
	
		        wr.write(output.charAt(i));
		    }
	
		    for(int i=0; i< (32 - (output.length()%32)); i++)
		    {
		    	wr.write("0");
		    }
		    wr.write("\nxxxx");
		    for(int i=0;i<dict.size();i++){
				output_dict.append(dict.get(i));
				wr.write("\n"+dict.get(i));
				//wr.write("\n");
			}
		    
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/*************************Decompression code****************************/	
	public static void decompress() {
		//System.out.println("DeCompression");
		try{
			input_fr = new FileReader(inputFile);
			input_br = new BufferedReader(input_fr);
			String line_input;
			input_br = new BufferedReader(new FileReader(inputFile));
			
			while ((line_input = input_br.readLine()) != null) 
			{
				if(line_input.startsWith("xxxx")){
					break;
				}
				
				output.append(line_input);
			}
			
			while((line_input = input_br.readLine()) != null){
				dict.add(line_input);
			}

			wr = new BufferedWriter(new FileWriter(new File(outputFile)));	
		
		
		/*System.out.println(output);
		for(int i=0;i<dict.size();i++){
			System.out.println(dict.get(i));
		}*/
		
			String compressed_in = output.toString();
			//System.out.println(compressed_in);
			String value ="initial", previous_value="initial";
			for(int i=0;i<compressed_in.length();i++){
				String dict_ind_str;
				
				int dict_ind;
				if(i+3<compressed_in.length()){
					
					String opcode = compressed_in.substring(i,i+3);
					i=i+3;
					if(opcode.equals("000")){
						if(i+32<compressed_in.length()){
							value =  compressed_in.substring(i,i+32);
							i = i+32;
							//System.out.println(value);
							wr.write(value+"\n");
						}
						else{
							break;
						}
					}
					else if(opcode.equals("111")){
						dict_ind_str = compressed_in.substring(i,i+4);
						i=i+4;
						//System.out.println("111 Dict index str: "+dict_ind_str);
						dict_ind = Integer.parseInt(dict_ind_str,2);
						//System.out.println("111 Dict index: "+dict_ind);
						value = dict.get(dict_ind);
						//System.out.println(value);
						wr.write(value+"\n");
					}
					else if(opcode.equals("001")){
						String loop_str = compressed_in.substring(i,i+3);
						i=i+3;
						int loop = Integer.parseInt(loop_str,2);
						for(int j=0;j<=loop;j++){
							//System.out.println(previous_value);
							wr.write(value+"\n");
						}
					}
					else if(opcode.equals("011")){
						String mm_loc_str = compressed_in.substring(i,i+5);
						i=i+5;
						String dic_ind_str = compressed_in.substring(i,i+4);
						i=i+4;
						int mm_loc = Integer.parseInt(mm_loc_str,2);
						int dic_ind = Integer.parseInt(dic_ind_str,2);
						value = dict.get(dic_ind);
					
						char c = value.charAt(mm_loc);
						if(c=='0')
							value = value.substring(0,mm_loc)+'1'+value.substring(mm_loc+1);
						else
							value = value.substring(0,mm_loc)+'0'+value.substring(mm_loc+1);
						
						//System.out.println(value);
						wr.write(value+"\n");
					}
					else if(opcode.equals("100")){
						String mm_loc_str = compressed_in.substring(i,i+5);
						i=i+5;
						String dic_ind_str = compressed_in.substring(i,i+4);
						i=i+4;
						int mm_loc = Integer.parseInt(mm_loc_str,2);
						int dic_ind = Integer.parseInt(dic_ind_str,2);
						String dict_value = dict.get(dic_ind);
						
						StringBuilder sb1 = new StringBuilder();
						StringBuilder sb2 = new StringBuilder();
						
						for(int j=0;j<mm_loc;j++){
							sb1.append("0");
						}
						String ex = "11";
						
						int rem = 32-(mm_loc+2);
						for(int j=0;j<rem;j++){
							sb2.append("0");
						}
						String ex_str = sb1+ex+sb2;
						
						StringBuilder sb3 = new StringBuilder();
					    for (int j = 0; j < ex_str.length(); j++) {
					        sb3.append(charOf(bitOf(ex_str.charAt(j)) ^ bitOf((dict_value).charAt(j))));
					    }
				
					    String result = sb3.toString();
					    value = result;
					    
					    //System.out.println(value);
					    wr.write(value+"\n");
					}
					else if(opcode.equals("101")){
						String mm_loc_str = compressed_in.substring(i,i+5);
						i=i+5;
						String dic_ind_str = compressed_in.substring(i,i+4);
						i=i+4;
						int mm_loc = Integer.parseInt(mm_loc_str,2);
						int dic_ind = Integer.parseInt(dic_ind_str,2);
						String dict_value = dict.get(dic_ind);
						
						StringBuilder sb1 = new StringBuilder();
						StringBuilder sb2 = new StringBuilder();
						
						for(int j=0;j<mm_loc;j++){
							sb1.append("0");
						}
						String ex = "1111";
						
						int rem = 32-(mm_loc+4);
						for(int j=0;j<rem;j++){
							sb2.append("0");
						}
						String ex_str = sb1+ex+sb2;
						
						StringBuilder sb3 = new StringBuilder();
					    for (int j = 0; j < ex_str.length(); j++) {
					        sb3.append(charOf(bitOf(ex_str.charAt(j)) ^ bitOf((dict_value).charAt(j))));
					    }
				
					    String result = sb3.toString();
					    value = result;
					    
					    //System.out.println(value);
					    wr.write(value+"\n");
					}
					else if(opcode.equals("110")){
						String mm_loc_one_str = compressed_in.substring(i,i+5);
						i=i+5;
						String mm_loc_two_str = compressed_in.substring(i,i+5);
						i=i+5;
						String dic_ind_str = compressed_in.substring(i,i+4);
						i=i+4;
						int mm_loc_one = Integer.parseInt(mm_loc_one_str,2);
						int mm_loc_two = Integer.parseInt(mm_loc_two_str,2);
						int dic_ind = Integer.parseInt(dic_ind_str,2);
						value = dict.get(dic_ind);
						
						char c1 = value.charAt(mm_loc_one);
						if(c1=='0')
							value = value.substring(0,mm_loc_one)+'1'+value.substring(mm_loc_one+1);
						else
							value = value.substring(0,mm_loc_one)+'0'+value.substring(mm_loc_one+1);
						
						char c2 = value.charAt(mm_loc_two);
						if(c2=='0')
							value = value.substring(0,mm_loc_two)+'1'+value.substring(mm_loc_two+1);
						else
							value = value.substring(0,mm_loc_two)+'0'+value.substring(mm_loc_two+1);
						
						//System.out.println(value);
						wr.write(value+"\n");
					}
					else{
						String mm_loc_str = compressed_in.substring(i,i+5);
						i=i+5;
						String bitmask_str = compressed_in.substring(i,i+4);
						i=i+4;
						String dic_ind_str = compressed_in.substring(i,i+4);
						i=i+4;
						int mm_loc = Integer.parseInt(mm_loc_str,2);
						int dic_ind = Integer.parseInt(dic_ind_str,2);
						String dict_value = dict.get(dic_ind);
						
						StringBuilder sb1 = new StringBuilder();
						StringBuilder sb2 = new StringBuilder();
						
						for(int j=0;j<mm_loc;j++){
							sb1.append("0");
						}
					
						int rem = 32-(mm_loc+4);
						for(int j=0;j<rem;j++){
							sb2.append("0");
						}
						String ex_str = sb1+bitmask_str+sb2;
						
						StringBuilder sb3 = new StringBuilder();
					    for (int j = 0; j < ex_str.length(); j++) {
					        sb3.append(charOf(bitOf(ex_str.charAt(j)) ^ bitOf((dict_value).charAt(j))));
					    }
				
					    String result = sb3.toString();
					    value = result;
					    
					    //System.out.println(value);
					    wr.write(value+"\n");
					}
					
				}
				else{
					break;
				}
				
				previous_value = value;
				i--;
			}
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
/***************************Main program************************************/
	public static void main(String[] args) {
		
		String choice = args[0];

		//System.out.println("Arguement passed: "+args[0]);
		
		if(choice.equals("1")){
			inputFile = "original.txt";
			outputFile = "cout.txt";
			compress();
			display();
			
		}
			
		else if(choice.equals("2")){
			inputFile = "compressed.txt";
			outputFile = "dout.txt";
			decompress();
		}
			
		else
			System.out.println("Wrong choice");
		 
			try 
			{
				if (input_br != null)
					input_br.close();
				if (input_fr != null)
					input_fr.close();
				
				wr.close();
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		
	}
}