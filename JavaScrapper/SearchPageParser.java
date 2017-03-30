package com.amazon.dexnyc.alexadelivery;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchPageParser {

	public static void main(String[] args) throws Exception {
		SearchPageParser parser = new SearchPageParser();
		String fileName = "/Users/chekevi/tmp/html/Amazon_ps3Controller.html";
		String searchHtml = parser.readFile(fileName);
		Map<String, List<String>> map = parser.getFirstNonSponsorItemWithFastTrackMessage(searchHtml);
		//System.out.println(map);;
	}
	
	public Map<String, List<String>> getFirstNonSponsorItemWithFastTrackMessage(String searchHtml) {
		Map<String, List<String>> res = new HashMap<>();
		if (searchHtml == null || searchHtml.length() == 0) {
			return res;
		}

		String regex1 = "<ul id=\"s-results-list-atf\" class=\"s-result-list .+?>(.+?)</div></li></ul>";
		Pattern pattern = Pattern.compile(regex1, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(searchHtml);
		
		String listTxt = "";
		if (matcher.find()) {
			listTxt = matcher.group(1);
			//System.out.println(listTxt);
		}
		
		//add trailing </li> back to listTxt
		listTxt = listTxt + "</div></li>";
		
		//pattern to search for title, asin, and fast track message 
		String regex2 = "(<li id=.+? data-asin=\"(\\S+)\".+? title=\"(.+?)\" .+?<\\/li>)";
		//String regex2 = "<li id=.+? data-asin=\"(\\S+)\".+? title=\"(.+?)\" .+?(<div .+?>Get it by  <span class=\"a-color-success a-text-bold\">(.+?)</span>.+?)?.+?<\\/li>";
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
