package net.md_5.bungee.netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.md_5.bungee.netty.Var;
import net.md_5.bungee.protocol.BadPacketException;

import java.util.List;

public class VarIntPacketLengthDecoder extends ByteToMessageDecoder
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception
    {
        msg.markReaderIndex();
        final byte[] buf = new byte[ 3 ];
        for ( int i = 0; i < buf.length; i++ )
        {
            if ( !msg.isReadable() )
            {
                msg.resetReaderIndex();
                return;
            }

            buf[i] = msg.readByte();
            if ( buf[i] >= 0 )
            {
                int length = Var.readVarInt(Unpooled.wrappedBuffer(buf));

                if ( msg.readableBytes() < length )
                {
                    msg.resetReaderIndex();
                    return;
                } else
                {
                    out.add( msg.readBytes( length ) );
                    return;
                }
            }
        }
        throw new BadPacketException( "length too long" );
    }
}
