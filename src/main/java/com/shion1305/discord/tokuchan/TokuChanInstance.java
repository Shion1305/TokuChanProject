package com.shion1305.discord.tokuchan;

import discord4j.common.util.Snowflake;
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

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class TokuChanInstance {
    /**
     * CAUTION
     * UPDATE OF CONFIGURATION FILE IS REQUIRED BEFORE UPGRADING TO THIS VERSION
     */
    //Loggerの設定
    private static final Logger logger = Logger.getLogger("TokuChanHandler");
    //クライエントを保管する
    private final GatewayDiscordClient client;
    //プロフィール色などのユーザー情報を保持する
    private HashMap<Long, User> data;
    //対象のチャンネルID
    private final long targetChannelId;
    private final long targetGuildId;
    private final Channel channel;
    //プロフィール色の候補
    //These colors chosen picked by... https://mokole.com/palette.html
    int[] colors = new int[]{0x000000, 0x2f4f4f, 0x556b3f, 0xa0522d, 0x191970, 0x006400, 0x8b0000, 0x808000, 0x778899, 0x3cb371, 0x20b2aa, 0x00008b, 0xdaa520, 0x7f007f, 0xb03060, 0xd2b48c, 0xff4500, 0xff8c00, 0x0000cd, 0x00ff00, 0xffffff, 0xdc143c, 0x00bfff, 0xa020f0, 0xf08080, 0xadff2f, 0xff7f50, 0xff00ff, 0xf0e68c, 0xffff54, 0x6495ed, 0xdda00dd, 0xb0e0e6, 0x7b68ee, 0xee82ee, 0x98fb98, 0x7fffd4, 0xfff69b4, 0xffffe0, 0xffc0cb};

    /*
     * Instance終了時の関数
     */

    public void stop() {
//        sendMaintenanceNotification();
        logger.info("SYSTEM SHUTDOWN");
    }

//    private void sendMaintenanceNotification() {
//        Objects.requireNonNull(client.getChannelById(Snowflake.of(targetChannelId)).block()).getRestChannel()
//                .createMessage(MessageCreateRequest.builder()
//                        .addEmbed(EmbedCreateSpec.builder()
//                                .title("メンテナンスのお知らせ")
//                                .description("サーバーメンテナンスのため一時的に利用不可となります。ボットが利用可能になるとこのメッセージは消えます。")
//                                .color(Color.DISCORD_WHITE)
//                                .image("https://media2.giphy.com/media/ocuQpTqeFlDOP4fFJI/giphy.gif")
//                                .build().asRequest())
//                        .build())
//                .doOnSuccess(messageData -> {
//                    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//                    buffer.putLong(messageData.id().asLong());
//                    try (FileOutputStream stream = new FileOutputStream(System.getProperty("user.home") + maintenanceInfoLocation)) {
//                        stream.write(buffer.array());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    saveConfig();
//                    logger.info("SYSTEM SHUTDOWN");
//                    System.exit(0);
//                }).block();
//    }

    public TokuChanInstance(String token, long targetGuildId, long targetChannelId) {
        logger.info("TokuChanHandler Started with " + targetChannelId);
        this.targetChannelId = targetChannelId;
        this.targetGuildId = targetGuildId;
        client = TokuChanDiscordManager.getClient(token);
        channel = Objects.requireNonNull(client).getChannelById(Snowflake.of(targetChannelId)).block();
    }

    /**
     * ユーザープロフィールデータを保存するための関数
     */
    private void saveConfig() {
        TokuChanPreferencesManager.saveData(targetChannelId, data);
    }

    /* 古いプロフィールデータを取得してマージする。
    新バージョンに移行するための一時的な関数
     */
    private void mergeData() {
        HashMap<Long, User> oldData = TokuChanPreferencesManager.importOldData();
        if (oldData != null) {
            for (Map.Entry<Long, User> d : oldData.entrySet()) {
                if (!data.containsKey(d.getKey())) {
                    data.put(d.getKey(), d.getValue());
                    logger.info("Merged Data: " + d.getKey() + "," + d.getValue());
                }
            }
        }
    }

    public void run() {
        /*
        Read User Data from preferences
         */
        data = TokuChanPreferencesManager.readUserdata(targetGuildId);
        //Temporary Line for Upgrade
        mergeData();
        if (data == null) {
            data = new HashMap<>();
        }
        /*
         * Check for previous maintenance notification.
         */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        try (FileInputStream stream = new FileInputStream(System.getProperty("user.home") + maintenanceInfoLocation)) {
//            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//            buffer.put(stream.readAllBytes());
//            buffer.flip();
//            long id = buffer.getLong();
//            //Wait for the message to be able to delete
//            try {
//                Objects.requireNonNull(client.getMessageById(Snowflake.of(targetChannel), Snowflake.of(id)).block()).delete().block();
//                logger.info("Server Maintenance Message Deletion Successful");
//            } catch (Exception e) {
//                logger.info("Server Maintenance Message seems to be not found");
//                logger.info(e.getMessage());
//            }
//        } catch (IOException e) {
//            logger.info("Server Maintenance Message Record Not found");
//            e.printStackTrace();
//        }
//        try {
//            File file = new File(System.getProperty("user.home") + maintenanceInfoLocation);
//            if (file.exists()) {
//                file.delete();
//            }
//        } catch (Exception e) {
//            logger.info("Failed to delete MaintenanceData");
//            e.printStackTrace();
//        }
        //このクラスはDMかつ!で始まらないメッセージを取得し、レスポンスを行う。
        client.on(MessageCreateEvent.class)
                .filter(event -> Objects.requireNonNull(event.getMessage().getChannel().block()).getType().getValue() == 1)
                .filter(event -> event.getMessage().getAuthor().isPresent() && !event.getMessage().getAuthor().get().isBot() && !event.getMessage().getContent().startsWith("!"))
                .subscribe(event -> {
                            logger.info("MessageReceived");
                            try {
                                MessageChannel channel = Objects.requireNonNull(event.getMessage().getChannel().block());
                                if (event.getMessage().getContent().length() > 400) {
                                    msgOverloadNotify(channel);
                                } else if (event.getMessage().getContent().length() == 0) {
                                    msgIllegalNotify(channel);
                                } else {
                                    if (event.getMessage().getContent().length() != 0 || !event.getMessage().getAttachments().isEmpty()) {
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
                                        EmbedCreateSpec.builder().title("\"匿ちゃん\"へようこそ!!").color(Color.DISCORD_WHITE)
                                                .description("やぁ!  匿名化BOTの匿ちゃんだよ!\n私にDMしてくれたら自動的に匿名チャンネルに転送するよ!\n送信取り消しも可能!\n質問しづらい事、答えにくい事、発言しつらい事などあったら気軽に使ってみてね!\n\nプロフィール(色/番号)は、いつでもリセットすることが可能です!")
                                                .image("https://raw.githubusercontent.com/shion1305/TokuChanProject/master/src/main/webapp/TokuChanHTU2.2.png").build()).block();

                                /*
                                 * Memo for how to send file.
                                 */
//                                event.getMessage().getChannel().block().createMessage(messageCreateSpec -> {
//                                    try {
//                                        messageCreateSpec.addFile("TokuChanHTU2.2.png", new FileInputStream(System.getProperty("catalina.base")+"/webapps/TokuChan/TokuChanHTU2.2.png"));
//                                    } catch (FileNotFoundException e) {
//                                        logger.warning("FILE NOT FOUND");
//                                        e.printStackTrace();
//                                    }
//                                }).block();
                            } catch (Exception e) {
                                logger.warning("EXCEPTION OCCURRED ON !intro");
                                logger.warning(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                );

        client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!whatsnew")).subscribe(event -> {
            try {
                msgWhatsNew(Objects.requireNonNull(event.getMessage().getChannel().block()));
            } catch (Exception e) {
                logger.warning("Error Occurred in !WhatsNew");
                logger.warning(e.getMessage());
                e.printStackTrace();
            }
        });

        client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!reset")).subscribe(event -> {
            try {
                if (event.getMessage().getAuthor().isEmpty()) return;
                data.remove(event.getMessage().getAuthor().get().getUserData().id().asLong());
                Objects.requireNonNull(event.getMessage().getChannel().block()).createMessage(
                        EmbedCreateSpec.builder().title("プロフィールをリセットしました!").color(Color.DISCORD_WHITE).build()).block();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.on(ButtonInteractionEvent.class)
                .subscribe(event -> {
                    try {
                        if (event.getMessage().isEmpty()) {
                            logger.info("Requested Message was empty");
                            return;
                        }
                        String customId = event.getCustomId();
                        if (customId.equals("YES")) {
                            handleInteractionYes(event);
                        } else if (customId.startsWith("NO")) {
                            handleMsgCancelDraft(event, event.getMessage().get().getTimestamp());
                            event.getMessage().get().delete().subscribe();
                        } else if (customId.startsWith("wd-")) {
                            client.getMessageById(Snowflake.of(targetChannelId), Snowflake.of(customId.substring(3)))
                                    .doOnError(Throwable::printStackTrace)
                                    .subscribe(message -> {
                                        if (message.getEmbeds().get(0).getTitle().isEmpty()) return;
                                        String content = message.getEmbeds().get(0).getTitle().get();
                                        message.delete().subscribe();
                                        handleMsgWithdrew(content, event);
                                        event.getMessage().get().delete().subscribe();
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 過去にサーバーのメンテナンスメッセージを送信したかを確認する関数
     * *****工事中******
     *
     * @return
     */
    private boolean seekMaintenanceMessage() {
//        client.getRestClient().getChannelService().getMessages(targetChannel,)
        return true;
    }

    /**
     * !whatsnewに対し、更新情報を返すための関数
     *
     * @param channel channel from which the command came
     * @return
     */
    private MessageData msgWhatsNew(MessageChannel channel) {
        return channel.getRestChannel().createMessage(
                EmbedCreateSpec.builder()
                        .title("匿ちゃん RELEASE NOTE")
                        .description("匿ちゃんが新しくなりました!!!")
                        .author("匿ちゃん ==UPDATE RELEASE==", null, "https://cdn.discordapp.com/app-icons/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512")
                        .image("https://raw.githubusercontent.com/shion1305/TokuChanProject/master/src/main/webapp/TokuChanUpdate2.2.png")
                        .color(Color.DISCORD_WHITE)
                        .build().asRequest()).block();
    }

    /**
     * 来たDMに対して確認メッセージを投稿するための関数
     *
     * @param msg            receivedMessage
     * @param messageChannel the channel from which it came
     * @return
     */
    private Message msgConfirm(Message msg, MessageChannel messageChannel) {
        return messageChannel.createMessage(EmbedCreateSpec.builder()
                .title("以下の内容で匿名チャンネルに投稿します。よろしいですか?")
                .description(msg.getContent())
                .color(Color.DEEP_SEA).build()).block();
    }

    /**
     * 送信確認メッセージに対して「取り消し」で発生した
     * ButtonInteractionEvent
     * を処理する関数。
     *
     * @param messageChannel channel from which the message came
     * @param timestamp      timestamp for the message
     * @return
     */
    private Message msgCancelDraft(MessageChannel messageChannel, Instant timestamp) {
        return messageChannel.createMessage(EmbedCreateSpec.builder().title("送信を取り消しました")
                .color(Color.DEEP_SEA)
                .timestamp(timestamp).color(Color.RED)
                .build()).block();
    }

    private void handleMsgCancelDraft(ButtonInteractionEvent event, Instant timestamp) {
        event.edit().withEmbeds(EmbedCreateSpec.builder()
                        .title("送信を取り消しました")
                        .color(Color.DEEP_SEA)
                        .timestamp(timestamp)
                        .build())
                .block();
    }

    /**
     * 文字数が超過するとメッセージを受け取れない・送信できないことが判明したため
     * バグを防ぐため、文字数を400文字で制限している。
     * メッセージのインスタンスを返すための関数
     *
     * @param channel channel from which the message came
     * @return prepared Message
     */
    private Message msgOverloadNotify(MessageChannel channel) {
        return channel.createMessage(
                EmbedCreateSpec.builder()
                        .title("文字数オーバー")
                        .description("400文字以内でお願いします。").build()).block();
    }

    /**
     * 空のメッセージ、画像、動画や添付ファイルは受け付けていない。
     * それを拒否するメッセージを送信し、そのインスタンスを返す関数
     * ButtonInteractionEvent用に設計されたイベントリスポンス
     *
     * @param channel channel from which the message came
     * @return
     */
    private Message msgIllegalNotify(MessageChannel channel) {
        return channel.createMessage(EmbedCreateSpec.builder()
                .title("無効なメッセージ")
                .description("画像、動画や添付ファイル、または空のメッセージは送信できません。")
                .color(Color.RED)
                .build()).block();
    }

    private Message msgSent(String content, MessageChannel channel, String mesID) {
        return channel.createMessage(EmbedCreateSpec.builder()
                        .title("送信完了しました")
                        .description(content)
                        .color(Color.GREEN)
                        .build())
                .withComponents(ActionRow.of(Button.secondary("wd-" + mesID, "送信取り消し")))
                .block();
    }

    //ButtonInteractionEvent用に設計されたイベントリスポンス
    private void handleMsgSent(String content, ButtonInteractionEvent event, String mesID) {
        event.edit().withEmbeds(EmbedCreateSpec.builder()
                        .title("送信完了しました")
                        .description(content)
                        .color(Color.GREEN)
                        .build())
                .withComponents(ActionRow.of(Button.secondary("wd-" + mesID, "送信取り消し")))
                .block();
    }

    private MessageData msgWithdrew(String content, MessageChannel channel) {
        return channel.getRestChannel().createMessage(
                EmbedCreateSpec.builder()
                        .title("メッセージを取り消しました")
                        .description(content)
                        .color(Color.BLUE)
                        .build().asRequest()).block();
    }

    private void handleMsgWithdrew(String content, ButtonInteractionEvent event) {
        event.edit().withEmbeds(EmbedCreateSpec.builder()
                .title("メッセージを取り消しました")
                .description(content)
                .color(Color.BLUE)
                .build()).block();
    }

    /**
     * Userはcom.shion1305.ynu_discord.tokuchan.Userのこと
     *
     * @param user
     * @return
     */
    private User getData(long user) {
        User userD = data.get(user);
        if (userD == null) {
            userD = allocate();
            data.put(user, userD);
            saveConfig();
        }
        return userD;
    }


    /**
     * @param color
     * @return
     */
    private boolean duplicateColor(int color) {
        for (User user : data.values()) {
            if (user.color == color) return true;
        }
        return false;
    }

    /**
     * @param tmp
     * @return
     */
    private boolean duplicateTemp(int tmp) {
        for (User user : data.values()) {
            if (user.tmp == tmp) return true;
        }
        return false;
    }

    /**
     * ユーザーに対し
     * プロフィール色とプロフィール番号を付与する関数
     *
     * @return
     */
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

    /**
     * 送信確認メッセージに対して「送信する」が押されて発生した
     * ButtonInteractionEvent
     * を処理する関数。
     *
     * @param event received ButtonInteractionEvent
     */
    private void handleInteractionYes(ButtonInteractionEvent event) {
        long userID = event.getInteraction().getUser().getId().asLong();
        User user = getData(userID);
        /*
        Media posting function is not implemented for now.
         */
//        String imgUrl = "";
        if (event.getMessage().isPresent() && event.getMessage().get().getEmbeds().get(0).getDescription().isPresent()) {
            String message = event.getMessage().get().getEmbeds().get(0).getDescription().get();
//            if (event.getMessage().getEmbeds().get(0).getImage().isPresent()) {
//                imgUrl = event.getMessage().getEmbeds().get(0).getImage().get().getUrl();
//            }
            //匿名チャンネルにメッセージを送信する。
            channel.getRestChannel().createMessage(
                            MessageCreateRequest.builder()
                                    .embed(EmbedCreateSpec.builder()
                                            .title(message)
                                            .color(Color.of(user.color))
                                            .description(" #" + user.tmp)
                                            .build().asRequest())
                                    .build()
                    ).doOnSuccess(messageData -> {
                        //送信完了メッセージを出す
                        //もしかしたらInteractionがタイムアウトする可能性も..?
                        handleMsgSent(message, event, messageData.id().asString());
                    }).doOnError(throwable -> {
                        logger.warning("ERROR");
                        logger.warning(throwable.getMessage());
                    })
                    .block();
        } else {
            logger.info("Skipped as the process because the message was empty.");
        }
    }
}
