package com.amazon.dexnyc.alexadelivery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchPageParser {

	public static void main(String[] args) throws Exception {
		SearchPageParser parser = new SearchPageParser();
		//String fileName = "/Users/chekevi/tmp/html/Amazon_ps3Controller.html";
		//String fileName = "/Users/chekevi/tmp/html/Amazon_xbox2.html";
		//String searchHtml = parser.readFile(fileName);
		
		//String urlStr = "https://www.amazon.com/s/ref=nb_sb_noss_2?field-keywords=xbox+one";
		//String urlStr = "https://www.amazon.com/s/ref=nb_sb_noss_1?field-keywords=ps+games";
		//String urlStr = "https://www.amazon.com/s/ref=nb_sb_ss_c_1_6?field-keywords=ps3+controller";
		//String urlStr = "https://www.amazon.com/s/ref=nb_sb_noss_1?field-keywords=python+books";
		//String urlStr = "https://www.amazon.com/s/ref=nb_sb_ss_c_1_9?field-keywords=dayan+rubiks+cube";
		//String urlStr = "https://www.amazon.com/s/ref=nb_sb_ss_i_5_10?field-keywords=water+mugs+with+handle+32+oz";
		String urlStr = "https://www.amazon.com/s/ref=nb_sb_noss_1?field-keywords=sponsored+kids+books";
		String searchPage = parser.downloadPage(urlStr);
		
		Map<String, List<String>> map = parser.getFirstNonSponsorItemWithFastTrackMessage(searchPage);
		//System.out.println(map);
	}
	
	
	public Map<String, List<String>> getFirstNonSponsorItemWithFastTrackMessage(String searchPage) {
		Map<String, List<String>> res = new HashMap<>();
		if (searchPage == null || searchPage.length() == 0) {
			return res;
		}

		String regex1 = "<ul id=\"s-results-list-atf\" class=\"s-result-list .+?>(<li .+?</div></li>)</ul>";
		Pattern pattern = Pattern.compile(regex1, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(searchPage);
		
		String listTxt = "";
		if (matcher.find()) {
			listTxt = matcher.group(1);
			//System.out.println(listTxt);
		}
		
		//pattern to search for title, asin, and fast track message 
		String regex2 = "(<li .+? data-asin=\"(\\S+)\" .+? title=\"(.+?)\" .+?</li>)";
		pattern = Pattern.compile(regex2, Pattern.DOTALL);
		matcher = pattern.matcher(listTxt);
		while (matcher.find()) {
			String itemTxt = matcher.group(1);
			String asin = matcher.group(2);
			String title = matcher.group(3);
			//System.out.println(asin);
			//System.out.println(title);

			String fastTrackMessage = "";
			if (itemTxt.contains("Get it by ") && !itemTxt.contains(">Sponsored<")) {
				//System.out.println("has fastTrack");
				//get fast track message
				String regex3 = "<div .+?>Get it by <span class=\"a-color-success a-text-bold\">(.+?)</span>";
				Pattern pattern3 = Pattern.compile(regex3);
				Matcher matcher3 = pattern3.matcher(itemTxt);
				if (matcher3.find()) {
					fastTrackMessage = matcher3.group(1);
				}
				
				//populate res map
				List<String> list = new ArrayList<>();
				list.add(title);
				list.add(fastTrackMessage);
				res.put(asin, list);
				return res;
				//System.out.println(fastTrackMessage);;
			}
			//System.out.println();
		}
		
		return res;
	}

	public String downloadPage(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		InputStream is = url.openStream();
		int ptr = 0;
		StringBuffer buffer = new StringBuffer();
		while ((ptr = is.read()) != -1) {
			buffer.append((char) ptr);
		}
		//save to a temp file
		BufferedWriter writer = null;
		writer = new BufferedWriter( new FileWriter("Amazon_temp.html"));
		writer.write(buffer.toString());
		writer.close();
		
		return buffer.toString();
	}
	
	public String readFile(String fileName) throws Exception{
		InputStream is = new FileInputStream(fileName); 
		BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
		String line = buf.readLine(); 
		StringBuilder sb = new StringBuilder(); 
		while(line != null) { 
			sb.append(line).append("\n"); 
			line = buf.readLine(); 
			} 
		String fileAsString = sb.toString(); 
		//System.out.println("Contents : " + fileAsString);
		
		return fileAsString;
	}
}
