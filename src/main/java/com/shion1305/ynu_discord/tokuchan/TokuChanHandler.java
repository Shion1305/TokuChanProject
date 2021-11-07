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
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.util.Color;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;


@WebListener
public class TokuChanHandler implements ServletContextListener {
    List<Long> msgBlockList;
    GatewayDiscordClient client;
    HashMap<Long, Integer> data;
    String token;
    Logger logger;
    private static String targetChannel = "901379148063322173";
    Channel channel;
    Color[] colors = new Color[]{Color.BLACK, Color.BLUE, Color.BISMARK, Color.BROWN, Color.CINNABAR, Color.CYAN, Color.DARK_GOLDENROD, Color.DEEP_LILAC
            , Color.ENDEAVOUR, Color.GRAY, Color.LIGHT_GRAY, Color.GREEN, Color.ORANGE, Color.MOON_YELLOW, Color.RED, Color.RUBY, Color.MEDIUM_SEA_GREEN, Color.VIVID_VIOLET,
            Color.SUMMER_SKY, Color.MAGENTA, Color.PINK, Color.DEEP_SEA};

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            TokuChanHandler handler = new TokuChanHandler();
            logger.info("Initialization Started");
            handler.run();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public TokuChanHandler() {
        logger = Logger.getLogger("YNU-DISCORD=ANONYMOUS");
        token = "OTAwODQzMDc2OTUwNTg1MzQ0.YXHNfg.XN7tCyebPiWyoIBHUUZ4QaYTq7c";
        client = DiscordClient.create(token).login().block();
        channel = client.getChannelById(Snowflake.of(targetChannel)).block();
        data = new HashMap<>();
        msgBlockList = new ArrayList<>();
    }

    public void run() throws InterruptedException {
        /**
         * このクラスはDMかつ!introでも!colorでもないメッセージを取得し、レスポンスを行う。
         */
        client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getChannel().block().getType().getValue() == 1)
                .filter(event -> !event.getMessage().getAuthor().get().isBot() && !event.getMessage().getContent().equals("!intro") && !event.getMessage().getContent().equals("!color"))
                .subscribe(event -> {
                            logger.info("User: " + event.getMessage().getAuthor().get().getUsername());
                            MessageChannel channel = event.getMessage().getChannel().block();
                            if (event.getMessage().getContent().length() > 200) {
                                msgIllegalNotify(channel);
                            } else {
                                msgConfirm(event.getMessage().getContent(), channel);
                            }
                        }
                );

        client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().equals("!intro"))
                .subscribe(event -> {
                            event.getMessage().getChannel().block().createMessage(
                                    messageCreateSpec -> {
                                        messageCreateSpec.addEmbed(embedCreateSpec -> {
                                            embedCreateSpec.setTitle("\"匿ちゃん\"へようこそ!!")
                                                    .setColor(Color.DISCORD_WHITE)
                                                    .setImage("https://cdn.discordapp.com/app-icons/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512")
                                                    .setDescription("やぁ!  匿名化BOTの匿ちゃんだよ!\n私にDMしてくれたら自動的に情報工の匿名チャンネルに転送するよ!\n送信取り消し機能もあるので気軽に使ってみてね!\n\nメッセージについているプロフィール色はそれぞれ各個人に割り当てられている色で、いつでもリセットすることが可能です!");
                                        });
                                    }).block();
                        }
                );

        client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!color")).subscribe(event -> {
            data.remove(event.getMessage().getAuthor().get().getUserData().id().asLong());
            event.getMessage().getChannel().block().createMessage(
                    messageCreateSpec -> {
                        messageCreateSpec.addEmbed(embedCreateSpec -> {
                            embedCreateSpec.setTitle("プロフィール色をリセットしました!")
                                    .setColor(Color.DISCORD_WHITE).asRequest();
                        });
                    }).block();
        });
        client.on(ButtonInteractionEvent.class)
                .subscribe(event -> {
                    if (conflictAccessManager(event.getMessage().getId().asLong())) return;
                    try {
                        logger.info("ButtonInteractionEvent Detected...");
                        String customId = event.getCustomId();
                        logger.info("ButtonInteractionEvent CheckPoint1");
                        if (customId.equals("YES")) {
                            logger.info("ButtonInteractionEvent \"YES\"");
                            int color;
                            long userID = event.getInteraction().getUser().getId().asLong();
                            if (data.get(userID) == null) {
                                color = allocateColor();
                                data.put(userID, color);
                            } else {
                                color = data.get(userID);
                            }
                            logger.info("ButtonInteractionEvent \"YES\"");
                            channel.getRestChannel().createMessage(
                                            MessageCreateRequest.builder()
                                                    .embed(new EmbedCreateSpec()
                                                            .setTitle(event.getMessage().getEmbeds().get(0).getDescription().get())
                                                            .setColor(Color.of(color))
                                                            .asRequest())
                                                    .build())
                                    .doOnSuccess(messageData -> {
                                        msgSent(event.getMessage().getEmbeds().get(0).getDescription().get(), Objects.requireNonNull(event.getMessage().getChannel().block()), messageData.id().asString());
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
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
//                    event.deferEdit().subscribe(subscriber);
                });
    }

    private Message msgConfirm(String content, MessageChannel messageChannel) {
        return messageChannel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("以下の内容で匿名チャンネルに投稿します。よろしいですか?")
                        .setDescription(content)
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

    private Message msgIllegalNotify(MessageChannel channel) {
        return channel.createMessage(messageCreateSpec -> {
            messageCreateSpec.addEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("文字数オーバー")
                        .setDescription("200文字以内でお願いします。").setColor(Color.RED);
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

    private int allocateColor() {
        int color;
        while (true) {
            int r = new Random().nextInt(22);
            color = colors[r % 22].getRGB();
            if (data.size() > 18) return color;
            if (!data.containsValue(color)) return color;
        }
    }

    private synchronized boolean conflictAccessManager(long id) {
        if (msgBlockList.contains(id)) return true;
        msgBlockList.add(id);
        if (msgBlockList.size()>15){
            msgBlockList.remove(0);
            msgBlockList.remove(0);
        }
        return false;
    }


}
