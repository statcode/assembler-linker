0000  PROGA    START     0                                  
               EXTDEF    LISTA,ENDA                         
               EXTREF    LISTB,ENDB,LISTC,ENDC              
0020  REF1     LDA       LISTA                      03201D  
0023  REF2    +LDT       LISTB+4                    77100004
0027  REF3     LDX      #ENDA-LISTA                 050014  
0040  LISTA    EQU       *                                  
0054  ENDA     EQU       *                                  
0054  REF4     WORD      ENDA-LISTA+LISTC           000014  
0057  REF5     WORD      ENDC-LISTC-10              FFFFF6  
005A  REF6     WORD      ENDC-LISTC+LISTA-1         00003F  
005D  REF7     WORD      ENDA-LISTA-(ENDB-LISTB)    000014  
0060  REF8     WORD      LISTB-LISTA                FFFFC0  
               END       REF1                               
0000  PROGB    START     0                                                                   
               EXTDEF    LISTB,ENDB                         
               EXTREF    LISTA,ENDA,LISTC,ENDC              
0036  REF1    +LDA       LISTA                      03100000  
003A  REF2     LDT       LISTB+4                    772027  
003D  REF3    +LDX      #ENDA-LISTA                 05100000
0060  LISTB    EQU       *                                  
0070  ENDB     EQU       *                                  
0070  REF4     WORD      ENDA-LISTA+LISTC           000000  
0073  REF5     WORD      ENDC-LISTC-10              FFFFF6  
0076  REF6     WORD      ENDC-LISTC+LISTA-1         FFFFFF  
0079  REF7     WORD      ENDA-LISTA-(ENDB-LISTB)    FFFFF0  
007C  REF8     WORD      LISTB-LISTA                000060  
               END                                          
0000  PROGC    START     0                                  
               EXTDEF    LISTC,ENDC                         
               EXTREF    LISTA,ENDA,LISTB,ENDB              
0018  REF1    +LDA       LISTA                      03100000
001C  REF2    +LDT       LISTB+4                    77100004
0020  REF3    +LDX      #ENDA-LISTA                 05000000
0030  LISTC    EQU       *                                  
0042  ENDC     EQU       *                                  
0042  REF4     WORD      ENDA-LISTA+LISTC           000030  
0045  REF5     WORD      ENDC-LISTC-10              000008  
0048  REF6     WORD      ENDC-LISTC+LISTA-1         000011  
004B  REF7     WORD      ENDA-LISTA-(ENDB-LISTB)    000000  
004E  REF8     WORD      LISTB-LISTA                000000  
               END                                          
