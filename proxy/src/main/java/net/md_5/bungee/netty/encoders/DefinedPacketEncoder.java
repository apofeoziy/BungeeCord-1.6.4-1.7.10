package net.md_5.bungee.netty.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.protocol.packet.DefinedPacket;

@ChannelHandler.Sharable
public class DefinedPacketEncoder extends MessageToByteEncoder<DefinedPacket>
{
    boolean debugOutput;
    public DefinedPacketEncoder(boolean debugOutput) {
        this.debugOutput = debugOutput;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, ByteBuf out) throws Exception
    {
        out.writeByte( msg.getId() );
        msg.write( out );
    }
}
