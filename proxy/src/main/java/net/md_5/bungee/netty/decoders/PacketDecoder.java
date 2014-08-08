package net.md_5.bungee.netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.netty.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.DefinedPacket;
import net.md_5.bungee.protocol.skip.PacketReader;

import java.util.List;

/**
 * This class will attempt to read a packet from {@link PacketReader}, with the
 * specified {@link #protocol} before returning a new {@link ByteBuf} with the
 * copied contents of all bytes read in this frame.
 * <p/>
 * It is based on {@link ReplayingDecoder} so that packets will only be returned
 * when all needed data is present.
 */
@RequiredArgsConstructor
public class PacketDecoder extends ReplayingDecoder<Void>
{

    @NonNull
    @Getter
    @Setter
    private Protocol protocol;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        // While we have enough data
        while ( true )
        {
            // Store our start index
            int  startIndex = in.readerIndex();
            short packetId = in.readUnsignedByte();
            //  Run packet through framer
            DefinedPacket packet = protocol.read( packetId, in );
            // If we got this far, it means we have formed a packet, so lets grab the end index
            int endIndex = in.readerIndex();
            // Allocate a buffer big enough for all bytes we have read
            ByteBuf buf = in.copy( startIndex, endIndex - startIndex );
            // Checkpoint our state incase we don't have enough data for another packet
            checkpoint();
            // Store our decoded message
            out.add( new PacketWrapper( packet, buf ) );
        }
    }
}
