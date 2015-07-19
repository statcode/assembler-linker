/*
Johnny Philavanh
a2.java
 */
import java.util.*;
import java.io.*;

public class a2 
{
	
	/*
     Function: a2
     Description:
     First assignment function. This function will read in the assembly source
     statements and store them in a structure (AsmTab) of each respective 
     fields (location, label, instruction, operand, and object code). After 
     that, it will invoke the Linker function to begin the assembly process.
     */
	public static void a2() throws IOException
	{
	String instr;
	Vector Instruction = new Vector(); //SIC/XE assem. instruction input
  	
  	///////////INPUT/OUTPUT FILES//////////////
	String fileNin = "a2in.asm";
	String fileNout = "est.out";
	String fileNoutOB = "a2out.obj";
	BufferedReader fileIn = new BufferedReader(new FileReader(fileNin));
	
	//PrintWriter for writing to a file		
	PrintWriter fileOut = new PrintWriter(new FileWriter(fileNout));
	PrintWriter fileOutOB = new PrintWriter(new FileWriter(fileNoutOB));
	
  		ReadIn(Instruction,fileIn);//Reading in the ininstructions from file
  		Linker(Instruction, fileOut, fileOutOB);
    }
    
    /*
     Function: a2
     Description:
     The linker function calls two main functions: Pass1 - to build the EST
     (External Symbol Table) so it can provide neccessary information for
     modification, relocation, and linking external definition/references
     addresses in the control sections.
     Pass2 will start the actual assembling process to build a linker for 
     external references in the object code.
     */
    public static void Linker(Vector Instruction, PrintWriter fileOut, 
     PrintWriter fileOutOB)
    {
     Vector EstabT = new Vector(); //External Symbol Table

     Pass1(Instruction, EstabT); //to build the external symbol table
     Pass2(Instruction, EstabT, fileOutOB);
     OutEST(EstabT, fileOut); //writing the ESTab to a file
     }
     
    //Build the EST (External Symbol Table) 
    public static void Pass1(Vector Instruction, Vector EstabT)
	{
	AsmTab Asm;
	Estab estemp; //temp data of EST
	String curCS = ""; //label of the current control section
	int i = 0;		
		
			do
			{
			//extract individual source statement record
     		Asm = (AsmTab)Instruction.elementAt(i);	
     		
					if(Asm.opcode.equalsIgnoreCase("START"))
					{
					curCS = Asm.label;	
					}
				do
				{

				   if(!Asm.opcode.equalsIgnoreCase("END"))
				   {
				   i++;
				   
				   //extract individual record
     			   Asm = (AsmTab)Instruction.elementAt(i);
     			   
     			   	  //if there's a symbol in the label field
     			   	  if(!Asm.label.equalsIgnoreCase(""))
     			   	  {
     			   	  //insert into ESTAB
     			   	  //curCS = cotrol section
     			   	  //Asm.label = symbol name
     			   	  //Asm.loc = Address
     			   	  	
     			   	  estemp = new Estab(curCS, Asm.label, Asm.loc);	
     			   	  EstabT.addElement(estemp);	
     			   	  }	//end if-in
    			   }//end if-out
    			   
    			   
    			}while(!Asm.opcode.equalsIgnoreCase("END")); //end of CS
			
			
			//reset the current control section variable
			curCS = "";	
			i++;		
			}while(i <= Instruction.size()-1); //end first-do-while
	
	} 
	
	/*
     Function: Pass2
     Description:Given the SIC/XE assmbly instructions in the Vector Instruction,
     this big Pass2 function will do the actual assembly and linking process.
     This consists of the assembler handling the control sections by linking
     different external definition/references between them. The information
     in the object program include:
     H = header file, D = define record, R= refine record,T = text(object code)
     M = modification record, and E = END directive. Pass2 will assemble the 
     source code to produce these information.
     */
    public static void Pass2(Vector Instruction, Vector EstabT, PrintWriter fileOutOB)
    {
    Vector TextR = new Vector(); //Text Record
    Vector ModR = new Vector(); //Modify Record (temporary, to match with)
    Vector ModT = new Vector(); //Overall Modify Record to generate object code
    
    //string for temporary appending of each information object record
    String asmH, asmD, asmR, asmT, asmM, asmE;
    asmH=asmD=asmR=asmT=asmM=asmE="";
    String[] ExtD; String[] ExtR; //for EXTDEF and EXTREF definitions
    String[] OperandR; //for operand expression
    String[] OperandO; //for operator expression
    
    String locctr = ""; //current location in the assembly source
    String[] Text = new String[10]; //max. of 10 text records
    int rn = 0; //record number counter
    int len, totL = 0;
    boolean txtFull; //flag to determine whether a new T record should start 
    String txtSize = ""; //variable to hold size of T record
    
    //Modification record variables
    String lenL = ""; //length of field to be modifed (in half-byte)
    String mflag = ""; //Modification flag (+ or -)
    Vector OpFlag = new Vector();//operators to append EXTREF symbol in M record
    
    int i = 0; //index counter for Instruction vector (whole assembly source)
    int locInt = 0; //temp. location counter in integer value
    
    //Structure to access different fields of the assembly source
    AsmTab Asm, AsmE; //Asm = main use, AsmE = just for the END directive
     
     //Text Record variables
     //Use to determine if the next opcode is an END directive; if so then it
     //will end the text record line in the object code
     String nextOP = "";
     String nextOBJ = "";
     
     String curCS = ""; //label of the current control section
     	
     	//Go through source record statement, line by line.
     	do
     	{
     	txtFull = true; //intialize flag for Text record
     				   //true = new Text record need to be made
     	
     		do //Start of Control Section
    		{
    		//extract individual statement record
     		Asm = (AsmTab)Instruction.elementAt(i);
     		if(!Asm.opcode.equalsIgnoreCase("END"))
     		{
     		AsmE = (AsmTab)Instruction.elementAt(i+1);//to check for END
     		nextOP = AsmE.opcode; //next opcode
     		nextOBJ = AsmE.obj;  //next object code
		    }	
     		
     		
            //current counter value
            if(!Asm.loc.equals("")) locctr = Asm.loc;
    			
    			//////////////Assemble header record/////////////////
    			if(Asm.opcode.equalsIgnoreCase("START"))
    			{
    			curCS = Asm.label;
    			asmH+="H"; //Header
    			asmH+="^" + LabF(Asm.label); //Program Name
    			asmH+="^" + LocF(Asm.loc);  //Starting Address
    			}
    			
    			///////////////////Assemble define record/////////////
    			else if(Asm.opcode.equalsIgnoreCase("EXTDEF"))
    			{
    			asmD+="D"; //Define
    			    
    			    //split external definitions seperating by comma to array
    			    ExtD = Asm.operand.split(",");
    			    	
    			    	for(int j = 0; j < ExtD.length; j++)
    			    	{
    			    	  for(int k = 0; k < EstabT.size(); k++)
    			    	  {
    			    	  Estab temp = (Estab)EstabT.elementAt(k);
    			    	  	
    			    	  	//if extdef match any in the EST within this CS
    			    	  	if(ExtD[j].equalsIgnoreCase(temp.symn) && 
    			    	  	curCS.equalsIgnoreCase(temp.controls))	
    			    	  	{
    			    	  	asmD+="^" + LabF(ExtD[j]);//name of ext. symbol defined
    			    	  	asmD+="^" + LocF(temp.addy);//relative address	
    			    	  	}
    			    	  
    			    	  }
    			    	}
    			}
    			
    			///////////////////Assemble refer record/////////////
    			else if(Asm.opcode.equalsIgnoreCase("EXTREF"))
    			{
    			asmR+="R"; //Refer
    			    
    			    //split external definitions seperating by comma to array
    			    ExtR = Asm.operand.split(",");
    			    	
    			    	for(int j = 0; j < ExtR.length; j++)
    			    	{
    			    	  asmR+="^" + LabF(ExtR[j]);
    			    	  ModR.addElement(ExtR[j]);
    			    	}
    			    	
    			}
    			
    			//////////////Assemble text record/////////////////
    			else
    			{
				    
				    if(!( Asm.opcode.equalsIgnoreCase("RESW") ||
				    Asm.opcode.equalsIgnoreCase("RESB") || 
				    Asm.opcode.equalsIgnoreCase("EQU") ) )
				    {
				    	//if there's not a new text record addy yet
				    	if(!txtFull)
				    	{
				    	   //insert into temp. array
				    	   /*
				    	   if length exceed 30(hex) or 1024bits then begin 
				    	   new Text record
				    	   */

				    	        if(totL <= 30) 
				    	   		{
				    	   		Text[rn] = Asm.obj;
				    	   		len = (Asm.obj.length())/2;
				    	   		totL+=len;
				    	   		rn++;
				    	   		}
				    	   		
				    	   		
				    	   		if(totL > 30 || nextOP.equalsIgnoreCase("END"))
				    	   		{
				    	   //calculate the size of the Text record
				    	   txtSize = Integer.toString(totL, 16).toUpperCase();
				    	   asmT+="^" + LenF(txtSize);
				    	   	
				    	   		//append rest of the text record together	
				    	   			for(int j = 0; j < rn; j++)
				    	   			{
				    	   				asmT+="^" + Text[j];
				    	   			}
				    	   			TextR.addElement(asmT);	
				    	   			
				    	   		//reset values to start a new T record
				    	   		txtFull = true;
				    	   		rn = 0; 
				    	   		totL = 0;
				    	   		asmT="";
				    	   		}
				    	   		
				    	   		/*
				    		Directives RESW, RESB, EQU reserve certain data area
				    		size. It's best to start a  new text record in the
				    		assembling process.
				    			*/
				    	   		else if( nextOP.equalsIgnoreCase("RESB") ||
				    	   		nextOP.equalsIgnoreCase("RESW") ||
				    	   		nextOP.equalsIgnoreCase("EQU") )
				    	   		{
				    	   		//calculate the size of the Text record
				    	   txtSize = Integer.toString(totL, 16).toUpperCase();
				    	   asmT+="^" + LenF(txtSize);
				    	   	
				    	   		//append rest of the text record together	
				    	   			for(int j = 0; j < rn; j++)
				    	   			{
				    	   				//to avoid two ^
				    	   				if(j == 0) asmT+=Text[j];
				    	   				else asmT+="^" + Text[j];
				    	   			}
				    	   			TextR.addElement(asmT);	
				    	   			
				    	   		//reset values to start a new T record
				    	   		txtFull = true;
				    	   		rn = 0; 
				    	   		totL = 0;
				    	   		asmT="";	
				    	   		}
				    	   	

				    	}
				    	else //Begin new Text Record!
				    	{
				    	   asmT+="T"; //Text
				    	   asmT+="^" + LocF(Asm.loc);
				    	   Text[rn] = Asm.obj;
				    	   totL+=(Asm.obj.length()/2);
				    	   rn++;
				    	   txtFull = false;
				    	}
				    }
				 
    			}
    			
    			//////////Assemble modification (revised) record////////////
    			//if external reference has been defined
    			    if(ModR.size() > 0 && !Asm.obj.equalsIgnoreCase(""))
    				{
    					
    				   //Change form A-(B-C) to A-B+C
    				   Asm.operand = DistrOP(Asm.operand);
    				   //extract the symbol names only
    				   OperandR = Asm.operand.split("[^a-zA-Z0-9]");
    				   //extract the operators
						String tempR = "";
						for(int j = 0; j < Asm.operand.length(); j++)
						{
						   if(Asm.operand.charAt(j) == '-' ||
						      Asm.operand.charAt(j) == '+')
						   {
						   	tempR+=Asm.operand.charAt(j);
						      OpFlag.addElement(tempR);	
						      tempR="";
						   }
						}
						
    				   //begin modify assembling process
    				   for(int j = 0; j < OperandR.length; j++)
    				   {
    				   	  for(int k = 0; k < ModR.size(); k++)
    				   	  {
    				   	    //of symbols in operand are part of EXTREF
    				   	    if(OperandR[j].equalsIgnoreCase((String)ModR.elementAt(k)))
    				   	    {
    				   	    asmM+="M";
    				   	    	
    				   	    	//if it's extended instruction
    				   	    	//increment adddress
    				   	    	if(Asm.opcode.charAt(0) == ('+'))
    				   	    	{
    				   	    	locInt = Integer.parseInt(Asm.loc, 16);
								locInt++;
					  String mLoc = Integer.toString(locInt, 16).toUpperCase();
								asmM+="^" + LocF(mLoc); 
								len = (Asm.obj.length()-3);//minus 3-byte
								lenL = Integer.toString(len);
								asmM+="^" + LenF(lenL);
    				   	    	}
    				   	    	
    				   	    	//3-byte size, modify whole field
    				   	    	else 
    				   	    	{
    				   	    	asmM+="^" + LocF(Asm.loc);
    				   	    	len = (Asm.obj.length());
    				   	    	lenL = Integer.toString(len);
    				   	    	asmM+="^" + LenF(lenL);
    				   	    	}
    				   	    	
    				   	    	//External symbol whose value is to be added to
								//or subtracted from the indicated field.
								String tempRef = OperandR[j];
								/////////Flag check
								//first symbol in expression is added
								if(j == 0) mflag = "+";
								else if(j > 0) mflag = (String)OpFlag.elementAt(j-1);
								/////append the modification flag bits
    				   	    	asmM+="^" + mflag + tempRef; 
    				   	    	ModT.addElement(asmM);
    				   	    
    				   	    	mflag = "";
    				   	    	asmM = "";
    				   	    
    				   	    }
    				   	   
    				   	    
    				   	  }
    				   }
    				   //Ensure that the operators flag bits vector is reset
    				   OpFlag.removeAllElements();
    				    
    				}
    			///////////////////Assemble end record/////////////
    			if(Asm.opcode.equalsIgnoreCase("END"))
    			{
    			 
    			    asmE+="E";
    				
    				//if END has a operand symbol, then append its address
    				if(!Asm.operand.equalsIgnoreCase(""))
    				{
    			          for(int j = 0; j < EstabT.size(); j++)
    			    	  {
    			    	  Estab temp = (Estab)EstabT.elementAt(j);
    			    	  	  if(Asm.operand.equalsIgnoreCase(temp.symn) &&
    			    	  	  curCS.equalsIgnoreCase(temp.controls))
    			    	  	  {
    			    	  	  	asmE+="^" + LocF(temp.addy);
    			    	  	  	break;
    			    	  	  }
    			    	  	  	
    			    	  }	
    			
    				}
    			}
    			
    			if(!Asm.opcode.equalsIgnoreCase("END"))i++;
    			
   
    				
    		}while(!Asm.opcode.equalsIgnoreCase("END")); //end of CS
    		
    		
    		//continuation of header record assembling
    		locInt = Integer.parseInt(locctr, 16);
			locInt = locInt+3;
			locctr = Integer.toString(locInt, 16).toUpperCase();
			asmH+="^" + LocF(locctr); //Header record
    	
    	//Done! Ready to output this object code
    	System.out.println(asmH);
    	fileOutOB.println(asmH);
    	System.out.println(asmD);
    	fileOutOB.println(asmD);
    	System.out.println(asmR);
    	fileOutOB.println(asmR);
    	Output(TextR, fileOutOB);
    	Output(ModT, fileOutOB);
    	System.out.println(asmE);
    	fileOutOB.println(asmE);
    	System.out.println("\n");
    	
    	//reset values for the next control section
        asmH = ""; asmD = ""; asmR = ""; asmT = ""; asmM = ""; asmE ="";
        TextR.removeAllElements();
        ModR.removeAllElements();
        ModT.removeAllElements();
    	OpFlag.removeAllElements();
        i++; //increment to next record
    	}while(i <= Instruction.size()-1); //end of overall assembly source
    	fileOutOB.close(); 
    }
    
   
	    
    //This function will read in the source input file and store the 
    //instructions in a data structure
    public static void ReadIn(Vector Instruction, BufferedReader fileIn) throws IOException
    {
    ////////////READ IN FILE//////////////
	String instr, loc, label, opcode, operand, obj;
	AsmTab myAsm;
		
		while ((instr = fileIn.readLine()) != null) //null = EOF 
        {
            //60 columns total (seperated by 2 spaces)
            loc = instr.substring(0,4); //4 columns
            loc = loc.trim();
		   	label = instr.substring(6,12); //6 columns
		   	label = label.trim();
			opcode = instr.substring(14,22); //8 columns
			opcode = opcode.trim();
			operand = instr.substring(24,50); //26 columns
			operand = operand.trim();
			obj = instr.substring(52,60); //8 columns
			obj = obj.trim();
			
			myAsm = new AsmTab(loc,label,opcode,operand,obj);
           Instruction.addElement(myAsm);
        }
        fileIn.close();	
    }
 
 ///////////////////////UTILITY METHODS////////////////////////////////////   
    //Change the form "A-A-(B-B)" to "A-A-B+B" of a string
    //This only process if the expression contains parentheses
    public static String DistrOP(String str)
    {
    StringBuffer sbf = new StringBuffer(str); //for character replacement
	String temp1 = ""; String temp2 = "";

		for(int i = 0; i < str.length()-1; i++)
		{
			
			//if -(, then do inverse sign distribution
			if(str.charAt(i) == '-' && str.charAt(i+1) == '(')
			{
			 sbf.setCharAt(i+1, '*');
			
				for(int j = i+1; j < str.length(); j++)
				{
					if(str.charAt(j) == '-')
					{
					sbf.setCharAt(j, '+');
					}
					else if(str.charAt(j) == '+')
					{
					sbf.setCharAt(j, '-');
					}
					else if(str.charAt(j) == ')')
					{
					sbf.setCharAt(j, '*');
					break;
					}
				}
				
			}
			
			//if +(, then do sign are fine
			else if(str.charAt(i) == '+' && str.charAt(i+1) == '(')
			{
			//replace with * to delete
			 sbf.setCharAt(i+1, '*');
			
				for(int j = i+1; j < str.length(); j++)
				{
				    if(str.charAt(j) == ')')
					{
					sbf.setCharAt(j, '*');
					break;
					}
				}
				
			}
			//if +(, then do sign are fine
			else if(str.charAt(i) == '#')
			{
			//replace with * to delete
			 sbf.setCharAt(i, '*');
			}	
		}
		
		temp1 = sbf.toString(); //stringbuffer to string
		
		//new string will have all * remove
		for(int i = 0; i < temp1.length(); i++)
		{
			if(temp1.charAt(i) != '*')
			{
				temp2+=temp1.charAt(i);
			}	
		}
		
		return temp2;
    }
    
    //Output the Text and Modification record
    public static void Output(Vector tempV, PrintWriter fileOutOB)
    {
    	for(int i = 0; i < tempV.size(); i++)
    	{
		System.out.println(tempV.elementAt(i));	
		fileOutOB.println(tempV.elementAt(i));	
    	}
    }
    
    //Output the EST (External Symbol Table) to a file
    public static void OutEST(Vector EstabT, PrintWriter fileOut)
    {
    Estab est;
    	
    	for(int i = 0; i < EstabT.size(); i++)
    	{
    		est = (Estab)EstabT.elementAt(i);
        	fileOut.println(est.controls + " " + est.symn + " " + est.addy);
    	}
    	fileOut.close(); 
    }
   
   //////////////FORMAT Column Specifications//////// 
   //Symbols columns must be 6 columns long
    public static String LabF(String label)
    {
    int max = 6;
    int len = max - label.length();
    
    	
    	for(int i = 0; i < len; i++)
    	{
    	label+=" ";	
    	}
    	return label;	
    }
    
    //location address must be 3-byte
    public static String LocF(String location)
    {
    int max = 6;
    int len = max - location.length();
    
    	for(int i = 0; i < len; i++)
    	{
    	location = "0" + location;	
    	}
    	return location;	
    }
    
    //Lenght of the field to be modified must be 2 columns (i.e 8-9)
    public static String LenF(String recordL) //record Length
    {
    int max = 2;
    int len = max - recordL.length();
    
    	for(int i = 0; i < len; i++)
    	{
    	recordL = "0" + recordL;	
    	}
    	return recordL;	
    }
    
 /////////////////////////////////MAIN////////////////////////////////   
    public static void main(String[] args) throws IOException 
    {
     a2();  
	}
}
