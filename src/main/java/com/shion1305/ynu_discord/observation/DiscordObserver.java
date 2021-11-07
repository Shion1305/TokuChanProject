package com.shion1305.ynu_discord.observation;//package com.shion1305.ynu_discord.observation;
//
//import discord4j.common.util.Snowflake;
//import discord4j.core.DiscordClient;
//import discord4j.core.DiscordClientBuilder;
//import discord4j.core.GatewayDiscordClient;
//import discord4j.core.event.domain.Event;
//import discord4j.core.event.domain.PresenceUpdateEvent;
//import discord4j.core.event.domain.VoiceServerUpdateEvent;
//import discord4j.core.event.domain.VoiceStateUpdateEvent;
//import discord4j.core.event.domain.lifecycle.ReadyEvent;
//import discord4j.core.object.entity.Guild;
//import discord4j.core.object.entity.Member;
//import discord4j.core.object.entity.User;
//import discord4j.discordjson.json.UserData;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//public class DiscordObserver {
//    static GatewayDiscordClient client;
//    static Logger logger;
//    static DiscordClient discordClient;
//
//    // ONLINE  ON     WEB: 15
//    // IDLE    ON     WEB: 8
//    // DND     ON     WEB: 1
//    // OFFLINE ON     WEB: 0
//    // ONLINE  ON  MOBILE: 16
//    // IDLE    ON  MOBILE: 9
//    // DND     ON  MOBILE: 2
//    // OFFLINE ON  MOBILE: 0
//    // ONLINE  ON DESKTOP: 18
//    // IDLE    ON DESKTOP: 11
//    // DND     ON DESKTOP: 4
//    // OFFLINE ON DESKTOP: 0
//
//    final static int CODE_PresenceUpdate_ONLINE = 20;
//    final static int CODE_PresenceUpdate_IDLE = 21;
//    final static int CODE_PresenceUpdate_OFFLINE = 22;
//    final static int CODE_PresenceUpdate_DND = 23;
//
//    final static int CODE_PresenceUpdate_Username=25;
//    final static int CODE_PresenceUpdate_Avatar=25;
//    final static int CODE_PresenceUpdate_Discriminator=25;
//    final static int CODE_PresenceUpdate_Activities=25;
//
//    final static int CODE_VoiceStateUpdate_JOIN_NewSession = 30;
//    final static int CODE_VoiceStateUpdate_JOIN_Existing = 31;
//    final static int CODE_VoiceStateUpdate_LEAVE = 32;
//    final static int CODE_VoiceStateUpdate_MOVE = 33;
//
//    private ArrayList<Long> userIds;
//    private ArrayList<Long> channelIds;
//    private ArrayList<UserData> users;
//
//
//    public static void start() {
//        // Initialize userIds
//        logger = Logger.getLogger("DiscordObserver");
//        client = DiscordClientBuilder.create("ODU5MTExNzM4ODA4ODYwNjgz.YNn8KA.kyIwWqI0plpszNcqyKoKx0EWuWs")
//                .build()
//                .login()
//                .block();
//        List<Guild> guilds = client.getGuilds().collectList().block();
//        for (Guild guild : guilds) {
//            List<Member> members = guild.getMembers().collectList().block();
//            for (Member member : members) {
//                logger.info(member.toString());
//            }
//        }
//
//        client.getEventDispatcher().on(PresenceUpdateEvent.class).subscribe(event -> {
//            logger.info(event.toString());
//            if (!event.getNewUsername().isEmpty()) {
//                //Username has changed
//                DiscordObserveDataManager.log(CODE_PresenceUpdate_Username,new Object[]{
//                        event.getGuildId().asLong(),
//                        event.getUserId().asLong(),
//                        event.getOldUser().get().getUsername(),
//                        event.getNewUsername()
//                });
//            }
//            if (!event.getNewAvatar().isEmpty()) {
//                //Avatar has changed
//                DiscordObserveDataManager.log(CODE_PresenceUpdate_Avatar,new Object[]{
//                        event.getGuildId().asLong(),
//                        event.getUserId().asLong(),
//                        event.getOldUser().get().getAvatarUrl(),
//                        event.getUser().block().getAvatarUrl()
//                });
//            }
//            if (!event.getNewDiscriminator().isEmpty()) {
//                //Discriminator has changed
//                DiscordObserveDataManager.log(CODE_PresenceUpdate_Discriminator,new Object[]{
//                        event.getGuildId(),
//                        event.getUserId().asLong(),
//                        event.getOldUser().get().getDiscriminator(),
//                        event.getNewDiscriminator().get()
//                });
//            }
//
//
//
//            logger.info(event.getCurrent().asStatusUpdate().toString());
////            logger.info(event.getCurrent().getActivities().toString());
////            logger.info(event.getCurrent().getStatus().getValue());
//        });
//
//
//
//
//        client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(event -> {
//            logger.info(event.toString());
//
//
//            User user = event.getCurrent().getUser().block();
//            if (event.isJoinEvent()) {
//                if (event.getOld() == null) {
//                    DiscordObserveDataManager.log(CODE_VoiceStateUpdate_JOIN_NewSession, new Object[]{
//                            //get Guild ID
//                            event.getCurrent().getGuildId(),
//                            //get Channel ID
//                            event.getCurrent().getChannelId(),
//                            //get target User Id
//                            event.getCurrent().getUserId().asLong()
//                    });
//                } else {
//                    DiscordObserveDataManager.log(CODE_VoiceStateUpdate_JOIN_Existing, new Object[]{
//                            //get Guild ID
//                            event.getCurrent().getGuildId(),
//                            //get Channel ID
//                            event.getCurrent().getChannelId(),
//                            //get target User Id
//                            event.getCurrent().getUserId().asLong()
//                    });
//                }
//            } else if (event.isLeaveEvent()) {
//
//            } else if (event.isMoveEvent()) {
//
//            } else {
//                throw new IllegalStateException("UNEXPECTED STATE OCCURRED");
//            }
//        });
//        client.getEventDispatcher().on(VoiceServerUpdateEvent.class).subscribe(event -> {
//            logger.info("Voice Server Update Event\n" + event.toString());
//        });
//
//
////        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(event -> {
////            long authorId = event.getMessage().getData().author().id().asLong();
////            long time = event.getMessage().getTimestamp().toEpochMilli();
////            long channelId = event.getMessage().getChannelId().asLong();
////            long guildId = event.getGuildId().get().asLong();
////            long messageId=event.getMessage().getId().asLong();
////            String content = event.getMessage().getContent();
////            String contentEmbed=event.getMessage().getEmbeds().toString();
////            String type=event.getMessage().getType().toString();
////            logger.info("\nMESSAGE_CREATE{"+
////                    "\nmessageID: "+messageId+
////                    "\nguildId:   "+guildId+
////                    "\nchannelId: "+channelId+
////                    "\ncontentEmbed: "+contentEmbed+
////                    "\ncontent: " +content+
////                    "\nauthorId:  "+authorId+
////                    "\ntime:      "+time+
////                    "\ntype:"+type+
////                    "\n}"
////            );
////        });
////
////        client.getEventDispatcher().on(MessageDeleteEvent.class).subscribe(event -> {
////            long authorId = event.getMessage().get().getAuthor().get().getId().asLong();
////            long time = event.getMessage().get().getTimestamp().toEpochMilli();
////            long channelId = event.getChannelId().asLong();
////            long messageId=event.getMessageId().asLong();
////            long guildId = event.getGuildId().get().asLong();
////            String contentEmbed=event.getMessage().get().getEmbeds().toString();
////
////            String content = event.getMessage().get().getContent();
////            logger.info("\nMESSAGE_DELETE{"+
////                    "\nmessageID: "+messageId+
////                    "\nguildId:   "+guildId+
////                    "\nchannelId: "+channelId+
////                    "\ncontentEmbed: "+contentEmbed+
////                    "\ncontent: " +content+
////                    "\nauthorId:  "+authorId+
////                    "\ntime:      "+time+
////                    "\n}"
////            );
////        });
////
////
////        client.getEventDispatcher().on(MessageUpdateEvent.class).subscribe(event -> {
////            long messageID=event.getMessageId().asLong();
////            long guildId = event.getGuildId().get().asLong();
////            long channelId = event.getChannelId().asLong();
////            String discordEmbed=event.getCurrentEmbeds().toString();
////            String oldContent= event.getOld().get().getContent();
////            String currentContent = event.getCurrentContent().get();
////            long authorId = event.getMessage().block().getAuthor().get().getId().asLong();
////            long time = Instant.now().getEpochSecond();
////            logger.info("\nMESSAGE_UPDATE{"+
////                    "\nmessageID: "+messageID+
////                    "\nguildId:   "+guildId+
////                    "\nchannelId: "+channelId+
////                    "\ndiscordEmbed: "+discordEmbed+
////                    "\noldContent: " +oldContent+
////                    "\ncurrentContent: "+currentContent+
////                    "\nauthorId:  "+authorId+
////                    "\ntime:      "+time+
////                    "\n}"
////                    );
////        });
////        client.getEventDispatcher().on()
//        client.getEventDispatcher().on(ReadyEvent.class)
//                .subscribe(event -> {
//                    StringBuilder log = new StringBuilder();
//                    final User self = event.getSelf();
//                    log.append("READY EVENT");
//                    log.append(" Username:");
//                    log.append(self.getUsername());
//                    log.append(", User ID:");
//                    log.append(self.getUserData().id());
//                    log.append(", User ID(2):");
//                    log.append(self.getId());
//                    log.append(", email:");
//                    log.append(self.getUserData().email().toString());
//                    logger.info(log.toString());
//                    DiscordLogCacheManager.write(log.toString());
//                });
//        client.getEventDispatcher().on(Event.class).subscribe(event -> System.out.println(event.getClass()));
//        logger.info("DISCORD READY");
//        client.getGuildMembers(Snowflake.of(234)).doOnComplete(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//    }
//
//    public void createProfile() {
//
//    }
//}
