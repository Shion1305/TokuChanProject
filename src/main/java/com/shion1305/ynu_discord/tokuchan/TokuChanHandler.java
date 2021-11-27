package com.shion1305.ynu_discord.tokuchan;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Presence;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ActivityUpdateRequest;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.util.Color;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

@WebListener
public class TokuChanHandler implements ServletContextListener {
    TokuChanHandler handler;
    private static String targetChannel = "901379148063322173";
    List<Long> msgBlockList;
    GatewayDiscordClient client;
    HashMap<Long, User> data;
    String token;
    Logger logger;
    String preferenceLocation = "/TokuChanConfig/TokuChan.config";
    File preferenceFile;
    Preferences preferences;
    Channel channel;
    //These colors chosen picked by... https://mokole.com/palette.html
    int[] colors = new int[]{0x000000, 0x2f4f4f, 0x556b3f, 0xa0522d, 0x191970, 0x006400, 0x8b0000, 0x808000, 0x778899, 0x3cb371, 0x20b2aa, 0x00008b, 0xdaa520, 0x7f007f, 0xb03060, 0xd2b48c, 0xff4500, 0xff8c00, 0x0000cd, 0x00ff00, 0xffffff, 0xdc143c, 0x00bfff, 0xa020f0, 0xf08080, 0xadff2f, 0xff7f50, 0xff00ff, 0xf0e68c, 0xffff54, 0x6495ed, 0xdda00dd, 0xb0e0e6, 0x7b68ee, 0xee82ee, 0x98fb98, 0x7fffd4, 0xfff69b4, 0xffffe0, 0xffc0cb};
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            handler = new TokuChanHandler();
            handler.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Objects.requireNonNull(client.getChannelById(Snowflake.of(targetChannel)).block()).getRestChannel().createMessage(new EmbedCreateSpec()
                .setTitle("メンテナンスのお知らせ")
                .setDescription("サーバーメンテナンスのため一時的に利用不可となります。ボットが利用可能になるとこのメッセージは消えます。")
                .setColor(Color.DISCORD_WHITE)
                .setImage("https://media2.giphy.com/media/ocuQpTqeFlDOP4fFJI/giphy.gif")
                .asRequest()).doOnSuccess(messageData -> {
            preferences.putLong("MaintenanceMessageID", messageData.id().asLong());
            saveConfig();
        }).block();
        System.out.println("SYSTEM SHUTDOWN");
        logger.info("SYSTEM SHUTDOWN");
    }

    public TokuChanHandler() {
        logger = Logger.getLogger("YNU-DISCORD=ANONYMOUS");
        logger.info("Initialization Started");
        String dir = System.getProperty("user.home");
        preferenceFile = new File(dir + preferenceLocation);
        logger.info("PREFERENCE FILE: " + preferenceFile.getAbsolutePath());
        if (preferenceFile.exists()) {
            logger.info("PREFERENCE FILE FOUND");
            try {
                Preferences.importPreferences(new FileInputStream(preferenceFile));
            } catch (IOException | InvalidPreferencesFormatException e) {
                e.printStackTrace();
            }
        } else {
            logger.warning("PREFERENCE FILE NOT FOUND");
            try {
                if (!preferenceFile.getParentFile().exists()) {
                    if (preferenceFile.getParentFile().mkdir()) {
                        logger.info("PREFERENCE FOLDER CREATED");
                    } else {
                        logger.info("FAILED TO CREATE PREFERENCE FOLDER");
                    }
                }
                if (preferenceFile.createNewFile()) {
                    logger.info("PREFERENCE FILE GENERATED!!");
                } else {
                    logger.warning("FAILED TO GENERATE PREFERENCE FILE");
                }
            } catch (IOException e) {
                logger.warning("FAILED TO GENERATE PREFERENCE FILE WITH ERROR...");
                logger.warning(e.getMessage());
                e.printStackTrace();
            }
        }
        preferences = Preferences.userRoot();
        preferences.put("Test", "Test");
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            logger.warning(e.toString());
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        token = System.getenv("TokuChanDiscordToken");
        logger.info("TOKEN: " + token);
        client = DiscordClient.create(token).gateway().setInitialPresence(s -> Presence.online(ActivityUpdateRequest.builder().type(0).name("!introで使い方を確認! メッセージはDMで送信してね!").url("https://cdn.discordapp.com/avatars/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512").build())).login().block();
        channel = Objects.requireNonNull(client).getChannelById(Snowflake.of(targetChannel)).block();
        data = new HashMap<>();
        msgBlockList = new ArrayList<>();
    }

    private void saveConfig() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            byte[] bytes = baos.toByteArray();
            preferences.putByteArray("UserData", bytes);
            preferences.exportSubtree(new FileOutputStream(preferenceFile));
        } catch (Exception e) {
            logger.warning("SAVE FAILED");
            logger.warning(e.toString());
            logger.warning(e.getLocalizedMessage());
            logger.warning(e.getCause().getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        /*
        Read User Data from preferences
         */
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(preferences.getByteArray("UserData", null)))) {
            data = (HashMap<Long, User>) stream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * Check for previous maintenance notification.
         */
        long id;
        if ((id = preferences.getLong("MaintenanceMessageID", 0L)) != 0L) {
            try {
                Objects.requireNonNull(client.getMessageById(Snowflake.of(targetChannel), Snowflake.of(id)).block()).delete().block();
                logger.info("Server Maintenance Message Deletion Successful");
            } catch (Exception e) {
                logger.info("Server Maintenance Message seems to be not found");
                logger.info(e.getMessage());
            }
        }

        //このクラスはDMかつ!で始まらないメッセージを取得し、レスポンスを行う。
        client.on(MessageCreateEvent.class)
                .filter(event -> Objects.requireNonNull(event.getMessage().getChannel().block()).getType().getValue() == 1)
                .filter(event -> event.getMessage().getAuthor().isPresent())
                //.isPresent() is required before getAuthor.get()
                .filter(event -> !event.getMessage().getAuthor().get().isBot() && !event.getMessage().getContent().startsWith("!"))
                .subscribe(event -> {
                            try {
                                MessageChannel channel = Objects.requireNonNull(event.getMessage().getChannel().block());
                                if (event.getMessage().getContent().length() > 200) {
                                    msgOverloadNotify(channel);
                                } else if (event.getMessage().getContent().length() == 0) {
                                    msgIllegalNotify(channel);
                                } else {
                                    if (event.getMessage().getContent().length() != 0 || !event.getMessage().getAttachments().isEmpty()) {
                                        logger.info(event.getMessage().toString());
                                        msgConfirm(event.getMessage(), channel);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

        client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().equals("!intro"))
                .subscribe(event -> {
                            try {
                                Objects.requireNonNull(event.getMessage().getChannel().block()).createMessage(
                                        messageCreateSpec -> {
                                            messageCreateSpec.addEmbed(embedCreateSpec -> {
                                                embedCreateSpec.setTitle("\"匿ちゃん\"へようこそ!!")
                                                        .setColor(Color.DISCORD_WHITE)
                                                        .setImage("https://shion1305.com/TokuChan/TokuChanHTU2.2.png")
                                                        .setDescription("やぁ!  匿名化BOTの匿ちゃんだよ!\n私にDMしてくれたら自動的に情報工の匿名チャンネルに転送するよ!\n送信取り消し機能もあるので気軽に使ってみてね!\n\nメッセージについているプロフィール色はそれぞれ各個人に割り当てられている色で、いつでもリセットすることが可能です!");
                                            });
                                        }).block();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

        client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!whatsnew")).subscribe(event -> {
            try{
                msgWhatsNew(Objects.requireNonNull(event.getMessage().getChannel().block()));
            }catch (Exception e){
                logger.warning("Error Occurred in !WhatsNew");
                logger.warning(e.getMessage());
                e.printStackTrace();
            }
        });
        client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!reset")).subscribe(event -> {
            try {
                data.remove(event.getMessage().getAuthor().get().getUserData().id().asLong());
                Objects.requireNonNull(event.getMessage().getChannel().block()).createMessage(
                        messageCreateSpec -> {
                            messageCreateSpec.addEmbed(embedCreateSpec -> {
                                embedCreateSpec.setTitle("プロフィールをリセットしました!")
                                        .setColor(Color.DISCORD_WHITE).asRequest();
                            });
                        }).block();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.on(ButtonInteractionEvent.class)
                .subscribe(event -> {
                    try {
                        if (conflictAccessManager(event.getMessage().getId().asLong())) return;
                        logger.info("ButtonInteractionEvent Detected...");
                        String customId = event.getCustomId();
                        if (customId.equals("YES")) {
                            logger.info("ButtonInteractionEvent \"YES\"");
                            handleInteractionYes(event);
                        } else if (customId.startsWith("NO")) {
                            logger.info("ButtonInteractionEvent \"NO\"");
                            msgCancelDraft(Objects.requireNonNull(event.getMessage().getChannel().block()), event.getMessage().getTimestamp());
                            event.getMessage().delete().subscribe(new Subscriber<Void>() {
                                @Override
                                public void onSubscribe(Subscription s) {

                                }

                                @Override
                                public void onNext(Void unused) {

                                }

                                @Override
                                public void onError(Throwable t) {
                                    t.printStackTrace();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        } else if (customId.startsWith("wd-")) {
                            client.getMessageById(Snowflake.of(targetChannel), Snowflake.of(customId.substring(3)))
                                    .doOnError(Throwable::printStackTrace)
                                    .subscribe(message -> {
                                        String content = message.getEmbeds().get(0).getTitle().get();
                                        message.delete().subscribe(new Subscriber<Void>() {
                                            @Override
                                            public void onSubscribe(Subscription s) {
                                            }

                                            @Override
                                            public void onNext(Void unused) {
                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                t.printStackTrace();
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                                        msgWithdrew(content, Objects.requireNonNull(event.getMessage().getChannel().block()));
                                        event.getMessage().delete().subscribe(new Subscriber<Void>() {
                                            @Override
                                            public void onSubscribe(Subscription s) {

                                            }

                                            @Override
                                            public void onNext(Void unused) {

                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                t.printStackTrace();
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private MessageData msgWhatsNew(MessageChannel channel) {
        return channel.getRestChannel().createMessage(new EmbedCreateSpec()
                .setTitle("\"匿ちゃん\" IS BACK!!!")
                .setDescription("長いチューニングを経て\"匿ちゃん\"が復活しました:partying_face: 機能の変更点は以下の通りです。")
                .setAuthor("匿ちゃん ==UPDATE RELEASE==", null, "https://cdn.discordapp.com/app-icons/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512")
                .setImage("https://shion1305.com/TokuChan/TokuChanUpdate2.2.png")
                .setColor(Color.DISCORD_WHITE)
                .asRequest()).block();
    }

    private Message msgConfirm(Message msg, MessageChannel messageChannel) {
        return messageChannel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("以下の内容で匿名チャンネルに投稿します。よろしいですか?")
                        .setDescription(msg.getContent())
                        .setColor(Color.DEEP_SEA);
            });
            messageCreateSpec.setComponents(ActionRow.of(Button.success("YES", "送信"), Button.danger("NO", "取り消し")));
        }).block();
    }

    private Message msgCancelDraft(MessageChannel messageChannel, Instant timestamp) {
        return messageChannel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("送信を取り消しました")
                        .setColor(Color.DEEP_SEA)
                        .setTimestamp(timestamp).setColor(Color.RED);
            });
        }).block();
    }

    private Message msgOverloadNotify(MessageChannel channel) {
        return channel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("文字数オーバー")
                        .setDescription("200文字以内でお願いします。").setColor(Color.RED);
            });
        }).block();
    }

    private Message msgIllegalNotify(MessageChannel channel) {
        return channel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("無効なメッセージ")
                        .setDescription("画像、動画や添付ファイル、または空のメッセージは送信できません。").setColor(Color.RED);
            });
        }).block();
    }

    private Message msgSent(String content, MessageChannel channel, String mesID) {
        return channel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("送信完了しました")
                        .setDescription(content)
                        .setColor(Color.GREEN);
            });
            messageCreateSpec.setComponents(ActionRow.of(Button.secondary("wd-" + mesID, "送信取り消し")));
        }).block();
    }

    private MessageData msgWithdrew(String content, MessageChannel channel) {
        return channel.getRestChannel().createMessage(new EmbedCreateSpec()
                .setTitle("メッセージを取り消しました")
                .setDescription(content)
                .setColor(Color.BLUE).asRequest()).block();
    }

    private User getData(long user) {
        User userD = data.get(user);
        if (userD == null) {
            userD = allocate();
            data.put(user, userD);
            saveConfig();
        }
        return userD;
    }


    private boolean duplicateColor(int color) {
        for (User user : data.values()) {
            if (user.color == color) return true;
        }
        return false;
    }

    private boolean duplicateTemp(int tmp) {
        for (User user : data.values()) {
            if (user.tmp == tmp) return true;
        }
        return false;
    }

    private User allocate() {
        int color, tmp;
        do {
            int r = new Random().nextInt(colors.length);
            color = colors[r % colors.length];
            if (data.size() > 18) break;
        } while (duplicateColor(color));
        do {
            tmp = new Random().nextInt(999);
        } while (duplicateTemp(tmp));
        return new User(color, tmp);
    }


    private void handleInteractionYes(ButtonInteractionEvent event) {
        long userID = event.getInteraction().getUser().getId().asLong();
        User user = getData(userID);
        logger.info("ButtonInteractionEvent \"YES\"");
        String message = "";
        /*
        Media posting function is not implemented for now.
         */
//        String imgUrl = "";
        if (event.getMessage().getEmbeds().get(0).getDescription().isPresent()) {
            message = event.getMessage().getEmbeds().get(0).getDescription().get();
//            if (event.getMessage().getEmbeds().get(0).getImage().isPresent()) {
//                imgUrl = event.getMessage().getEmbeds().get(0).getImage().get().getUrl();
//            }
            String finalMessage = message;
            channel.getRestChannel().createMessage(
                            MessageCreateRequest.builder()
                                    .embed(new EmbedCreateSpec()
                                            .setTitle(message)
                                            .setColor(Color.of(user.color))
                                            .setDescription(" #" + user.tmp)
                                            .asRequest())
                                    .build())
                    .doOnSuccess(messageData -> {
                        msgSent(finalMessage, Objects.requireNonNull(event.getMessage().getChannel().block()), messageData.id().asString());
                    }).doOnError(throwable -> {
                        logger.warning("ERROR");
                        logger.warning(throwable.getMessage());
                    })
                    .block();
            event.getMessage().delete().subscribe(new Subscriber<Void>() {
                @Override
                public void onSubscribe(Subscription s) {

                }

                @Override
                public void onNext(Void unused) {

                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            logger.info("Skipped the process because the message was empty.");
        }
    }

    private synchronized boolean conflictAccessManager(long id) {
        if (msgBlockList.contains(id)) return true;
        msgBlockList.add(id);
        if (msgBlockList.size() > 15) {
            msgBlockList.remove(0);
            msgBlockList.remove(0);
        }
        return false;
    }
}
