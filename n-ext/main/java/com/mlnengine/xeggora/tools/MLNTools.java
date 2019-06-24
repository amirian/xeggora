package com.mlnengine.xeggora.tools;
import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.googlecode.rockit.app.evaluator.ObjectiveEvaluator;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;

public class MLNTools {
    public static float SAMPLE_RATE = 8000f;
    public static void tone(int hz, int msecs) 
    {
        try {
			tone(hz, msecs, 1.0);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void tone(int hz, int msecs, double vol)
    throws LineUnavailableException 
    {
        byte[] buf = new byte[1];
        AudioFormat af = new AudioFormat(SAMPLE_RATE,8,1,true,false);     
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for (int i=0; i < msecs*8; i++) {
              double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
              buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
              sdl.write(buf,0,1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
    
    public static void generateMLN() throws FileNotFoundException, UnsupportedEncodingException {
/*    	PrintWriter writer = new PrintWriter("data//main//evidence.db", "UTF-8");
    	for (int i=0; i<200000;i++)
    		writer.println("Sad(R"+i+")");
    	writer.close();    	
*/
    	PrintWriter writer = new PrintWriter("data//main//evidence.db", "UTF-8");
    	for (int i=0; i<25000;i++)
    		writer.println("Kind(B,R"+i+",F,I)");
    	for (int i=25000; i<50000;i++)
    		writer.println("Kind(C,R"+i+",F,I)");
    	for (int i=50000; i<60000;i++)
    		writer.println("Kind(C,R"+i+",F,G)");
    	for (int i=60000; i<75000;i++)
    		writer.println("Kind(B,R"+i+",H,G)");
    	for (int i=75000; i<90000;i++)
    		writer.println("Kind(B,R"+i+",H,I)");
    	for (int i=90000; i<100000;i++)
    		writer.println("Kind(C,R"+i+",H,G)");
    	for (int i=0; i<50000;i++)
    		writer.println("Sad(R"+i*2+")");
    	writer.close();    	
//    	System.out.println(System.getenv("LD_LIBRARY_PATH"));
    	
    }

    public static void modifyMLN_ER() throws FileNotFoundException, UnsupportedEncodingException {

    	BufferedReader br = new BufferedReader(new FileReader("source.txt"));
    	PrintWriter writer = new PrintWriter("evidence.db", "UTF-8");
    	PrintWriter writer0 = new PrintWriter("evidence0.db", "UTF-8");
    	String[] src = {
    			"SameAuthor(a1,a2) v !HasWordAuthor(a1,\"",
    			"SameAuthor(a1,a2) v HasWordAuthor(a1,\"",
    			"SameAuthor(a1,a2) v HasWordAuthor(a2,\"",
    			"SameTitle(a1,a2) v !HasWordTitle(a1,\"",
    			"SameTitle(a1,a2) v HasWordTitle(a1,\"",
    			"SameTitle(a1,a2) v HasWordTitle(a2,\"",
    			"SameVenue(a1,a2) v !HasWordVenue(a1,\"",
    			"SameVenue(a1,a2) v HasWordVenue(a1,\"",
    			"SameVenue(a1,a2) v HasWordVenue(a2,\"",
    			"!Author(a1,a2) v !Author(a3,a4) v SameBib(a1,a3) v !HasWordAuthor(a2,\"",
    			"!Author(a1,a2) v !Author(a3,a4) v SameBib(a1,a3) v HasWordAuthor(a2,\"",
    			"!Author(a1,a2) v !Author(a3,a4) v SameBib(a1,a3) v HasWordAuthor(a4,\"",
    			"!Title(a1,a2) v !Title(a3,a4) v SameBib(a1,a3) v !HasWordTitle(a2,\"",
    			"!Title(a1,a2) v !Title(a3,a4) v SameBib(a1,a3) v HasWordTitle(a2,\"",
    			"!Title(a1,a2) v !Title(a3,a4) v SameBib(a1,a3) v HasWordTitle(a4,\"",
    			"!Venue(a1,a2) v !Venue(a3,a4) v SameBib(a1,a3) v !HasWordVenue(a2,\"",
    			"!Venue(a1,a2) v !Venue(a3,a4) v SameBib(a1,a3) v HasWordVenue(a2,\"",
    			"!Venue(a1,a2) v !Venue(a3,a4) v SameBib(a1,a3) v HasWordVenue(a4,\""
    	};
    	String[] des = {
    			"ConcludeSameAuthorConf(",
    			"ConcludeSameAuthorConf1(",
    			"ConcludeSameAuthorConf2(",
    			"ConcludeSameTitleConf(",
    			"ConcludeSameTitleConf1(",
    			"ConcludeSameTitleConf2(",
    			"ConcludeSameVenueConf(",
    			"ConcludeSameVenueConf1(",
    			"ConcludeSameVenueConf2(",
    			"ConcludeSameBibfromAuthorConf(",
    			"ConcludeSameBibfromAuthorConf1(",
    			"ConcludeSameBibfromAuthorConf2(",
    			"ConcludeSameBibfromTitleConf(",
    			"ConcludeSameBibfromTitleConf1(",
    			"ConcludeSameBibfromTitleConf2(",
    			"ConcludeSameBibfromVenueConf(",
    			"ConcludeSameBibfromVenueConf1(",
    			"ConcludeSameBibfromVenueConf2("
    	};
    	int i;
    	int numofcomment=0;
    	int numofspace=0;
    	int numofformula=0;
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
        	    if (line.length()==0) {
        	    	numofspace ++;
        	    	continue;
        	    }
        	    if (line.startsWith("//")) {
        	    	numofcomment ++;
        	    	continue;
        	    }
	        	StringBuilder sb = new StringBuilder();
	        	i = line.indexOf("  ");
	        	String conf = line.substring(0, i);
	        	line = line.substring(i);
	        	for (int r = 0; r < 18; r++) {
		        	i = line.indexOf(src[r]);
		        	if (i >= 0) {
		        		numofformula ++;
			        	sb.append(des[r]);
			        	line = line.substring(i+src[r].length());
			        	i = line.indexOf("\"");
//			        	if (line.substring(0, i).equalsIgnoreCase("Wordicml"))
	//		        		r = r + 1 - 1;
			        	sb.append(line.substring(0, i));
			        	sb.append(","+conf+")");
			        	if (conf.equalsIgnoreCase("0"))
			        		writer0.println(sb.toString());
			        	else
			        		writer.println(sb.toString());
		        		break;
		        	}
	        	}
    	    }
    	} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(numofspace+" Spaces.\n"+numofcomment+" Comments.\n"+numofformula+" Formulas.");
    	    try {
				br.close();
				writer.close();
				writer0.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    public static void modifyMLN_IE() throws FileNotFoundException, UnsupportedEncodingException {

    	BufferedReader br = new BufferedReader(new FileReader("source.txt"));
    	PrintWriter writer = new PrintWriter("evidence.db", "UTF-8");
    	PrintWriter writer0 = new PrintWriter("evidence0.db", "UTF-8");
    	String[] src = {
    			"  !Token(\""
    	};
    	String[] des = {
    			"ConcludeInFieldConf("
    	};
    	int i;
    	int numofcomment=0;
    	int numofspace=0;
    	int numofformula=0;
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
        	    if (line.length()==0) {
        	    	numofspace ++;
        	    	continue;
        	    }
        	    if (line.startsWith("//")) {
        	    	numofcomment ++;
        	    	continue;
        	    }
	        	StringBuilder sb = new StringBuilder();
	        	i = line.indexOf("  ");
	        	String conf = line.substring(0, i);
	        	line = line.substring(i);
	        	for (int r = 0; r < 1; r++) {
		        	i = line.indexOf(src[r]);
		        	if (i >= 0) {
		        		numofformula ++;
			        	sb.append(des[r]);
			        	line = line.substring(i+src[r].length());
			        	i = line.indexOf("\"");
//			        	if (line.substring(0, i).equalsIgnoreCase("Wordicml"))
	//		        		r = r + 1 - 1;
			        	sb.append(line.substring(0, i));
			        	
			        	
			        	i = line.indexOf(" v InField(a1,\"");
			        	line = line.substring(i+" v InField(a1,\"".length());
			        	i = line.indexOf("\"");
//			        	if (line.substring(0, i).equalsIgnoreCase("Wordicml"))
	//		        		r = r + 1 - 1;
			        	sb.append(",");
			        	sb.append(line.substring(0, i));
			        	sb.append(","+conf+")");
			        	if (conf.equalsIgnoreCase("0"))
			        		writer0.println(sb.toString());
			        	else
			        		writer.println(sb.toString());
		        		break;
		        	}
	        	}
    	    }
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println(numofspace+" Spaces.\n"+numofcomment+" Comments.\n"+numofformula+" Formulas.");
    	    try {
				br.close();
				writer.close();
				writer0.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public static void modifyMLN_CiteSeer(int n) throws FileNotFoundException, UnsupportedEncodingException {

    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/Citeseer/" + n + "/simple/progArith.mln"));
    	PrintWriter writer = new PrintWriter("data/GSW/Citeseer/" + n + "/evidence.db", "UTF-8");
    	PrintWriter writer1 = new PrintWriter("data/GSW/Citeseer/" + n + "/simple/prog.mln", "UTF-8");
    	
    	DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    	df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

    	int i;
    	int numofcomment=0;
    	int numofspace=0;
    	int numofformula=0;
    	int numofmisc = 0;
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
        	    if (line.length()==0) {
        	    	numofspace ++;
        	    	writer1.println(line);
        	    	continue;
        	    }
        	    if (line.startsWith("//")) {
        	    	numofcomment ++;
        	    	writer1.println(line);
        	    	continue;
        	    }
	        	i = line.indexOf("  !Token(\"");
	        	if (i < 0) {
	        		numofmisc++;
        	    	writer1.println(line);
	        		continue;
	        	}
        		numofformula ++;
	        	String confs = line.substring(0, i);
	        	double conf = Double.valueOf(confs);
    	    	writer1.println(df.format(conf) + line.substring(i));
	        	StringBuilder sb = new StringBuilder("ConcludeTDDIConf");
	        	if (conf < 0)
	        		sb.append("Neg");
//	        	System.out.println(df.format(conf));
	        	line = line.substring(i + 10);
	        	sb.append('(').append(line.substring(0,line.indexOf('\"'))).append(',');
	        	int j = line.indexOf("\",a1)");
	        	if (j < 0) {
	        		numofmisc ++;
	        		continue;
	        	}
	        	sb.append(line.substring(line.indexOf("\") v InField(a2,\"") + 17, j)).append(',');
	        	sb.append(df.format(conf)).append(')');
	        	writer.println(sb);
    	    }
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println(numofspace+" Spaces.\n"+numofcomment+" Comments.\n"+numofformula+" Formulas.\n"+numofmisc+" Micsellanious.");
    	    try {
				br.close();
				writer.close();
				writer1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public static void modifyMLN_WEBKB(int PredN, int UnivN) throws FileNotFoundException, UnsupportedEncodingException {

    	String[] Conf = {"ClassLinkToClass","HasClass","NotHasClass","Class"};
    	String[] Univ = {"Cornell","Texas","Washington","Wisconsin"};
    	BufferedReader br = new BufferedReader(new FileReader("data/WebKB/learn/"+Univ[UnivN]+"/" + Conf[PredN] + ".db"));
    	PrintWriter writer = new PrintWriter("data/WebKB/learn/"+Univ[UnivN]+"/new/" + Conf[PredN] + ".db", "UTF-8");
    	PrintWriter writer2 = new PrintWriter("data/WebKB/learn/"+Univ[UnivN]+"/simple/" + Conf[PredN] + ".db", "UTF-8");
    	writer.println("//auto-generated evidence");
    	writer2.println("//auto-generated rules");
    	int i;
    	int numofcomment=0;
    	int numofspace=0;
    	int numofformula=0;
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
        	    if (line.length()==0) {
        	    	numofspace ++;
        	    	continue;
        	    }
        	    if (line.startsWith("//")) {
        	    	numofcomment ++;
        	    	continue;
        	    }
	        	StringBuilder sb = new StringBuilder();
	        	StringBuilder sb2 = new StringBuilder();
	        	i = line.indexOf("(");
	        	if (i >= 0) {
	        		numofformula ++;
	        		sb.append(Conf[PredN]+"Conf");
		        	String conf = line.substring(0, i);
		        	if (conf.startsWith("-"))
		        		sb.append("Neg");
		        	sb.append(line.substring(i,line.indexOf(")"))).append(",").append(conf).append(")");
	        		writer.println(sb);
	        		String first = "";
	        		String second = "";
	        		if (PredN != 3) {
	        			int j = line.indexOf(",");
	        			first = line.substring(i + 1,j);
	        			second = line.substring(j + 1, line.indexOf(')'));
	        		} else
	        			first = line.substring(i + 1, line.indexOf(')'));
	        		sb2.append(conf).append(" PageClass(\"").append(first).append("\",p");
	        		switch (PredN) {
	        		case 0:sb2.append("2) v !PageClass(\"").append(second).append("\",p1) v !LinkTo(id,p1,p2)");break;
	        		case 1:sb2.append(") v !Has(").append(second).append(",p)");break;
	        		case 2:sb2.append(") v Has(").append(second).append(",p)");break;
	        		case 3:sb2.append(")");
	        		}
	        		writer2.println(sb2);
	        	}
    	    }
    	} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(numofspace+" Spaces.\n"+numofcomment+" Comments.\n"+numofformula+" Formulas.");
    	    try {
				br.close();
				writer.close();
				writer2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static void changeArithmeticDisplay(int PredN, int UnivN) throws FileNotFoundException, UnsupportedEncodingException {
    	String[] Conf = {"ClassLinkToClass","HasClass","NotHasClass","Class"};
    	String[] Univ = {"Cornell","Texas","Washington","Wisconsin"};
    	BufferedReader br = new BufferedReader(new FileReader("data/WebKB/learn/" + Univ[UnivN] + "/oldarith/" + Conf[PredN] + ".db"));
    	PrintWriter writer = new PrintWriter("data/WebKB/learn/" + Univ[UnivN] + "/" + Conf[PredN] + ".db", "UTF-8");
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
    	        int j = line.indexOf("e-0");
        	    if (j >= 0) {
        	    	System.out.println(line);
        	    	int i = line.indexOf(".");
        	    	switch (line.charAt(j+3)) {
        	    	case '5':line = line.substring(0, i - 1) + "0.0000" + line.charAt(i - 1) + line.substring(i + 1, j) + line.substring(j + 4);break; 
        	    	case '6':line = line.substring(0, i - 1) + "0.00000" + line.charAt(i - 1) + line.substring(i + 1, j) + line.substring(j + 4);break;
        	    	case '7':line = line.substring(0, i - 1) + "0.000000" + line.charAt(i - 1) + line.substring(i + 1, j) + line.substring(j + 4);break;
        	    		default: System.err.println("WOW: (" + line + ")");
        	    	}
            	    System.out.println("-> " + line);
        	    }
        	    writer.println(line);
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		writer.close();
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    public static void generateNegPredicate() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/ie/evidence1.db"));
    	PrintWriter writer = new PrintWriter("data/GSW/ie/evidence.db", "UTF-8");
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
    	        int j = line.indexOf("Conf");
        	    if (j >= 0) {
        	    	if (line.indexOf('-') >= 0) {
        	    		line = line.substring(0, j + 4) + "Neg" + line.substring(j + 4);
        	    		System.out.println(line);
        	    	}
        	    }
        	    writer.println(line);
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		writer.close();
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    public static void splitNegPredicate() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/evidence.db"));
    	PrintWriter writer0 = new PrintWriter("data/GSW/webkb/Wisconsin/test/evidence0.db", "UTF-8");
    	PrintWriter writer1 = new PrintWriter("data/GSW/webkb/Wisconsin/test/evidence1.db", "UTF-8");
    	PrintWriter writer2 = new PrintWriter("data/GSW/webkb/Wisconsin/test/evidence2.db", "UTF-8");
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
        	    if (line.indexOf("Conf") >= 0)
        	    	if (line.indexOf("Neg") >= 0)
                	    writer2.println(line);
        	    	else
                	    writer1.println(line);
        	    else
        	    	writer0.println(line);
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		writer0.close();
        		writer1.close();
        		writer2.close();
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void calculateNegPredicate() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/evidential/webkb/Texas/evidenceOnlyNeg.db"));
	    double sum = 0;
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
    	        int j = line.indexOf(",-");
    	        //j = Math.max(line.indexOf(",0"),line.indexOf("\",")+1);
        	    if (j<0) continue;
        	    line = line.substring(j+1, line.indexOf(')'));
        	    sum += Double.parseDouble(line);
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	System.out.println(sum);
    }

    public static void find() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/evidence.db"));
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
    	        if (line.contains("Conf") && line.contains("-") && !line.contains("Neg"))
    	        	System.out.println(line);
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void findSame() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/simple/temp.mln"));
	    String line = "";
	    String line2= " ";
    	try {
    	    while (true) {
    	    	do {
    	        line = br.readLine();
    	        if (line == null) break;
    	        line = line.trim();
    	    	} while (line.startsWith("//"));
    	        if (line == null) break;
    	        if (line.equals(line2))
    	        	System.out.println(line);
    	        line2=line;
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    
    public static void findPattern() throws FileNotFoundException, UnsupportedEncodingException {
    	String[] p = 
    		{"-0.00014618  PageClass(\"ResearchProject\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.001186  PageClass(\"Course\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00144074  PageClass(\"ResearchProject\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00194435  PageClass(\"Course\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00218966  PageClass(\"Staff\",a1) v !PageClass(\"Faculty\",a2) v !LinkTo(a3,a2,a1)",
    				"0.00244502  PageClass(\"Department\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"0.0027419  PageClass(\"Department\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)",
    				"0.00333257  PageClass(\"ResearchProject\",a1) v !PageClass(\"Faculty\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00337614  PageClass(\"Faculty\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"0.00412523  PageClass(\"Course\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00419418  PageClass(\"Staff\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00431793  PageClass(\"Staff\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.00444183  PageClass(\"Faculty\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)",
    				"0.00461973  PageClass(\"Staff\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"0.00548048  PageClass(\"Faculty\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.01043  PageClass(\"Student\",a1) v !PageClass(\"Faculty\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0137025  PageClass(\"Faculty\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"0.0146039  PageClass(\"ResearchProject\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"0.0149911  PageClass(\"Department\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0152733  PageClass(\"Staff\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0152897  PageClass(\"Department\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0180999  PageClass(\"ResearchProject\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0195214  PageClass(\"Person\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0199792  PageClass(\"Student\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0212115  PageClass(\"Person\",a1) v !PageClass(\"Staff\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0220856  PageClass(\"Course\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0239026  PageClass(\"Student\",a1) v !PageClass(\"ResearchProject\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0244292  PageClass(\"Student\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0285868  PageClass(\"Person\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0341319  PageClass(\"Student\",a1) v !PageClass(\"Student\",a2) v !LinkTo(a3,a2,a1)",
    				"-0.0432587  PageClass(\"Person\",a1) v !PageClass(\"Person\",a2) v !LinkTo(a3,a2,a1)"};    			
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/simple/prog.mln"));
	    String line = "";
	    String line2= "";
    	try {
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
    	        for (String s:p)
    	        	if (line.startsWith(s))
    	        		System.out.println("\n"+line2+"\n"+line);
    	        line2=line;
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void compare() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/simple/temp.mln"));
    	BufferedReader br2 = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/simple/temp2.mln"));
	    String line = "";
	    String line2= "";
	    int i=0;
    	try {
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
    	        line2= br2.readLine();
    	        if (line2==null) break;
    	        i++;
    	        if (line2.startsWith(line.substring(0, line.indexOf(' ')))) continue;
   	        	System.out.println(line);
   	        	br.readLine();
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void remove() throws FileNotFoundException, UnsupportedEncodingException {
    	BufferedReader br = new BufferedReader(new FileReader("data/GSW/webkb/Wisconsin/simple/temp.mln"));
    	PrintWriter writer = new PrintWriter("data/GSW/webkb/Wisconsin/simple/temp2.mln", "UTF-8");
    	try {
    	    String line = "";
    	    while (true) {
    	        line = br.readLine();
    	        if (line == null) break;
//       	    	if (/*line.indexOf("v Has") < 0 &&*/ !(line.indexOf("-0") < 0 && line.indexOf("-1") < 0) && line.indexOf("//") < 0 && line.length() > 0)
    	        if (!line.startsWith("//"))
       	    		writer.println(line);
    	    }
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        		writer.close();
        		br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    public static void compareMethods(String univ, String small) {		
		try {
			//		Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION = false;
			System.out.println("Start comparing on " + univ + ":");
//			System.out.println("\nGSW Model Grounding on TRAD Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputTRAD.db");
//			System.out.println("\nGSW Model Grounding on FCA Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputFCA.db");
//			System.out.println("\nGSW Model Grounding on newTRAD Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputnewTRAD.db");
//			System.out.println("\nGSW Model Grounding on newFCA Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputnewFCA.db");
//			System.out.println("\nGSW Model Grounding on GSW Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputGSW.db");
//			System.out.println("\nGSW Model Grounding on common GSW Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputcommonGSW.db");
//			System.out.println("\nGSW Model Grounding on common Tuffy Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/outputTuffy.db");
			System.out.println("\nGSW Model Grounding on Alchemy Output:");
			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/"+univ+small+"log/alchemy.db");
//			System.out.println("\nGSW Model Grounding on Empty Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"prog.mln", "data/GSW/webkb/"+univ+small+"evidence.db", "data/GSW/webkb/Empty.db");
//			System.out.println("\nSimple Model Grounding on TRAD Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputTRAD.db");
//			System.out.println("\nSimple Model Grounding on FCA Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputFCA.db");
//			System.out.println("\nSimple Model Grounding on newTRAD Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputnewTRAD.db");
//			System.out.println("\nSimple Model Grounding on newFCA Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputnewFCA.db");
//			System.out.println("\nSimple Model Grounding on GSW Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputGSW.db");
//			System.out.println("\nSimple Model Grounding on common GSW Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputcommonGSW.db");
//			System.out.println("\nSimple Model Grounding on common Tuffy Output");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/outputTuffy.db");
			System.out.println("\nSimple Model Grounding on Alchemy Output:");
			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/"+univ+small+"log/alchemy.db");
//			System.out.println("\nSimple Model Grounding on Empty Output:");
//			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/"+univ+small+"simple/prog.mln", "data/GSW/webkb/"+univ+small+"simple/evidence.db", "data/GSW/webkb/Empty.db");
			System.out.println("\nFinished Comparing " + univ + ".\n");
		} catch (ParseException | SQLException | SolveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
    	tone(1000,500);
//    	modifyMLN_CiteSeer(4);
// 		unionConstants();
//      generateMLN();
//      modifyMLN_IE();
//      modifyMLN_ER();
//		generateNegPredicate();
//    	splitNegPredicate();
//    	calculateNegPredicate();
//    	for (int i = 0; i < 4; i++)
//    		for (int j = 0; j < 4; j++)
//    			modifyMLN_WEBKB(j, i);
//		    	changeArithmeticDisplay(j,i);
//    	findSame();
//    	remove();
//    	compare();
//    	findPattern();
//    	compareMethods("Texas", "/");
//   	compareMethods("Cornell", "/");
//    	compareMethods("Washington", "/");
//    	compareMethods("Wisconsin", "/");
//    	remove();
/*		try {
			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/test/new/prog.mln", "data/GSW/webkb/test/new/evidence.db", "data/GSW/webkb/test/simple.db");
			(new ObjectiveEvaluator()).evaluate("data/GSW/webkb/test/new/prog.mln", "data/GSW/webkb/test/new/evidence.db", "data/GSW/webkb/test/FCA.db");
		} catch (ParseException | SQLException | SolveException e) {
			e.printStackTrace();
		}
*/
//    	for (int j = 0; j < 4; j++)
//    		modifyMLN_WEBKB(j, 3);
    }
}
