package com.shion1305.ynu_discord.tokuchan;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateMono;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.util.Color;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.Instant;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;


@WebListener
public class TokuChanHandler implements ServletContextListener {
    GatewayDiscordClient client;
    HashMap<Long, Integer> data;
    String token;
    Logger logger;
    private static String targetChannel = "901379148063322173";
    Channel channel;
    Color[] colors = new Color[]{Color.BLACK, Color.BLUE, Color.BISMARK, Color.BROWN, Color.CINNABAR, Color.CYAN, Color.DARK_GOLDENROD, Color.DEEP_LILAC
            , Color.ENDEAVOUR, Color.GRAY, Color.LIGHT_GRAY, Color.GREEN, Color.ORANGE, Color.MOON_YELLOW, Color.RED, Color.RUBY, Color.MEDIUM_SEA_GREEN, Color.VIVID_VIOLET,
            Color.SUMMER_SKY, Color.MAGENTA, Color.PINK, Color.DEEP_SEA};

    public static void main(String[] args) {
        try {
            TokuChanHandler handler = new TokuChanHandler();
            handler.run();
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

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
    }

    public void run() throws InterruptedException {
        /**
         * ���̃N���X��DM����!intro�ł�!color�ł��Ȃ����b�Z�[�W���擾���A���X�|���X���s���B
         */
        client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getChannel().block().getType().getValue() == 1)
                .filter(event -> !event.getMessage().getAuthor().get().isBot() && !event.getMessage().getContent().equals("!intro") && !event.getMessage().getContent().equals("!color"))
                .subscribe(event -> {
                            logger.info("User: " + event.getMessage().getAuthor().get().getUsername());
                            MessageChannel channel = event.getMessage().getChannel().block();
                            if (event.getMessage().getContent().length() > 200) {
                                msgIllegalNotify(channel).block();
                            } else {
                                msgConfirm(event.getMessage().getContent(), channel).block();
                            }
                        }
                );

        client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().equals("!intro"))
                .subscribe(event -> {
                            event.getMessage().getChannel().block().createMessage(EmbedCreateSpec.builder()
                                    .title("\"�������\"�ւ悤����!!")
                                    .color(Color.DISCORD_WHITE)
                                    .image("https://cdn.discordapp.com/app-icons/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512")
                                    .description("�₟!  ������BOT�̓�����񂾂�!\n����DM���Ă��ꂽ�玩���I�ɏ��H�̓����`�����l���ɓ]�������!\n���M�������@�\������̂ŋC�y�Ɏg���Ă݂Ă�!\n\n���b�Z�[�W�ɂ��Ă���v���t�B�[���F�͂��ꂼ��e�l�Ɋ��蓖�Ă��Ă���F�ŁA���ł����Z�b�g���邱�Ƃ��\�ł�!")
                                    .build()).block();
                            event.getMessage().getChannel().block().createMessage(EmbedCreateSpec.builder()
                                    .title("\"�������\"�̎g����")
                                    .color(Color.DISCORD_WHITE)
                                    .description("�����`�����l���Ƀ��b�Z�[�W�𑗂肽���Ƃ�\n   ������BOT��DM���Ă�! ���M�������������łł����!\n\n�����̃v���t�B�[���F��ύX�������Ƃ�\n   �� `!color` �ƑłƐF�����Z�b�g�ł����!\n\n�o�O�񍐁A�����ⓙ��@Shion1305�܂�")
                                    .build()).block();
                        }
                );

        client.on(MessageCreateEvent.class).filter(event -> event.getMessage().getContent().equals("!color")).subscribe(event -> {
            data.remove(event.getMessage().getAuthor().get().getUserData().id().asLong());
            event.getMessage().getChannel().block().createMessage(EmbedCreateSpec.builder().title("�v���t�B�[���F�����Z�b�g���܂���!").color(Color.DISCORD_WHITE).build()).block();
        });
        client.on(ButtonInteractionEvent.class)
                .subscribe(event -> {
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
                        logger.info("ButtonInteractionEvent \"YES\"-11");
                        channel.getRestChannel().createMessage(
                                        MessageCreateRequest.builder()
                                                .embed(EmbedCreateSpec.builder()
                                                        .title(event.getMessage().get().getEmbeds().get(0).getDescription().get())
                                                        .color(Color.of(color))
                                                        .build().asRequest())
                                                .build())
                                .doOnSuccess(messageData -> {
                                    msgSent(event.getMessage().get().getEmbeds().get(0).getDescription().get(), event.getMessage().get().getChannel().block(), messageData.id().asString()).block();
                                }).doOnError(throwable -> {
                                    logger.warning("ERROR");
                                    logger.warning(throwable.getMessage());
                                })
                                .block();
                        logger.info("ButtonInteractionEvent \"YES\"-2");
                        event.getMessage().get().delete().block();
                    } else if (customId.startsWith("NO")) {
                        logger.info("ButtonInteractionEvent \"NO\"");
                        msgCancelDraft(event.getMessage().get().getChannel().block(), event.getMessage().get().getTimestamp())
                                .doOnError(Throwable::printStackTrace).block();
                        event.getMessage().get().delete().block();
                    } else if (customId.startsWith("wd-")) {
                        client.getMessageById(Snowflake.of(targetChannel), Snowflake.of(customId.substring(3)))
                                .doOnError(Throwable::printStackTrace)
                                .subscribe(message -> {
                                    String content = message.getEmbeds().get(0).getTitle().get();
                                    message.delete().block();
                                    msgWithdrew(content, event.getMessage().get().getChannel().block());
                                    event.getMessage().get().delete().block();
                                });
                    }
                    event.deferEdit().block();
                });
    }

    private MessageCreateMono msgConfirm(String content, MessageChannel messageChannel) {
        return messageChannel.createMessage(EmbedCreateSpec.builder().title("�ȉ��̓��e�œ����`�����l���ɓ��e���܂��B��낵���ł���?")
                        .description(content)
                        .color(Color.DEEP_SEA).build())
                .withComponents(ActionRow.of(Button.success("YES", "���M"), Button.danger("NO", "������")));
    }

    private MessageCreateMono msgCancelDraft(MessageChannel channel, Instant timestamp) {
        return channel.createMessage(EmbedCreateSpec.builder().title("���M���������܂���")
                .timestamp(timestamp).color(Color.RED).build());
    }

    private MessageCreateMono msgIllegalNotify(MessageChannel channel) {
        return channel.createMessage(EmbedCreateSpec.builder().title("�������I�[�o�[")
                .description("200�����ȓ��ł��肢���܂��B").color(Color.RED).build());
    }

    private MessageCreateMono msgSent(String content, MessageChannel channel, String mesID) {
        return channel.createMessage(EmbedCreateSpec.builder()
                .title("���M�������܂���")
                .description(content)
                .color(Color.GREEN)
                .build()).withComponents(ActionRow.of(Button.secondary("wd-" + mesID, "���M������")));
    }

    private MessageData msgWithdrew(String content, MessageChannel channel) {
        return channel.getRestChannel().createMessage(EmbedCreateSpec.builder()
                .title("���b�Z�[�W���������܂���")
                .description(content)
                .color(Color.BLUE).build().asRequest()).block();
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
}
