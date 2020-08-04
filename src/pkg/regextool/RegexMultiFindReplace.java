package pkg.regextool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMultiFindReplace {
	private static String configFileName;
	private static String regexFileName;
	private static String inputFilesDir;
	private static String outputFilesDirPath;
	private static String tempRegStr;
	private static StringBuffer tempRepStr;
	
	public static void main(String[] args) {
		configFileName = System.getProperty("user.dir") + "\\Input-config.txt";
		inputFilesDir = "";
		outputFilesDirPath = "";
		try {		
			LineNumberReader  lnr = new LineNumberReader(new FileReader(configFileName)); 
			String temp = "";
			while((temp = lnr.readLine()) != null){ 
				int lineCount = lnr.getLineNumber();
				if( lineCount == 1){
					regexFileName = temp;
				} else if (lineCount == 2){
					inputFilesDir = temp;
				} else if(lineCount == 3){
					outputFilesDirPath = temp;
				}
			}
			
			File outputFilesDir = new File(outputFilesDirPath);
			if(outputFilesDir.mkdir()){
				System.out.println("~ Output Dir Created!");
			}
			
			final File folder = new File(inputFilesDir);     
		    final List<File> fileList = Arrays.asList(folder.listFiles(new FileFilter() {public boolean accept(File pathname) {return pathname.isFile();}})); 
		    Writer outputFileWriter = null;
		    for(File f : fileList){
		    	outputFileWriter = new BufferedWriter(new FileWriter(outputFilesDir+"\\"+f.getName()+"_NEW.txt"));
				String inputFileString = "";
				inputFileString = readFile(f);
				inputFileString = processRegexFile(inputFileString);					
				outputFileWriter.write(inputFileString);
				outputFileWriter.flush();				
			}
		    outputFileWriter.close();
			lnr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String processRegexFile(String inputFileString){
		try {
			BufferedReader regexFile = new BufferedReader(new FileReader(regexFileName));
			Scanner scan = new Scanner(regexFile);
			tempRegStr = "";
			while(scan.hasNext()){				
				tempRegStr = scan.next();
				tempRepStr = new StringBuffer("");
				boolean firstLine = true;
				while(scan.hasNext()){
					String lookAhead = scan.next();
					if(!lookAhead.equals("~")){
						if (!firstLine)
						{
							tempRepStr.append(System.getProperty("line.separator"));
						}
						tempRepStr.append(lookAhead);
						firstLine = false;
					} else {
						inputFileString = findReplace(inputFileString, tempRegStr.toString(), tempRepStr.toString());
						break;
					}
				}
			}
			regexFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputFileString;	
	}
	
	private static String findReplace(String inFileReadLineTMP, String tempLineRegex, String tempLineReplace){
		Pattern pattern = Pattern.compile(tempLineRegex, Pattern.DOTALL|Pattern.MULTILINE|Pattern.COMMENTS);
		Matcher matcher = pattern.matcher(inFileReadLineTMP);
		inFileReadLineTMP = matcher.replaceAll(tempLineReplace);
		return inFileReadLineTMP;
	}
	
	private static String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader (file));
		String fileLine  = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");     
			while((fileLine = reader.readLine())!= null) {
				stringBuilder.append(fileLine);
				stringBuilder.append(ls);
			}
		reader.close();
		return stringBuilder.toString();
	}
}
