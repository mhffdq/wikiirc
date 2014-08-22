package com.jr2jme;

import com.mac.tarchan.irc.bot.SampleClient;
import com.mac.tarchan.irc.client.IRCEvent;
import com.mac.tarchan.irc.client.Reply;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hirotaka on 2014/04/30.
 */
public class Handler {
    @Reply("001")
    public void welcome(IRCEvent event)
    {   //System.out.println(event.toString());
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
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
    {
        Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
        System.out.println(event.getMessage());
    }

    @Reply("privmsg")
    public void privmsg(IRCEvent event)
    {//System.out.println(event.toString());
        //Logger.getLogger(SampleClient.class.getName()).log(Level.INFO, event.toString());
    }
}
