package com.jr2jme;

import com.mac.tarchan.irc.bot.SampleClient;
import com.mac.tarchan.irc.client.IRCClient;
import com.mac.tarchan.irc.client.IRCEvent;
import com.mac.tarchan.irc.client.Reply;
import com.mongodb.DBCollection;
import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;
import net.java.sen.filter.stream.CompositeTokenFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hirotaka on 2014/04/30.
 */
public class WikiIRC {
    static IRCClient irc;
    static Boolean finish=false;
    DBCollection dbCollection;
    String regex = "14\\[\\[\\u000307(.+)\\u000314\\]\\].+diff=(.+)&oldid=(.+).+5\\*\\u0003 \\u000303(.+)\\u0003 \\u00035\\*";//記事名と編集者名がそれぞれ括弧の中にマッチ
    String regexnew = "14\\[\\[\\u000307(.+)\\u000314\\]\\].+oldid=(.+)&rcid.+5\\*\\u0003 \\u000303(.+)\\u0003 \\u00035\\*";//新しい記事
    Pattern pattern = Pattern.compile(regex);
    Pattern patternnew = Pattern.compile(regexnew);
    Matcher matcher=null;
    Matcher matchernew=null;
    static ExecutorService exec = Executors.newFixedThreadPool(20);
    public static void main(java.lang.String[] args) {
        WikiIRC handler = new WikiIRC();
        irc = IRCClient.createClient("irc.wikimedia.org",6667, "faledo", null, "UTF-8");
        irc.addEventHandler(handler);
        exec.submit(new Task());
        //irc =IRCClient.createClient(handler);//なんかイベントハンドラを追加できる．理解していない．
        try {
            //irc.connect("irc.wikimedia.org",6667,"UTF-8").login("faledo",null,null,0,null).start().join("#ja.wikipedia");
            irc.start().join("#ja.wikipedia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*public void firstdoing(){

        MongoClient mongo=null;
        try {
            mongo = new MongoClient("dragons",27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db=mongo.getDB("wiki_kondou");
        DBCollection dbCollection=db.getCollection("wikiirc");
        ExecutorService exec = Executors.newFixedThreadPool(20);
        exec.submit(new Task());
        try {
            irc.connect("irc.wikimedia.org",6667,"UTF-8").login("faledo",null,null,0,null);//つないで名前jmeでログインしてチャンネルに参加 なんか元のライブラリだとutilの中のKanaなんとかを呼び出すせいでうまくいかなかったので呼び出さないようにIRCClientをいじった．
            //System.out.println(irc.nextMessage());
            //exec.shutdown();
            /*while(it.hasNext()){

                if(exec.isTerminated()){//enter押したことがあって，一行読んだら終了
                    irc.quit();
                    break;
                }
                if(finish){//enter押したことがあって，一行読んだら終了
                    irc.quit();

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //try {
                //irc.close();
                //exec.shutdown();
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }

    }*/
    @Reply("001")
    public void welcome(IRCEvent event)
    {   //System.out.println(event.toString());
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
        //irc.join("#ja.wikipedia");
    }

    @Reply("join")
    public void join(IRCEvent event)
    {//System.out.println(event.toString());
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());

    }

    @Reply("part")
    public void part(IRCEvent event)
    {
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("quit")
    public void quit(IRCEvent event)
    {
        // Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
        System.out.println("hogeeeeeeeeeeeeeeeeeeeee");
    }

    @Reply("mode")
    public void mode(IRCEvent event)
    {
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("topic")
    public void topic(IRCEvent event)
    {//System.out.println(event.toString());
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("332")
    public void topic332(IRCEvent event)
    {//System.out.println(event.toString());
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("353")
    public void nickStart(IRCEvent event)
    {
        Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("366")
    public void nickEnd(IRCEvent event)
    {
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("433")
    public void onNickConflict(IRCEvent event)
    {
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("nick")
    public void nick(IRCEvent event)
    {
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("error")
    public void error(IRCEvent event)
    {
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }

    @Reply("ping")
    public void ping(IRCEvent event)
    {   String text=event.getMessage().toString();
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
        //System.out.println(event.getMessage());
        //System.out.println(text.substring(6));
        //irc.pong(text.substring(6));
        System.out.println(text);
    }

    @Reply("privmsg")
    public void privmsg(IRCEvent event)
    {//System.out.println(event.toString());
        String text=event.getMessage().getTrail();
        System.out.println(text);
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
        Boolean newflag=false;
        String revid=null;
        String oldid;
        //System.out.println(text);
        matcher=pattern.matcher(text);
        matchernew=patternnew.matcher(text);
        String title=null;
        String editor=null;
        if(matcher.find()) {
            System.out.println(matcher.group(1) + " " + matcher.group(2) + " " + matcher.group(3) + " " + matcher.group(4));//group(1)に記事名,group(2)に編集者名が入っている．
            revid=matcher.group(2);
            title=matcher.group(1);
            editor=matcher.group(4);
            oldid=matcher.group(3);
            exec.submit(new ContentGetter(revid,oldid,title,editor,dbCollection));
        }
        else if(matchernew.find()) {//new
            System.out.println(matchernew.group(1) + " " + matchernew.group(2) + " " + matchernew.group(3));//
            revid=matchernew.group(2);
            title=matchernew.group(1);
            editor=matchernew.group(3);
            exec.submit(new NewArticle(revid,title,editor,dbCollection));
        }

            //System.out.println(gchildren.getTextContent());

        if(finish){
            irc.quit();
            System.exit(5000);
        }


    }
}

class Task implements Callable<Boolean>{
    public Boolean call(){
        BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                while (!reader.ready()) {
                    // ここで割り込みを待つ
                    Thread.sleep(200);
                }
                String command = reader.readLine();//enter押されたら終了
                break;
                // do something based on command
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        WikiIRC.finish=true;
        return true;
    }
}

class ContentGetter implements Runnable{
    String id;
    String title;
    String editor;
    DBCollection dbc;
    String oldid;
    public ContentGetter(String id,String oldid,String title,String editor,DBCollection dbc){
        this.id=id;
        this.oldid=oldid;
        this.editor=editor;
        this.title=title;
        this.dbc=dbc;
    }
    public void run(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimeZone timezone=TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
        sdf.setTimeZone(timezone);
        final String TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content|timestamp&format=xml&revids=";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document root = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            root = builder.parse(TARGET_HOST+id+"|"+oldid);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Node page = root.getElementsByTagName("page").item(0);
        if(page.getAttributes().getNamedItem("ns").getNodeValue().equals("0")) {
            Node gchildrennew = root.getElementsByTagName("rev").item(0);//本文取得
            Node gchildrenold = root.getElementsByTagName("rev").item(1);
            try {
                Date date=sdf.parse(gchildrennew.getAttributes().getNamedItem("timestamp").getNodeValue());
                String contentnew=gchildrennew.getTextContent();
                String contentold=gchildrenold.getTextContent();
                Levenshtein3<String> d = new Levenshtein3<String>();
                List<String>res=d.diff(kaiseki(contentnew),kaiseki(contentold));
                int moji=0;
                for(String type:res){
                    if(!type.equals("|")){
                        moji++;
                    }
                }
                System.out.println("変化した文字数="+moji);
                //System.out.println(content);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    public List<String>kaiseki(String wikitext){

        StringTagger tagger = SenFactory.getStringTagger(null);
        CompositeTokenFilter ctFilter = new CompositeTokenFilter();

        try {
            ctFilter.readRules(new BufferedReader(new StringReader("名詞-数")));
            tagger.addFilter(ctFilter);

            ctFilter.readRules(new BufferedReader(new StringReader("記号-アルファベット")));
            tagger.addFilter(ctFilter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Token> tokens = new ArrayList<Token>();
        try {
            tokens=tagger.analyze(wikitext, tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> current_text = new ArrayList<String>(tokens.size());

        for(Token token:tokens){

            current_text.add(token.getSurface());
        }

        return current_text;
    }
    public synchronized void sleep(long msec)
    {	//指定ミリ秒実行を止めるメソッド
        try
        {
            wait(msec);
        }catch(InterruptedException e){}
    }
}

class NewArticle implements Runnable{
    String id;
    String title;
    String editor;
    DBCollection dbc;
    public NewArticle(String id,String title,String editor,DBCollection dbc){
        this.id=id;
        this.editor=editor;
        this.title=title;
        this.dbc=dbc;
    }
    public void run(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimeZone timezone=TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
        sdf.setTimeZone(timezone);
        final String TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content|timestamp&format=xml&revids=";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document root = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            root = builder.parse(TARGET_HOST+id);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Node page = root.getElementsByTagName("page").item(0);
        System.out.println(id);
        if(page.getAttributes().getNamedItem("ns").getNodeValue().equals("0")) {
            Node gchildren = root.getElementsByTagName("rev").item(0);//本文取得
            try {
                Date date=sdf.parse(gchildren.getAttributes().getNamedItem("timestamp").getNodeValue());
                String content=gchildren.getTextContent();
                //System.out.println(date);
                System.out.println("新しい記事の文字数="+kaiseki(content).size());
                //System.out.println(content);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println(id);
    }

    public List<String>kaiseki(String wikitext){

        StringTagger tagger = SenFactory.getStringTagger(null);
        CompositeTokenFilter ctFilter = new CompositeTokenFilter();

        try {
            ctFilter.readRules(new BufferedReader(new StringReader("名詞-数")));
            tagger.addFilter(ctFilter);

            ctFilter.readRules(new BufferedReader(new StringReader("記号-アルファベット")));
            tagger.addFilter(ctFilter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Token> tokens = new ArrayList<Token>();
        try {
            tokens=tagger.analyze(wikitext, tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> current_text = new ArrayList<String>(tokens.size());

        for(Token token:tokens){

            current_text.add(token.getSurface());
        }

        return current_text;
    }
    public synchronized void sleep(long msec)
    {	//指定ミリ秒実行を止めるメソッド
        try
        {
            wait(msec);
        }catch(InterruptedException e){}
    }
}

class Kaiseki implements Callable<List<String>> {//形態素解析
    String wikitext;//gosenだとなんか駄目だった→kuromojimo別のでダメ
    public Kaiseki(String wikitext){
        this.wikitext=wikitext;
    }
    @Override
    public List<String> call() {

        StringTagger tagger = SenFactory.getStringTagger(null);
        CompositeTokenFilter ctFilter = new CompositeTokenFilter();

        try {
            ctFilter.readRules(new BufferedReader(new StringReader("名詞-数")));
            tagger.addFilter(ctFilter);

            ctFilter.readRules(new BufferedReader(new StringReader("記号-アルファベット")));
            tagger.addFilter(ctFilter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Token> tokens = new ArrayList<Token>();
        try {
            tokens=tagger.analyze(wikitext, tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> current_text = new ArrayList<String>(tokens.size());

        for(Token token:tokens){

            current_text.add(token.getSurface());
        }

        return current_text;
    }


}