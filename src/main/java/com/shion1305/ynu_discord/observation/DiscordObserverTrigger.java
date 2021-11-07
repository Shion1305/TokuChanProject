package com.shion1305.ynu_discord.observation;//package com.shion1305.ynu_discord.observation;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebServlet("/helloDiscord")
//public class DiscordObserverTrigger extends HttpServlet {
//    static DiscordObserver observer;
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        observer=new DiscordObserver();
//        resp.setStatus(200);
//        resp.getWriter().println("DiscordObserver Launched");
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        doGet(req,resp);
//    }
//}
