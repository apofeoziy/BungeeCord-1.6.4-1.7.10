package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;

public class UseBedRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( false );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        out.writeBytes( in.readBytes( 4 ) );
        in.skipBytes( 1 );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

}
