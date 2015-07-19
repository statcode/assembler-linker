Johnny Philavanh
 masc1260
 Assignment 2 - Linker
 
---- A listing of all files in the project 

a2.java - main file to be compiled

a2in.asm - input file (Figure 3.8)

a2out.obj - output of the object code (Figure 3.9)

est.out - output file of the EST (External Symbol Table)

Estab.java - struct/class for external symbols
Format of file: (Control Section) (Symbol) (Address)

AsmTab.java - struct/class for the whole assembly instructions

---- Instructions on how to compile and run the program 
rohan%: make

OR

rohan%: javac a2.java
rohan%: java a2

--- Program inputs and program outputs 
a2in.asm - input file (of assembly source)
a2out.obj - output file of the object code for loading
est.out - output file (just the EST)

For a2in.asm - each line must be 60 columns in width, following format as specifed:

Input file spec
///////Each field seperators are 2 spaces each.
Location = 4 cols
[field separator]
Label (optional, else cols hold space chars) = 6 cols
[field separator]
Instruction/directive = 8 cols (note, 1st col reserved for + or @ or =)
[field separator]
Operand = 26 cols (note, 1st col reserved for #)
[field separator]
Object Code = 8 cols

The input source assumes there are no duplicate symbols.
It assumes there's no comment i.e "." 

Control section begins w/ directive START and the end of the control section is indicated by 
the directive END. If those are not in the assembly source, then the object code will produce 
an error.

As long as the "SIC/XE Assembler Listing Format" is similar to Figure 3.8 (w/o the comments)
or even the same format as my a2in.asm, the assembler should have no problem reading
and processing the file.

--- A description of the organization of your program 
This is a SIC/XE linker. It will read program listings like Figure 3.8 of the text and
produce an object file like Figure 3.9 of the text. The main goal of this linker is to
assemble the external definitions/references between different control sections. The object
code produce will allow linking loader to load that program into memory and for relocation. 

Overall, a2.java begins by reading in the program listings (a2in.asm) and store those
as a structure. Pass 1 is used to build an EST (external symbol table), which will 
pass that information to Pass 2. Pass 2 does the full assembling and linking process
between cotrol sections. The result if the object code that's output to the screen and 
one written to a file (a2out.obj). 

--- Issues (functions not implemented, potential errors, filename limitations/assumptions, etc) 
My modified record (col 11-16) is straight forward. Any  symbols in the operand
that requires external references will be inluded in the object code (modificaiton record). 
There's no PROGA,PROGB, PROC, etc in the modification portion of the object code in my 
version.

This input assumes there's no literals (i.e =C'EOF'), LTORG, indirect, and indexed addressing
in the source statements. Entering those types of instructions might yield an error.


--- Lessons learned, significant findings 
The difficulty in producing the object code was the Text record and Modification
part. With Text record, catious was be made for the length of the text
record. New line began whenever the length over 1024 in bits (30 bytes), (RESW,RESB, EQU)
is declared, or END directive is the next instruction.

There was also an issue in the factoring an expression i.e ENDA-LISTA-(ENDB-LISTB)
Rather than creating one big expression tree structure to distribute the "-("
I just wrote a simple loop to detect any operands with "-(" and have the
sign of the operator inside the parenthese be reverse. For instance,
"ENDA-LISTA-(ENDB-LISTB)" becomes "ENDA-LISTA-ENDB+LISTB" This is done in the
DistrOP(String str) method in a2.java
