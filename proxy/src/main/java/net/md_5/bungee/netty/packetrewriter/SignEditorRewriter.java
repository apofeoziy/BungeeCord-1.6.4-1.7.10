package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;

public class SignEditorRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        in.skipBytes( 1 );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }
}
