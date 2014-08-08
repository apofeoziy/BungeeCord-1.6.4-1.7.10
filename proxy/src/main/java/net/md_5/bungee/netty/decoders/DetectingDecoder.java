package net.md_5.bungee.netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.netty.encoders.PacketTranslatorEncoder;
import net.md_5.bungee.netty.encoders.Varint21LengthFieldPrepender;
import net.md_5.bungee.protocol.Protocol;

import java.util.List;

@AllArgsConstructor
public class DetectingDecoder extends ByteToMessageDecoder {

    Protocol protocol;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception
    {
        ChannelPipeline p = ctx.pipeline();
        if ( identifyProtocol( msg ) )
        {
            // 1.7.2
            PacketTranslatorDecoder trDecoder = new PacketTranslatorDecoder( protocol );
            p.addBefore( PipelineUtils.PACKET_ENCODE_HANDLER, PipelineUtils.TRANSLATOR_DECODE_HANDLER, trDecoder );
            p.addBefore( PipelineUtils.TRANSLATOR_DECODE_HANDLER, PipelineUtils.TRANSLATOR_ENCODE_HANDLER, new PacketTranslatorEncoder( trDecoder ) );
            p.addBefore( PipelineUtils.TRANSLATOR_ENCODE_HANDLER, PipelineUtils.VARINT_ENCODE_HANDLER, new Varint21LengthFieldPrepender() );
            p.addBefore( PipelineUtils.VARINT_ENCODE_HANDLER, PipelineUtils.PACKET_DECODE_HANDLER, new VarIntPacketLengthDecoder() );
        } else
        {
            // 1.6.4
            p.addBefore( PipelineUtils.PACKET_ENCODE_HANDLER, PipelineUtils.PACKET_DECODE_HANDLER, new PacketDecoder( protocol ) );
        }
        ctx.pipeline().flush();
        ctx.pipeline().remove( this );
    }

    private boolean identifyProtocol(ByteBuf in)
    {
        int index = in.readerIndex();
        try
        {
            short packetId = in.readUnsignedByte();
            return packetId != 0x02 && packetId != 0xFE;
        }
        finally {
            in.readerIndex( index );
        }
    }
}
