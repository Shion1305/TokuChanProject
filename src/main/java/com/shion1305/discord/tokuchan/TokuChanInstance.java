package com.shion1305.discord.tokuchan;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.rest.util.Color;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class TokuChanInstance {
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
    //holds process to manage properly
    private final List<Disposable> processList = new ArrayList<>();

    /*
     * Instance終了時の関数
     */
    public void stop() {
        //KILL ALL THE PROCESS
        logger.info("SYSTEM SHUTDOWN SEQUENCE STARTED");
        for (Disposable d : processList) {
            d.dispose();
        }
        processList.clear();
        logger.info("SYSTEM SHUTDOWN SEQUENCE SUCCESSFULLY ENDED");
    }

    public TokuChanInstance(String token, long targetGuildId, long targetChannelId) {
        logger.info("TokuChanHandler Started with " + targetChannelId);
        this.targetChannelId = targetChannelId;
        this.targetGuildId = targetGuildId;
        client = TokuChanDiscordManager.getClient(token);
        channel = Objects.requireNonNull(client).getChannelById(Snowflake.of(targetChannelId)).block();
//        Read User Data from preferences
        data = TokuChanPreferencesManager.readUserdata(targetGuildId);
        if (data == null) {
            data = new HashMap<>();
        }
        client.on(ReadyEvent.class)
                .subscribe(reconnectEvent -> {
                    logger.info("CONNECT EVENT");
                    if (!processList.isEmpty()) stop();
                    this.run();
                });
    }

    //      ユーザープロフィールデータを保存
    private void saveConfig() {
        TokuChanPreferencesManager.saveData(targetGuildId, data);
    }

    private void run() {
        //このクラスはDMかつ!で始まらないメッセージを取得し、レスポンスを行う。

        processList.add(client.on(MessageCreateEvent.class)
                .publishOn(Schedulers.boundedElastic())
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
                ));
        processList.add(client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().equals("!intro"))
                .subscribe(event -> {
                            try {
                                Objects.requireNonNull(event.getMessage().getChannel().block()).createMessage(
                                        EmbedCreateSpec.builder().title("\"匿ちゃん\"へようこそ!!").color(Color.DISCORD_WHITE)
                                                .description("やぁ!  匿名化BOTの匿ちゃんだよ!\n私にDMしてくれたら自動的に匿名チャンネルに転送するよ!\n送信取り消しも可能!\n質問しづらい事、答えにくい事、発言しつらい事などあったら気軽に使ってみてね!\n\nプロフィール(色/番号)は、いつでもリセットすることが可能です!")
                                                .image("https://raw.githubusercontent.com/shion1305/TokuChanProject/master/src/main/webapp/TokuChanHTU2.3.png").build()).block();
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
                ));
        processList.add(client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!whatsnew")).subscribe(event -> {
            try {
                msgWhatsNew(Objects.requireNonNull(event.getMessage().getChannel().block()));
            } catch (Exception e) {
                logger.warning("Error Occurred in !WhatsNew");
                logger.warning(e.getMessage());
                e.printStackTrace();
            }
        }));
        processList.add(client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!reset")).subscribe(event -> {
            try {
                if (event.getMessage().getAuthor().isEmpty()) return;
                data.remove(event.getMessage().getAuthor().get().getUserData().id().asLong());
                Objects.requireNonNull(event.getMessage().getChannel().block()).createMessage(
                        EmbedCreateSpec.builder().title("プロフィールをリセットしました!").color(Color.DISCORD_WHITE).build()).block();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        processList.add(client.on(ButtonInteractionEvent.class)
                .subscribe(event -> {
                    try {
                        if (event.getMessage().isEmpty()) {
                            logger.info("Requested Message was empty");
                            return;
                        }
                        String customId = event.getCustomId();
                        logger.info("CUSTOM_ID: " + customId);
                        if (customId.equals("TokuChan-YES")) {
                            logger.info("TokuChan-YES received");
                            handleInteractionYes(event);
                        } else if (customId.startsWith("TokuChan-NO")) {
                            logger.info("TokuChan-NO received");
                            handleMsgCancelDraft(event, event.getMessage().get().getTimestamp());
                        } else if (customId.startsWith("wd-")) {
                            logger.info("WD message received");
                            handleMsgWithdrew(event);
                            client.getMessageById(Snowflake.of(targetChannelId), Snowflake.of(customId.substring(3)))
                                    .doOnError(throwable -> {
                                        if (throwable.getMessage().contains(" returned 404 Not Found with response {code=10008,"))
                                            logger.info("Message Not Found");
                                        else throwable.printStackTrace();
                                    })
                                    .publishOn(Schedulers.boundedElastic())
                                    .doOnSuccess(message -> {
                                        if (message.getEmbeds().get(0).getTitle().isEmpty()) return;
                                        message.delete().subscribe();
                                    }).subscribe();
                        } else {
                            event.getMessage().get().delete().block();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

    /**
     * !whatsnewに対し、更新情報を返すための関数
     *
     * @param channel channel from which the command came
     */
    private void msgWhatsNew(MessageChannel channel) {
        channel.getRestChannel().createMessage(
                EmbedCreateSpec.builder()
                        .title("匿ちゃん RELEASE NOTE")
                        .description("匿ちゃんが新しくなりました!!!")
                        .image("https://raw.githubusercontent.com/shion1305/TokuChanProject/master/src/main/webapp/TokuChanUpdate2.3.png")
                        .color(Color.DISCORD_WHITE)
                        .build().asRequest()).block();
    }

    /**
     * 来たDMに対して確認メッセージを投稿するための関数
     *
     * @param msg            receivedMessage
     * @param messageChannel the channel from which it came
     */
    private void msgConfirm(Message msg, MessageChannel messageChannel) {
        messageChannel.createMessage(EmbedCreateSpec.builder()
                        .title("以下の内容で匿名チャンネルに投稿します。よろしいですか?")
                        .description(msg.getContent())
                        .color(Color.DEEP_SEA).build())
                .withComponents(ActionRow.of(Button.success("TokuChan-YES", "送信"), Button.danger("TokuChan-NO", "取り消し"))).block();
    }

    /**
     * 送信確認メッセージに対して「取り消し」で発生した
     * ButtonInteractionEvent
     * を処理する関数。
     *
     * @param event     target ButtonInteractionEvent
     * @param timestamp timestamp for the message
     */

    private void handleMsgCancelDraft(ButtonInteractionEvent event, Instant timestamp) {
        event.edit().withEmbeds(EmbedCreateSpec.builder()
                        .title("送信を取り消しました")
                        .color(Color.DEEP_SEA)
                        .timestamp(timestamp)
                        .build())
                .withComponents()
                .block();
    }

    /**
     * 文字数が超過するとメッセージを受け取れない・送信できないことが判明したため
     * バグを防ぐため、文字数を400文字で制限している。
     * メッセージのインスタンスを返すための関数
     *
     * @param channel channel from which the message came
     */
    private void msgOverloadNotify(MessageChannel channel) {
        channel.createMessage(
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
     */
    private void msgIllegalNotify(MessageChannel channel) {
        channel.createMessage(EmbedCreateSpec.builder()
                .title("無効なメッセージ")
                .description("画像、動画や添付ファイル、または空のメッセージは送信できません。")
                .color(Color.RED)
                .build()).block();
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

    private void handleMsgWithdrew(ButtonInteractionEvent event) {
        event.edit().withEmbeds(EmbedCreateSpec.builder()
                        .title("メッセージを取り消しました")
                        .color(Color.BLUE)
                        .build())
                .withComponents().subscribe();
    }

    /**
     * Userはcom.shion1305.ynu_discord.tokuchan.Userのこと
     *
     * @param user target userId
     * @return User with color and tmp data
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
     * @param color color you want to check duplication
     * @return result of the check
     */
    private boolean duplicateColor(int color) {
        for (User user : data.values()) {
            if (user.color == color) return true;
        }
        return false;
    }

    /**
     * @param tmp tmp number you want to check for
     * @return the check result
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
     * @return User with their allocated color and tmp
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
                        logger.severe("ERROR");
                        logger.severe(throwable.getMessage());
                    })
                    .block();
        } else {
            logger.info("Skipped as the process because the message was empty.");
        }
    }
}
