package net.md_5.bungee.netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.netty.PacketMapping;
import net.md_5.bungee.netty.PacketWrapper;
import net.md_5.bungee.netty.Var;
import net.md_5.bungee.netty.packetrewriter.PacketRewriter;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.DefinedPacket;
import net.md_5.bungee.protocol.packet.protocolhack.PacketHandshake;

import java.util.List;

public class PacketTranslatorDecoder extends ByteToMessageDecoder
{

    @Setter
    @Getter
    int nextState = 0;

    public final static int STATUS = 1;
    public final static int LOGIN = 2;
    public final static int INGAME = 3;

    Protocol protocol;
    public PacketTranslatorDecoder(Protocol protocol)
    {
        this.protocol = protocol;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        int packetId = Var.readVarInt( in );
        if ( nextState == INGAME )
        {
            short mappedPacketId = PacketMapping.cpm[ packetId ]; // Convert to 1.6.4 packet id
            PacketRewriter rewriter = PacketMapping.rewriters[ mappedPacketId ];
            ByteBuf content = Unpooled.buffer();
            try
            {
                content.writeByte( mappedPacketId );
                if ( rewriter == null )
                {
                    content.writeBytes( in.readBytes( in.readableBytes() ) );
                } else
                {
                    rewriter.rewriteClientToServer( in, content );
                }
                ByteBuf copy = content.copy();
                DefinedPacket packet = protocol.read( content.readUnsignedByte(), content );
                if ( in.readableBytes() != 0 )
                {
                    throw new BadPacketException( "Did not read all bytes from packet " + packet.getClass() + " " + packetId + " Protocol " + protocol );
                }
                out.add( new PacketWrapper( packet, copy ) );
            } finally
            {
                content.release();
            }
        } else
        {
            ByteBuf copy = in.copy();
            DefinedPacket packet = PacketMapping.readInitialPacket( packetId, nextState, in );
            if ( packet instanceof PacketHandshake )
            {
                int state = ((PacketHandshake) packet).getState();
                if ( state == STATUS || state == LOGIN )
                {
                    nextState = state;
                }
            }
            if ( in.readableBytes() != 0 )
            {
                throw new BadPacketException( "Did not read all bytes from packet " + packet.getClass() + " " + packetId + " Protocol " + protocol );
            }
            out.add( new PacketWrapper( packet, copy ) );
        }
    }
}
