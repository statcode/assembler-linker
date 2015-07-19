/*
Johnny Philavanh
Asm.java
 */
class AsmTab
{
public String loc; //locations
public String label; //label field
public String opcode; //instruction/directives
public String operand; //operand field
public String obj; //object code field

    	public AsmTab(String loc, String label, String opcode, String operand,
    	String obj)
    	{
    	this.loc = loc;
    	this.label = label;
    	this.opcode = opcode;
    	this.operand = operand;
    	this.obj = obj;	
    	}
}
