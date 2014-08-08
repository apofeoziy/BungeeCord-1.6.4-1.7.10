package net.md_5.bungee.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.decoders.DetectingDecoder;
import net.md_5.bungee.netty.decoders.PacketDecoder;
import net.md_5.bungee.netty.encoders.DefinedPacketEncoder;
import net.md_5.bungee.protocol.Vanilla;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class PipelineUtils
{

    public static final AttributeKey<ListenerInfo> LISTENER = new AttributeKey<>( "ListerInfo" );
    public static final AttributeKey<UserConnection> USER = new AttributeKey<>( "User" );
    public static final AttributeKey<BungeeServerInfo> TARGET = new AttributeKey<>( "Target" );
    public static final ChannelInitializer<Channel> SERVER_CHILD = new ChannelInitializer<Channel>()
    {
        @Override
        protected void initChannel(Channel ch) throws Exception
        {
            if ( BungeeCord.getInstance().getConnectionThrottle().throttle( ( (InetSocketAddress) ch.remoteAddress() ).getAddress() ) )
            {
                // TODO: Better throttle - we can't throttle this way if we want to maintain 1.7 compat!
                // ch.close();
                // return;
            }

            CLIENT_BASE.initChannel( ch );
            ch.pipeline().get( HandlerBoss.class ).setHandler( new InitialHandler( ProxyServer.getInstance(), ch.attr( LISTENER ).get() ) );
        }
    };
    public static final Base BASE = new Base();
    public static final Client CLIENT_BASE = new Client();
    private static final DefinedPacketEncoder packetEncoder = new DefinedPacketEncoder( false );
    public static String TIMEOUT_HANDLER = "timeout";
    public static String PACKET_DECODE_HANDLER = "packet-decoder";
    public static String PACKET_ENCODE_HANDLER = "packet-encoder";
    public static String BOSS_HANDLER = "inbound-boss";
    public static String ENCRYPT_HANDLER = "encrypt";
    public static String DECRYPT_HANDLER = "decrypt";

    // 1.7.2 support
    public static String VARINT_ENCODE_HANDLER = "varint-encoder";
    public static String VARINT_DECODE_HANDLER = "varint-decoder";
    public static String TRANSLATOR_DECODE_HANDLER = "translate-decoder";
    public static String TRANSLATOR_ENCODE_HANDLER = "translate-encoder";
    public static String DETECTING_DECODER = "dual-protocol-packet-decoder";

    public final static class Base extends ChannelInitializer<Channel>
    {

        @Override
        public void initChannel(Channel ch) throws Exception
        {
            try
            {
                ch.config().setOption( ChannelOption.IP_TOS, 0x18 );
            } catch ( ChannelException ex )
            {
                // IP_TOS is not supported (Windows XP / Windows Server 2003)
            }

            ch.pipeline().addLast( TIMEOUT_HANDLER, new ReadTimeoutHandler( BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS ) );
            ch.pipeline().addLast( PACKET_DECODE_HANDLER, new PacketDecoder( Vanilla.getInstance() ) );
            ch.pipeline().addLast( PACKET_ENCODE_HANDLER, new DefinedPacketEncoder( true ) );
            ch.pipeline().addLast( BOSS_HANDLER, new HandlerBoss() );
        }
    };



    public final static class Client extends ChannelInitializer<Channel>
    {

        @Override
        public void initChannel(Channel ch) throws Exception
        {
            try
            {
                ch.config().setOption( ChannelOption.IP_TOS, 0x18 );
            } catch ( ChannelException ex )
            {
                // IP_TOS is not supported (Windows XP / Windows Server 2003)
            }

            ch.pipeline().addLast( TIMEOUT_HANDLER, new ReadTimeoutHandler( BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS ) );
            ch.pipeline().addLast( DETECTING_DECODER, new DetectingDecoder( Vanilla.getInstance() ) );
            ch.pipeline().addLast( PACKET_ENCODE_HANDLER, packetEncoder );
            ch.pipeline().addLast( BOSS_HANDLER, new HandlerBoss() );
        }
    };
}
