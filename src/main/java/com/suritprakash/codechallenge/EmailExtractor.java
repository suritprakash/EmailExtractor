package com.suritprakash.codechallenge;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.HashSet;

public class EmailExtractor {
    private static final int MAX_DEPTH = 3;
    private HashSet<String> links;
    private String _URL;

    public EmailExtractor(String URL, int depth) {
        _URL =(URL.startsWith("http://"))? URL:"http://" + URL;

        links = new HashSet<>();

        getPageLinks(_URL, depth);
    }

    public void getPageLinks(String URL, int depth) {

        if ((!links.contains(URL) && (depth < MAX_DEPTH ) && URL.contains(_URL))) {
            System.out.println(">> Depth: " + depth + " [" + URL + "]");
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");


                depth++;
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                //System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

    public void getEmails(){
        links.forEach(x -> {
            Document document;
            try {
                document = Jsoup.connect(x).get();
                Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
                Matcher matcher = p.matcher(document.text());
                HashSet<String> emails = new HashSet<String>();
                while (matcher.find()) {
                    //System.out.println(matcher.group());
                    emails.add(matcher.group());
                }

                //System.out.println(emails);
                for (String s : emails) {
                    System.out.println(s);
                }
            } catch (IOException e) {
               // System.err.println(e.getMessage());
            }
        });

    }

    public static void main(String[] args) {
        //Check if arguments are supplied and URL is supplied
        if(args.length > 0 && args[0] != null) {
            
            EmailExtractor wc = new EmailExtractor(args[0], 0);
            wc.getEmails();

        } else {
            System.out.println("Invalid Arguments supplied...");
        }

    }
}