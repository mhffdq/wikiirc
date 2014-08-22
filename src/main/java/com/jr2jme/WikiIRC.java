package com.jr2jme;

import com.mac.tarchan.irc.client.IRCClient;
import com.mongodb.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hirotaka on 2014/04/30.
 */
public class WikiIRC {
    IRCClient irc;
    static Boolean finish=false;
    DBCollection dbCollection;
    public static void main(java.lang.String[] args){
        IRCClient irc =IRCClient.createClient(new Handler());//なんかイベントハンドラを追加できる．理解していない．
        String regex = "14\\[\\[\\u000307(.+)\\u000314\\]\\].+diff=(.+)&oldid=(.+).+5\\*\\u0003 \\u000303(.+)\\u0003 \\u00035\\*";//記事名と編集者名がそれぞれ括弧の中にマッチ
        String regexnew = "14\\[\\[\\u000307(.+)\\u000314\\]\\].+oldid=(.+)&rcid.+5\\*\\u0003 \\u000303(.+)\\u0003 \\u00035\\*";//新しい記事
        Pattern pattern = Pattern.compile(regex);
        Pattern patternnew = Pattern.compile(regexnew);
        Matcher matcher=null;
        Matcher matchernew=null;
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
            irc.connect("irc.wikimedia.org",6667,"UTF-8").login("qunagi",null,null,0,null).join("#ja.wikipedia");//つないで名前jmeでログインしてチャンネルに参加 なんか元のライブラリだとutilの中のKanaなんとかを呼び出すせいでうまくいかなかったので呼び出さないようにIRCClientをいじった．
            //System.out.println(irc.nextMessage());
            Iterator<String> it = irc.iterator();

            //exec.shutdown();
            while(it.hasNext()){
                Boolean newflag=false;
                String revid=null;
                String text= it.next();//元のライブラリではlogを読んでいて見にくかったのでその部分を消した
                if(text.startsWith("PING")){//pingに対してPONGを返さないとtimeoutになる
                    System.out.println(text.substring(6));
                    irc.pong(text.substring(6));
                }
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
                    newflag=true;
                }
                else if(matchernew.find()) {//new
                    System.out.println(matchernew.group(1) + " " + matchernew.group(2) + " " + matchernew.group(3));//
                    revid=matchernew.group(2);
                    title=matchernew.group(1);
                    editor=matchernew.group(3);
                    newflag=true;
                }
                if(newflag){
                    exec.submit(new ContentGetter(revid,title,editor,dbCollection));
                    //System.out.println(gchildren.getTextContent());


                }
                /*if(exec.isTerminated()){//enter押したことがあって，一行読んだら終了
                    irc.quit();
                    break;
                }*/
                if(finish){//enter押したことがあって，一行読んだら終了
                    irc.quit();

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                irc.close();
                exec.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public ContentGetter(String id,String title,String editor,DBCollection dbc){
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
                System.out.println();
                BasicDBObject object = new BasicDBObject();
                object.append("title",title).append("editor",editor).append("date",date).append("content",content).append("id",id);
                dbc.insert(object);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println(id);
    }
    public synchronized void sleep(long msec)
    {	//指定ミリ秒実行を止めるメソッド
        try
        {
            wait(msec);
        }catch(InterruptedException e){}
    }
}
