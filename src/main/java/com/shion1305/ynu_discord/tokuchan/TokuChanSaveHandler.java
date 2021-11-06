package com.shion1305.ynu_discord.tokuchan;//package com.shion1305.ynu_discord.tokuchan;
//
//import discord4j.common.util.Snowflake;
//import discord4j.core.spec.EmbedCreateSpec;
//import discord4j.rest.util.Color;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.nio.charset.StandardCharsets;
//import static com.shion1305.ynu_discord.tokuchan.TokuChanHandler.*;
//
//@WebServlet("/saveTokuChan")
//public class TokuChanSaveHandler extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(TokuChanHandler.data);
//            byte[] bytes = baos.toByteArray();
//            FileOutputStream stream = new FileOutputStream(configUsersData);
//            stream.write(bytes);
//            stream.close();
//            oos.close();
//            baos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        resp.getWriter().println("SAVE_SUCCESS");
//        resp.setStatus(200);
////        client.getChannelById(Snowflake.of(targetChannel)).block().getRestChannel().createMessage(EmbedCreateSpec.builder()
////                .title("サービス停止のお知らせ")
////                .description("サーバーメンテナンスのため一時的に利用不可となります。サーバーが利用可能になるとこのメッセージは消えます。").color(Color.DISCORD_WHITE)
////                .build().asRequest()).doOnSuccess(messageData -> {
////            try {
////                new FileOutputStream(configLastMsg).write(messageData.id().asString().getBytes(StandardCharsets.UTF_8));
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }).block();
//    }
//}
